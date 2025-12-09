package co.com.crediya.app.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApprovalEvent {
    private Long applicationId;
    private BigDecimal amount;
    private String decision;
    private LocalDateTime timestamp;
    private String userEmail;
}