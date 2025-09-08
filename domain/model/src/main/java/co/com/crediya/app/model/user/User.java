package co.com.crediya.app.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String email;
    private String firstName;
    private String lastName;
    private String identityDocument;
    private BigDecimal baseSalary;
    public String getFullName() {
        return firstName + " " + lastName;
    }
}