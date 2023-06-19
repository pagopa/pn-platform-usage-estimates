package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.middleware.db.dao.ProfilationDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class ProfilationDAOImplTest extends BaseTest {

    @Autowired
    private ProfilationDAO  profilationDAO;

    private List<PnProfilation> profilationList = new ArrayList<>();

    private  PnProfilation profilation1 = new PnProfilation();
    private  PnProfilation profilation2 = new PnProfilation();

    @BeforeEach
    public void setUp(){
        initialValue();
    }

    @Test
    void getAllProfilations(){
        profilationList = this.profilationDAO.getAllProfilations("1234").block();
        assertNotNull(profilationList);
        System.out.println(profilationList);
        assertEquals(2, profilationList.size());
    }

    @Test
    void getProfilationDetail(){
        PnProfilation pnProfilation = this.profilationDAO.getProfilationDetail(profilation1.getPaId(),profilation1.getReferenceYear()).block();
        assertNotNull(pnProfilation);
        assertEquals(profilation1.getPaId(), pnProfilation.getPaId());
        assertEquals(profilation1.getStatus(), pnProfilation.getStatus());
        assertEquals(profilation1.getDeadlineDate(), pnProfilation.getDeadlineDate());
        assertEquals(profilation1.getLastModifiedDate(), pnProfilation.getLastModifiedDate());
        assertEquals(profilation1.getMailAddress(), pnProfilation.getMailAddress());
        assertEquals(profilation1.getDescription(), pnProfilation.getDescription());
        assertEquals(profilation1.getSplitPayment(), pnProfilation.getSplitPayment());
        assertEquals(profilation1.getReferenceYear(),pnProfilation.getReferenceYear());

    }

    private void initialValue() {

        profilation1.setPaId("1234");
        profilation1.setDeadlineDate(DateUtils.getStartDeadLineDate());
        profilation1.setStatus("VALIDATED");
        profilation1.setLastModifiedDate(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        profilation1.setDescription("description1");
        profilation1.setMailAddress("mailAddress1");
        profilation1.setSplitPayment(true);
        profilation1.setReferenceYear("2023");
        profilationList.add(profilation1);
        this.profilationDAO.createOrUpdate(profilation1).block();
        log.info("PROFILATION CREATED");

        profilation2.setPaId("1234");
        profilation2.setDeadlineDate(DateUtils.getStartDeadLineDate());
        profilation2.setStatus("DRAFT");
        profilation2.setLastModifiedDate(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        profilation2.setDescription("description2");
        profilation2.setMailAddress("mailAddress2");
        profilation2.setReferenceYear("2024");
        profilation2.setSplitPayment(true);
        profilationList.add(profilation2);
        this.profilationDAO.createOrUpdate(profilation2).block();
        log.info("PROFILATION CREATED");

    }
}
