package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.datalake.v1.dto.MonthlyNotificationPreorderDto;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.model.TimelineEstimate;
import it.pagopa.pn.platform.model.TimelineProfilation;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import it.pagopa.pn.platform.utils.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EstimateMapperTest {

    private final PnEstimate estimate1 = new PnEstimate();
    private final PnEstimate estimate2 = new PnEstimate();
    private List<PnEstimate> history = new ArrayList<>();


    private TimelineEstimate timelineEstimate = new TimelineEstimate(estimate1, history);


    @BeforeEach
    public void setUp(){
        initialValue();
    }

    @Test
    void dtoToFileRecordVersionNull(){

        estimate1.setRecordVersion(null);
        MonthlyNotificationPreorderDto monthlyNotificationPreorderDto = EstimateMapper.dtoToFile(estimate1);
        Assertions.assertEquals(monthlyNotificationPreorderDto.getRecordVersion(), BigDecimal.ONE);
    }

    @Test
    void dtoToFile(){

        MonthlyNotificationPreorderDto monthlyNotificationPreorderDto = EstimateMapper.dtoToFile(estimate1);
        Assertions.assertNotNull(monthlyNotificationPreorderDto.getRecordVersion());
        Assertions.assertEquals(estimate1.getRecordVersion(),2);
    }

    @Test
    void toPageableResponse(){
        Pageable pageable = PageRequest.of(4, 20);
        PageableEstimateResponseDto pageableEstimateResponseDto  = EstimateMapper.toPageableResponse(pageable, timelineEstimate);
        Assertions.assertNotNull(pageableEstimateResponseDto.getActual().getLastModifiedDate());
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

        history.add(estimate1);
    }
}
