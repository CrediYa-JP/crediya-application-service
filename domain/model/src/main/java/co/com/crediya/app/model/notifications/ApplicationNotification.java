package co.com.crediya.app.model.notifications;

import co.com.crediya.app.model.capacityevaluation.PaymentDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationNotification {
    private Long applicationId;
    private String userEmail;
    private String userName;
    private String status;
    private BigDecimal amount;
    private Integer term;
    private List<PaymentDetail> paymentPlan;
    private String timestamp;
}