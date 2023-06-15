package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.mapper.ActivityReportMapper;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.model.ActivityReport;
import it.pagopa.pn.platform.service.QueueListenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueueListenerServiceImpl implements QueueListenerService {

    @Autowired
    private ActivityReportMetaDAO activityReportMetaDAO;

    @Override
    public void activityReportListener(ActivityReport activityReport) {

        PnActivityReport pnActivityReport = ActivityReportMapper.toEntity(activityReport);
        activityReportMetaDAO.createMetaData(pnActivityReport);


    }

    @Override
    public void safeStorageResponseListener(String key) {

    }

}
