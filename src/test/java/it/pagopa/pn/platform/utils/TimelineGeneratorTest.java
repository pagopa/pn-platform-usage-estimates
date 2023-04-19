package it.pagopa.pn.platform.utils;

import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.model.TimelineEstimate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

class TimelineGeneratorTest {
    private List<PnEstimate> dbList ;
    private List<PnEstimate> timelineList = new ArrayList<>();
    Instant onboardingDate = Instant.parse("2022-07-02T10:15:30Z"); //settare in base al test che si vuole fare

    @BeforeEach
    void setUp(){
        this.initialize();
    }

    @Test
    void estimatesGeneratorTest(){
        String paId = "12345";
        TimelineGenerator timelineGenerator = new TimelineGenerator(paId, dbList);
        TimelineEstimate timelineList = timelineGenerator.extractAllEstimates(onboardingDate, paId);
        System.out.println(timelineList);
        Assertions.assertNotNull(timelineList.getActual());
        Assertions.assertEquals(10, timelineList.getHistory().size());
    }


    private void initialize() {
        dbList = new ArrayList<>();
        PnEstimate estimate = new PnEstimate();
        PnEstimate estimate1 = new PnEstimate();
        PnEstimate estimate2 = new PnEstimate();

        //1 caso
        estimate.setPaId("12345");
        estimate.setStatus("VALIDATED");
        estimate.setDeadlineDate(Instant.parse("2023-02-15T10:15:30Z"));
        estimate.setReferenceMonth("MAR-2023");
        estimate.setTotalDigitalNotif(141);
        estimate.setTotal890Notif(200);
        estimate.setTotalAnalogNotif(242);
        estimate.setDescription("description");
        estimate.setMailAddress("mailAddress");
        estimate.setLastModifiedTimestamp(Instant.now());
        estimate.setSplitPayment(true);

        //2 caso
        estimate1.setPaId("23453");
        estimate1.setStatus("VALIDATED");
        estimate1.setDeadlineDate(Instant.parse("2023-01-15T10:15:30Z"));
        estimate1.setReferenceMonth("FEBB-2023");
        estimate1.setTotalDigitalNotif(114);
        estimate1.setTotal890Notif(530);
        estimate1.setTotalAnalogNotif(210);
        estimate1.setDescription("description");
        estimate1.setMailAddress("mailAddress");
        estimate1.setLastModifiedTimestamp(Instant.now());
        estimate1.setSplitPayment(true);

        //3 caso
        estimate2.setPaId("89025");
        estimate2.setStatus("VALIDATED");
        estimate2.setDeadlineDate(Instant.parse("2022-10-15T10:15:30Z"));
        estimate2.setReferenceMonth("NOV-2022");
        estimate2.setTotalDigitalNotif(139);
        estimate2.setTotal890Notif(520);
        estimate2.setTotalAnalogNotif(222);
        estimate2.setDescription("description");
        estimate2.setMailAddress("mailAddress");
        estimate2.setLastModifiedTimestamp(Instant.now());
        estimate2.setSplitPayment(true);

        dbList.add(estimate);
        dbList.add(estimate1);
        dbList.add(estimate2);

    }
  
}