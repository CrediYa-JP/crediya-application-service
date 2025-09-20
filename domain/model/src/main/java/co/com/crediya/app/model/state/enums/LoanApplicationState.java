package co.com.crediya.app.model.state.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum LoanApplicationState {
    PENDING_REVIEW(1L), APPROVED(2L), REJECTED(3L), MANUAL_REVIEW(4L),;

    private final long id;
    LoanApplicationState(long id) { this.id = id; }

    private static final Map<Long, LoanApplicationState> BY_ID =
            Arrays.stream(values()).collect(Collectors.toMap(LoanApplicationState::getId, Function.identity()));

    public static LoanApplicationState fromId(long id) {
        var s = BY_ID.get(id);
        if (s == null) throw new IllegalArgumentException("Invalid state id: " + id);
        return s;
    }
}
