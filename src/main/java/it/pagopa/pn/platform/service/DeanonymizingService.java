package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.model.ActivityReportCSV;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DeanonymizingService {

    Mono<List<ActivityReportCSV>> getCSV (String paId, String fileKey);
}
