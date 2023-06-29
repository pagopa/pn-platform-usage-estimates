package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.model.PageModel;
import it.pagopa.pn.platform.rest.v1.dto.ReportDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableDeanonymizedFilesResponseDto;
import it.pagopa.pn.platform.rest.v1.dto.ReportDTO;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public class FileMapper {

    private FileMapper() {
        throw new IllegalCallerException();
    }

    public static ReportDTO toDownloadFile(PnActivityReport activityReport, String url){
        ReportDTO reportDTO = new ReportDTO();

        reportDTO.setPaId(activityReport.getPaId());
        reportDTO.setUrl(url);
        reportDTO.setReportKey(activityReport.getReportKey());
        reportDTO.setStatus(ReportStatusEnum.READY);

        return reportDTO;
    }


    public static ReportDTO fromPnActivityReportToInfoDownloadDTO(String paId, String referenceMonth, PnActivityReport pnActivityReportsList){
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setPaId(paId);
        reportDTO.setReferenceMonth(referenceMonth);
        reportDTO.setReportKey(pnActivityReportsList.getReportKey());

        return reportDTO;
    }

    public static PageableDeanonymizedFilesResponseDto toPageableResponse(PageModel<PnActivityReport> pnActivityReportPageModel){
        PageableDeanonymizedFilesResponseDto response = new PageableDeanonymizedFilesResponseDto();
        response.setPageable(pnActivityReportPageModel.getPageable());
        response.setNumber(pnActivityReportPageModel.getNumber());
        response.setNumberOfElements(pnActivityReportPageModel.getNumberOfElements());
        response.setSize(pnActivityReportPageModel.getSize());
        response.setTotalElements(pnActivityReportPageModel.getTotalElements());
        response.setTotalPages((long) pnActivityReportPageModel.getTotalPages());
        response.setFirst(pnActivityReportPageModel.isFirst());
        response.setLast(pnActivityReportPageModel.isLast());
        response.setEmpty(pnActivityReportPageModel.isEmpty());
        response.setContent(pnActivityReportPageModel.mapTo(FileMapper::deanonymizedFilesToDto));
        return response;
    }

    public static PageModel<PnActivityReport> toPagination(Pageable pageable, List<PnActivityReport> list){
        return PageModel.builder(list, pageable);
    }

    public static ReportDTO deanonymizedFilesToDto(PnActivityReport activityReport){
        ReportDTO filesList = new ReportDTO();
        filesList.setPaId(activityReport.getPaId());
        filesList.setReferenceMonth(activityReport.getReferenceMonth());
        filesList.setLastModifiedDate(activityReport.getLastModifiedDate() != null ? Date.from(activityReport.getLastModifiedDate()) : null);
        filesList.setStatus(ReportStatusEnum.valueOf(activityReport.getStatus()));
        filesList.setReportKey(activityReport.getReportKey());
        if (activityReport.getStatus().equals(String.valueOf(ReportStatusEnum.ERROR))){
            filesList.setErrorMessage(activityReport.getErrorMessage());
        }

        return filesList;
    }
}
