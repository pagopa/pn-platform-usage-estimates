package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ActivityReportMetaDAO {

    Mono<PnActivityReport> createMetaData (PnActivityReport pnActivityReport);


    /**
     * @param paId NOT NULL
     * @param referenceMonth CAN BE NULL
     * @return List of estimate files from paId and referenceMonth if I have it
     */
    Flux<PnActivityReport> findAllFromPaId(String paId, String referenceMonth);
    Flux<PnActivityReport> findAllFromPaId(String paId);

    Mono<PnActivityReport> findByPaIdAndFileKey(String paId, String fileKey);

    Flux<PnActivityReport> findAllFromPaIdAndStatus(String paId, String status);

}
