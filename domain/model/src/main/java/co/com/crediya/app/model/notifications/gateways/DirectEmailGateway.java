package co.com.crediya.app.model.notifications.gateways;

import co.com.crediya.app.model.capacityevaluation.PaymentDetail;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.List;

public interface DirectEmailGateway {
    Mono<Void> sendLoanDecisionWithPaymentPlan(
            String email,
            String userName,
            String decision,
            BigDecimal amount,
            Integer term,
            List<PaymentDetail> paymentPlan
    );
}