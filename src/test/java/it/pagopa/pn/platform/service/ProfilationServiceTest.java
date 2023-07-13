package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.middleware.db.dao.ProfilationDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.model.Month;
import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.msclient.impl.ExternalRegistriesClientImpl;
import it.pagopa.pn.platform.rest.v1.dto.*;
import it.pagopa.pn.platform.service.impl.ProfilationServiceImpl;
import it.pagopa.pn.platform.utils.DateUtils;
import it.pagopa.pn.platform.utils.TimelineGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opensaml.saml.saml1.core.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.REFERENCE_YEAR_NOT_CORRECT;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class ProfilationServiceTest extends BaseTest {

    @MockBean
    private ExternalRegistriesClientImpl externalRegistriesClient;

    @MockBean
    private ProfilationDAO profilationDAO;

    @Autowired
    private ProfilationServiceImpl profilationService;

    @MockBean
    private TimelineGenerator timelineGenerator;

    @MockBean
    private S3Bucket s3Bucket;

    private final ProfilationCreateBody profilationCreateBody = new ProfilationCreateBody();

    private String  referenceYear = "2023";

    private List<PnProfilation> profilations = new ArrayList<>();

    @BeforeEach
    public void setUp(){
        initialize();
    }

    @Test
    @DisplayName("profilationInDB")
    void getProfilationDetailOk(){
        String paId = "12345";


        PnProfilation pnProfilation = getPnProfilation();
        PaInfoDto paInfoDto = getPaInfoDto();

        Mockito.when(this.profilationDAO.getProfilationDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnProfilation));
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        ProfilationDetail profilationDetail = this.profilationService.getProfilationDetail(paId, referenceYear).block();

        assertNotNull(profilationDetail);

    }

    @Test
    @DisplayName("getProfilationDetailRefYearBeforeOnBoardingDate")
    void getProfilationDetailRefYearBeforeOnBoardingDate(){
        String paId = "12345";


        PnProfilation pnProfilation = getPnProfilation();
        PaInfoDto paInfoDto = getPaInfoDto();
        OffsetDateTime agreementDate = OffsetDateTime.now();
        paInfoDto.setAgreementDate(agreementDate.plus(3,ChronoUnit.YEARS));

        Mockito.when(this.profilationDAO.getProfilationDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnProfilation));
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        this.profilationService.getProfilationDetail(paId, referenceYear)
                .subscribe(
                        value -> Assertions.fail("Expected an error"),
                        error -> {
                            Assertions.assertTrue(error instanceof PnGenericException);
                        }
                );

    }

    @Test
    @DisplayName("getProfilationDetailRefYearNull")
    void getProfilationDetailRefYearNull(){
        String paId = "12345";


        PnProfilation pnProfilation = getPnProfilation();
        PaInfoDto paInfoDto = getPaInfoDto();

        Mockito.when(this.profilationDAO.getProfilationDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnProfilation));
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        Mono<ProfilationDetail> result = profilationService.getProfilationDetail(paId, null);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnGenericException)
                .verify();

    }

    @Test
    @DisplayName("createOrUpdateProfilationYearNull")
    void createOrUpdateProfilation(){

        profilationService.createOrUpdateProfilation(getPnProfilation().getStatus(), getPnProfilation().getPaId(), null,profilationCreateBody)
                .subscribe(
                value -> Assertions.fail("Expected an error"),
                error -> {
                    Assertions.assertTrue(error instanceof PnGenericException);
                }
        );

    }

    @Test
    @DisplayName("createOrUpdateProfilationYearMinorRefToday")
    void createOrUpdateProfilationYearMinorRefToday(){

        profilationService.createOrUpdateProfilation(getPnProfilation().getStatus(), getPnProfilation().getPaId(), "2010",profilationCreateBody)
                .subscribe(
                        value -> Assertions.fail("Expected an error"),
                        error -> {
                            Assertions.assertTrue(error instanceof PnGenericException);
                        }
                );

    }

    @Test
    @DisplayName("createOrUpdateProfilationYearMaggioreUgualeRefTodayPlusTwo")
    void createOrUpdateProfilationYearMaggioreUgualeRefTodayPlusTwo(){

        Instant date =Instant.now();
        Integer plusTwo = DateUtils.getYear(date) + 4;
        profilationService.createOrUpdateProfilation(getPnProfilation().getStatus(), getPnProfilation().getPaId(), plusTwo.toString(),profilationCreateBody)
                .subscribe(
                        value -> Assertions.fail("Expected an error"),
                        error -> {
                            Assertions.assertTrue(error instanceof PnGenericException);
                        }
                );

    }

    @Test
    @DisplayName("createOrUpdateProfilation")
    void createOrUpdateProfilationOk(){
        String paId = "12345";


        PnProfilation pnProfilation = getPnProfilation();
        PaInfoDto paInfoDto = getPaInfoDto();

        Mockito.when(this.profilationDAO.getProfilationDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnProfilation));
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        Mono<ProfilationPeriod> profilation = profilationService.createOrUpdateProfilation(getPnProfilation().getStatus(), getPnProfilation().getPaId(), DateUtils.getYear(Instant.now()).toString(),profilationCreateBody);

        Assertions.assertNotNull(profilation);


    }

    @Test
    @DisplayName("getAllProfilations")
    void getAllProfilations(){
        String paId = "12345";

        PnProfilation pnProfilation = getPnProfilation();
        PaInfoDto paInfoDto = getPaInfoDto();

        Mockito.when(this.profilationDAO.getAllProfilations(Mockito.anyString())).thenReturn(Mono.just(profilations));
        Mockito.when(this.externalRegistriesClient.getOnePa(paId)).thenReturn(Mono.just(paInfoDto));

        Mono<PageableProfilationResponseDto> pageableProfilationResponseDto = this.profilationService.getAllProfilations(paId, null,null,1, 5);

        Assertions.assertNotNull(pageableProfilationResponseDto);
    }

    @Test
    @DisplayName("validatedProfilationYearNull")
    void validatedProfilationYearNull(){

        PnProfilation pnProfilation = getPnProfilation();

        Mockito.when(this.profilationDAO.getProfilationDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnProfilation));

        profilationService.validatedProfilation( getPnProfilation().getPaId(), null)
        .subscribe(
                value -> Assertions.fail("Expected an error"),
                error -> {
                    Assertions.assertTrue(error instanceof PnGenericException);
                }
        );

    }

    @Test
    @DisplayName("validatedProfilationYearMaggioreNow")
    void validatedProfilationYearMaggioreNow(){

        Instant date =Instant.now();
        Integer year = DateUtils.getYear(date)+5;

        PnProfilation pnProfilation = getPnProfilation();

        Mockito.when(this.profilationDAO.getProfilationDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnProfilation));

        profilationService.validatedProfilation( getPnProfilation().getPaId(), year.toString())
                .subscribe(
                        value -> Assertions.fail("Expected an error"),
                        error -> {
                            Assertions.assertTrue(error instanceof PnGenericException);
                        }
                );

    }



    @Test
    @DisplayName("validatedProfilationYearMinorNow")
    void validatedProfilationYearMinorNow(){

        PnProfilation pnProfilation = getPnProfilation();

        Mockito.when(this.profilationDAO.getProfilationDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnProfilation));

        profilationService.validatedProfilation( getPnProfilation().getPaId(), "2010")
                .subscribe(
                        value -> Assertions.fail("Expected an error"),
                        error -> {
                            Assertions.assertTrue(error instanceof PnGenericException);
                        }
                );

    }

    @Test
    @DisplayName("validatedProfilation")
    void validatedProfilation(){

        Instant date =Instant.parse("2100-04-02T10:15:30Z");
        Instant date1 =Instant.now();
        Integer year = DateUtils.getYear(date1);

        PnProfilation pnProfilation = getPnProfilation();
        pnProfilation.setDeadlineDate(DateUtils.fromDayMonthYear(31,10, DateUtils.getYear(date)));

        Mockito.when(this.profilationDAO.getProfilationDetail(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnProfilation));

        Mono<ProfilationPeriod> profilationPeriodMono = this.profilationService.validatedProfilation( getPnProfilation().getPaId(), year.toString());

        Assertions.assertNotNull(profilationPeriodMono);

    }


    private PnProfilation getPnProfilation(){
        PnProfilation profilation = new PnProfilation();
        profilation.setStatus("DRAFT");
        profilation.setDescription("description");
        profilation.setReferenceYear(referenceYear);
        profilation.setPaId("12345");
        profilation.setSplitPayment(true);
        profilation.setDeadlineDate(DateUtils.fromDayMonthYear(31,10, DateUtils.getYear(Instant.now())));
        profilation.setLastModifiedDate(Instant.parse("2023-04-02T10:15:30Z"));
        profilations.add(profilation);
        return profilation;
    }

    private PnProfilation getPnProfilationValidate(){
        PnProfilation profilation = new PnProfilation();
        profilation.setStatus("DRAFT");
        profilation.setDescription("description");
        profilation.setReferenceYear(referenceYear);
        profilation.setPaId("12345");
        profilation.setSplitPayment(true);
        profilation.setDeadlineDate(DateUtils.fromDayMonthYear(DateUtils.getDay(Instant.now()), DateUtils.getMonth(Instant.now()), DateUtils.getYear(Instant.now())));
        profilation.setLastModifiedDate(Instant.parse("2023-04-02T10:15:30Z"));
        return profilation;
    }

    private PnProfilation getProfilationDetail(){
        PnProfilation profilation = new PnProfilation();
        profilation.setStatus("VALIDATED");
        profilation.setDescription("description");
        profilation.setReferenceYear("APR-2023");
        profilation.setPaId("12345");
        profilation.setSplitPayment(true);
        profilation.setDeadlineDate(Instant.parse("2023-06-15T10:15:30Z"));
        profilation.setLastModifiedDate(Instant.parse("2023-04-02T10:15:30Z"));
        return profilation;
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

        profilationCreateBody.setDescription("description");
        profilationCreateBody.mailAddress("mail.address@comune.it");
        profilationCreateBody.splitPayment(true);

        Instant refYearInstant = DateUtils.addOneYear(Instant.now());
        String year = String.valueOf(DateUtils.getYear(refYearInstant));
        Instant deadLineDate = DateUtils.getMaxDeadlineYearDate ();
        Instant today = Instant.now();
        if (!(today.isBefore(deadLineDate) )){
            year = String.valueOf(DateUtils.getYear(DateUtils.plusYear(refYearInstant, 1)));
        }
        referenceYear = year;
    }
}
