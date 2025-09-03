package co.com.crediya.app.model.loanapplication;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    private Long applicationId;
    private BigDecimal amount;
    private Integer term;
    private Long stateId;
    private Long loanTypeId;

    private String userIdentityDocument;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;
}