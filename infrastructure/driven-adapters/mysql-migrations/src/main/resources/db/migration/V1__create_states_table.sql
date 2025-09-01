CREATE TABLE states
(
    state_id    BIGINT      NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    PRIMARY KEY (state_id)
);

INSERT INTO states (name, description)
VALUES ('PENDING_REVIEW', 'Application pending review'),
       ('APPROVED', 'Application approved'),
       ('REJECTED', 'Application rejected'),
       ('MANUAL_REVIEW', 'Application requires manual review');