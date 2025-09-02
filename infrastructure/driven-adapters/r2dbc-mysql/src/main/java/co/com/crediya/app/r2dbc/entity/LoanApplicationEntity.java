package co.com.crediya.app.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("loan_applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationEntity {

    @Id
    @Column("application_id")
    private Long applicationId;

    @Column("amount")
    private BigDecimal amount;

    @Column("term")
    private Integer term;

    @Column("state_id")
    private Long stateId;

    @Column("loan_type_id")
    private Long loanTypeId;

    @Column("user_identity_document")
    private String userIdentityDocument;

    @Column("creation_date")
    private LocalDateTime creationDate;

    @Column("last_modification_date")
    private LocalDateTime lastModificationDate;
}