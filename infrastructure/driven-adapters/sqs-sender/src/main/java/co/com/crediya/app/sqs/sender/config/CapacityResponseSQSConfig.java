package co.com.crediya.app.sqs.sender.config;

import co.com.crediya.app.sqs.sender.config.CapacityResponseSQSProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(CapacityResponseSQSProperties.class)
public class CapacityResponseSQSConfig {

    @Bean("capacityResponseSqsClient")
    public SqsAsyncClient configCapacityResponseSqs(CapacityResponseSQSProperties properties) {
        return SqsAsyncClient.builder()
                .region(Region.of(properties.region()))
                .endpointOverride(URI.create(properties.endpoint()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}