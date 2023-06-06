package it.pagopa.pn.platform.rest;

import it.pagopa.pn.platform.rest.v1.api.ProfilationApi;
import it.pagopa.pn.platform.rest.v1.dto.Billing;
import it.pagopa.pn.platform.rest.v1.dto.ProfilingDetail;
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
    public Mono<ResponseEntity<Billing>> createOrUpdateProfilation(String paId, String referenceYear, ServerWebExchange exchange) {
        return this.profilationService.createOrUpdateProfilation(paId, referenceYear).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<ProfilingDetail>> getProfilationDetail(String paId, String referenceYear, ServerWebExchange exchange) {
        return this.profilationService.getProfilationDetail(paId, referenceYear).map(ResponseEntity::ok);
    }
}