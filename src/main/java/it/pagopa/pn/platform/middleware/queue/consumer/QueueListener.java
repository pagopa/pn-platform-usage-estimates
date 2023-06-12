package it.pagopa.pn.platform.middleware.queue.consumer;

import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import it.pagopa.pn.platform.service.QueueListenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@Slf4j
public class QueueListener {
    @Autowired
    private QueueListenerService queueListenerService;

    @SqsListener(value = "${pn.platform-usage-estimates.queue-data-lake}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void pullFromDataLakeQueue(@Payload String node, @Headers Map<String, Object> headers){

        log.info(node);

    }

}