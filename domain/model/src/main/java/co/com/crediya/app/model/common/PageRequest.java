package co.com.crediya.app.model.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageRequest {
    private final int page;
    private final int size;

    public static PageRequest of(int page, int size) {
        return PageRequest.builder()
                .page(page)
                .size(size)
                .build();
    }
}