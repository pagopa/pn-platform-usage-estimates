package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import it.pagopa.pn.platform.service.impl.ReportServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;

@Slf4j
public class ReportServiceImplTest extends BaseTest {

    @Autowired
    private ReportServiceImpl reportServiceImpl;

    @Autowired
    private ActivityReportMetaDAO activityReportMetaDAO;

    @MockBean
    private SafeStorageClient safeStorageClient;

    @MockBean
    private S3Bucket s3Bucket;

    @Autowired
    private AwsBatchService awsBatchService;

    private PnActivityReport pnActivityReport1 = new PnActivityReport();
    private PnActivityReport pnActivityReport2 = new PnActivityReport();


    @BeforeEach
    public void setUp(){
        initialValue();
    }

    private void initialValue(){
        pnActivityReport1.setPaId("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a");
        pnActivityReport1.setReportKey("DICEMBRE-03");
        pnActivityReport1.setStatusReport("DOWNLOADED");
        pnActivityReport1.setReferenceMonth("DIC-2022");
        pnActivityReport1.setReportZipKey("reportZipKey");
        pnActivityReport1.setBucketName("BucketName");
        pnActivityReport1.setLastModifiedDate(Instant.now());
        pnActivityReport1.setErrorMessage("Error message");

        this.activityReportMetaDAO.createMetaData(pnActivityReport1);

    }


}
