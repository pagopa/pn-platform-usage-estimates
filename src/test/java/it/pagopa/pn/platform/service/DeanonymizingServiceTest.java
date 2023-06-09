package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.service.impl.DeanonymizingServiceImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;

class DeanonymizingServiceTest extends BaseTest{

    @Autowired
    DeanonymizingServiceImpl deanonymizingServiceImpl;

    @Autowired
    S3Bucket s3Bucket;

    //@Test
    void givenCSVFile_whenRead_thenContentsAsExpected() throws IOException {
        enum Headers {cap, stato_estero}

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(Headers.class)
                .setSkipHeaderRecord(true)
                .build();

        Reader in = new FileReader("src/test/resources/filecsv/testCSV.csv");

        Iterable<CSVRecord> records = csvFormat.parse(in);

        for (CSVRecord record : records) {
            String cap = record.get(Headers.cap);
            String statoEstero = record.get(Headers.stato_estero);
            assertEquals("00012", cap);
            assertEquals("Italia", statoEstero);
        }

//        CSVParser csvParser = CSVParser.parse(DeanonymizingServiceTest.class.getResourceAsStream("/filecsv/test.csv"), StandardCharsets.UTF_8, CSVFormat.DEFAULT.builder().setSkipHeaderRecord(true).build());
//        Iterator <CSVRecord> iterator = csvParser.iterator();
//        if (iterator.hasNext()){
//            iterator.next();
//        }
//        while (iterator.hasNext()) {
//            CSVRecord record = iterator.next();
//            String cap = record.get("name");
//            String statoEstero = record.get("surname");
//            assertEquals(ACTIVITY_REPORT_CSV.get(cap), statoEstero);
//        }
    }
//    @Test
    void givenAuthorBookMap_whenWrittenToStream_thenOutputStreamAsExpected() throws IOException {
        StringWriter sw = new StringWriter();

        Map<String, String> FILE_CSV = new HashMap<>() {
            {
                put("00012", "Italia");
            }
        };

        enum Headers {cap, stato_estero}

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(Headers.class)
                .build();

        try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
            FILE_CSV.forEach((cap, stato_estero) -> {
                try {
                    printer.printRecord(cap, stato_estero);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        assertEquals("00012, Italia", sw.toString().trim());
    }
}
