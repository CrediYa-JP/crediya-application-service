package co.com.crediya.app.api.dto.response;

import java.time.LocalDateTime;

public record UpdateApplicationStatusResponse(
        Long applicationId,
        String status,
        LocalDateTime updatedAt
) {}