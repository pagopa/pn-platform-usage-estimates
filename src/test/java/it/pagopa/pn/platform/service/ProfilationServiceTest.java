package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.middleware.db.dao.ProfilationDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.model.Month;
import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.msclient.impl.ExternalRegistriesClientImpl;
import it.pagopa.pn.platform.rest.v1.dto.EstimateCreateBody;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDetail;
import it.pagopa.pn.platform.rest.v1.dto.ProfilationCreateBody;
import it.pagopa.pn.platform.rest.v1.dto.ProfilationDetail;
import it.pagopa.pn.platform.service.impl.ProfilationServiceImpl;
import it.pagopa.pn.platform.utils.DateUtils;
import it.pagopa.pn.platform.utils.TimelineGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    private String  referenceYear = null;

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

    private PnProfilation getPnProfilation(){
        PnProfilation profilation = new PnProfilation();
        profilation.setStatus("DRAFT");
        profilation.setDescription("description");
        profilation.setReferenceYear(referenceYear);
        profilation.setPaId("12345");
        profilation.setSplitPayment(true);
        profilation.setDeadlineDate(DateUtils.fromDayMonthYear(31,10, DateUtils.getYear(Instant.now())));
        profilation.setLastModifiedDate(Instant.parse("2023-04-02T10:15:30Z"));
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
