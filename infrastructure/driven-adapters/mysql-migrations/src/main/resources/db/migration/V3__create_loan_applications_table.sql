CREATE TABLE loan_applications
(
    application_id          BIGINT         NOT NULL AUTO_INCREMENT,
    amount                  DECIMAL(15, 2) NOT NULL,
    term                    INT            NOT NULL,
    state_id                BIGINT         NOT NULL,
    loan_type_id            BIGINT         NOT NULL,

    user_email              VARCHAR(150)   NOT NULL,
    user_name               VARCHAR(200)   NOT NULL,
    user_salary             DECIMAL(15, 2) NOT NULL,
    user_data_snapshot_date DATETIME       NOT NULL,

    creation_date           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modification_date  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (application_id),
    FOREIGN KEY (state_id) REFERENCES states (state_id),
    FOREIGN KEY (loan_type_id) REFERENCES loan_types (loan_type_id),
    INDEX                   idx_user_email (user_email),
    INDEX                   idx_state_id (state_id),
    INDEX                   idx_loan_type_id (loan_type_id)
);