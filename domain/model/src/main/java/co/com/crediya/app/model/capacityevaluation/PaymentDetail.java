package co.com.crediya.app.model.capacityevaluation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetail {
    private Integer month;
    private BigDecimal totalPayment;
    private BigDecimal principalPayment;
    private BigDecimal interestPayment;
    private BigDecimal remainingBalance;
}