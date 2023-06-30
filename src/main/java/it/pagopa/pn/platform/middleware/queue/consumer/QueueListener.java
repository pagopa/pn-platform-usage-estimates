package it.pagopa.pn.platform.middleware.queue.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.model.ActivityReport;
import it.pagopa.pn.platform.service.QueueListenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.util.Map;
import it.pagopa.pn.platform.utils.Utility;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.MAPPER_ERROR;

@Component
@Slf4j
public class QueueListener {
    @Autowired
    private QueueListenerService queueListenerService;
    @Autowired
    private ObjectMapper objectMapper;

    @SqsListener(value = "${pn.platform-usage-estimates.queue-data-lake}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void pullFromDataLakeQueue(@Payload String node, @Headers Map<String, Object> headers){

        log.info(node);
        ActivityReport activityReport = convertToObject(node, ActivityReport.class);
        this.queueListenerService.activityReportListener(activityReport);


    }

    private <T> T convertToObject(String body, Class<T> tClass){
        T entity = Utility.jsonToObject(this.objectMapper, body, tClass);
        if (entity == null) throw new PnGenericException(MAPPER_ERROR, MAPPER_ERROR.getMessage());
        return entity;
    }

}