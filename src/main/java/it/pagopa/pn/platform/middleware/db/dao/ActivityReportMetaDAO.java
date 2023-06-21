package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ActivityReportMetaDAO {

    Mono<PnActivityReport> createMetaData (PnActivityReport pnActivityReport);


    /**
     * @param paId NOT NULL
     * @param referenceMonth CAN BE NULL
     * @return List of estimate files from paId and referenceMonth if I have it
     */
    Flux<PnActivityReport> findAllFromPaId(String paId, String referenceMonth);

    Mono<PnActivityReport> findByPaIdAndFileKey(String paId, String fileKey);

    //Mono<List<PnActivityReport>>getAllEstimateFile(String paId, String referenceMonth);

}
