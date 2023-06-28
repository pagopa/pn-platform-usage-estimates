package it.pagopa.pn.platform.msclient.impl;

import it.pagopa.pn.platform.datalake.v1.dto.MonthlyNotificationPreorderDto;
import it.pagopa.pn.platform.mapper.EstimateMapper;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.msclient.DataLakeClient;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDetail;
import it.pagopa.pn.platform.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@Component
public class DataLakeClientImpl implements DataLakeClient {

    @Autowired
    private EstimateDAO estimateDAO;

    @Override
    public Mono<List<MonthlyNotificationPreorderDto>> sendMonthlyOrders() {

        List<MonthlyNotificationPreorderDto> monthlyNotificationPreorderDto = new ArrayList<>();

        return this.estimateDAO.getAllEstimates().map(pnEstimates -> {
            pnEstimates.forEach(pnEstimate -> {
                if (pnEstimate.getStatus().equalsIgnoreCase(EstimateDetail.StatusEnum.VALIDATED.getValue())
                        && pnEstimate.getSendToDatalake() && pnEstimate.getDeadlineDate().isBefore(DateUtils.getDateBeforeOneMonth()) ) {
                    monthlyNotificationPreorderDto.add(EstimateMapper.dtoToFile(pnEstimate));
//                    VEDERE SE IMPLEMENTARE LOGICA SALVATAGGIO SU S3 QUI
                }
            });
            return monthlyNotificationPreorderDto;
        });
    }


}
