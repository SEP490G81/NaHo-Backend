package org.naho.file.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.static-resource")
public class StaticResourceProperties {
    String localRoot;
    String localPath;
    String backendFilesBaseUrl;
    String avatars;
    String books;
    String recordings;
    String temp;
}
