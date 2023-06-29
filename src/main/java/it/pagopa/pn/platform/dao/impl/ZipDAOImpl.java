package it.pagopa.pn.platform.dao.impl;

import it.pagopa.pn.platform.dao.DAOException;
import it.pagopa.pn.platform.dao.ZipDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Component
@Slf4j
public class ZipDAOImpl implements ZipDAO {
    private static final String ZIP_DEANONYMIZED = "/report_compressed.zip";


    @Override
    public void zipFiles(String directoryPath) {
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(directoryPath.concat(ZIP_DEANONYMIZED));
                ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        ) {
            File directory = new File(directoryPath);
            if (!directory.isDirectory()) throw new DAOException("The folder of the reports doesn't exist");

            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (!file.getName().contains(".zip")) {

                    zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = fileInputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                    zipOutputStream.closeEntry();
                    fileInputStream.close();
                }
            }

        } catch (IOException exception) {
            log.error("exception zip files {}", exception.getMessage());
            throw new DAOException("Error with creating zip file");
        }
    }

    @Override
    public byte[] getZipFile(String directoryPath) {
        byte[] bytes;
        try (InputStream stream = new FileInputStream(directoryPath.concat(ZIP_DEANONYMIZED));) {
            bytes = stream.readAllBytes();
            return bytes;
        } catch (IOException ex) {
            log.error("Error with read of zip file");
            throw new DAOException("Zip file not found");
        }
    }

}
