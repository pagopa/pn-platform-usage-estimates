package it.pagopa.pn.platform.rest;

import it.pagopa.pn.platform.rest.v1.api.BillingApi;
import it.pagopa.pn.platform.rest.v1.dto.Billing;
import it.pagopa.pn.platform.rest.v1.dto.Profiling;
import it.pagopa.pn.platform.rest.v1.dto.ProfilingDetail;
import it.pagopa.pn.platform.service.ProfilationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class ProfilationApiController implements BillingApi {
    @Autowired
    private ProfilationService profilationService;
    @Override
    public Mono<ResponseEntity<ProfilingDetail>> createOrUpdateBilling(String paId, String referenceYear, String status, Mono<Billing> billing, final ServerWebExchange exchange){
        return billing
                .flatMap(request-> this.profilationService.createOrUpdateBilling(paId, referenceYear, status, request))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Profiling>> getProfilationDetail(String paId, final ServerWebExchange exchange){
        return this.profilationService.getProfilationDetail(paId).map(ResponseEntity::ok);
    }
}