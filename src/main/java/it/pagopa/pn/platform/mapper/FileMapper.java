package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileDownloadResponseDto;
import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;

import java.util.ArrayList;
import java.util.List;

public class FileMapper {

    private FileMapper() {
        throw new IllegalCallerException();
    }

    public static InfoDownloadDTO toDownloadFile(String paId, FileDownloadResponseDto responseFile){
        InfoDownloadDTO infoDownloadDTO = new InfoDownloadDTO();

        infoDownloadDTO.setPaId(paId);
        infoDownloadDTO.setUrl(responseFile.getDownload().getUrl());
        infoDownloadDTO.setFileKey(responseFile.getKey());
        infoDownloadDTO.setStatus(responseFile.getDocumentStatus());

        return infoDownloadDTO;
    }

    public static InfoDownloadDTO fromPnActivityReportToInfoDownloadDTO(String paId, String referenceMonth, PnActivityReport pnActivityReportsList){
        InfoDownloadDTO infoDownloadDTO = new InfoDownloadDTO();
        infoDownloadDTO.setPaId(paId);
        infoDownloadDTO.setFileKey(pnActivityReportsList.getFileKey());
        return infoDownloadDTO;
    }
}
