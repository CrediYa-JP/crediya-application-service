package co.com.crediya.app.api.dto.response;

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
public class LoanApplicationResponse {
    private Long applicationId;
    private BigDecimal amount;
    private Integer term;
    private String userEmail;
    private String userName;
    private String status;
    private LocalDateTime creationDate;
}