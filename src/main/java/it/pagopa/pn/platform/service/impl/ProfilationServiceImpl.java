package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.rest.v1.dto.BillingDTO;
import it.pagopa.pn.platform.rest.v1.dto.ProfilationDTO;
import it.pagopa.pn.platform.rest.v1.dto.ProfiliationAndBillingDTO;
import it.pagopa.pn.platform.service.ProfilationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProfilationServiceImpl implements ProfilationService {

    @Override
    public Mono<BillingDTO> createOrUpdateBilling(BillingDTO data) {
        return null;
    }

    @Override
    public Mono<ProfilationDTO> getProfilationDetail(String paId) {
        return null;
    }

    @Override
    public Mono<ProfiliationAndBillingDTO> getProfilationAndBillingDetail(String paId) {
        return null;
    }
}
