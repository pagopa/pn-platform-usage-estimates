package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class EstimateDAOImplTest extends BaseTest {
    @Autowired
    private EstimateDAO estimateDAO;
    private final PnEstimate estimate1 = new PnEstimate();
    private final PnEstimate estimate2 = new PnEstimate();

    @BeforeEach
    public void setUp(){
        initialValue();
    }

    @Test
    void getEstimateDetailTest(){
        PnEstimate pnEstimate = this.estimateDAO.getEstimateDetail(estimate1.getPaId(), estimate1.getReferenceMonth()).block();
        assertNotNull(pnEstimate);
        assertEquals(estimate1.getPaId(), pnEstimate.getPaId());
        assertEquals(estimate1.getStatus(), pnEstimate.getStatus());
        assertEquals(estimate1.getDeadlineDate(), pnEstimate.getDeadlineDate());
        assertEquals(estimate1.getReferenceMonth(), pnEstimate.getReferenceMonth());
        assertEquals(estimate1.getLastModifiedDate(), pnEstimate.getLastModifiedDate());
        assertEquals(estimate1.getMailAddress(), pnEstimate.getMailAddress());
        assertEquals(estimate1.getDescription(), pnEstimate.getDescription());
        assertEquals(estimate1.getDescription(), pnEstimate.getDescription());
        assertEquals(estimate1.getSplitPayment(), pnEstimate.getSplitPayment());
        assertEquals(estimate1.getTotal890Notif(), pnEstimate.getTotal890Notif());
        assertEquals(estimate1.getTotalDigitalNotif(), pnEstimate.getTotalDigitalNotif());
        assertEquals(estimate1.getTotalAnalogNotif(), pnEstimate.getTotalAnalogNotif());
    }

    @Test
    void getAllEstimates() {
        List<PnEstimate> estimateList = this.estimateDAO.getAllEstimates("12345").block();
        assertNotNull(estimateList);
        System.out.println(estimateList);
        assertEquals(3, estimateList.size());
    }


    private void initialValue() {
        estimate1.setPaId("1234");
        estimate1.setDeadlineDate(DateUtils.getStartDeadLineDate());
        estimate1.setReferenceMonth("MAR-2023");
        estimate1.setStatus("VALIDATED");
        estimate1.setLastModifiedDate(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        estimate1.setTotalAnalogNotif(1234);
        estimate1.setTotal890Notif(344);
        estimate1.setTotalDigitalNotif(5644);
        estimate1.setDescription("description");
        estimate1.setMailAddress("mailAddress");
        estimate1.setSplitPayment(true);
        estimate1.setRecordVersion(1);
        this.estimateDAO.createOrUpdate(estimate1).block();
        log.info("ESTIMATE CREATED");

        estimate2.setPaId("1234");
        estimate2.setDeadlineDate(DateUtils.getStartDeadLineDate());
        estimate2.setReferenceMonth("APR-2023");
        estimate2.setStatus("DRAFT");
        estimate2.setLastModifiedDate(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        estimate2.setTotalAnalogNotif(454);
        estimate2.setTotal890Notif(744);
        estimate2.setTotalDigitalNotif(564);
        estimate2.setDescription("description");
        estimate2.setMailAddress("mailAddress");
        estimate2.setSplitPayment(true);
        estimate2.setRecordVersion(1);
        this.estimateDAO.createOrUpdate(estimate2).block();
        log.info("ESTIMATE CREATED");


    }

}