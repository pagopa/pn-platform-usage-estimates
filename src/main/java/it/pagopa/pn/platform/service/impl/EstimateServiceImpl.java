package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.datalake.v1.dto.MonthlyNotificationPreorderDto;
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
import it.pagopa.pn.platform.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.ESTIMATE_NOT_EXISTED;
import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT;

@Slf4j
@Service
public class EstimateServiceImpl implements EstimateService {

    private static final String PAID = "paid_";
    private static final String MONTH = "month_";
    private static final String SNAPSHOT = "snapshot";
    private static final String LAST = "last";
    private static final String MONTHLY = "monthlypreorder_";
    private static final String EXTENSION = ".json";
    private static final String SLASH = "/";

    @Autowired
    private EstimateDAO estimateDAO;

    @Autowired
    private S3Bucket s3Bucket;

    @Autowired
    private ExternalRegistriesClient externalRegistriesClient;

    @Override
    public Mono<EstimateDetail> createOrUpdateEstimate(String status, String paId, String referenceMonth, EstimateCreateBody estimate) {
        Instant refMonthInstant = getInstantFromMonth(referenceMonth);
        if (refMonthInstant == null) {
            return Mono.error(new PnGenericException(REFERENCE_MONTH_NOT_CORRECT, REFERENCE_MONTH_NOT_CORRECT.getMessage()));
        }
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
                                if (pnEstimate.getStatus().equals(EstimateDetail.StatusEnum.ABSENT.getValue())) {
                                    log.error("PnEstimate inconsistent status. {}", pnEstimate.getStatus());
                                    return Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage()));
                                }
                                if (status.equals(EstimateDetail.StatusEnum.VALIDATED.getValue())) {
                                    MonthlyNotificationPreorderDto dtoDatalake = EstimateMapper.dtoToFile(pnEstimate, estimate);
                                    String json = Utility.objectToJson(dtoDatalake);
                                    if (json != null) {
                                        String prefix = PAID.concat(paId).concat(SLASH).concat(MONTH).concat(referenceMonth).concat(SLASH);
                                        String snapshotPath = prefix.concat(SNAPSHOT).concat(SLASH);
                                        String snapshotFilename = MONTHLY.concat(DateUtils.buildTimestamp(pnEstimate)).concat("_")
                                                .concat(UUID.randomUUID().toString()).concat(EXTENSION);
                                        String lastPath = prefix.concat(LAST).concat(SLASH);
                                        String lastFilename = MONTHLY.concat(referenceMonth).concat(EXTENSION);
                                        File fileSnapshot = new File(snapshotFilename);
                                        File fileLast = new File(lastFilename);
                                        FileWriter snapshot = null;
                                        FileWriter last = null;
                                        try {
                                            snapshot = new FileWriter(fileSnapshot);
                                            last = new FileWriter(fileLast);
                                            snapshot.write(json);
                                            snapshot.flush();
                                            last.write(json);
                                            last.flush();
                                            s3Bucket.putObject(snapshotPath, fileSnapshot);
                                            s3Bucket.putObject(lastPath, fileLast);

                                        } catch (IOException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                        finally{
                                            try{
                                                if (snapshot != null) {
                                                    snapshot.close();
                                                    fileSnapshot.delete();
                                                }
                                                if (last != null){
                                                    last.close();
                                                    fileSnapshot.delete();
                                                }
                                            } catch (IOException e) {
                                                Mono.error(new RuntimeException(e));
                                            }
                                        }
                                    }
                                }
                                return estimateDAO.createOrUpdate(EstimateMapper.dtoToPnEstimate(pnEstimate, status, estimate));
                            })
                            .map(pnEstimate -> EstimateMapper.estimateDetailToDto(pnEstimate, paInfo));
                });
    }



    @Override
    public Mono<EstimateDetail> getEstimateDetail(String paId, String referenceMonth) {
        Instant startDeadlineDate = Instant.now();
        Instant refMonthInstant = getInstantFromMonth(referenceMonth);
        if (refMonthInstant == null) {
            return Mono.error(new PnGenericException(REFERENCE_MONTH_NOT_CORRECT, REFERENCE_MONTH_NOT_CORRECT.getMessage()));
        }
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

    private Instant getInstantFromMonth(String referenceMonth) throws PnGenericException {
        Instant result = null;
        String[] splitMonth = referenceMonth.split("-");
        if (!(splitMonth.length > 1)) {
            log.info("ReferenceMonth has not correct format");
        }
        Integer numberOfMonth = Month.getNumberMonth(splitMonth[0]);
        result = (numberOfMonth != null) ? DateUtils.fromDayMonthYear(15, numberOfMonth, Integer.parseInt(splitMonth[1])) : null;
        return result;
    }
}
