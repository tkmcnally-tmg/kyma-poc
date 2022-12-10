package com.whirlpool.frauddetectionservice;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
@RefreshScope
public class FraudDetectionSettings {

    private boolean frauddetection_enabled;

    public boolean isEnabled() {
        return frauddetection_enabled;
    }

    public void setEnabled(boolean enabled) {
        this.frauddetection_enabled = enabled;
    }
}
