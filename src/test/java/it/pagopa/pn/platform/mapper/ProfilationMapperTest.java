package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.model.TimelineProfilation;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.rest.v1.dto.*;
import it.pagopa.pn.platform.utils.TimelineGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

class ProfilationMapperTest {

    private List<PnProfilation> history = new ArrayList<>();
    private ProfilationCreateBody profilationCreateBody = new ProfilationCreateBody();
    private PnProfilation pnProfilation = new PnProfilation();
    private PaInfoDto paInfoDto = new PaInfoDto();
    private TimelineProfilation timelineProfilation = new TimelineProfilation(pnProfilation, history);

    @BeforeEach
    void setUp(){
        this.initialize();
    }

    @Test
    void dtoToPnProfilation(){
        PnProfilation pnProfilation = new PnProfilation();
        String status = "";
        PnProfilation profilation = ProfilationMapper.dtoToPnProfilation(pnProfilation, status,profilationCreateBody);

        Assertions.assertNotNull(profilation);
        Assertions.assertEquals(profilationCreateBody.getSplitPayment(),pnProfilation.getSplitPayment());
        Assertions.assertEquals(profilationCreateBody.getDescription(),pnProfilation.getDescription());
        Assertions.assertEquals(profilationCreateBody.getMailAddress(),pnProfilation.getMailAddress());

    }

    @Test
    void profilationDetailToDto(){

        ProfilationDetail profilationDetail = ProfilationMapper.profilationDetailToDto(pnProfilation, paInfoDto);

        Assertions.assertNotNull(profilationDetail);
        Assertions.assertEquals(paInfoDto.getId(), profilationDetail.getPaInfo().getPaId());
        Assertions.assertEquals(paInfoDto.getName(), profilationDetail.getPaInfo().getPaName());
        Assertions.assertEquals(paInfoDto.getTaxId(), profilationDetail.getPaInfo().getTaxId());
        Assertions.assertEquals(pnProfilation.getMailAddress(), profilationDetail.getBilling().getMailAddress());
        Assertions.assertEquals(pnProfilation.getDescription(), profilationDetail.getBilling().getDescription());
        Assertions.assertEquals(pnProfilation.getSplitPayment(), profilationDetail.getBilling().getSplitPayment());
        Assertions.assertEquals(pnProfilation.getStatus(), profilationDetail.getStatus().toString());
        Assertions.assertEquals(pnProfilation.getReferenceYear(), profilationDetail.getReferenceYear());
        Assertions.assertEquals(pnProfilation.getLastModifiedDate().getEpochSecond(), profilationDetail.getLastModifiedDate().toInstant().getEpochSecond());
        Assertions.assertEquals(pnProfilation.getDeadlineDate().getEpochSecond(), profilationDetail.getDeadlineDate().toInstant().getEpochSecond());
        Assertions.assertTrue(profilationDetail.getShowEdit());
    }

    @Test
    void profilationDetailToDtoKo(){

        pnProfilation.setLastModifiedDate(null);
        ProfilationDetail profilationDetail = ProfilationMapper.profilationDetailToDto(pnProfilation, paInfoDto);

        Assertions.assertNotNull(profilationDetail);
        Assertions.assertEquals(paInfoDto.getId(), profilationDetail.getPaInfo().getPaId());
        Assertions.assertEquals(paInfoDto.getName(), profilationDetail.getPaInfo().getPaName());
        Assertions.assertEquals(paInfoDto.getTaxId(), profilationDetail.getPaInfo().getTaxId());
        Assertions.assertEquals(pnProfilation.getMailAddress(), profilationDetail.getBilling().getMailAddress());
        Assertions.assertEquals(pnProfilation.getDescription(), profilationDetail.getBilling().getDescription());
        Assertions.assertEquals(pnProfilation.getSplitPayment(), profilationDetail.getBilling().getSplitPayment());
        Assertions.assertEquals(pnProfilation.getStatus(), profilationDetail.getStatus().toString());
        Assertions.assertEquals(pnProfilation.getReferenceYear(), profilationDetail.getReferenceYear());
        Assertions.assertNull(pnProfilation.getLastModifiedDate());
        Assertions.assertEquals(pnProfilation.getDeadlineDate().getEpochSecond(), profilationDetail.getDeadlineDate().toInstant().getEpochSecond());
        Assertions.assertTrue(profilationDetail.getShowEdit());
    }

    @Test
    void  profilationPeriodToDto(){

        ProfilationPeriod profilationPeriod = ProfilationMapper.profilationPeriodToDto(pnProfilation);

        Assertions.assertNotNull(profilationPeriod);
        Assertions.assertEquals(pnProfilation.getMailAddress(), profilationPeriod.getBilling().getMailAddress());
        Assertions.assertEquals(pnProfilation.getDescription(), profilationPeriod.getBilling().getDescription());
        Assertions.assertEquals(pnProfilation.getSplitPayment(), profilationPeriod.getBilling().getSplitPayment());
        Assertions.assertEquals(pnProfilation.getStatus(), profilationPeriod.getStatus().toString());
        Assertions.assertEquals(pnProfilation.getReferenceYear(), profilationPeriod.getReferenceYear());
        Assertions.assertEquals(pnProfilation.getLastModifiedDate().getEpochSecond(), profilationPeriod.getLastModifiedDate().toInstant().getEpochSecond());
        Assertions.assertEquals(pnProfilation.getDeadlineDate().getEpochSecond(), profilationPeriod.getDeadlineDate().toInstant().getEpochSecond());
        Assertions.assertTrue(profilationPeriod.getShowEdit());

    }

    @Test
    void toPageableResponse(){

        Pageable pageable = PageRequest.of(4, 20);
        PageableProfilationResponseDto pageableProfilationResponseDto = ProfilationMapper.toPageableResponse(pageable, timelineProfilation);

        Assertions.assertNotNull(pageableProfilationResponseDto);
        Assertions.assertNotNull(pageableProfilationResponseDto.getActual());
        Assertions.assertNotNull(pageableProfilationResponseDto.getHistory());
        Assertions.assertNotNull(pageableProfilationResponseDto.getHistory().getContent());
        Assertions.assertEquals(timelineProfilation.getActual().getStatus(), pageableProfilationResponseDto.getActual().getStatus().toString());
        Assertions.assertEquals("2023", pageableProfilationResponseDto.getActual().getReferenceYear());
        Assertions.assertEquals(timelineProfilation.getActual().getLastModifiedDate().getEpochSecond(), pageableProfilationResponseDto.getActual().getLastModifiedDate().toInstant().getEpochSecond());
        Assertions.assertEquals(timelineProfilation.getActual().getDeadlineDate().getEpochSecond(), pageableProfilationResponseDto.getActual().getDeadlineDate().toInstant().getEpochSecond());
        Assertions.assertTrue(pageableProfilationResponseDto.getActual().getShowEdit());
        Assertions.assertEquals(pageable, pageableProfilationResponseDto.getHistory().getPageable());

    }

    @Test
    void toPageableResponseKO(){

        timelineProfilation.getActual().setLastModifiedDate(null);
        Pageable pageable = PageRequest.of(4, 20);
        PageableProfilationResponseDto pageableProfilationResponseDto = ProfilationMapper.toPageableResponse(pageable, timelineProfilation);

        Assertions.assertNotNull(pageableProfilationResponseDto);
        Assertions.assertNull(pageableProfilationResponseDto.getActual().getLastModifiedDate());

    }

    @Test
    void profilationsToDTODraft(){

        pnProfilation.setStatus("DRAFT");
        ProfilationHistory dto = ProfilationMapper.profilationsToDto(pnProfilation);

        Assertions.assertNotNull(dto);
        Assertions. assertEquals(pnProfilation.getReferenceYear(), dto.getReferenceYear());
        Assertions.assertEquals(pnProfilation.getDeadlineDate().getEpochSecond(), dto.getDeadlineDate().toInstant().getEpochSecond());
        Assertions.assertEquals(pnProfilation.getLastModifiedDate().getEpochSecond(), dto.getLastModifiedDate().toInstant().getEpochSecond());
        Assertions.assertEquals(ProfilationHistory.StatusEnum.ABSENT, dto.getStatus());
        Assertions.assertEquals(pnProfilation.getDeadlineDate().isAfter(Instant.now()), dto.getShowEdit());
    }

    @Test
    void profilationsToDTO(){

        pnProfilation.setLastModifiedDate(null);
        ProfilationHistory dto = ProfilationMapper.profilationsToDto(pnProfilation);

        Assertions.assertNotNull(dto);
        Assertions. assertEquals(pnProfilation.getReferenceYear(), dto.getReferenceYear());
        Assertions.assertEquals(pnProfilation.getDeadlineDate().getEpochSecond(), dto.getDeadlineDate().toInstant().getEpochSecond());
        Assertions.assertNull(dto.getLastModifiedDate());
        Assertions.assertEquals(ProfilationHistory.StatusEnum.VALIDATED, dto.getStatus());
        Assertions.assertEquals(pnProfilation.getDeadlineDate().isAfter(Instant.now()), dto.getShowEdit());
    }



    private void initialize(){

        profilationCreateBody.setSplitPayment(true);
        profilationCreateBody.setDescription("description profilation");
        profilationCreateBody.setMailAddress("mail.profilation@mail.pn");

        pnProfilation.setPaId("12345");
        pnProfilation.setMailAddress("test@example.com");
        pnProfilation.setDescription("Test description");
        pnProfilation.setSplitPayment(true);
        pnProfilation.setStatus("VALIDATED");
        pnProfilation.setReferenceYear("2023");
        pnProfilation.setLastModifiedDate(Instant.now());
        pnProfilation.setDeadlineDate(Instant.now().plus(Duration.ofDays(7)));

        paInfoDto.setId("12345");
        paInfoDto.setName("Test PA");
        paInfoDto.setTaxId("CCALVROW87DBUNA98");
        history.add(pnProfilation);

    }
}
