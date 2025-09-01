package co.com.crediya.app.model.state.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum LoanApplicationState {
    PENDING_REVIEW(1L), APPROVED(2L);

    private final long id;
    LoanApplicationState(long id) { this.id = id; }
    public long getId() { return id; }

    private static final Map<Long, LoanApplicationState> BY_ID =
            Arrays.stream(values()).collect(Collectors.toMap(LoanApplicationState::getId, Function.identity()));

    public static LoanApplicationState fromId(long id) {
        var s = BY_ID.get(id);
        if (s == null) throw new IllegalArgumentException("Invalid state id: " + id);
        return s;
    }
}
