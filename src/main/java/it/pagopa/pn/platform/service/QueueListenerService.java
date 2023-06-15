package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.model.ActivityReport;

public interface QueueListenerService {

    void activityReportListener(ActivityReport data);
    void safeStorageResponseListener(String key);


}
