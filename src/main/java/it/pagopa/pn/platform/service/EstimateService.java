package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.rest.v1.dto.*;
import reactor.core.publisher.Mono;

public interface EstimateService {

    Mono<EstimatePeriod> createOrUpdateEstimate(String status, String paId, String referenceMonth, EstimateCreateBody estimate);
    Mono<EstimatePeriod> validated(String paId, String referenceMonth);
    Mono<PageableEstimateResponseDto> getAllEstimate(String originFe, String paId, String taxId, String ipaId, Integer page, Integer size);
    Mono<EstimateDetail> getEstimateDetail(String paId, String referenceMonth);
}
