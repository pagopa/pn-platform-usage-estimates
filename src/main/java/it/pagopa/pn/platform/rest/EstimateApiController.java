package it.pagopa.pn.platform.rest;

import it.pagopa.pn.platform.rest.v1.api.EstimateApi;
import it.pagopa.pn.platform.rest.v1.dto.*;
import it.pagopa.pn.platform.service.EstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class EstimateApiController implements EstimateApi {

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
    public Mono<ResponseEntity<InfoDownloadDTO>> downloadEstimateFile(String paId, String reportKey, ServerWebExchange exchange) {
        return this.estimateService.downloadEstimateFile(paId, reportKey).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PageableEstimateResponseDto>> getAllEstimate(String originFe, String paId, String taxId, String ipaId, Integer page, Integer size, ServerWebExchange exchange) {
        return this.estimateService.getAllEstimate(originFe, paId, taxId, ipaId, page, size).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<InfoDownloadDTO>>> getAllEstimateFile(String paId, String referenceMonth, ServerWebExchange exchange) {
        return this.estimateService.getAllEstimateFile(paId, referenceMonth).collectList().map(list -> ResponseEntity.ok(Flux.fromStream(list.stream())));
    }

    @Override
    public Mono<ResponseEntity<EstimateDetail>> getEstimateDetail(String paId, String referenceMonth, ServerWebExchange exchange) {
        return this.estimateService.getEstimateDetail(paId, referenceMonth).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PageableDeanonymizedFilesResponseDto>> getAllDeanonymizedFiles(String paId, String status, Integer page, Integer size,  final ServerWebExchange exchange){
        return this.estimateService.getAllDeanonymizedFiles(paId, status, page, size).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> getScheduleDeanonymizedFiles(String paId, String reportKey,  final ServerWebExchange exchange){
        return this.estimateService.getScheduleDeanonymizedFiles(paId, reportKey)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}
