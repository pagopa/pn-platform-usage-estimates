package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.mapper.EstimateMapper;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.rest.v1.dto.*;
import it.pagopa.pn.platform.service.EstimateService;
import it.pagopa.pn.platform.utils.DateUtils;
import it.pagopa.pn.platform.utils.TimelineGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.*;


@Slf4j
@Service
public class EstimateServiceImpl implements EstimateService {

    @Autowired
    private EstimateDAO estimateDAO;


    @Autowired
    private ExternalRegistriesClient externalRegistriesClient;


    @Override
    public Mono<Void> createOrUpdateEstimate(String status, String paId, String referenceMonth, EstimateCreateBody estimate) {
        return estimateDAO.createOrUpdate(EstimateMapper.dtoToPnEstimate(status, paId, referenceMonth, estimate))
                .flatMap(item-> Mono.empty());
    }

    @Override
    public Mono<EstimateDetail> getEstimateDetail(String paId, String referenceMonth) {
        //check referenceMonth NON deve essere precedente a data di onboarding
        //Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage()))
        //check per vedere se referenceMonth è compatibile
        //Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage()))
        return this.externalRegistriesClient.getOnePa(paId)
                .zipWhen(paInfo -> {
                    //Instant refMonth = DateUtils.addOneMonth(paInfo.getMonth());
                    //if (refMonth <)
                    return this.estimateDAO.getEstimateDetail(paId,referenceMonth)
                            .switchIfEmpty(Mono.just(TimelineGenerator.getEstimate(paId, referenceMonth, null)));
                })
                .map(paInfoAndEstimate -> EstimateMapper.estimateDetailToDto(paInfoAndEstimate.getT2(), paInfoAndEstimate.getT1()));

    }


    //PER HELP DESK
    @Override
    public Mono<PageableEstimateResponseDto> getAllEstimate(String paId, String taxId, String ipaId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);

        return estimateDAO.getAllEstimates(paId)
                .map(list -> EstimateMapper.toPagination(pageable, list))
                .map(EstimateMapper::toPageableResponse);
    }



    //PER CONSUNTIVI
    @Override
    public Mono<Flux<InfoDownloadDTO>> getAllEstimateFile(String paId, String referenceMonth) {
        return null;
    }

    @Override
    public Mono<InfoDownloadDTO> downloadEstimateFile(String paId, String fileId) {
        return null;
    }
}
