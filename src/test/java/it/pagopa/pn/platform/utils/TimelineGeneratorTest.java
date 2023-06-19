package it.pagopa.pn.platform.utils;

import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.model.TimelineEstimate;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDetail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@AutoConfigureMockMvc
class TimelineGeneratorTest {
    private List<PnEstimate> dbList ;
    private List<PnEstimate> emptyDbList ;
    private List<PnEstimate> timelineList = new ArrayList<>();
    Instant onboardingDate = Instant.parse("2023-01-02T10:15:30Z"); //settare in base al test che si vuole fare
    Instant currentDateMock = Instant.parse("2023-05-19T10:15:30Z");
    private MockedStatic<DateUtils> dateUtilsMockedStatic;

    @BeforeEach
    void setUp(){
        this.initialize();
    }


    @AfterEach
    void afterEach(){
        if (dateUtilsMockedStatic != null){
            dateUtilsMockedStatic.close();
        }
    }

    //caso con dbList popolata
    @Test
    void estimatesGeneratorTest() {
        String paId = "12345";
        this.dateUtilsMockedStatic = Mockito.mockStatic(DateUtils.class);
        dateUtilsMockedStatic.when(DateUtils::getStartDeadLineDate)
                .thenReturn(currentDateMock);

        Mockito.when(DateUtils.minusMonth(any(), anyInt()))
                .thenCallRealMethod();

        TimelineGenerator timelineGenerator = new TimelineGenerator(paId, dbList);

        TimelineEstimate timelineList = timelineGenerator.extractAllEstimates(onboardingDate);
        System.out.println(timelineList);
        Assertions.assertNotNull(timelineList.getActual());
        Assertions.assertEquals(4, timelineList.getHistory().size());
    }


    //ALL MISSING CASE
    @Test
    void emptyDbListCaseTest(){
        String paId = "12345";
        this.dateUtilsMockedStatic = Mockito.mockStatic(DateUtils.class);
        dateUtilsMockedStatic.when(DateUtils::getStartDeadLineDate)
                .thenReturn(currentDateMock);

        Mockito.when(DateUtils.minusMonth(any(), anyInt()))
                .thenCallRealMethod();
        TimelineGenerator timelineGenerator = new TimelineGenerator(paId, emptyDbList);
        TimelineEstimate timelineList = timelineGenerator.extractAllEstimates(onboardingDate);
        System.out.println(timelineList);
        Assertions.assertTrue(emptyDbList.isEmpty());
        Assertions.assertNotNull(timelineList.getActual());
        Assertions.assertEquals(4,timelineList.getHistory().size());
    }



    @Test
    void generatePnEstimateErrorTest(){
        String paId = "12345";
        AssertionError exception = Assertions.assertThrows(AssertionError.class, () -> {
            PnEstimate pnEstimate = TimelineGenerator.getEstimate(paId, null, null);
            Assertions.assertNull(pnEstimate);
        });
        String actualMessage = exception.getMessage();
        Assertions.assertNull(actualMessage);
    }

    //deadline date before instant now -> stima assente
    @Test
    void generatePnEstimateWithoutRefMonthFirstTest(){
        String paId = "12345";
        Instant deadlineDate = Instant.parse("2022-10-15T10:15:30Z");
        PnEstimate pnEstimate = TimelineGenerator.getEstimate(paId, null, deadlineDate);
        Assertions.assertNotNull(pnEstimate);
        Assertions.assertEquals(pnEstimate.getStatus(), EstimateDetail.StatusEnum.ABSENT.getValue());

    }

    //deadline date after instant now -> stima assente
    @Test
    void generatePnEstimateWithoutRefMonthSecondTest(){
        String paId = "12345";
        Instant deadlineDate = Instant.parse("2023-04-25T10:15:30Z");
        PnEstimate pnEstimate = TimelineGenerator.getEstimate(paId, null, deadlineDate);
        Assertions.assertNotNull(pnEstimate);
        Assertions.assertEquals(pnEstimate.getStatus(), EstimateDetail.StatusEnum.ABSENT.getValue());

    }

    @Test
    void generatePnEstimateWithoutDeadlineDateFirstTest(){
        String paId = "12345";
        String referenceMonth = "MAR-2023";
        PnEstimate pnEstimate = TimelineGenerator.getEstimate(paId, referenceMonth, null);
        Assertions.assertNotNull(pnEstimate);
        Assertions.assertEquals(pnEstimate.getStatus(), EstimateDetail.StatusEnum.ABSENT.getValue());

    }



    private void initialize(){

        dbList = new ArrayList<>();
        emptyDbList = new ArrayList<>();
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
        estimate.setLastModifiedDate(Instant.now());
        estimate.setSplitPayment(true);

        //2 caso
        estimate1.setPaId("12345");
        estimate1.setStatus("VALIDATED");
        estimate1.setDeadlineDate(Instant.parse("2023-01-15T10:15:30Z"));
        estimate1.setReferenceMonth("FEBB-2023");
        estimate1.setTotalDigitalNotif(114);
        estimate1.setTotal890Notif(530);
        estimate1.setTotalAnalogNotif(210);
        estimate1.setDescription("description");
        estimate1.setMailAddress("mailAddress");
        estimate1.setLastModifiedDate(Instant.now());
        estimate1.setSplitPayment(true);

        //3 caso
        estimate2.setPaId("12345");
        estimate2.setStatus("VALIDATED");
        estimate2.setDeadlineDate(Instant.parse("2022-10-15T10:15:30Z"));
        estimate2.setReferenceMonth("NOV-2022");
        estimate2.setTotalDigitalNotif(139);
        estimate2.setTotal890Notif(520);
        estimate2.setTotalAnalogNotif(222);
        estimate2.setDescription("description");
        estimate2.setMailAddress("mailAddress");
        estimate2.setLastModifiedDate(Instant.now());
        estimate2.setSplitPayment(true);

        dbList.add(estimate);
        dbList.add(estimate1);
        dbList.add(estimate2);

    }
  
}