package co.com.crediya.app.model.capacityevaluation;

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
public class CapacityEvaluationResponse {
    private Long applicationId;
    private String decision;
    private BigDecimal maxCapacity;
    private BigDecimal availableCapacity;
    private BigDecimal newLoanPayment;
    private List<PaymentDetail> paymentPlan;
    private String evaluationTimestamp;
}