package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnPublicAdministration;
import reactor.core.publisher.Mono;

public interface PublicAdministrationDAO {
    Mono<PnPublicAdministration> createOrUpdate(PnPublicAdministration pnPublicAdministration);
    Mono<PnPublicAdministration> getPaDetail(String paId, String referenceMonth);
}
