package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.exception.ExceptionTypeEnum;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.model.Month;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.msclient.impl.ExternalRegistriesClientImpl;
import it.pagopa.pn.platform.rest.v1.dto.EstimateCreateBody;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDetail;
import it.pagopa.pn.platform.rest.v1.dto.EstimatePeriod;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import it.pagopa.pn.platform.service.impl.EstimateServiceImpl;
import it.pagopa.pn.platform.utils.DateUtils;
import it.pagopa.pn.platform.utils.TimelineGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class EstimateServiceTest extends BaseTest {
    private static final String PA_ID_FUTURE = "PA_ID_FUTURE";
    private static final String PA_ID = "PA_ID";

    @Autowired
    private EstimateServiceImpl estimateService;
    @MockBean
    private EstimateDAO estimateDAO;
    @MockBean
    private ExternalRegistriesClientImpl externalRegistriesClient;
    @MockBean
    private TimelineGenerator timelineGenerator;
    @MockBean
    private S3Bucket s3Bucket;
    private final EstimateCreateBody estimateCreateBody = new EstimateCreateBody();

    private String referenceMonth = "";


    @BeforeEach
    public void setUp(){
        initialize();
        Instant instantOld = Instant.parse("2022-04-02T10:15:30Z");
        Mockito.when(this.externalRegistriesClient.getOnePa(PA_ID))
                .thenReturn(Mono.just(getPaInfoDTO(instantOld)));

        Instant instantFuture = Instant.parse("2100-04-02T10:15:30Z");
        Mockito.when(this.externalRegistriesClient.getOnePa(PA_ID_FUTURE))
                .thenReturn(Mono.just(getPaInfoDTO(instantFuture)));

    }

    @Test
    @DisplayName("estimateInDB")
    void getEstimateDetailOk(){
        PnEstimate pnEstimate = getPnEstimate(PA_ID);

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.just(pnEstimate));

        EstimateDetail estimateDetail = this.estimateService.getEstimateDetail(PA_ID, referenceMonth).block();

        assertNotNull(estimateDetail);

    }

    @Test
    @DisplayName("estimateEmptyInDB")
    void getEstimateDetailEmpty(){
        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.empty());

        EstimateDetail estimateDetail = this.estimateService.getEstimateDetail(PA_ID, referenceMonth)
                .block();

        assertNotNull(estimateDetail);
    }

    @Test
    @DisplayName("MaxDeadlineDateBeforeDeadlineRefMonth")
    void getEstimateDetailErrorBeforeDate(){
        String referenceMonth = "GEN-2100";
        StepVerifier.create(estimateService.getEstimateDetail(PA_ID, referenceMonth))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    @DisplayName("DeadlineDateBeforeOnboardingDate")
    void getEstimateDetailErrorBeforeDate2(){

        String referenceMonth = "APR-2021";

        StepVerifier.create(estimateService.getEstimateDetail(PA_ID, referenceMonth))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    @DisplayName("RefMonthInstantNull")
    void getEstimateDetailErrorRefMonthNull(){
        String referenceMonthNull = " ";

        StepVerifier.create(estimateService.getEstimateDetail(PA_ID, referenceMonthNull))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    @DisplayName("getAllEstimateOK")
    void getAllEstimate (){

        List<PnEstimate> pnEstimates = new ArrayList<>();

        Mockito.when(this.estimateDAO.getAllEstimates(PA_ID))
                .thenReturn(Mono.just(pnEstimates));

        PageableEstimateResponseDto pageableEstimateResponseDto =
                this.estimateService.getAllEstimate("PN-PLATFORM-NOTIFICATION-FE", PA_ID,null, null, 1, 5)
                        .block();

        assertNotNull(pageableEstimateResponseDto);
        assertNotNull(pageableEstimateResponseDto.getActual());
        assertNotNull(pageableEstimateResponseDto.getHistory());

    }

    @Test
    @DisplayName("createOrUpdateReferenceMonthNotCorrectFormat")
    void createOrUpdateErrorReferenceMonth(){

        String referenceMonthNotCorrect = "APR-";
        String status = "VALIDATED";

        StepVerifier.create(estimateService.createOrUpdateEstimate(status, PA_ID, referenceMonthNotCorrect, null))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();

    }

    @Test
    @DisplayName("createOrUpdateReferenceMonthYearNotCorrectFormat")
    void createOrUpdateErrorReferenceMonth2(){
        String referenceMonthNotCorrect = "APR-23";
        String status = "VALIDATED";

        StepVerifier.create(estimateService.createOrUpdateEstimate(status, PA_ID, referenceMonthNotCorrect, null))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    @DisplayName("createOrUpdateReferenceMonthNotInRange")
    void createOrUpdateErrorAfterDate(){

        String referenceMonth = "GEN-2023";
        String status = "VALIDATED";

        StepVerifier.create(estimateService.createOrUpdateEstimate(status, PA_ID, referenceMonth, null))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.ESTIMATE_EXPIRED, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();

    }

    @Test
    @DisplayName("createOrUpdateDeadlineDateBeforeOnboardingDate")
    void createOrUpdateErrorBeforeDate(){
        String status = "VALIDATED";

        StepVerifier.create(estimateService.createOrUpdateEstimate(status, PA_ID_FUTURE, referenceMonth, null))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.ON_BOARDING_DATE_INCOMPATIBLE, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();

    }

    @Test
    @DisplayName("createOrUpdateestimateEmptyInDB")
    void createOrUpdateEstimateDetailEmpty(){

        String status = "VALIDATED";

        PnEstimate pnEstimate = getPnEstimate(PA_ID);

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(this.estimateDAO.createOrUpdate(Mockito.any())).thenReturn(Mono.just(pnEstimate));

        EstimatePeriod estimatePeriod = this.estimateService.createOrUpdateEstimate(status, PA_ID, referenceMonth, estimateCreateBody).block();

        assertNotNull(estimatePeriod);

    }

    @Test
    @DisplayName("createOrUpdateGetEstimateDetailNotEmpty")
    void createOrUpdateOk(){
        String status = "VALIDATED";

        PnEstimate pnEstimate = getPnEstimate(PA_ID);

        Mockito.doNothing().when(s3Bucket).putObject(Mockito.anyString(), Mockito.any(),  Mockito.any());
        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnEstimate));
        Mockito.when(this.estimateDAO.createOrUpdate(Mockito.any())).thenReturn(Mono.just(pnEstimate));

        EstimatePeriod estimatePeriod =
                this.estimateService.createOrUpdateEstimate(status, PA_ID, referenceMonth, estimateCreateBody)
                        .block();

        assertNotNull(estimatePeriod);

    }

//    @Test
    @DisplayName("createOrUpdateGetEstimateDetailNotEmptyStatusAbsent")
    void createOrUpdateNotDraft(){

        String referenceMonth = "GEN-2021";
        String status = "VALIDATED";

        PnEstimate pnEstimate = getEstimateDetail(PA_ID);

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnEstimate));
        Mockito.when(this.estimateDAO.createOrUpdate(Mockito.any())).thenReturn(Mono.just(pnEstimate));

        StepVerifier.create(estimateService.createOrUpdateEstimate(status, PA_ID, referenceMonth, null))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();

    }

    @Test
    @DisplayName("createOrUpdateGetEstimateDetailRequestStatusDraftDBValidate")
    void createOrUpdateRequestStatusDraftDbValidated(){
        String status = "DRAFT";

        PnEstimate pnEstimate = getEstimateDetail(PA_ID);

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnEstimate));


        StepVerifier.create(estimateService.createOrUpdateEstimate(status, PA_ID, referenceMonth, null))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.OPERATION_NOT_ALLOWED, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();

    }

    @Test
    @DisplayName("validateReferenceMonthYearFormatNotCorrect")
    void validateRefMonthFormatNotCorrect(){
        StepVerifier.create(estimateService.validated(PA_ID, "PO_233"))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    @DisplayName("validateReferenceMonthFormatNotCorrect")
    void validateRefMonthFormatNotCorrect2(){
        String referenceMonthNotCorrect = "APR";

        StepVerifier.create(estimateService.validated(PA_ID, referenceMonthNotCorrect))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();

    }

    @Test
    @DisplayName("validateReferenceMonthNotInRange")
    void validateRefMonthNonInRange(){
        String referenceMonth = "GEN-2023";

        StepVerifier.create(estimateService.validated(PA_ID, referenceMonth))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.ESTIMATE_EXPIRED, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    @DisplayName("validateRecordDBEmpty")
    void validateDBEmpty(){
        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());

        StepVerifier.create(estimateService.validated(PA_ID, referenceMonth))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    @DisplayName("validateIsOK")
    void validateOK(){
        PnEstimate pnEstimate = getPnEstimateValidate(PA_ID);

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnEstimate));
        Mockito.when(this.estimateDAO.createOrUpdate(Mockito.any())).thenReturn(Mono.just(pnEstimate));

        EstimatePeriod estimatePeriod = this.estimateService.validated(PA_ID, referenceMonth).block();

        assertNotNull(estimatePeriod);

    }

    private PnEstimate getPnEstimate(String paId){
        PnEstimate estimate = new PnEstimate();
        estimate.setStatus("DRAFT");
        estimate.setDescription("description");
        estimate.setReferenceMonth(referenceMonth);
        estimate.setPaId(paId);
        estimate.setSplitPayment(true);
        estimate.setDeadlineDate(DateUtils.fromDayMonthYear(15, DateUtils.getMonth(Instant.now()), DateUtils.getYear(Instant.now())));
        estimate.setTotal890Notif(100);
        estimate.setTotalAnalogNotif(100);
        estimate.setTotalDigitalNotif(100);
        estimate.setLastModifiedDate(Instant.parse("2023-04-02T10:15:30Z"));
        return estimate;
    }

    private PnEstimate getPnEstimateValidate(String paId){
        PnEstimate estimate = new PnEstimate();
        estimate.setStatus("DRAFT");
        estimate.setDescription("description");
        estimate.setReferenceMonth(referenceMonth);
        estimate.setPaId(paId);
        estimate.setSplitPayment(true);
        estimate.setDeadlineDate(DateUtils.fromDayMonthYear(DateUtils.getDay(Instant.now()), DateUtils.getMonth(Instant.now()), DateUtils.getYear(Instant.now())));
        estimate.setTotal890Notif(100);
        estimate.setTotalAnalogNotif(100);
        estimate.setTotalDigitalNotif(100);
        estimate.setLastModifiedDate(Instant.parse("2023-04-02T10:15:30Z"));
        return estimate;
    }

    private PnEstimate getEstimateDetail(String paId){
        PnEstimate estimate = new PnEstimate();
        estimate.setStatus("VALIDATED");
        estimate.setDescription("description");
        estimate.setReferenceMonth("APR-2023");
        estimate.setPaId(paId);
        estimate.setSplitPayment(true);
        estimate.setDeadlineDate(Instant.parse("2023-06-15T10:15:30Z"));
        estimate.setTotal890Notif(50);
        estimate.setTotalAnalogNotif(60);
        estimate.setTotalDigitalNotif(40);
        estimate.setLastModifiedDate(Instant.parse("2023-04-02T10:15:30Z"));
        return estimate;
    }

    private PaInfoDto getPaInfoDTO(Instant onBoardingDate){
        PaInfoDto paInfoDto = new PaInfoDto();
        OffsetDateTime time = OffsetDateTime.ofInstant(onBoardingDate, ZoneOffset.UTC);
        paInfoDto.setAgreementDate(time);
        paInfoDto.setId("b6c5b42a-8a07-436f-96ce-8c2ab7f4dbd2");
        paInfoDto.setSdiCode("s234");
        paInfoDto.setTaxId("03334231200");
        paInfoDto.setName("Comune di Valsamoggia");
        return paInfoDto;
    }

    private void initialize() {
        getEstimateCreateBody();
        createCurrentRefMonth();
    }

    private void getEstimateCreateBody(){
        estimateCreateBody.setTotal890Notif(60);
        estimateCreateBody.setTotalAnalogNotif(50);
        estimateCreateBody.setTotalDigitalNotif(40);
        estimateCreateBody.setDescription("description");
        estimateCreateBody.mailAddress("mail.address@comune.it");
        estimateCreateBody.splitPayment(true);
    }

    private void createCurrentRefMonth(){
        Instant refMonthInstant = DateUtils.addOneMonth(Instant.now());
        String month = Month.getValueFromNumber(DateUtils.getMonth(refMonthInstant));

        Pair<Instant, Instant> range = DateUtils.getStartEndFromRefMonth(refMonthInstant);
        Instant today = Instant.now();
        if (!(range.getFirst().isBefore(today) && range.getSecond().isAfter(today))){
            month = Month.getValueFromNumber(DateUtils.getMonth(DateUtils.plusMonth(refMonthInstant, 1)));
        }
        Integer year = DateUtils.getYear(refMonthInstant);
        referenceMonth = month + "-" + year;
    }
}
