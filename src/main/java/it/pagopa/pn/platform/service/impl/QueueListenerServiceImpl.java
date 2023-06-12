package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.service.QueueListenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueueListenerServiceImpl implements QueueListenerService {

    @Override
    public void activityReportListener(Object data) {

    }

    @Override
    public void safeStorageResponseListener(String key) {

    }

}
