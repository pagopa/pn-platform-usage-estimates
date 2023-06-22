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
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class EstimateServiceTest extends BaseTest{

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
    }

    @Test
    @DisplayName("estimateInDB")
    void getEstimateDetailOk(){
        String paId = "12345";


        PnEstimate pnEstimate = getPnEstimate();
        PaInfoDto paInfoDto = getPaInfoDto();

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnEstimate));
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        EstimateDetail estimateDetail = this.estimateService.getEstimateDetail(paId, referenceMonth).block();

        assertNotNull(estimateDetail);

    }

    @Test
    @DisplayName("estimateEmptyInDB")
    void getEstimateDetailEmpty(){

        String paId = "12345";

        PaInfoDto paInfoDto = getPaInfoDto();

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        EstimateDetail estimateDetail = this.estimateService.getEstimateDetail(paId, referenceMonth).block();

        assertNotNull(estimateDetail);

    }

    @Test
    @DisplayName("MaxDeadlineDateBeforeDeadlineRefMonth")
    void getEstimateDetailErrorBeforeDate(){

        String paId = "12345";
        String referenceMonth = "GEN-2100";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.getEstimateDetail(paId, referenceMonth).block();
        });

        assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, exception.getExceptionType());

    }

    @Test
    @DisplayName("DeadlineDateBeforeOnboardingDate")
    void getEstimateDetailErrorBeforeDate2(){

        String paId = "12345";
        String referenceMonth = "APR-2021";

        PaInfoDto paInfoDto = getPaInfoDto();

        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.getEstimateDetail(paId, referenceMonth).block();
        });

        assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, exception.getExceptionType());

    }

    @Test
    @DisplayName("RefMonthInstantNull")
    void getEstimateDetailErrorRefMonthNull(){

        String paId = "12345";
        String referenceMonthNull = " ";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.getEstimateDetail(paId, referenceMonthNull).block();
        });

        assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, exception.getExceptionType());

    }

    @Test
    @DisplayName("getAllEstimateok")
    void getAllEstimate (){

        String paId = "12345";

        PaInfoDto paInfoDto = getPaInfoDto();
        List<PnEstimate> pnEstimates = new ArrayList<>();

        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));
        Mockito.when(this.estimateDAO.getAllEstimates(paId)).thenReturn(Mono.just(pnEstimates));

        PageableEstimateResponseDto pageableEstimateResponseDto = this.estimateService.getAllEstimate("PN-PLATFORM-NOTIFICATION-FE", paId,null, null, 1, 5).block();

        assertNotNull(pageableEstimateResponseDto);
        assertNotNull(pageableEstimateResponseDto.getActual());
        assertNotNull(pageableEstimateResponseDto.getHistory());

    }

    @Test
    @DisplayName("createOrUpdateReferenceMonthNotCorrectFormat")
    void createOrUpdateErrorReferenceMonth(){

        String paId = "12345";
        String referenceMonthNotCorrect = "APR-";
        String status = "VALIDATED";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.createOrUpdateEstimate(status, paId, referenceMonthNotCorrect, null).block();
        });

        assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, exception.getExceptionType());

    }

    @Test
    @DisplayName("createOrUpdateReferenceMonthYearNotCorrectFormat")
    void createOrUpdateErrorReferenceMonth2(){

        String paId = "12345";
        String referenceMonthNotCorrect = "APR-23";
        String status = "VALIDATED";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.createOrUpdateEstimate(status, paId, referenceMonthNotCorrect, null).block();
        });

        assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, exception.getExceptionType());

    }

    @Test
    @DisplayName("createOrUpdateReferenceMonthNotInRange")
    void createOrUpdateErrorAfterDate(){

        String paId = "12345";
        String referenceMonth = "GEN-2023";
        String status = "VALIDATED";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, null).block();
        });

        assertEquals(ExceptionTypeEnum.ESTIMATE_EXPIRED, exception.getExceptionType());

    }

    @Test
    @DisplayName("createOrUpdateDeadlineDateBeforeOnboardingDate")
    void createOrUpdateErrorBeforeDate(){

        String paId = "12345";
        String status = "VALIDATED";

        PaInfoDto paInfoDto = getPaInfoDtoErrorAgreementDate();

        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, null).block();
        });

        assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, exception.getExceptionType());

    }

    @Test
    @DisplayName("createOrUpdateestimateEmptyInDB")
    void createOrUpdateEstimateDetailEmpty(){

        String paId = "12345";
        String status = "VALIDATED";

        PaInfoDto paInfoDto = getPaInfoDto();
        PnEstimate pnEstimate = getPnEstimate();

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));
        Mockito.when(this.estimateDAO.createOrUpdate(Mockito.any())).thenReturn(Mono.just(pnEstimate));

        EstimatePeriod estimatePeriod = this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, estimateCreateBody).block();

        assertNotNull(estimatePeriod);

    }

    @Test
    @DisplayName("createOrUpdateGetEstimateDetailNotEmpty")
    void createOrUpdateOk(){

        String paId = "12345";
        String status = "VALIDATED";

        PaInfoDto paInfoDto = getPaInfoDto();
        PnEstimate pnEstimate = getPnEstimate();

        Mockito.when(s3Bucket.putObject(Mockito.anyString(), Mockito.any())).thenReturn(Mono.just(new File("tmp")));
        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnEstimate));
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));
        Mockito.when(this.estimateDAO.createOrUpdate(Mockito.any())).thenReturn(Mono.just(pnEstimate));

        EstimatePeriod estimatePeriod = this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, estimateCreateBody).block();

        assertNotNull(estimatePeriod);

    }

//    @Test
    @DisplayName("createOrUpdateGetEstimateDetailNotEmptyStatusAbsent")
    void createOrUpdateNotDraft(){

        String paId = "12345";
        String referenceMonth = "GEN-2021";
        String status = "VALIDATED";

        PaInfoDto paInfoDto = getPaInfoDto();
        PnEstimate pnEstimate = getEstimateDetail();

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnEstimate));
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));
        Mockito.when(this.estimateDAO.createOrUpdate(Mockito.any())).thenReturn(Mono.just(pnEstimate));

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, estimateCreateBody).block();
                });
        assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, exception.getExceptionType());

    }

    @Test
    @DisplayName("createOrUpdateGetEstimateDetailRequestStatusDraftDBValidate")
    void createOrUpdateRequestStatusDraftDbValidated(){

        String paId = "12345";
        String status = "DRAFT";

        PaInfoDto paInfoDto = getPaInfoDto();
        PnEstimate pnEstimate = getEstimateDetail();

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnEstimate));
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, estimateCreateBody).block();
        });
        assertEquals(ExceptionTypeEnum.OPERATION_NOT_ALLOWED, exception.getExceptionType());

    }

    @Test
    @DisplayName("validateReferenceMonthYearFormatNotCorrect")
    void validateRefMonthFormatNotCorrect(){
        String paId = "12345";
        String referenceMonthNotCorrect = "APR-23";
        String status = "VALIDATED";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.validated(paId, referenceMonthNotCorrect).block();
        });

        assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, exception.getExceptionType());
    }

    @Test
    @DisplayName("validateReferenceMonthFormatNotCorrect")
    void validateRefMonthFormatNotCorrect2(){
        String paId = "12345";
        String referenceMonthNotCorrect = "APR";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.validated(paId, referenceMonthNotCorrect).block();
        });

        assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, exception.getExceptionType());
    }

    @Test
    @DisplayName("validateReferenceMonthNotInRange")
    void validateRefMonthNonInRange(){
        String paId = "12345";
        String referenceMonth = "GEN-2023";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.validated(paId, referenceMonth).block();
        });

        assertEquals(ExceptionTypeEnum.ESTIMATE_EXPIRED, exception.getExceptionType());
    }

    @Test
    @DisplayName("validateRecordDBEmpty")
    void validateDBEmpty(){
        String paId = "";

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.validated(paId, referenceMonth).block();
        });

        assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, exception.getExceptionType());
    }

    @Test
    @DisplayName("validateIsOK")
    void validateOK(){
        String paId = "12345";
        PnEstimate pnEstimate = getPnEstimateValidate();

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnEstimate));
        Mockito.when(this.estimateDAO.createOrUpdate(Mockito.any())).thenReturn(Mono.just(pnEstimate));

        EstimatePeriod estimatePeriod = this.estimateService.validated(paId, referenceMonth).block();

        assertNotNull(estimatePeriod);

    }

    private PnEstimate getPnEstimate(){
        PnEstimate estimate = new PnEstimate();
        estimate.setStatus("DRAFT");
        estimate.setDescription("description");
        estimate.setReferenceMonth(referenceMonth);
        estimate.setPaId("12345");
        estimate.setSplitPayment(true);
        estimate.setDeadlineDate(DateUtils.fromDayMonthYear(15, DateUtils.getMonth(Instant.now()), DateUtils.getYear(Instant.now())));
        estimate.setTotal890Notif(100);
        estimate.setTotalAnalogNotif(100);
        estimate.setTotalDigitalNotif(100);
        estimate.setLastModifiedDate(Instant.parse("2023-04-02T10:15:30Z"));
        return estimate;
    }

    private PnEstimate getPnEstimateValidate(){
        PnEstimate estimate = new PnEstimate();
        estimate.setStatus("DRAFT");
        estimate.setDescription("description");
        estimate.setReferenceMonth(referenceMonth);
        estimate.setPaId("12345");
        estimate.setSplitPayment(true);
        estimate.setDeadlineDate(DateUtils.fromDayMonthYear(DateUtils.getDay(Instant.now()), DateUtils.getMonth(Instant.now()), DateUtils.getYear(Instant.now())));
        estimate.setTotal890Notif(100);
        estimate.setTotalAnalogNotif(100);
        estimate.setTotalDigitalNotif(100);
        estimate.setLastModifiedDate(Instant.parse("2023-04-02T10:15:30Z"));
        return estimate;
    }

    private PnEstimate getEstimateDetail(){
        PnEstimate estimate = new PnEstimate();
        estimate.setStatus("VALIDATED");
        estimate.setDescription("description");
        estimate.setReferenceMonth("APR-2023");
        estimate.setPaId("12345");
        estimate.setSplitPayment(true);
        estimate.setDeadlineDate(Instant.parse("2023-06-15T10:15:30Z"));
        estimate.setTotal890Notif(50);
        estimate.setTotalAnalogNotif(60);
        estimate.setTotalDigitalNotif(40);
        estimate.setLastModifiedDate(Instant.parse("2023-04-02T10:15:30Z"));
        return estimate;
    }

    private PaInfoDto getPaInfoDto(){
        PaInfoDto paInfoDto = new PaInfoDto();
        OffsetDateTime time = OffsetDateTime.ofInstant(Instant.parse("2022-12-12T11:51:43.777+00:00"), ZoneOffset.UTC);
        paInfoDto.setAgreementDate(time);
        paInfoDto.setId("b6c5b42a-8a07-436f-96ce-8c2ab7f4dbd2");
        paInfoDto.setSdiCode("s234");
        paInfoDto.setTaxId("03334231200");
        paInfoDto.setName("Comune di Valsamoggia");
        return paInfoDto;
    }

    private PaInfoDto getPaInfoDtoErrorAgreementDate(){
        PaInfoDto paInfoDto = new PaInfoDto();
        OffsetDateTime time = OffsetDateTime.ofInstant(Instant.parse("2100-12-12T11:51:43.777+00:00"), ZoneOffset.UTC);
        paInfoDto.setAgreementDate(time);
        paInfoDto.setId("b6c5b42a-8a07-436f-96ce-8c2ab7f4dbd2");
        paInfoDto.setSdiCode("s234");
        paInfoDto.setTaxId("03334231200");
        paInfoDto.setName("Comune di Valsamoggia");
        return paInfoDto;
    }

    private void initialize(){

        estimateCreateBody.setTotal890Notif(60);
        estimateCreateBody.setTotalAnalogNotif(50);
        estimateCreateBody.setTotalDigitalNotif(40);
        estimateCreateBody.setDescription("description");
        estimateCreateBody.mailAddress("mail.address@comune.it");
        estimateCreateBody.splitPayment(true);

        Instant refMonthInstant = DateUtils.addOneMonth(Instant.now());
        String month = "";
        month = Month.getValueFromNumber(DateUtils.getMonth(refMonthInstant));
        Pair<Instant, Instant> range = DateUtils.getStartEndFromRefMonth(refMonthInstant);
        Instant today = Instant.now();
        if (!(range.getFirst().isBefore(today) && range.getSecond().isAfter(today))){
            month = Month.getValueFromNumber(DateUtils.getMonth(DateUtils.plusMonth(refMonthInstant, 1)));
        }
        Integer year = DateUtils.getYear(refMonthInstant);
        referenceMonth = month + "-" + year;
    }
}
