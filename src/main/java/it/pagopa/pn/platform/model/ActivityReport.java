package it.pagopa.pn.platform.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityReport {
    @JsonProperty("Records")
    private List<Record> records;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Record {
        @JsonProperty("s3.object.key")
        private String fileKey;

        @JsonProperty("s3.bucket.name")
        private String bucketName;

        public String getPaId() {
            int start = bucketName.indexOf("report_attivita_pn_from_datalake/");
            int end = bucketName.indexOf("/");
            return bucketName.substring(start + 1, end);
        }

        public String getPartNum() {
            int start = bucketName.indexOf("part-");
            int end = bucketName.indexOf(".csv");
            return bucketName.substring(start +1, end);
        }

    }



}
