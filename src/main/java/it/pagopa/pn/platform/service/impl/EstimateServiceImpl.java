package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.mapper.EstimateMapper;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.model.Month;
import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.rest.v1.dto.EstimateCreateBody;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDetail;
import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import it.pagopa.pn.platform.service.EstimateService;
import it.pagopa.pn.platform.utils.DateUtils;
import it.pagopa.pn.platform.utils.TimelineGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.ESTIMATE_NOT_EXISTED;
import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT;


@Slf4j
@Service
public class EstimateServiceImpl implements EstimateService {

    @Autowired
    private EstimateDAO estimateDAO;


    @Autowired
    private ExternalRegistriesClient externalRegistriesClient;


    @Override
    public Mono<EstimateDetail> createOrUpdateEstimate(String status, String paId, String referenceMonth, EstimateCreateBody estimate) {
        String[] splitMonth = referenceMonth.split("-");
        if (!(splitMonth.length > 1)) {
            log.info("ReferenceMonth has not correct format");
            return Mono.error(new PnGenericException(REFERENCE_MONTH_NOT_CORRECT, REFERENCE_MONTH_NOT_CORRECT.getMessage()));
        }
        int numberOfMonth = Month.getNumberMonth(splitMonth[0]);
        Instant refMonthInstant = DateUtils.fromDayMonthYear(15, numberOfMonth, Integer.parseInt(splitMonth[1]));
        Instant startDeadlineDate = Instant.now();
        if (startDeadlineDate.isAfter(refMonthInstant)) {
            log.info("ReferenceMonth that is just occurred is greater then startDeadlineDate {}", startDeadlineDate);
            return Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage()));
        }
        return this.externalRegistriesClient.getOnePa(paId)
                .flatMap(paInfo -> {
                    Instant onBoardingDate = DateUtils.addOneMonth(DateUtils.toInstant(paInfo.getAgreementDate()));
                    if (refMonthInstant.isBefore(onBoardingDate)) {
                        log.error("ReferenceMonth inconsistent with onBoardindate {}", onBoardingDate);
                        return Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage()));
                    }
                    return this.estimateDAO.getEstimateDetail(paId, referenceMonth)
                            .switchIfEmpty(Mono.just(TimelineGenerator.getEstimate(paId, referenceMonth, null)))
                            .flatMap(pnEstimate -> {
                                if (!pnEstimate.getStatus().equals(EstimateDetail.StatusEnum.DRAFT.getValue())) {
                                    log.error("PnEstimate inconsistent status. {}", pnEstimate.getStatus());
                                    return Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage()));
                                }
                                return estimateDAO.createOrUpdate(EstimateMapper.dtoToPnEstimate(pnEstimate, status, estimate));
                            })
                            .map(pnEstimate -> EstimateMapper.estimateDetailToDto(pnEstimate, paInfo));
                });
    }

    @Override
    public Mono<EstimateDetail> getEstimateDetail(String paId, String referenceMonth) {
        String[] splitMonth = referenceMonth.split("-");
        Instant startDeadlineDate = Instant.now();
        int numberOfMonth = Month.getNumberMonth(splitMonth[0]);
        Instant refMonthInstant = DateUtils.fromDayMonthYear(15, numberOfMonth, Integer.parseInt(splitMonth[1]));
        if (startDeadlineDate.isAfter(refMonthInstant)) {
            return Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage()));
        }
        return this.externalRegistriesClient.getOnePa(paId)
                .zipWhen(paInfo -> {

                    Instant onBoardingDate = DateUtils.addOneMonth(DateUtils.toInstant(paInfo.getAgreementDate()));
                    if (refMonthInstant.isBefore(onBoardingDate)){
                        log.error("ReferenceMonth inconsistent with onBoardindate {}", onBoardingDate);
                        return Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage()));
                    }
                    log.debug("Retrieve estimate from db and create it if it's not present.");
                    return this.estimateDAO.getEstimateDetail(paId,referenceMonth)
                            .switchIfEmpty(Mono.just(TimelineGenerator.getEstimate(paId, referenceMonth, null)));
                })
                .map(paInfoAndEstimate -> EstimateMapper.estimateDetailToDto(paInfoAndEstimate.getT2(), paInfoAndEstimate.getT1()));

    }


    //PER HELP DESK
    @Override
    public Mono<PageableEstimateResponseDto> getAllEstimate(String paId, String taxId, String ipaId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return this.externalRegistriesClient.getOnePa(paId)
                .flatMap(paInfoDto ->
                        this.estimateDAO.getAllEstimates(paId)
                                .map(pnEstimates -> {
                                            log.debug("Build timeline.");
                                            TimelineGenerator timelineGenerator = new TimelineGenerator(paId, pnEstimates);
                                            return timelineGenerator.extractAllEstimates(DateUtils.toInstant(paInfoDto.getAgreementDate()), paId);
                                        }
                                )
                )
                .map(list -> EstimateMapper.toPageableResponse(pageable, list));
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
