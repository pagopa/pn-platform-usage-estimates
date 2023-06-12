package it.pagopa.pn.platform.service;

public interface QueueListenerService {

    void activityReportListener(Object data);
    void safeStorageResponseListener(String key);


}
