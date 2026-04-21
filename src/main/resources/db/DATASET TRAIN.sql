--Analisis rapido
select MAX(p.temperatura),  MIN(p.temperatura), AVG(p.temperatura)
from public.predicciones p

--Agregacion de nueva tabla
ALTER TABLE predicciones
ADD COLUMN temp_categoria smallint;

UPDATE predicciones
SET temp_categoria =
    CASE
        WHEN temperatura < 60 THEN 1
        WHEN temperatura < 70 THEN 2
        WHEN temperatura < 85 THEN 3
        ELSE 4
    END;

---Realizar un muestreo basado distribucion real
SELECT
    temp_categoria,
    COUNT(*) AS total,
    ROUND(100.0 * COUNT(*) / SUM(COUNT(*)) OVER (), 2) AS porcentaje
FROM predicciones
GROUP BY temp_categoria
ORDER BY temp_categoria;

--- Definir la distribución DESEADA
-- 1 - > 30% --Normal
-- 2 - > 30% --Advertencia
-- 3 - > 25% -- Peligro
-- 4 - > 15% --Critico

--- Calcular el porcentaje de muestreo por Categoria
-- =====================================================
-- 1. TABLA DE CONFIGURACIÓN DE MUESTREO
-- =====================================================
CREATE TABLE IF NOT EXISTS configuracion_muestreo (
    id SERIAL PRIMARY KEY,
    temp_categoria INTEGER NOT NULL UNIQUE,
    porcentaje_objetivo DECIMAL(5,2) NOT NULL CHECK (porcentaje_objetivo >= 0 AND porcentaje_objetivo <= 100),
    cuota_objetivo INTEGER NOT NULL, -- Número exacto de registros deseados del 1M
    prioridad_completado INTEGER DEFAULT 1, -- Mayor número = más prioridad para completar déficits
    porcentaje_recientes DECIMAL(5,2) DEFAULT 40.0, -- % de la cuota que viene de recientes
    descripcion VARCHAR(120),
    activo BOOLEAN DEFAULT TRUE,
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);

--- 1 Mes 1M  
--- 2 Mes 1.5M
--- 3 Mes 2M
--- 12 (0.5M) =6 M => 7M
--- 24 (0.5M) =6 M => 14M (1 M) -> 40% Reciente / 60% Historicos (recientes atras)

select * from predicciones p 

-- Insertar configuración inicial (suman 1,000,000 registros)
INSERT INTO configuracion_muestreo (temp_categoria, porcentaje_objetivo, cuota_objetivo, prioridad_completado, descripcion) VALUES
(1, 30.0, 300000, 2, 'Temperatura normal - controlada - 30% del dataset'),
(2, 30.0, 300000, 2, 'Temperatura alarmante - signo de peligro- 30% del dataset'),
(3, 25.0, 250000, 3, 'Temperatura peligrosa - potencial perdida - 25% del dataset'),
(4, 15.0, 150000, 4, 'Temperatura critica - daño inminente - 15% del dataset')
ON CONFLICT (temp_categoria) DO UPDATE SET
    porcentaje_objetivo = EXCLUDED.porcentaje_objetivo,
    cuota_objetivo = EXCLUDED.cuota_objetivo,
    prioridad_completado = EXCLUDED.prioridad_completado,
    fecha_actualizacion = NOW();

select * from configuracion_muestreo cm 

-- =====================================================
-- 2. VISTA MATERIALIZADA CON DISTRIBUCIÓN EQUILIBRADA
-- =====================================================
create MATERIALIZED  VIEW mv_dataset_entrenamiento AS
WITH configuracion AS (
    SELECT 
        temp_categoria,
        cuota_objetivo,
        FLOOR(cuota_objetivo * porcentaje_recientes / 100.0)::INTEGER as cuota_recientes,
        prioridad_completado
    FROM configuracion_muestreo
    WHERE activo = TRUE
),
-- OPTIMIZACIÓN 1: Un solo escaneo para datos recientes con LIMIT por categoría
recientes_ordenados AS (
    SELECT
        p.ambiente, p.corriente_d, p.corriente_q, p.refrigeracion, p.temperatura,
        p.velocidad, p.voltaje_d, p.voltaje_q, p.fecha_creacion, p.fk_usuario,
        p.uuid, p.temp_categoria,
        ROW_NUMBER() OVER (PARTITION BY p.temp_categoria ORDER BY p.fecha_creacion DESC) as rn
    FROM predicciones p
    WHERE p.fecha_creacion >= NOW() - INTERVAL '45 days'
      AND EXISTS (SELECT 1 FROM configuracion c WHERE c.temp_categoria = p.temp_categoria)
),
recientes_limitados AS (
    SELECT
        r.ambiente, r.corriente_d, r.corriente_q, r.refrigeracion, r.temperatura,
        r.velocidad, r.voltaje_d, r.voltaje_q, r.fecha_creacion, r.fk_usuario,
        r.uuid, r.temp_categoria
    FROM recientes_ordenados r
    INNER JOIN configuracion c ON r.temp_categoria = c.temp_categoria
    WHERE r.rn <= c.cuota_recientes
),
-- OPTIMIZACIÓN 2: Calcular faltantes sin subconsultas múltiples
resumen_recientes AS (
    SELECT
        c.temp_categoria,
        c.cuota_objetivo,
        c.prioridad_completado,
        COALESCE(COUNT(r.uuid), 0) as obtenidos,
        c.cuota_objetivo - COALESCE(COUNT(r.uuid), 0) as faltantes
    FROM configuracion c
    LEFT JOIN recientes_limitados r ON c.temp_categoria = r.temp_categoria
    GROUP BY c.temp_categoria, c.cuota_objetivo, c.prioridad_completado
),
-- OPTIMIZACIÓN 3: Un solo escaneo para históricos usando hash determinista
historicos_candidatos AS (
    SELECT
        p.ambiente, p.corriente_d, p.corriente_q, p.refrigeracion, p.temperatura,
        p.velocidad, p.voltaje_d, p.voltaje_q, p.fecha_creacion, p.fk_usuario,
        p.uuid, p.temp_categoria,
        abs(hashtext(p.uuid::text)) % 10000 as hash_bucket
    FROM predicciones p
    INNER JOIN resumen_recientes rr ON p.temp_categoria = rr.temp_categoria
    WHERE p.fecha_creacion < NOW() - INTERVAL '45 days'
      AND rr.faltantes > 0
      AND NOT EXISTS (SELECT 1 FROM recientes_limitados r WHERE r.uuid = p.uuid)
),
historicos_muestreados AS (
    SELECT
        h.*,
        ROW_NUMBER() OVER (PARTITION BY h.temp_categoria ORDER BY h.hash_bucket) as rn
    FROM historicos_candidatos h
),
historicos_seleccionados AS (
    SELECT
        h.ambiente, h.corriente_d, h.corriente_q, h.refrigeracion, h.temperatura,
        h.velocidad, h.voltaje_d, h.voltaje_q, h.fecha_creacion, h.fk_usuario,
        h.uuid, h.temp_categoria
    FROM historicos_muestreados h
    INNER JOIN resumen_recientes rr ON h.temp_categoria = rr.temp_categoria
    WHERE h.rn <= rr.faltantes
),
-- OPTIMIZACIÓN 4: Union temporal y verificación de déficit en una sola pasada
dataset_parcial AS (
    SELECT * FROM recientes_limitados
    UNION ALL
    SELECT * FROM historicos_seleccionados
),
verificacion_deficit AS (
    SELECT
        c.temp_categoria,
        c.cuota_objetivo,
        COALESCE(COUNT(d.uuid), 0) as obtenidos,
        c.cuota_objetivo - COALESCE(COUNT(d.uuid), 0) as deficit
    FROM configuracion c
    LEFT JOIN dataset_parcial d ON c.temp_categoria = d.temp_categoria
    GROUP BY c.temp_categoria, c.cuota_objetivo
),
deficit_total_calc AS (
    SELECT SUM(GREATEST(deficit, 0)) as total_deficit
    FROM verificacion_deficit
),
-- OPTIMIZACIÓN 5: Completar déficit solo si es necesario
completado_pool AS (
    SELECT
        p.ambiente, p.corriente_d, p.corriente_q, p.refrigeracion, p.temperatura,
        p.velocidad, p.voltaje_d, p.voltaje_q, p.fecha_creacion, p.fk_usuario,
        p.uuid, p.temp_categoria,
        c.prioridad_completado,
        ROW_NUMBER() OVER (ORDER BY c.prioridad_completado DESC, RANDOM()) as rn
    FROM predicciones p
    INNER JOIN configuracion c ON p.temp_categoria = c.temp_categoria
    CROSS JOIN deficit_total_calc dtc
    WHERE dtc.total_deficit > 0
      AND NOT EXISTS (SELECT 1 FROM dataset_parcial dp WHERE dp.uuid = p.uuid)
    LIMIT (SELECT total_deficit * 2 FROM deficit_total_calc) -- Buffer 2x para garantizar suficientes
),
completado_final AS (
    SELECT
        ambiente, corriente_d, corriente_q, refrigeracion, temperatura,
        velocidad, voltaje_d, voltaje_q, fecha_creacion, fk_usuario,
        uuid, temp_categoria
    FROM completado_pool
    WHERE rn <= (SELECT COALESCE(total_deficit, 0) FROM deficit_total_calc)
)
-- RESULTADO FINAL
SELECT * FROM dataset_parcial
UNION ALL
SELECT * FROM completado_final
LIMIT 1000000;


CREATE UNIQUE INDEX IF NOT EXISTS idx_mv_dataset_uuid 
ON mv_dataset_entrenamiento(uuid);

CREATE INDEX IF NOT EXISTS idx_mv_dataset_categoria 
ON mv_dataset_entrenamiento(temp_categoria);

-- =====================================================
-- 4. FUNCIÓN DE REFRESH OPTIMIZADA
-- =====================================================
CREATE OR REPLACE FUNCTION refrescar_dataset_con_analisis()
RETURNS TABLE (
    temp_categoria smallint,
    cuota_objetivo INTEGER,
    total BIGINT,
    porcentaje_cuota DECIMAL(5,2),
    estado TEXT
) AS $$
DECLARE
    inicio TIMESTAMP;
    fin TIMESTAMP;
    duracion INTERVAL;
BEGIN
    inicio := clock_timestamp();
    
    -- Refrescar
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_dataset_entrenamiento;
    
    fin := clock_timestamp();
    duracion := fin - inicio;
    
    RAISE NOTICE 'Refresh completado en: %', duracion;
    
    -- Estadísticas detalladas
    RETURN QUERY
    WITH stats AS (
        SELECT
            m.temp_categoria,
            c.cuota_objetivo,
            COUNT(*) as total_registros,
            COUNT(*) * 100.0 / NULLIF(c.cuota_objetivo, 0) as pct_cuota
        FROM mv_dataset_entrenamiento m
        INNER JOIN configuracion_muestreo c ON m.temp_categoria = c.temp_categoria
        GROUP BY m.temp_categoria, c.cuota_objetivo
    )
    SELECT
        s.temp_categoria,
        s.cuota_objetivo,
        s.total_registros as total,
        ROUND(s.pct_cuota, 2) as porcentaje_cuota,
        CASE
            WHEN s.pct_cuota >= 100 THEN 'Completo'
            WHEN s.pct_cuota >= 95 THEN 'Casi completo'
            WHEN s.pct_cuota >= 80 THEN 'Déficit moderado'
            ELSE 'Déficit alto'
        END as estado
    FROM stats s
    ORDER BY s.temp_categoria;
END;
$$ LANGUAGE plpgsql;


SELECT * FROM refrescar_dataset_con_analisis();

--  Verificar resultado
SELECT 
    temp_categoria,
    COUNT(*) as registros,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 2) as porcentaje
FROM mv_dataset_entrenamiento
GROUP BY temp_categoria;
