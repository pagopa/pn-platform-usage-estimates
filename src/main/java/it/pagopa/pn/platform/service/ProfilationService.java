package it.pagopa.pn.platform.service;


import it.pagopa.pn.platform.rest.v1.dto.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ProfilationService {

    Mono<ProfilationPeriod> createOrUpdateProfilation(String status, String paId, String referenceYear, ProfilationCreateBody profilationCreateBody);

    Mono<ProfilationDetail> getProfilationDetail(String paId, String referenceYear);
    Mono<PageableProfilationResponseDto> getAllProfilations(String paId, String taxId, String ipaId, Integer page, Integer size);

    Mono<ProfilationPeriod> validatedProfilation(String paId, String referenceYear);

}
