package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.rest.v1.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EstimateService {

    Mono<EstimateDetail> createOrUpdateEstimate(String status, String paId, String referenceMonth, EstimateCreateBody estimate);
    Mono<InfoDownloadDTO> downloadEstimateFile(String paId, String fileId);
    Mono<PageableEstimateResponseDto> getAllEstimate(String paId, String taxId, String ipaId, Integer page, Integer size);
    Mono<EstimateDetail> getEstimateDetail(String paId, String referenceMonth);
    Mono<Flux<InfoDownloadDTO>> getAllEstimateFile(String paId, String referenceMonth);
}
