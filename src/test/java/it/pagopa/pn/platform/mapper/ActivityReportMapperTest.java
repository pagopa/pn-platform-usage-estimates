package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.model.ActivityReport;
import it.pagopa.pn.platform.model.ActivityReportCSV;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class ActivityReportMapperTest extends BaseTest {

    @Mock
    private ActivityReport.Record record;

    private ActivityReport activityReport = new ActivityReport();

    private List<ActivityReport.Record> records = new ArrayList<ActivityReport.Record>();

    @BeforeEach
    public void setUp(){
        initialize();
    }

    private void initialize(){
        records.add(record);
        activityReport.setRecords(records);
    }

    @Test
    @DisplayName("toEntity")
    void toEntity(){

        Mockito.when(record.getPaId()).thenReturn("paid");
        PnActivityReport result = ActivityReportMapper.toEntity(record);

        // Assert
        Assertions.assertEquals(record.getBucketName(), result.getBucketName());
        Assertions.assertEquals(record.getFileKey(), result.getReportKey());
        Assertions.assertEquals(record.getPaId(), result.getPaId());
    }

}
