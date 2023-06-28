package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.encription.model.DataEncryption;
import it.pagopa.pn.platform.mapper.ActivityReportMapper;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.model.ActivityReportCSV;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileCreationResponseDto;
import it.pagopa.pn.platform.service.DeanonymizingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@Slf4j
public class DeanonymizingServiceImpl implements DeanonymizingService {


    @Autowired
    private ActivityReportMetaDAO activityReportMetaDAO;
    @Autowired
    private S3Bucket s3Bucket;
    private DataEncryption dataEncryption;

    @Autowired
    private SafeStorageClient safeStorageClient;

    @Override
    public Mono<Void> execute(String paId, String fileKey) {
        return getCSV(paId, fileKey)
                .flatMap(activityReportCSVList -> activityReportMetaDAO.findByPaIdAndFileKey(paId, fileKey)
                        .flatMap(pnActivityReport -> {
                            File file = writeObjectsToCSV(activityReportCSVList, pnActivityReport.getBucketName());
                            zipFile(file.getPath(), "compressed.zip");
                            return getPresignedUrlSaveKey(pnActivityReport);
                        })).map(fileCreationResponseDtoMono -> {
                            safeStorageClient.uploadFile(fileCreationResponseDtoMono.getUploadUrl(), null);
                    return Mono.empty();
                }).then();
    }

    private Mono<List<ActivityReportCSV>> getCSV(String paId, String fileKey) {

        return this.activityReportMetaDAO.findByPaIdAndFileKey(paId, fileKey)
                .map(pnActivityReport -> {
                    List<ActivityReportCSV> activityReportDeanonymizingCSV;
                    InputStreamReader file = s3Bucket.getObjectData(pnActivityReport.getReportKey());
                    CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                            .setHeader(ActivityReportCSV.Header.class)
                            .setSkipHeaderRecord(true)
                            .build();
                    try {
                        Reader in = new BufferedReader(file);
                        Iterable<CSVRecord> records = csvFormat.parse(in);
                        activityReportDeanonymizingCSV = deanonymizingRaw(ActivityReportMapper.csvToObject(records));
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                    return activityReportDeanonymizingCSV;
                });
    }

    private List<ActivityReportCSV> deanonymizingRaw(List<ActivityReportCSV> activityReportCSVList){
        List<ActivityReportCSV> fileDeanonymizing = new ArrayList<>();
        activityReportCSVList.forEach(activityReportCSV -> {
           if (StringUtils.isNotBlank(activityReportCSV.getRecipientTaxId())){
               activityReportCSV.setRecipientTaxId(dataEncryption.decode(activityReportCSV.getRecipientTaxId()));
               fileDeanonymizing.add(activityReportCSV);
           }
        });
        return fileDeanonymizing;
    }

    private File writeObjectsToCSV (List<ActivityReportCSV> activityReportCSVList, String filePath) {
        File file = new File(filePath);
        activityReportCSVList.forEach(activityReportCSV -> {
            try (FileWriter fileWriter = new FileWriter(file);
                 CSVPrinter csvPrinter = new CSVPrinter(fileWriter,
                         CSVFormat.DEFAULT.builder().setHeader(ActivityReportCSV.Header.class).build())) {
                csvPrinter.printRecord(activityReportCSV);
                csvPrinter.flush();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });
        return file;
    }

    public void zipFile(String sourceFile, String zipFile) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            addFolderToZip(sourceFile, zipOutputStream);

            zipOutputStream.close();
            fileOutputStream.close();

        }catch (IOException exception){
            throw new RuntimeException(exception);
        }

    }

    private static void addFolderToZip(String folderPath, ZipOutputStream zipStream) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                zipStream.putNextEntry(new ZipEntry(file.getName()));
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipStream.write(buffer, 0, length);
                }
                zipStream.closeEntry();
                fileInputStream.close();
            }
        }
    }

    private Mono<FileCreationResponseDto> getPresignedUrlSaveKey (PnActivityReport pnActivityReport){

        return safeStorageClient.getPresignedUrl().map(fileCreationResponseDto -> {
            String key = fileCreationResponseDto.getKey();
            pnActivityReport.setKeySafeStorage(key);
            activityReportMetaDAO.createMetaData(pnActivityReport);
            return fileCreationResponseDto;
        });
    }

    public Mono<Void> uploadZipFile (String presignedUrl) throws IOException {
        InputStream stream = new FileInputStream("src/main/resources/compressed.zip");
        byte[] bytes = stream.readAllBytes();
        return safeStorageClient.uploadFile(presignedUrl, bytes).then();
    }

}
