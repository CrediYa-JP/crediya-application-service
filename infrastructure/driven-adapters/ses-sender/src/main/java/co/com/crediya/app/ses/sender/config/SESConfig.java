package co.com.crediya.app.ses.sender.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesAsyncClient;
import java.net.URI;

@Configuration
@EnableConfigurationProperties(SESProperties.class)
public class SESConfig {

    @Bean("directEmailSesClient")
    public SesAsyncClient sesAsyncClient(SESProperties properties) {
        return SesAsyncClient.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}