package it.pagopa.pn.platform.service;


import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.exception.ExceptionTypeEnum;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.msclient.impl.ExternalRegistriesClientImpl;
import it.pagopa.pn.platform.rest.v1.dto.EstimateCreateBody;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDetail;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import it.pagopa.pn.platform.service.impl.EstimateServiceImpl;
import it.pagopa.pn.platform.utils.TimelineGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EstimateServiceTest extends BaseTest{

    @Autowired
    private EstimateServiceImpl estimateService;
    @MockBean
    private EstimateDAO estimateDAO;
    @MockBean
    private ExternalRegistriesClientImpl externalRegistriesClient;
    @MockBean
    private TimelineGenerator timelineGenerator;

    private final EstimateCreateBody estimateCreateBody = new EstimateCreateBody();

    @BeforeEach
    public void setUp(){
        initialize();
    }

    @Test
    @DisplayName("estimateInDB")
    void getEstimateDetailOk(){

        String paId = "12345";
        String referenceMonth = "GIU-2100";

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
        String referenceMonth = "GIU-2100";

        PaInfoDto paInfoDto = getPaInfoDto();

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        EstimateDetail estimateDetail = this.estimateService.getEstimateDetail(paId, referenceMonth).block();

        assertNotNull(estimateDetail);

    }

    @Test
    @DisplayName("DeadlineDateAfterStartDeadlineDate")
    void getEstimateDetailErrorAfterDate(){

        String paId = "12345";
        String referenceMonth = "GEN-2023";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.getEstimateDetail(paId, referenceMonth).block();
        });

        assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, exception.getExceptionType());

    }

    @Test
    @DisplayName("DeadlineDateBeforeOnboardingDate")
    void getEstimateDetailErrorBeforeDate(){

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
    @DisplayName("getAllEstimateok")
    void getAllEstimate (){

        String paId = "12345";

        PaInfoDto paInfoDto = getPaInfoDto();
        List<PnEstimate> pnEstimates = new ArrayList<>();

        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));
        Mockito.when(this.estimateDAO.getAllEstimates(paId)).thenReturn(Mono.just(pnEstimates));

        PageableEstimateResponseDto pageableEstimateResponseDto = this.estimateService.getAllEstimate(paId,null, null, 1, 5).block();

        assertNotNull(pageableEstimateResponseDto);
        assertNotNull(pageableEstimateResponseDto.getActual());
        assertNotNull(pageableEstimateResponseDto.getHistory());

    }

    @Test
    @DisplayName("createOrUpdateReferenceMonthNotCorrectFormat")
    void createOrUpdateErrorReferenceMonth(){

        String paId = "12345";
        String referenceMonth = "APR";
        String status = "VALIDATED";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, null).block();
        });

        assertEquals(ExceptionTypeEnum.REFERENCE_MONTH_NOT_CORRECT, exception.getExceptionType());

    }

    @Test
    @DisplayName("createOrUpdateStartDeadlineDateAfterReferenceMonth")
    void createOrUpdateErrorAfterDate(){

        String paId = "12345";
        String referenceMonth = "GEN-2023";
        String status = "VALIDATED";

        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, null).block();
        });

        assertEquals(ExceptionTypeEnum.ESTIMATE_NOT_EXISTED, exception.getExceptionType());

    }

    @Test
    @DisplayName("createOrUpdateDeadlineDateBeforeOnboardingDate")
    void createOrUpdateErrorBeforeDate(){

        String paId = "12345";
        String referenceMonth = "APR-2021";
        String status = "VALIDATED";

        PaInfoDto paInfoDto = getPaInfoDto();

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
        String referenceMonth = "GIU-2100";
        String status = "VALIDATED";

        PaInfoDto paInfoDto = getPaInfoDto();
        PnEstimate pnEstimate = getPnEstimate();

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));
        Mockito.when(this.estimateDAO.createOrUpdate(Mockito.any())).thenReturn(Mono.just(pnEstimate));

        EstimateDetail estimateDetail = this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, estimateCreateBody).block();

        assertNotNull(estimateDetail);

    }

    @Test
    @DisplayName("createOrUpdateGetEstimateDetailNotEmpty")
    void createOrUpdateOk(){

        String paId = "12345";
        String referenceMonth = "GIU-2100";
        String status = "VALIDATED";

        PaInfoDto paInfoDto = getPaInfoDto();
        PnEstimate pnEstimate = getPnEstimate();

        Mockito.when(this.estimateDAO.getEstimateDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnEstimate));
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));
        Mockito.when(this.estimateDAO.createOrUpdate(Mockito.any())).thenReturn(Mono.just(pnEstimate));

        EstimateDetail estimateDetail = this.estimateService.createOrUpdateEstimate(status, paId, referenceMonth, estimateCreateBody).block();

        assertNotNull(estimateDetail);

    }

    @Test
    @DisplayName("createOrUpdateGetEstimateDetailNotEmptyStatusNotDraft")
    void createOrUpdateNotDraft(){

        String paId = "12345";
        String referenceMonth = "GIU-2100";
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

    private PnEstimate getPnEstimate(){
        PnEstimate estimate = new PnEstimate();
        estimate.setStatus("DRAFT");
        estimate.setDescription("description");
        estimate.setReferenceMonth("APR-2023");
        estimate.setPaId("12345");
        estimate.setSplitPayment(true);
        estimate.setDeadlineDate(Instant.parse("2023-06-15T10:15:30Z"));
        estimate.setTotal890Notif(50);
        estimate.setTotalAnalogNotif(60);
        estimate.setTotalDigitalNotif(40);
        estimate.setLastModifiedTimestamp(Instant.parse("2023-04-02T10:15:30Z"));
        return estimate;
    }

    private PnEstimate getEstimateDetail(){
        PnEstimate estimate = new PnEstimate();
        estimate.setStatus("VALIDATE");
        estimate.setDescription("description");
        estimate.setReferenceMonth("APR-2023");
        estimate.setPaId("12345");
        estimate.setSplitPayment(true);
        estimate.setDeadlineDate(Instant.parse("2023-06-15T10:15:30Z"));
        estimate.setTotal890Notif(50);
        estimate.setTotalAnalogNotif(60);
        estimate.setTotalDigitalNotif(40);
        estimate.setLastModifiedTimestamp(Instant.parse("2023-04-02T10:15:30Z"));
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

    private void initialize(){

        estimateCreateBody.setTotal890Notif(60);
        estimateCreateBody.setTotalAnalogNotif(50);
        estimateCreateBody.setTotalDigitalNotif(40);
        estimateCreateBody.setDescription("description");
        estimateCreateBody.mailAddress("mail.address@comune.it");
        estimateCreateBody.splitPayment(true);
    }
}
