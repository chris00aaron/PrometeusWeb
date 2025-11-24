package com.prometeus.prometeus.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.prometeus.prometeus.dto.PrediccionRequest;

@Service
public class CSVService {

    public List<PrediccionRequest> parseCsv(MultipartFile file) throws IOException {

        List<PrediccionRequest> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            int numLinea = 0;

            while ((linea = br.readLine()) != null) {
                numLinea++;

                // Saltar encabezado
                if (numLinea == 1) continue;

                if (linea.trim().isEmpty()) continue;

                String[] campos = linea.split(";");

                if (campos.length < 7) {
                    throw new IllegalArgumentException(
                            "La línea " + numLinea + " no tiene 7 columnas.");
                }

                PrediccionRequest dto = new PrediccionRequest();

                dto.setAmbiente(parseBig(campos[0], "ambiente", numLinea));
                dto.setCoolant(parseBig(campos[1], "coolant", numLinea));
                dto.setU_d(parseBig(campos[2], "u_d", numLinea));
                dto.setU_q(parseBig(campos[3], "u_q", numLinea));
                dto.setMotor_speed(parseInt(campos[4], "motor_speed", numLinea));
                dto.setI_d(parseBig(campos[5], "i_d", numLinea));
                dto.setI_q(parseBig(campos[6], "i_q", numLinea));

                lista.add(dto);
            }
        }

        return lista;
    }

    public byte[] exportarResultados(List<PrediccionRequest> datos, List<BigDecimal> resultados) throws IOException {

        StringBuilder sb = new StringBuilder();

        // Encabezado
        sb.append("ambiente;coolant;u_d;u_q;motor_speed;i_d;i_q;resultado\n");

        // Filas
        for (int i = 0; i < datos.size(); i++) {
            PrediccionRequest d = datos.get(i);
            BigDecimal temp = resultados.get(i);

            sb.append(d.getAmbiente()).append(";")
              .append(d.getCoolant()).append(";")
              .append(d.getU_d()).append(";")
              .append(d.getU_q()).append(";")
              .append(d.getMotor_speed()).append(";")
              .append(d.getI_d()).append(";")
              .append(d.getI_q()).append(";")
              .append(temp).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private BigDecimal parseBig(String v, String campo, int linea) {
        try {
            return new BigDecimal(v.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Error en campo '" + campo + "' en línea " + linea + ": valor inválido (" + v + ")");
        }
    }

    private Integer parseInt(String v, String campo, int linea) {
        try {
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Error en campo '" + campo + "' en línea " + linea + ": valor inválido (" + v + ")");
        }
    }
}
