package org.naho.cost.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.azure.cost")
public class AzureCostConfigProperties {
    private String tenantId;
    private String clientId;
    private String clientSecret;
    private String subscriptionId;
    private String scope;
    private String queryUrlTemplate;
}


