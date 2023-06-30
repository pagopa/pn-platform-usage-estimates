package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.mapper.ActivityReportMapper;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.model.ActivityReport;
import it.pagopa.pn.platform.service.QueueListenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class QueueListenerServiceImpl implements QueueListenerService {

    @Autowired
    private ActivityReportMetaDAO activityReportMetaDAO;

    @Override
    public Mono<Void> activityReportListener(ActivityReport activityReport) {

        return Flux.fromStream(activityReport.getRecords().stream())
                .map(ActivityReportMapper::toEntity)
                .flatMap(pnActivityReport -> activityReportMetaDAO.createMetaData(pnActivityReport))
                .map(pnActivityReport -> {
                 // schedulare il BATCH
                 return pnActivityReport;
                })
                .then();

    }

    @Override
    public void safeStorageResponseListener(String key) {

    }

}
