package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.rest.v1.dto.EstimateDto;
import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import it.pagopa.pn.platform.service.EstimateService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EstimateServiceImpl implements EstimateService {

    @Override
    public Mono<EstimateDto> createOrUpdateEstimate(EstimateDto dto) {
        return null;
    }

    @Override
    public Mono<InfoDownloadDTO> downloadEstimateFile(String paId, String fileId) {
        return null;
    }

    @Override
    public Mono<PageableEstimateResponseDto> getAllEstimate(String paId, String taxId, String ipaId, Integer page, Integer size) {
        return null;
    }

    @Override
    public Mono<EstimateDto> getEstimateDetail(String paId, String referenceMonth) {
        return null;
    }

    @Override
    public Mono<Flux<InfoDownloadDTO>> getAllEstimateFile(String paId) {
        return null;
    }
}
