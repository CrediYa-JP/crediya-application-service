package co.com.crediya.app.model.notifications.gateways;

import co.com.crediya.app.model.capacityevaluation.PaymentDetail;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface NotificationGateway {
    Mono<Void> sendApplicationStatusNotification(Long applicationId, String userEmail,
                                                 String userName, String status,
                                                 BigDecimal amount, Integer term);

    Mono<Void> sendApplicationStatusWithPaymentPlan(Long applicationId, String userEmail,
                                                    String userName, String status,
                                                    BigDecimal amount, Integer term,
                                                    List<PaymentDetail> paymentPlan);
}