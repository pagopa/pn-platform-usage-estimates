package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.model.ActivityReport;

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

    public static PnActivityReport toEntity (ActivityReport activityReport){
        PnActivityReport entity = new PnActivityReport();

        entity.setBucketName(activityReport.getBucketName());
        entity.setFileKey(activityReport.getFileKey());

        return entity;

    }
}
