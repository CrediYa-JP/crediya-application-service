package co.com.crediya.app.model.state.enums;

public enum LoanType {
    PERSONAL(1L, "Personal Loan"),
    MORTGAGE(2L, "Mortgage Loan"),
    VEHICLE(3L, "Vehicle Loan");

    private final Long id;
    private final String description;

    LoanType(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static LoanType findById(Long id) {
        for (LoanType type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isValidId(Long id) {
        return findById(id) != null;
    }
}