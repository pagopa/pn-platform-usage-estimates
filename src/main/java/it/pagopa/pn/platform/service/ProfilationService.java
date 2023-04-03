package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.rest.v1.dto.BillingDTO;
import it.pagopa.pn.platform.rest.v1.dto.ProfilationDTO;
import it.pagopa.pn.platform.rest.v1.dto.ProfiliationAndBillingDTO;
import reactor.core.publisher.Mono;

public interface ProfilationService {

    Mono<BillingDTO> createOrUpdateBilling(String paId, String referenceYear, BillingDTO data);

    Mono<ProfilationDTO> getProfilationDetail(String paId);

    Mono<ProfiliationAndBillingDTO> getProfilationAndBillingDetail(String paId, String referenceYear);

}
