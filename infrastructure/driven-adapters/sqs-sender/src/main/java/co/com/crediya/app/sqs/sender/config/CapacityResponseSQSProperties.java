package co.com.crediya.app.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.sqs.capacity-response")
public record CapacityResponseSQSProperties(
        String region,
        String queueUrl,
        String endpoint
) {
}