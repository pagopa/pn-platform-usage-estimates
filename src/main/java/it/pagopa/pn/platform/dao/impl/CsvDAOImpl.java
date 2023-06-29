package it.pagopa.pn.platform.dao.impl;


import it.pagopa.pn.platform.dao.CsvDAO;
import it.pagopa.pn.platform.dao.DAOException;
import it.pagopa.pn.platform.mapper.ActivityReportMapper;
import it.pagopa.pn.platform.model.ActivityReportCSV;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.*;
import java.util.List;

@Slf4j
@Component
public class CsvDAOImpl implements CsvDAO {
    private final CSVFormat csvFormat;

    public CsvDAOImpl() {

        this.csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(ActivityReportCSV.Header.class)
                .build();
    }

    @Override
    public Flux<ActivityReportCSV> toRows(InputStreamReader reader) {
        try {
            Reader in = new BufferedReader(reader);
            Iterable<CSVRecord> records = csvFormat.parse(in);
            return Flux.fromIterable(records)
                    .parallel()
                    .map(ActivityReportMapper::csvToObject)
                    .sequential();
        } catch (IOException exception) {
            throw new DAOException("Error with parsing csv");
        }
    }

    @Override
    public void write(List<ActivityReportCSV> rows, String fileName)  {
        File file = new File(fileName);
        try (
            FileWriter fileWriter = new FileWriter(file);
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.RFC4180.builder().setHeader(ActivityReportCSV.Header.class).build())
        ) {
            for (ActivityReportCSV row : rows) {
                row.getPrinter(csvPrinter);
            }
            csvPrinter.flush();
        } catch (IOException ex) {
            throw new DAOException("Error with creating csv file from object");
        }
    }
}
