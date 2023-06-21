package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.model.ActivityReport;
import reactor.core.publisher.Mono;

public interface QueueListenerService {

    Mono<Void> activityReportListener(ActivityReport data);
    void safeStorageResponseListener(String key);


}
