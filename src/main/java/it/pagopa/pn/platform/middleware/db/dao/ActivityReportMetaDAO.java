package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ActivityReportMetaDAO {

    void createMetaData (PnActivityReport pnActivityReport);

    Mono<PnActivityReport> getCSVName (String paId,  String fileKey);

}
