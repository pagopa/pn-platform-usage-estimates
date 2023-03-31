package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnBilling;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import reactor.core.publisher.Mono;

public interface BillingDAO {
    Mono<PnBilling> createOrUpdate(PnBilling data);
    Mono<PnBilling> getProfilationDetail(String paId);
    Mono<PnBilling> getProfilationAndBillingDetail(String paId, String referenceYear);
}
