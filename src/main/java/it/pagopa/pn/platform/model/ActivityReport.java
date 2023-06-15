package it.pagopa.pn.platform.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityReport {

    private String paId;

    private String fileKey;

    private String status;

    private String bucketName;

    private String fileZipKey;
}
