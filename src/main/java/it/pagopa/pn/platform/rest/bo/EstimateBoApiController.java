package it.pagopa.pn.platform.rest.bo;

import it.pagopa.pn.platform.rest.v1.api.EstimateBoApi;
import it.pagopa.pn.platform.rest.v1.dto.EstimateCreateBody;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDetail;
import it.pagopa.pn.platform.rest.v1.dto.EstimatePeriod;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import it.pagopa.pn.platform.service.EstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class EstimateBoApiController implements EstimateBoApi {

    @Autowired
    private EstimateService estimateService;


    @Override
    public Mono<ResponseEntity<EstimatePeriod>> createOrUpdateEstimate(String status, String paId, String referenceMonth, Mono<EstimateCreateBody> estimate, ServerWebExchange exchange) {
        return estimate.flatMap(request -> this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, request))
                .map(ResponseEntity::ok);
    }
    @Override
    public Mono<ResponseEntity<EstimatePeriod>> validated(String paId, String referenceMonth, ServerWebExchange exchange) {
        return this.estimateService.validated(paId, referenceMonth).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PageableEstimateResponseDto>> getAllEstimate(String originFe, String paId, String taxId, String ipaId, Integer page, Integer size, ServerWebExchange exchange) {
        return this.estimateService.getAllEstimate(originFe, paId, taxId, ipaId, page, size).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<EstimateDetail>> getEstimateDetail(String paId, String referenceMonth, ServerWebExchange exchange) {
        return this.estimateService.getEstimateDetail(paId, referenceMonth).map(ResponseEntity::ok);
    }

}
