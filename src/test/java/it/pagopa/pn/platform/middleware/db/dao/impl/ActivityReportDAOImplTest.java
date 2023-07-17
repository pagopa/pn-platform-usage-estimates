package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.model.ActivityReport;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ActivityReportDAOImplTest extends BaseTest {

    @Autowired
    ActivityReportMetaDAO activityReportMetaDAO;

    private final PnActivityReport activityReport = new PnActivityReport();

    private final String bucketName = "/report_attivita_pn_from_datalake/1234/<YYYY-MM>/<timestampGenerazioneReport>/report-notificazioni-perfezionate-part-1.csv";

    private final ActivityReport.Record record = new ActivityReport.Record("",bucketName);

    @BeforeEach
    public void setUp(){
        initialValue();
    }

    @Test
    void createMeta (){

        PnActivityReport pnActivityReport = this.activityReportMetaDAO.createMetaData(activityReport).block();
        assertNotNull(pnActivityReport);
        assertEquals(activityReport.getReportKey(), pnActivityReport.getReportKey());
        assertEquals(activityReport.getBucketName(), pnActivityReport.getBucketName());
        assertEquals(activityReport.getPaId(), pnActivityReport.getPaId());
        assertEquals(activityReport.getStatusReport(), pnActivityReport.getStatusReport());
        assertEquals(activityReport.getGenerationDate(), pnActivityReport.getGenerationDate());
        assertEquals(activityReport.getPart(), pnActivityReport.getPart());

    }

    @Test
    void findAllFromPaId (){

        List<PnActivityReport> pnActivityReports = this.activityReportMetaDAO.findAllFromPaId(activityReport.getPaId()).collectList().block();
        assertNotNull(pnActivityReports);
        assertEquals(3, pnActivityReports.size());

    }

    @Test
    void findAllFromPaIdAndRefMonth (){

        List<PnActivityReport> pnActivityReports = this.activityReportMetaDAO.findAllFromPaId(activityReport.getPaId(), "DIC-2022", ReportStatusEnum.READY.getValue()).collectList().block();
        assertNotNull(pnActivityReports);
        assertEquals(0, pnActivityReports.size());

    }

    @Test
    void findByPaIdAndReportKey (){

        PnActivityReport pnActivityReport = this.activityReportMetaDAO.findByPaIdAndReportKey(activityReport.getPaId(), activityReport.getReportKey()).block();
        assertNotNull(pnActivityReport);
        assertEquals(activityReport.getPaId(), pnActivityReport.getPaId());
        assertEquals(activityReport.getReportKey(), pnActivityReport.getReportKey());
        assertEquals(activityReport.getStatusReport(), pnActivityReport.getStatusReport());

    }

    @Test
    void findAllFromPaIdAndStatus (){

        List<PnActivityReport> pnActivityReports = this.activityReportMetaDAO.findAllFromPaIdAndStatus(activityReport.getPaId(), ReportStatusEnum.READY.getValue()).collectList().block();
        assertNotNull(pnActivityReports);
        assertEquals(0, pnActivityReports.size());

    }

    void initialValue(){
        activityReport.setPaId("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a");
        activityReport.setBucketName(record.getBucketName());
        activityReport.setReportKey("abc1234");
        activityReport.setGenerationDate(Instant.now());
        activityReport.setStatusReport(ReportStatusEnum.RAW.getValue());
        activityReport.setPart(record.getPartNum());
    }


}
