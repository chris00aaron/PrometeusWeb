package com.prometeus.prometeus.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BackupService {
    private static final String PG_DUMP_PATH = "C:\\Program Files\\PostgreSQL\\17\\bin\\pg_dump.exe";
    private static final String BACKUP_DIR = "C:\\BackupsPostgres";
    private static final String DB_NAME = "prometeus";
    private static final String USER = "postgres";

    //@Scheduled(cron = "0 0 3 * * *")  
    // todos los días a las 3 AM: cron = "0 0 3 * * *" 
    // cada 30s: fixedRate = 30000
    public void backupDatabase() {
        try {
            String timestamp = LocalDateTime.now()
                    .toString()
                    .replace(":", "-")
                    .replace(".", "-");

            String backupFile = BACKUP_DIR + "\\backup_" + timestamp + ".backup";

            String command = String.format(
                "\"%s\" -U %s -F c -f \"%s\" %s",
                PG_DUMP_PATH, USER, backupFile, DB_NAME
            );

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);

            // Variables de entorno: pedir contraseña sin prompt
            pb.environment().put("PGPASSWORD", "1234");

            pb.redirectErrorStream(true);
            Process process = pb.start();

            process.waitFor();
            System.out.println("Backup generado: " + backupFile);

            String zipFile = backupFile.replace(".backup", ".zip");
            zipBackup(backupFile, zipFile);

            // Elimina el archivo .backup original
            new java.io.File(backupFile).delete();

            System.out.println("Backup comprimido: " + zipFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zipBackup(String sourceFile, String zipFile) throws Exception {
        try (
            java.io.FileOutputStream fos = new java.io.FileOutputStream(zipFile);
            java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream(fos);
            java.io.FileInputStream fis = new java.io.FileInputStream(sourceFile)
        ) {
            java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(new java.io.File(sourceFile).getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

}