package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.mapper.EstimateMapper;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDto;
import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import it.pagopa.pn.platform.service.EstimateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.ESTIMATE_NOT_EXISTED;


@Slf4j
@Service
public class EstimateServiceImpl implements EstimateService {

    @Autowired
    private EstimateDAO estimateDAO;

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
        Pageable pageable = PageRequest.of(page-1, size);

        return estimateDAO.getAllEstimates(paId)
                .map(list -> EstimateMapper.toPagination(pageable, list))
                .map(EstimateMapper::toPageableResponse);
    }

    @Override
    public Mono<EstimateDto> getEstimateDetail(String paId, String referenceMonth) {
        //fare zip when tra getEstimateDetail e getPaDetail
        //fare mapper per PublicAdministration (?)
        return this.estimateDAO.getEstimate(paId)
                .switchIfEmpty(Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage())))
                .map(EstimateMapper::estimateDetailToDto);
    }

    @Override
    public Mono<Flux<InfoDownloadDTO>> getAllEstimateFile(String paId) {
        return null;
    }
}
