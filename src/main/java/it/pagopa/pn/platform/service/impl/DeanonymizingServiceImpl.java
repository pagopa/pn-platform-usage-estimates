package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.encription.model.DataEncryption;
import it.pagopa.pn.platform.mapper.ActivityReportMapper;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.model.ActivityReportCSV;
import it.pagopa.pn.platform.service.DeanonymizingService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class DeanonymizingServiceImpl implements DeanonymizingService {


    @Autowired
    private ActivityReportMetaDAO activityReportMetaDAO;
    @Autowired
    private S3Bucket s3Bucket;
    private DataEncryption dataEncryption;

    public Mono<List<ActivityReportCSV>> getCSV(String paId, String fileKey) {

        return activityReportMetaDAO.findByPaIdAndFileKey(paId, fileKey)
                .map(pnActivityReport -> {
                    List<ActivityReportCSV> activityReportCSV;
                    InputStreamReader file = s3Bucket.getObjectData(pnActivityReport.getReportKey());
                    CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                            .setHeader(ActivityReportCSV.Header.class)
                            .setSkipHeaderRecord(true)
                            .build();
                    try {
                        Reader in = new BufferedReader(file);
                        Iterable<CSVRecord> records = csvFormat.parse(in);
                        activityReportCSV = ActivityReportMapper.csvToObject(records);
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                    return activityReportCSV;
                });
    }

    private List<ActivityReportCSV> deanonymizingRaw(List<ActivityReportCSV> activityReportCSVList){
        List<ActivityReportCSV> fileDeanonymizing = new ArrayList<>();
        activityReportCSVList.forEach(activityReportCSV -> {
           if (activityReportCSV.getRecipientTaxId() != null){
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return file;
    }

}
