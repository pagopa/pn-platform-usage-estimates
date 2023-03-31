package it.pagopa.pn.platform.rest;

import it.pagopa.pn.platform.rest.v1.api.BillingApi;
import it.pagopa.pn.platform.rest.v1.dto.BillingDTO;
import it.pagopa.pn.platform.rest.v1.dto.ProfilationDTO;
import it.pagopa.pn.platform.rest.v1.dto.ProfiliationAndBillingDTO;
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
    public Mono<ResponseEntity<BillingDTO>> createOrUpdateBilling(String paId, Mono<BillingDTO> billingDTO, final ServerWebExchange exchange){
        return billingDTO
                .flatMap(request-> this.profilationService.createOrUpdateBilling(request))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<ProfilationDTO>> getProfilationDetail(String paId,  final ServerWebExchange exchange){
        return this.profilationService.getProfilationDetail(paId).map(ResponseEntity::ok);
    }
    @Override
    public Mono<ResponseEntity<ProfiliationAndBillingDTO>> getProfilationAndBillingDetail(String paId, final ServerWebExchange exchange){
        return this.profilationService.getProfilationAndBillingDetail(paId).map(ResponseEntity::ok);
    }
}