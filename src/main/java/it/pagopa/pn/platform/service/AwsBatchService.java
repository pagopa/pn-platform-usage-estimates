package it.pagopa.pn.platform.service;

public interface AwsBatchService {

    String scheduleJob(String paId, String bucket, String reportKey);

}
