package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.datalake.v1.dto.MonthlyNotificationPreorderDto;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.mapper.EstimateMapper;
import it.pagopa.pn.platform.mapper.FileMapper;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.model.Month;
import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import it.pagopa.pn.platform.rest.v1.dto.*;
import it.pagopa.pn.platform.service.EstimateService;
import it.pagopa.pn.platform.utils.DateUtils;
import it.pagopa.pn.platform.utils.TimelineGenerator;
import it.pagopa.pn.platform.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.*;

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
    private ActivityReportMetaDAO activityReportMetaDAO;

    @Autowired
    private SafeStorageClient safeStorageClient;

    @Autowired
    private S3Bucket s3Bucket;

    @Autowired
    private ExternalRegistriesClient externalRegistriesClient;

    @Override
    public Mono<EstimatePeriod> createOrUpdateEstimate(String status, String paId, String referenceMonth, EstimateCreateBody estimate) {
        Instant refMonthInstant = getInstantFromMonth(referenceMonth);
        if (refMonthInstant == null) {
            return Mono.error(new PnGenericException(REFERENCE_MONTH_NOT_CORRECT, REFERENCE_MONTH_NOT_CORRECT.getMessage()));
        }
        if (todayIsNotInRange(refMonthInstant)) {
            log.info("ReferenceMonth that is just occurred is greater then startDeadlineDate {}", refMonthInstant);
            return Mono.error(new PnGenericException(ESTIMATE_EXPIRED, ESTIMATE_EXPIRED.getMessage()));
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
                                if (pnEstimate.getStatus().equals(EstimatePeriod.StatusEnum.ABSENT.getValue())) {
                                    log.error("PnEstimate inconsistent status. {}", pnEstimate.getStatus());
                                    return Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage()));
                                } else if (pnEstimate.getStatus().equalsIgnoreCase(EstimatePeriod.StatusEnum.VALIDATED.getValue())
                                                && status.equalsIgnoreCase(EstimatePeriod.StatusEnum.DRAFT.getValue())) {
                                    log.error("PnEstimate inconsistent status. {}", pnEstimate.getStatus());
                                    return Mono.error(new PnGenericException(OPERATION_NOT_ALLOWED, OPERATION_NOT_ALLOWED.getMessage()));
                                }
                                PnEstimate forSave = EstimateMapper.dtoToPnEstimate(pnEstimate, status, estimate);
                                saveFile(status, paId, referenceMonth, forSave);
                                return estimateDAO.createOrUpdate(forSave);
                            })
                            .map(EstimateMapper::estimatePeriodToDto);
                });
    }

    @Override
    public Mono<EstimatePeriod> validated(String paId, String referenceMonth) {
        Instant refMonthInstant = getInstantFromMonth(referenceMonth);
        if (refMonthInstant == null) {
            return Mono.error(new PnGenericException(REFERENCE_MONTH_NOT_CORRECT, REFERENCE_MONTH_NOT_CORRECT.getMessage()));
        }
        Instant today = Instant.now();
        if (todayIsNotInRange(refMonthInstant)) {
            log.info("ReferenceMonth that is just occurred is greater then startDeadlineDate {}", refMonthInstant);
            return Mono.error(new PnGenericException(ESTIMATE_EXPIRED, ESTIMATE_EXPIRED.getMessage()));
        }

        return this.estimateDAO.getEstimateDetail(paId, referenceMonth)
                .switchIfEmpty(Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage())))
                .flatMap(pnEstimate -> {
                    if (pnEstimate.getStatus().equalsIgnoreCase(EstimatePeriod.StatusEnum.DRAFT.getValue())
                            && pnEstimate.getDeadlineDate().isAfter(today)){
                        pnEstimate.setStatus(EstimatePeriod.StatusEnum.VALIDATED.getValue());
                        pnEstimate.setLastModifiedDate(today);
                        estimateDAO.createOrUpdate(pnEstimate);
                        saveFile( EstimatePeriod.StatusEnum.VALIDATED.getValue(), paId, referenceMonth, pnEstimate);
                    }
                    return Mono.just(pnEstimate);
                }).map(EstimateMapper::estimatePeriodToDto);
    }

    @Override
    public Mono<EstimateDetail> getEstimateDetail(String paId, String referenceMonth) {
        Instant refMonthInstant = getInstantFromMonth(referenceMonth);

        if (refMonthInstant == null) {
            return Mono.error(new PnGenericException(REFERENCE_MONTH_NOT_CORRECT, REFERENCE_MONTH_NOT_CORRECT.getMessage()));
        }

        Instant deadlineRefMonth = DateUtils.minusMonth(refMonthInstant,1);
        Instant maxDeadlineDate = DateUtils.getMaxDeadlineDate();


        if (maxDeadlineDate.isBefore(deadlineRefMonth))
            return Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage(), HttpStatus.NOT_FOUND));

        return this.externalRegistriesClient.getOnePa(paId)
                .zipWhen(paInfo -> {
                    Instant onBoardingDate = DateUtils.addOneMonth(DateUtils.toInstant(paInfo.getAgreementDate()));
                    if (refMonthInstant.isBefore(onBoardingDate)){
                        log.error("ReferenceMonth inconsistent with onBoardindate {}", onBoardingDate);
                        return Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage(), HttpStatus.NOT_FOUND));
                    }
                    log.debug("Retrieve estimate from db and create it if it's not present.");
                    return this.estimateDAO.getEstimateDetail(paId,referenceMonth)
                            .switchIfEmpty(Mono.just(TimelineGenerator.getEstimate(paId, referenceMonth, null)));
                })
                .map(paInfoAndEstimate -> EstimateMapper.estimateDetailToDto(paInfoAndEstimate.getT2(), paInfoAndEstimate.getT1()));

    }


    //PER HELP DESK
    @Override
    public Mono<PageableEstimateResponseDto> getAllEstimate(String originFe, String paId, String taxId, String ipaId, Integer page, Integer size) {
        if (!originFe.equals("PN-PLATFORM-NOTIFICATION-FE") || paId ==  null ) {
            throw new PnGenericException(BAD_REQUEST, BAD_REQUEST.getMessage(), HttpStatus.BAD_REQUEST);
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        return this.externalRegistriesClient.getOnePa(paId)
                .flatMap(paInfoDto ->
                        this.estimateDAO.getAllEstimates(paId)
                                .map(pnEstimates -> {
                                            log.debug("Build timeline.");
                                            TimelineGenerator timelineGenerator = new TimelineGenerator(paId, pnEstimates);
                                            return timelineGenerator.extractAllEstimates(DateUtils.toInstant(paInfoDto.getAgreementDate()));
                                        }
                                )
                )
                .map(list -> EstimateMapper.toPageableResponse(pageable, list));
    }


    //PER CONSUNTIVI
    @Override
    public Flux<InfoDownloadDTO> getAllEstimateFile(String paId, String referenceMonth) {
        return this.activityReportMetaDAO.findAllFromPaId(paId, referenceMonth)
                .filter(pnActivityReport ->  pnActivityReport.getStatus().equalsIgnoreCase("READY"))
                .map(pnActivityReport -> FileMapper.fromPnActivityReportToInfoDownloadDTO(paId, referenceMonth, pnActivityReport));
    }

    @Override
    public Mono<InfoDownloadDTO> downloadEstimateFile(String paId, String fileKey) {
        return this.activityReportMetaDAO.findByPaIdAndFileKey(paId, fileKey)
                .switchIfEmpty(Mono.error(new PnGenericException(REPORT_NOT_EXISTS, REPORT_NOT_EXISTS.getMessage())))
                .flatMap(pnActivityReport -> {
                    if(!pnActivityReport.getStatus().equalsIgnoreCase("READY")) {
                        return Mono.error(new PnGenericException(STATUS_NOT_READY, STATUS_NOT_READY.getMessage()));
                    }
                    return this.safeStorageClient.getFile(pnActivityReport.getFileZipKey())
                            .switchIfEmpty(Mono.error(new PnGenericException(FILE_KEY_NOT_EXISTED, FILE_KEY_NOT_EXISTED.getMessage())))
                            .map(file -> FileMapper.toDownloadFile(paId, file));
                });
    }

    private Instant getInstantFromMonth(String referenceMonth) throws PnGenericException {
        Instant result = null;
        String[] splitMonth = referenceMonth.split("-");
        if (!(splitMonth.length > 1)) {
            log.info("ReferenceMonth has not correct format");
            return null;
        }
        Integer numberOfMonth = Month.getNumberMonth(splitMonth[0]);
        int year = Integer.parseInt(splitMonth[1]);
        result = (numberOfMonth != null) && (splitMonth[1].length() == 4) ? DateUtils.fromDayMonthYear(15, numberOfMonth, year) : null;
        return result;
    }

    private boolean todayIsNotInRange(Instant refMonthInstant){
        Pair<Instant,Instant> range = DateUtils.getStartEndFromRefMonth(refMonthInstant);
        Instant today = Instant.now();
        return !(range.getFirst().isBefore(today) && range.getSecond().isAfter(today) );
    }

    private void saveFile(String status, String paId, String referenceMonth, PnEstimate pnEstimate) {
        if (status.equals(EstimateDetail.StatusEnum.VALIDATED.getValue())) {
            MonthlyNotificationPreorderDto dtoDatalake = EstimateMapper.dtoToFile(pnEstimate);
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
                try {
                    try (FileWriter snapshot = new FileWriter(fileSnapshot)) {
                        snapshot.write(json);
                        snapshot.flush();
                    }
                    try(FileWriter last = new FileWriter(fileLast)){
                        last.write(json);
                        last.flush();
                    }
                    s3Bucket.putObject(snapshotPath, fileSnapshot);
                    s3Bucket.putObject(lastPath, fileLast);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally{
                    if (!fileSnapshot.delete()){
                        log.info("fileSnapshot non eliminato: " + fileSnapshot);
                    }
                    if (!fileLast.delete()){
                        log.info("fileLast non eliminato: " + fileLast);
                    }
                }
            }
        }
    }
}
