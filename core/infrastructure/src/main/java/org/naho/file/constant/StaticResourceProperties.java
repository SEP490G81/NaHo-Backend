package org.naho.file.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.static-resource")
public class StaticResourceProperties {
    String baseLocation;
    String avatars;
    String books;
    String recordings;
}
