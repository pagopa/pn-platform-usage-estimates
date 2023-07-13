package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ActivityReportMetaDAO {

    Mono<PnActivityReport> createMetaData (PnActivityReport pnActivityReport);

    Flux<PnActivityReport> findAllFromPaId(String paId, String referenceMonth, String status);
    Flux<PnActivityReport> findAllFromPaId(String paId);

    Mono<PnActivityReport> findByPaIdAndReportKey(String paId, String reportKey);

    Flux<PnActivityReport> findAllFromPaIdAndStatus(String paId, String status);

}
