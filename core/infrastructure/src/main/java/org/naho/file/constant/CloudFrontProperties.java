package org.naho.file.constant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.cloudfront")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CloudFrontProperties {
    String domain;
}
