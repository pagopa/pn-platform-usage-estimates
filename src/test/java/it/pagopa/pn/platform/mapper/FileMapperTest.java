package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.model.PageModel;

import it.pagopa.pn.platform.rest.v1.dto.PageableDeanonymizedFilesResponseDto;
import it.pagopa.pn.platform.rest.v1.dto.ReportDTO;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


class FileMapperTest {

    private PnActivityReport activityReport1 = new PnActivityReport();
    private PnActivityReport activityReport2 = new PnActivityReport();

    @BeforeEach
    void setUp(){
        this.initialize();
    }

    @Test
    void toDownloadFileoOk() {
        String url = "url";
        ReportDTO infoDownloadDTO = FileMapper.toDownloadFile(activityReport1, url);
        
        Assertions.assertNotNull(infoDownloadDTO);
        Assertions.assertEquals(activityReport1.getPaId(),infoDownloadDTO.getPaId());
        Assertions.assertEquals(url,infoDownloadDTO.getUrl());
        Assertions.assertEquals(activityReport1.getReportKey(),infoDownloadDTO.getReportKey());
        Assertions.assertEquals(ReportStatusEnum.READY,infoDownloadDTO.getStatus());
    }

    @Test
    void fromPnActivityReportToInfoDownloadDTOOK() {

        String url = "url";
        ReportDTO infoDownloadDTO = FileMapper.fromPnActivityReportToInfoDownloadDTO("12345","APR-2023",activityReport1);

        Assertions.assertNotNull(infoDownloadDTO);
        Assertions.assertEquals(activityReport1.getPaId(),infoDownloadDTO.getPaId());
        Assertions.assertEquals(activityReport1.getReferenceMonth(),infoDownloadDTO.getReferenceMonth());
        Assertions.assertEquals(activityReport1.getReportKey(),infoDownloadDTO.getReportKey());
    }

    @Test
    void toPageableResponse() {
        Pageable pageable = PageRequest.of(4, 20);
        List<PnActivityReport> pnActivityReportList = new ArrayList<>();
        PageModel<PnActivityReport> pagmodel= FileMapper.toPagination(pageable,pnActivityReportList);
        PageableDeanonymizedFilesResponseDto response = FileMapper.toPageableResponse(pagmodel);

        Assertions.assertNotNull(pagmodel);
        Assertions.assertNotNull(response);
    }

    @Test
    void toPagination() {
        Pageable pageable = PageRequest.of(4, 20);
        List<PnActivityReport> pnActivityReportList = new ArrayList<>();
        PageModel<PnActivityReport> pagmodel= FileMapper.toPagination(pageable,pnActivityReportList);

        Assertions.assertNotNull(pagmodel);
    }

    @Test
    void deanonymizedFilesToDto() {
        ReportDTO infoDownloadDTO = FileMapper.deanonymizedFilesToDto(activityReport1);

        Assertions.assertNotNull(infoDownloadDTO);
        Assertions.assertEquals(activityReport1.getPaId(),infoDownloadDTO.getPaId());
        Assertions.assertEquals(activityReport1.getReferenceMonth(),infoDownloadDTO.getReferenceMonth());
        Assertions.assertEquals(activityReport1.getReportKey(),infoDownloadDTO.getReportKey());
        Assertions.assertEquals(activityReport1.getLastModifiedDate().getEpochSecond(),infoDownloadDTO.getLastModifiedDate().toInstant().getEpochSecond());
        Assertions.assertEquals(activityReport1.getStatusReport(),String.valueOf(infoDownloadDTO.getStatus()));
        Assertions.assertNull(infoDownloadDTO.getErrorMessage());

    }

    @Test
    void deanonymizedFilesToDtoWithError() {
        ReportDTO infoDownloadDTO = FileMapper.deanonymizedFilesToDto(activityReport2);

        Assertions.assertNotNull(infoDownloadDTO);
        Assertions.assertEquals(activityReport2.getPaId(),infoDownloadDTO.getPaId());
        Assertions.assertEquals(activityReport2.getReferenceMonth(),infoDownloadDTO.getReferenceMonth());
        Assertions.assertEquals(activityReport2.getReportKey(),infoDownloadDTO.getReportKey());
        Assertions.assertNull(activityReport2.getLastModifiedDate());
        Assertions.assertEquals(activityReport2.getStatusReport(),String.valueOf(infoDownloadDTO.getStatus()));
        Assertions.assertEquals(activityReport2.getErrorMessage(), infoDownloadDTO.getErrorMessage());

    }

    private void initialize(){

        activityReport1.setPaId("12345");
        activityReport1.setReferenceMonth("APR-2023");
        activityReport1.setReportKey("ReportZipKey");
        activityReport1.setStatusReport("READY");
        activityReport1.setBucketName("bucketName");
        activityReport1.setReportKey("reportKey");
        activityReport1.setLastModifiedDate(Instant.now());
        activityReport1.setErrorMessage(null);

        activityReport2.setPaId("12345");
        activityReport2.setReferenceMonth("MAG-2023");
        activityReport2.setReportKey("ReportZipKey");
        activityReport2.setStatusReport("ERROR");
        activityReport2.setBucketName("bucketName");
        activityReport2.setReportKey("reportKey");
        activityReport2.setLastModifiedDate(null);
        activityReport2.setErrorMessage("error message");

    }
}