CREATE TABLE loan_types
(
    loan_type_id         BIGINT         NOT NULL AUTO_INCREMENT,
    name                 VARCHAR(100)   NOT NULL UNIQUE,
    minimum_amount       DECIMAL(15, 2) NOT NULL,
    maximum_amount       DECIMAL(15, 2) NOT NULL,
    interest_rate        DECIMAL(5, 4)  NOT NULL,
    automatic_validation BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (loan_type_id)
);

INSERT INTO loan_types (name, minimum_amount, maximum_amount, interest_rate, automatic_validation)
VALUES ('Personal', 1000000.00, 50000000.00, 0.0250, FALSE),
       ('Mortgage', 20000000.00, 500000000.00, 0.0180, TRUE),
       ('Vehicle', 5000000.00, 100000000.00, 0.0220, FALSE);