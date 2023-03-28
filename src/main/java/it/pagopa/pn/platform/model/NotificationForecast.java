package it.pagopa.pn.platform.model;

import it.pagopa.pn.platform.utils.Utility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationForecast {

    private String paId ;

    private String deadlineDate;

    private String referenceMonth;

    private Integer totalDigitalNotif;

    private Integer totalPaper890Notif ;

    private Integer totalPaperNationalNotif ;

    private Integer totalPaperInternationalNotif ;

    private Date lastModifiedTimestamp ;


    private boolean fromNationalRegistry = false;

    public String convertToHash() {
        if (
                this.paId == null &&
                        this.deadlineDate == null &&
                        this.referenceMonth == null &&
                        this.totalDigitalNotif == null &&
                        this.totalPaper890Notif == null &&
                        this.totalPaperNationalNotif == null &&
                        this.totalPaperInternationalNotif == null &&
                        this.lastModifiedTimestamp == null
        ) return null;

        return Utility.convertToHash(this.deadlineDate) +
                Utility.convertToHash(this.referenceMonth) +
                Utility.convertToHash(this.totalDigitalNotif.toString()) +
                Utility.convertToHash(this.totalPaper890Notif.toString()) +
                Utility.convertToHash(this.totalPaperNationalNotif.toString()) +
                Utility.convertToHash(this.totalPaperInternationalNotif.toString()) +
                Utility.convertToHash(this.lastModifiedTimestamp.toString()) ;
    }


}
