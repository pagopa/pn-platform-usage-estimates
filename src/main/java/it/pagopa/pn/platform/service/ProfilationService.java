package it.pagopa.pn.platform.service;


import it.pagopa.pn.platform.rest.v1.dto.Billing;
import it.pagopa.pn.platform.rest.v1.dto.ProfilingDetail;
import reactor.core.publisher.Mono;

public interface ProfilationService {

    Mono<Billing> createOrUpdateProfilation(String paId, String referenceYear);

    Mono<ProfilingDetail> getProfilationDetail(String paId, String referenceYear);

}
