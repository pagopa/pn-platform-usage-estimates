package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ActivityReportMetaDAO {

    Mono<PnActivityReport> createMetaData (PnActivityReport pnActivityReport);

}
