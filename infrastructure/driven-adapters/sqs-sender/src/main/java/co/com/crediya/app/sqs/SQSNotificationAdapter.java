package co.com.crediya.app.sqs;

import co.com.crediya.app.model.capacityevaluation.PaymentDetail;
import co.com.crediya.app.model.notifications.ApplicationNotification;
import co.com.crediya.app.model.notifications.gateways.NotificationGateway;
import co.com.crediya.app.sqs.sender.SQSSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SQSNotificationAdapter implements NotificationGateway {

    private final SQSSender sqsSender;
    private final ObjectMapper objectMapper;

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

    @Override
    public Mono<Void> sendApplicationStatusWithPaymentPlan(Long applicationId, String userEmail,
                                                           String userName, String status,
                                                           BigDecimal amount, Integer term,
                                                           List<PaymentDetail> paymentPlan) {
        String message = buildNotificationMessageWithPaymentPlan(applicationId, userEmail, userName,
                status, amount, term, paymentPlan);
        return sqsSender.send(message)
                .doOnNext(messageId -> log.info("SQS_MESSAGE_SENT_WITH_PAYMENT_PLAN applicationId={}, messageId={}",
                        applicationId, messageId))
                .then();
    }

    private String buildNotificationMessageWithPaymentPlan(Long applicationId, String userEmail, String userName,
                                                           String status, BigDecimal amount, Integer term,
                                                           List<PaymentDetail> paymentPlan) {
        try {
            ApplicationNotification notification = ApplicationNotification.builder()
                    .applicationId(applicationId)
                    .userEmail(userEmail)
                    .userName(userName)
                    .status(status)
                    .amount(amount)
                    .term(term)
                    .paymentPlan(paymentPlan)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            return objectMapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize notification message", e);
        }
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