package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import reactor.core.publisher.Mono;

public interface ActivityReportMetaDAO {

    Mono<PnActivityReport> createMetaData (PnActivityReport pnActivityReport);

}
