package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.rest.v1.dto.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EstimateService {

    Mono<EstimatePeriod> createOrUpdateEstimate(String status, String paId, String referenceMonth, EstimateCreateBody estimate);
    Mono<EstimatePeriod> validated(String paId, String referenceMonth);
    Mono<InfoDownloadDTO> downloadEstimateFile(String paId, String reportKey);
    Mono<PageableEstimateResponseDto> getAllEstimate(String originFe, String paId, String taxId, String ipaId, Integer page, Integer size);
    Mono<EstimateDetail> getEstimateDetail(String paId, String referenceMonth);
    Flux<InfoDownloadDTO> getAllEstimateFile(String paId, String referenceMonth);
    Mono<PageableDeanonymizedFilesResponseDto> getAllDeanonymizedFiles(String paId, String status, Integer page, Integer size);
    Mono<Void> getScheduleDeanonymizedFiles(String paId, String reportKey);
}
