package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.rest.v1.dto.Billing;
import it.pagopa.pn.platform.rest.v1.dto.ProfilingDetail;
import it.pagopa.pn.platform.service.ProfilationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProfilationServiceImpl implements ProfilationService {

    @Autowired
    private ExternalRegistriesClient externalRegistriesClient;

    @Override
    public Mono<Billing> createOrUpdateProfilation(String paId, String referenceYear) {
        return null;
    }

    @Override
    public Mono<ProfilingDetail> getProfilationDetail(String paId, String referenceYear) {
        return null;
    }

}
