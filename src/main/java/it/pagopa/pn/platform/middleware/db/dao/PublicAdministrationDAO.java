package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnPublicAdministration;
import reactor.core.publisher.Mono;

public interface PublicAdministrationDAO {
    Mono<PnPublicAdministration> getPaDetail(String paId, String referenceMonth);
}
