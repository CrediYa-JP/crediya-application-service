package co.com.crediya.app.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.sqs.capacity-evaluation")
public record CapacityEvaluationSQSProperties(
        String region,
        String queueUrl,
        String endpoint
) {
}