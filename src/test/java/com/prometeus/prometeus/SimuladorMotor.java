package com.prometeus.prometeus;

import java.util.Random;

public class SimuladorMotor {

    private Random random = new Random();

    // Variables a simular
    private double temperaturaAmbiente;
    private double temperaturaRefrigerante;
    private double voltajeD;
    private double voltajeQ;
    private double corrienteD;
    private double corrienteQ;
    private double velocidadMotor;
    private double torqueInducido;

    // Rango máximo y mínimo para cada variable
    private final double MIN_TEMP_AMBIENTE = 15; // 15°C
    private final double MAX_TEMP_AMBIENTE = 50; // 50°C
    private final double MIN_TEMP_REFRIGERANTE = 20; // 20°C
    private final double MAX_TEMP_REFRIGERANTE = 100; // 100°C
    private final double MIN_VOLTAGE = 0; // 0V
    private final double MAX_VOLTAGE = 600; // 600V
    private final double MIN_CORRIENTE = 0; // 0A
    private final double MAX_CORRIENTE = 100; // 100A
    private final double MIN_TORQUE = 0; // 0 Nm
    private final double MAX_TORQUE = 1000; // 1000 Nm
    private final double MIN_VELOCIDAD = 0; // 0 RPM
    private final double MAX_VELOCIDAD = 3000; // 3000 RPM

    // Constructor
    public SimuladorMotor() {
        // Inicializar con valores aleatorios dentro de rangos razonables
        this.temperaturaAmbiente = generarTemperatura(MIN_TEMP_AMBIENTE, MAX_TEMP_AMBIENTE);
        this.temperaturaRefrigerante = generarTemperatura(MIN_TEMP_REFRIGERANTE, MAX_TEMP_REFRIGERANTE);
        this.voltajeD = generarVoltaje(MIN_VOLTAGE, MAX_VOLTAGE);
        this.voltajeQ = generarVoltaje(MIN_VOLTAGE, MAX_VOLTAGE);
        this.corrienteD = generarCorriente(MIN_CORRIENTE, MAX_CORRIENTE);
        this.corrienteQ = generarCorriente(MIN_CORRIENTE, MAX_CORRIENTE);
        this.velocidadMotor = generarVelocidad(MIN_VELOCIDAD, MAX_VELOCIDAD);
        this.torqueInducido = generarTorque(MIN_TORQUE, MAX_TORQUE);
    }

    // Método para generar una temperatura aleatoria y limitarla a un rango
    private double generarTemperatura(double min, double max) {
        double temp = min + (max - min) * random.nextDouble();
        return limitarValor(temp, min, max);
    }

    // Método para generar un voltaje aleatorio y limitarlo a un rango
    private double generarVoltaje(double min, double max) {
        double volt = min + (max - min) * random.nextDouble();
        return limitarValor(volt, min, max);
    }

    // Método para generar una corriente aleatoria y limitarla a un rango
    private double generarCorriente(double min, double max) {
        double corriente = min + (max - min) * random.nextDouble();
        return limitarValor(corriente, min, max);
    }

    // Método para generar una velocidad del motor aleatoria y limitarla a un rango
    private double generarVelocidad(double min, double max) {
        double velocidad = min + (max - min) * random.nextDouble();
        return limitarValor(velocidad, min, max);
    }

    // Método para generar el torque inducido aleatorio y limitarlo a un rango
    private double generarTorque(double min, double max) {
        double torque = min + (max - min) * random.nextDouble();
        return limitarValor(torque, min, max);
    }

    // Método para limitar cualquier valor dentro de los límites definidos
    private double limitarValor(double valor, double min, double max) {
        if (valor < min) {
            return min;
        } else if (valor > max) {
            return max;
        } else {
            return valor;
        }
    }

    // Lógica de interacción entre las variables
    private void actualizarCondiciones() {
        // Correlación entre torque y temperatura del motor
        if (torqueInducido > 300) {
            temperaturaAmbiente += 2; // A mayor torque, mayor temperatura ambiente
        }

        // La velocidad del motor afecta el torque (relación simple)
        if (velocidadMotor > 2000) {
            torqueInducido += 50; // Mayor velocidad, mayor torque
        } else {
            torqueInducido -= 10; // Menor velocidad, menor torque
        }

        // La temperatura ambiente afecta la temperatura del refrigerante
        if (temperaturaAmbiente > 30) {
            temperaturaRefrigerante += 5; // Si hace más calor, el refrigerante se calienta más
        } else {
            temperaturaRefrigerante -= 2; // En temperaturas más bajas, el refrigerante está más frío
        }

        // La corriente afecta los voltajes
        voltajeD = corrienteD * 5; // Voltaje relacionado con la corriente, escala simple
        voltajeQ = corrienteQ * 5; // Mismo para el voltaje Q

        // Si el voltaje es muy alto, la corriente puede disminuir
        if (voltajeD > 250) {
            corrienteD -= 5; // Si el voltaje sube mucho, la corriente puede reducirse
        }
        if (voltajeQ > 250) {
            corrienteQ -= 5; // Similar para el voltaje Q
        }

        // Aseguramos que las variables siguen dentro de los límites
        temperaturaAmbiente = limitarValor(temperaturaAmbiente, MIN_TEMP_AMBIENTE, MAX_TEMP_AMBIENTE);
        temperaturaRefrigerante = limitarValor(temperaturaRefrigerante, MIN_TEMP_REFRIGERANTE, MAX_TEMP_REFRIGERANTE);
        voltajeD = limitarValor(voltajeD, MIN_VOLTAGE, MAX_VOLTAGE);
        voltajeQ = limitarValor(voltajeQ, MIN_VOLTAGE, MAX_VOLTAGE);
        corrienteD = limitarValor(corrienteD, MIN_CORRIENTE, MAX_CORRIENTE);
        corrienteQ = limitarValor(corrienteQ, MIN_CORRIENTE, MAX_CORRIENTE);
        velocidadMotor = limitarValor(velocidadMotor, MIN_VELOCIDAD, MAX_VELOCIDAD);
        torqueInducido = limitarValor(torqueInducido, MIN_TORQUE, MAX_TORQUE);
    }

    // Método para obtener los valores del simulador
    public void mostrarLecturas() {
        // Actualizar las condiciones de las variables con sus interacciones
        actualizarCondiciones();
        
        // Mostrar las lecturas actuales
        System.out.println("Temperatura Ambiente: " + temperaturaAmbiente + "°C");
        System.out.println("Temperatura Refrigerante: " + temperaturaRefrigerante + "°C");
        System.out.println("Voltaje D: " + voltajeD + "V");
        System.out.println("Voltaje Q: " + voltajeQ + "V");
        System.out.println("Corriente D: " + corrienteD + "A");
        System.out.println("Corriente Q: " + corrienteQ + "A");
        System.out.println("Velocidad del Motor: " + velocidadMotor + " RPM");
        System.out.println("Torque Inducido: " + torqueInducido + " Nm");
    }

    // Método principal para simular el monitoreo
    public static void main(String[] args) {
        SimuladorMotor simulador = new SimuladorMotor();
        
        // Simulación en intervalos (por ejemplo, cada 2 segundos)
        for (int i = 0; i < 10; i++) {
            System.out.println("Simulación #" + (i + 1));
            simulador.mostrarLecturas();
            System.out.println("--------------------------");
            
            // Esperar 2 segundos (2000 ms)
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

