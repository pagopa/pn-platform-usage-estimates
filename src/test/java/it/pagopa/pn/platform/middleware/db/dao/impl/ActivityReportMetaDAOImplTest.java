package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
class ActivityReportMetaDAOImplTest extends BaseTest {

    @Autowired
    private ActivityReportMetaDAO activityReportMetaDAO;

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
        pnActivityReport1.setBucketName("BucketName");
        pnActivityReport1.setLastModifiedDate(Instant.now());
        pnActivityReport1.setErrorMessage("Error message");

        this.activityReportMetaDAO.createMetaData(pnActivityReport1);
    }

    @Test
    void findAllFromPaIdAndReferenceMonth(){
        Flux<PnActivityReport> result = this.activityReportMetaDAO.findAllFromPaId("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "DIC-2022", ReportStatusEnum.READY.getValue());

        Assertions.assertNotNull(result);
        result
                .collectList()
                .subscribe(listResult -> {
                    Assertions.assertEquals(3,listResult.size());
                });

    }

    @Test
    void createMetaData(){

        pnActivityReport1.setPaId("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a");
        pnActivityReport1.setReportKey("DICEMBRE-03");
        pnActivityReport1.setStatusReport("DOWNLOADED");
        pnActivityReport1.setReferenceMonth("DIC-2022");
        pnActivityReport1.setBucketName("BucketName");
        pnActivityReport1.setLastModifiedDate(Instant.now());
        pnActivityReport1.setErrorMessage("Error message");

        Mono<PnActivityReport> pnActivityReportMono =  this.activityReportMetaDAO.createMetaData(pnActivityReport1);

        Assertions.assertNotNull(pnActivityReportMono);
        pnActivityReportMono.subscribe(
                result -> {
                    Assertions.assertEquals(pnActivityReport1.getPaId(), result.getPaId());
                    Assertions.assertEquals(pnActivityReport1.getReportKey(), result.getReportKey());
                    Assertions.assertEquals(pnActivityReport1.getStatusReport(), result.getStatusReport());
                    Assertions.assertEquals(pnActivityReport1.getReferenceMonth(), result.getReferenceMonth());
                    Assertions.assertEquals(pnActivityReport1.getBucketName(), result.getBucketName());
                    Assertions.assertEquals(pnActivityReport1.getLastModifiedDate(), result.getLastModifiedDate());
                    Assertions.assertEquals(pnActivityReport1.getErrorMessage(), result.getErrorMessage());
                });

    }

    @Test
    void findAllFromPaId(){
        Flux<PnActivityReport> result = this.activityReportMetaDAO.findAllFromPaId("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a");

        Assertions.assertNotNull(result);
        result
                .collectList()
                .subscribe(listResult -> {
                    Assertions.assertEquals(3,listResult.size());
                });

    }

    @Test
    void findByPaIdAndReportKey(){
        Mono<PnActivityReport> result = this.activityReportMetaDAO.findByPaIdAndReportKey("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "DICEMBRE-01");

        Assertions.assertNotNull(result);
        result.map(mono ->{
            Assertions.assertEquals("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", mono.getPaId());
            Assertions.assertEquals("DIC-2022", mono.getReferenceMonth());
            Assertions.assertEquals("DICEMBRE-01", mono.getReportKey());
            return null;
        });
    }

    @Test
    void findAllByPaIdAndStatus(){
        Flux<PnActivityReport> result = this.activityReportMetaDAO.findAllFromPaIdAndStatus("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "DOWNLOADED");

        Assertions.assertNotNull(result);
        result
                .collectList()
                .subscribe(listResult -> {
                    Assertions.assertEquals(3,listResult.size());
                });
    }


}
