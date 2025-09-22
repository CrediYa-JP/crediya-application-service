package co.com.crediya.app.ses.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.ses")
public record SESProperties(
        String region,
        String endpoint,
        String sourceEmail
) {}