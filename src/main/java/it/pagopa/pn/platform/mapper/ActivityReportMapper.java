package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.model.ActivityReport;
import it.pagopa.pn.platform.model.ActivityReportCSV;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.List;

public class ActivityReportMapper {

//    public static ActivityReport fromQueueListner(){
//
//        ActivityReport activityReport = new ActivityReport();
//        activityReport.setPaId();
//        activityReport.setFileKey();
//        activityReport.setBucketName();
//        activityReport.setStatus();
//        activityReport.setFileZipKey();
//
//        return activityReport;
//
//    }

    public static PnActivityReport toEntity (ActivityReport.Record record){
        PnActivityReport entity = new PnActivityReport();


        entity.setBucketName(record.getBucketName());
        entity.setReportKey(record.getFileKey());
        entity.setPaId(record.getPaId());

        return entity;

    }

    public static List<ActivityReportCSV> csvToObject(Iterable<CSVRecord> records){
        List<ActivityReportCSV> reports = new ArrayList<>();

        for (CSVRecord record : records) {
            ActivityReportCSV activityReportCSV = new ActivityReportCSV();
            activityReportCSV.setCap(record.get(ActivityReportCSV.Header.cap));
            activityReportCSV.setStatoEstero(record.get(ActivityReportCSV.Header.stato_estero));
            activityReportCSV.setIun(record.get(ActivityReportCSV.Header.iun));
            activityReportCSV.setCosto(record.get(ActivityReportCSV.Header.costo_eurocent));
            activityReportCSV.setEventId(record.get(ActivityReportCSV.Header.event_id));
            activityReportCSV.setEventTimestamp(record.get(ActivityReportCSV.Header.event_timestamp));
            activityReportCSV.setNumeroPagine(record.get(ActivityReportCSV.Header.numero_pagine));
            activityReportCSV.setNotificationSentAt(record.get(ActivityReportCSV.Header.notification_sent_at));
            activityReportCSV.setRecipientIndex(record.get(ActivityReportCSV.Header.recipient_index));
            activityReportCSV.setPesoPlicoGrammi(record.get(ActivityReportCSV.Header.peso_plico_g));
            activityReportCSV.setRecipientType(record.get(ActivityReportCSV.Header.recipient_type));
            activityReportCSV.setRecipientTaxId(record.get(ActivityReportCSV.Header.recipient_tax_id));
            reports.add(activityReportCSV);
        }
        return reports;
    }
}
