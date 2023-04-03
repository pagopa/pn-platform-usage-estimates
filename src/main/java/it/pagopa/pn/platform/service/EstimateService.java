package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.rest.v1.dto.EstimateDto;
import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EstimateService {

    Mono<EstimateDto> createOrUpdateEstimate(EstimateDto dto);
    Mono<InfoDownloadDTO> downloadEstimateFile(String paId, String fileId);
    Mono<PageableEstimateResponseDto> getAllEstimate(String paId, String taxId, String ipaId, Integer page, Integer size);
    Mono<EstimateDto> getEstimateDetail(String paId, String referenceMonth);
    Mono<Flux<InfoDownloadDTO>> getAllEstimateFile(String paId, String referenceMonth);
}
