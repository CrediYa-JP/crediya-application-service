package co.com.crediya.app.ses.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.ses")
public record SESProperties(
        String region,
        String sourceEmail
) {}