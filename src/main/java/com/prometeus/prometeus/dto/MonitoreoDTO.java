package com.prometeus.prometeus.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class MonitoreoDTO {
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String timestamp;
    private final String temperatura;

    public MonitoreoDTO(LocalDateTime timestamp, String temperatura) {
        this.timestamp = timestamp.format(formatter);  // Formateamos el timestamp
        this.temperatura = temperatura;
    }
}
