package it.pagopa.pn.platform.rest;


import it.pagopa.pn.platform.rest.v1.api.ProfilationApi;
import it.pagopa.pn.platform.rest.v1.dto.*;

import it.pagopa.pn.platform.service.ProfilationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class ProfilationApiController implements ProfilationApi {
    @Autowired
    private ProfilationService profilationService;


    @Override
    public Mono<ResponseEntity<ProfilationPeriod>> createOrUpdateProfilation(String status, String paId, String referenceYear, Mono<ProfilationCreateBody> profilationCreateBody,  final ServerWebExchange exchange) {
        return profilationCreateBody.flatMap(request -> this.profilationService.createOrUpdateProfilation(status, paId, referenceYear, request))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PageableProfilationResponseDto>> getAllProfilations(String paId, String taxId, String ipaId, Integer page, Integer size, final ServerWebExchange exchange){
        return this.profilationService.getAllProfilations(paId, taxId, ipaId, page, size).map(ResponseEntity::ok);
    }
    @Override
    public Mono<ResponseEntity<ProfilationDetail>> getProfilationDetail(String paId, String referenceYear, final ServerWebExchange exchange) {
        return this.profilationService.getProfilationDetail(paId, referenceYear).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<ProfilationPeriod>> validatedProfilation(String paId, String referenceYear, final ServerWebExchange exchange){
        return this.profilationService.validatedProfilation(paId, referenceYear).map(ResponseEntity::ok);
    }
}