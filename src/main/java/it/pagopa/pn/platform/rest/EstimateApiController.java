package it.pagopa.pn.platform.rest;

import it.pagopa.pn.platform.rest.v1.api.EstimateApi;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDto;
import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import it.pagopa.pn.platform.service.EstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EstimateApiController implements EstimateApi {

    @Autowired
    private EstimateService estimateService;

    @Override
    public Mono<ResponseEntity<EstimateDto>> createOrUpdateEstimate(Mono<EstimateDto> estimateDto, ServerWebExchange exchange) {
        return estimateDto
                .flatMap(request-> this.estimateService.createOrUpdateEstimate(request))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<InfoDownloadDTO>> downloadEstimateFile(String paId, String fileId, ServerWebExchange exchange) {
        return this.estimateService.downloadEstimateFile(paId, fileId).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PageableEstimateResponseDto>> getAllEstimate(String paId, String taxId, String ipaId, Integer page, Integer size, ServerWebExchange exchange) {
        return this.estimateService.getAllEstimate(paId, taxId, ipaId, page, size).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<InfoDownloadDTO>>> getAllEstimateFile(String paId, ServerWebExchange exchange) {
        return this.estimateService.getAllEstimateFile(paId).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<EstimateDto>> getEstimateDetail(String paId, String referenceMonth, ServerWebExchange exchange) {
        return this.estimateService.getEstimateDetail(paId, referenceMonth).map(ResponseEntity::ok);
    }
}
