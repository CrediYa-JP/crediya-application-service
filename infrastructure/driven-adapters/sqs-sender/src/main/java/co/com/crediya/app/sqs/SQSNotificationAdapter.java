package co.com.crediya.app.sqs;

import co.com.crediya.app.model.notifications.gateways.NotificationGateway;
import co.com.crediya.app.sqs.sender.SQSSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SQSNotificationAdapter implements NotificationGateway {

    private final SQSSender sqsSender;

    @Override
    public Mono<Void> sendApplicationStatusNotification(Long applicationId, String userEmail,
                                                        String userName, String status,
                                                        BigDecimal amount, Integer term) {

        String message = buildNotificationMessage(applicationId, userEmail, userName, status, amount, term);

        return sqsSender.send(message)
                .doOnNext(messageId -> log.info("SQS_MESSAGE_SENT applicationId={}, messageId={}",
                        applicationId, messageId))
                .then();
    }

    private String buildNotificationMessage(Long applicationId, String userEmail, String userName,
                                            String status, BigDecimal amount, Integer term) {
        return String.format("""
            {
                "applicationId": %d,
                "userEmail": "%s",
                "userName": "%s", 
                "status": "%s",
                "amount": %s,
                "term": %d,
                "timestamp": "%s"
            }
            """, applicationId, userEmail, userName, status, amount, term,
                LocalDateTime.now().toString());
    }
}