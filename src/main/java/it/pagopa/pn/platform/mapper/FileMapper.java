package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.model.PageModel;
import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableDeanonymizedFilesResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public class FileMapper {

    private FileMapper() {
        throw new IllegalCallerException();
    }

    public static InfoDownloadDTO toDownloadFile(PnActivityReport activityReport, String url){
        InfoDownloadDTO infoDownloadDTO = new InfoDownloadDTO();

        infoDownloadDTO.setPaId(activityReport.getPaId());
        infoDownloadDTO.setUrl(url);
        infoDownloadDTO.setReportKey(activityReport.getReportKey());
        infoDownloadDTO.setStatus(InfoDownloadDTO.StatusEnum.READY);

        return infoDownloadDTO;
    }


    public static InfoDownloadDTO fromPnActivityReportToInfoDownloadDTO(String paId, String referenceMonth, PnActivityReport pnActivityReportsList){
        InfoDownloadDTO infoDownloadDTO = new InfoDownloadDTO();
        infoDownloadDTO.setPaId(paId);
        infoDownloadDTO.setReferenceMonth(referenceMonth);
        infoDownloadDTO.setReportKey(pnActivityReportsList.getReportKey());

        return infoDownloadDTO;
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

    public static InfoDownloadDTO deanonymizedFilesToDto(PnActivityReport activityReport){
        InfoDownloadDTO filesList = new InfoDownloadDTO();
        filesList.setPaId(activityReport.getPaId());
        filesList.setReferenceMonth(activityReport.getReferenceMonth());
        filesList.setLastModifiedDate(activityReport.getLastModifiedDate() != null ? Date.from(activityReport.getLastModifiedDate()) : null);
        filesList.setStatus(InfoDownloadDTO.StatusEnum.valueOf(activityReport.getStatus()));
        filesList.setReportKey(activityReport.getReportKey());
        if (activityReport.getStatus().equals(String.valueOf(InfoDownloadDTO.StatusEnum.ERROR))){
            filesList.setErrorMessage(activityReport.getErrorMessage());
        }

        return filesList;
    }
}
