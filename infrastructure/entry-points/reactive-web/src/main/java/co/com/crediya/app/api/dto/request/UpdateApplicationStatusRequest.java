package co.com.crediya.app.api.dto.request;

import co.com.crediya.app.model.state.enums.LoanApplicationState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(APPROVED|REJECTED)$", message = "Status must be APPROVED or REJECTED")
    private String status;

    public LoanApplicationState getLoanApplicationState() {
        return switch (this.status) {
            case "APPROVED" -> LoanApplicationState.APPROVED;
            case "REJECTED" -> LoanApplicationState.REJECTED;
            default -> throw new IllegalArgumentException("Invalid status: " + this.status);
        };
    }
}