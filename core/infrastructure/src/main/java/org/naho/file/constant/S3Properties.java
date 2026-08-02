package org.naho.file.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.s3")
public class S3Properties {
    String publicBucketName;
    String privateBucketName;
    String region;
    Duration signatureDuration;
}
