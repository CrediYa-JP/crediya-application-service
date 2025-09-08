package co.com.crediya.app.model.loanapplication.constants;

import java.util.Arrays;

public enum ApplicationState {
    PENDING_REVIEW(1L, "Pending Review"),
    APPROVED(2L, "Approved"),
    REJECTED(3L, "Rejected"),
    MANUAL_REVIEW(4L, "Manual Review");

    private final Long id;
    private final String name;

    ApplicationState(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String getNameById(Long id) {
        return Arrays.stream(values())
                .filter(state -> state.id.equals(id))
                .map(state -> state.name)
                .findFirst()
                .orElse("Unknown");
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}