package co.com.crediya.app.model.notifications.gateways;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface NotificationGateway {
    Mono<Void> sendApplicationStatusNotification(Long applicationId, String userEmail,
                                                 String userName, String status,
                                                 BigDecimal amount, Integer term);
}