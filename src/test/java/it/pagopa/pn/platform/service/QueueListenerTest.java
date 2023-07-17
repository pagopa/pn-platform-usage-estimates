package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.middleware.queue.consumer.QueueListener;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.MAPPER_ERROR;
import static org.junit.jupiter.api.Assertions.*;

public class QueueListenerTest extends BaseTest {

    @Autowired
    private QueueListener queueListener;
    @MockBean
    private ActivityReportMetaDAO activityReportMetaDAO;

    @Test
    void activityReportListnerOK(){

        PnActivityReport pnActivityReport = getPnActivityReport();

        String json = """
                {
                "Records":[
                {
                    "s3":{
                        "bucket":{
                            "name":"/report_attivita_pn_from_datalake/cc1c6a8e-5967-42c6-9d83-bfb12ba1665a/<YYYY-MM>/<timestampGenerazioneReport>/report-notificazioni-perfezionate-part-1.csv"
                        },
                        "object":{
                            "key":"fileCsv.csv"
                        }
                    }
                }
                ]
                }
                """;

        Map<String, Object> headers = new HashMap<>();
        Mockito.when(activityReportMetaDAO.createMetaData(Mockito.any())).thenReturn(Mono.just(pnActivityReport));

        this.queueListener.pullFromDataLakeQueue(json, headers);

        assertTrue(true);

    }

    @Test
    void activityReportListnerKO(){

        PnActivityReport pnActivityReport = getPnActivityReport();

        String json = """
                {
                {
                    "s3":{
                        "bucket":{
                            "name":"/report_attivita_pn_from_datalake/cc1c6a8e-5967-42c6-9d83-bfb12ba1665a/<YYYY-MM>/<timestampGenerazioneReport>/report-notificazioni-perfezionate-part-1.csv"
                        },
                        "object":{
                            "key":"fileCsv.csv"
                        }
                    }
                }
                }
                """;

        Map<String, Object> headers = new HashMap<>();
        Mockito.when(activityReportMetaDAO.createMetaData(Mockito.any())).thenReturn(Mono.just(pnActivityReport));

        PnGenericException exception = assertThrows(PnGenericException.class, () ->{
            this.queueListener.pullFromDataLakeQueue(json, headers);
        });
        assertEquals(MAPPER_ERROR, exception.getExceptionType());

    }

    private PnActivityReport getPnActivityReport(){
        PnActivityReport activityReport = new PnActivityReport();
        activityReport.setPaId("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a");
        activityReport.setBucketName("/report_attivita_pn_from_datalake/cc1c6a8e-5967-42c6-9d83-bfb12ba1665a/<YYYY-MM>/<timestampGenerazioneReport>/report-notificazioni-perfezionate-part-1.csv");
        activityReport.setReportKey("fileCsv.csv");
        activityReport.setGenerationDate(Instant.now());
        activityReport.setStatusReport(ReportStatusEnum.RAW.getValue());
        activityReport.setPart("1");
        return activityReport;

    }
}
