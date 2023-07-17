package it.pagopa.pn.platform.dao;

import it.pagopa.pn.platform.model.ActivityReportCSV;
import reactor.core.publisher.Flux;
import java.io.InputStreamReader;
import java.util.List;

public interface CsvDAO {


    Flux<ActivityReportCSV> toRows(InputStreamReader reader);


    void write(List<ActivityReportCSV> rows, String fileName);


}
