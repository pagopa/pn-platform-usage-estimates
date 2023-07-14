package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileDownloadResponseDto;
import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.service.impl.ReportServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    private List<PnActivityReport> reports = new ArrayList<>();
    private Flux<PnActivityReport> reportFlux = Flux.empty();;


    @BeforeEach
    public void setUp(){
        initialValue();
    }

   // @Test
    void getAllReportFile(){
        Mockito.when(this.activityReportMetaDAO.findAllFromPaId(Mockito.anyString(), Mockito.anyString())).thenReturn(reportFlux);
        Flux<InfoDownloadDTO> fluxDTO = this.reportServiceImpl.getAllReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a","DIC-2022");
        Assertions.assertNotNull(fluxDTO);
    }

    private void initialValue(){
        pnActivityReport1.setPaId("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a");
        pnActivityReport1.setReportKey("DICEMBRE-03");
        pnActivityReport1.setStatus("DOWNLOADED");
        pnActivityReport1.setReferenceMonth("DIC-2022");
        pnActivityReport1.setReportZipKey("reportZipKey");
        pnActivityReport1.setBucketName("BucketName");
        pnActivityReport1.setLastModifiedDate(Instant.now());
        pnActivityReport1.setAction("Action");
        pnActivityReport1.setErrorMessage("Error message");

        pnActivityReport2.setPaId("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a");
        pnActivityReport2.setReportKey("DICEMBRE-04");
        pnActivityReport2.setStatus("DOWNLOADED");
        pnActivityReport2.setReferenceMonth("DIC-2022");
        pnActivityReport2.setReportZipKey("reportZipKey");
        pnActivityReport2.setBucketName("BucketName");
        pnActivityReport2.setLastModifiedDate(Instant.now());
        pnActivityReport2.setAction("Action");
        pnActivityReport2.setErrorMessage("Error message");

        reports.add(pnActivityReport1);
        reports.add(pnActivityReport2);

        reportFlux = Flux.fromIterable(reports);

        this.activityReportMetaDAO.createMetaData(pnActivityReport1);
        this.activityReportMetaDAO.createMetaData(pnActivityReport2);
    }


}
