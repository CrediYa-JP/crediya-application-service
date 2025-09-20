package co.com.crediya.app.sqs.config;

import co.com.crediya.app.sqs.sender.config.CapacityEvaluationSQSProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(CapacityEvaluationSQSProperties.class)
public class CapacityEvaluationSQSConfig {

    @Bean("capacityEvaluationSqsClient")
    public SqsAsyncClient configCapacityEvaluationSqs(CapacityEvaluationSQSProperties properties) {
        return SqsAsyncClient.builder()
                .region(Region.of(properties.region()))
                .endpointOverride(URI.create(properties.endpoint()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}