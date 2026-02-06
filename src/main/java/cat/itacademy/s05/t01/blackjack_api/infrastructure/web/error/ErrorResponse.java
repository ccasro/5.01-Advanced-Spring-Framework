package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "ErrorResponse", description = "Standard error response")
public record ErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path
) {
    public static ErrorResponse of(int status, String code, String message, String path) {
        return new ErrorResponse(Instant.now(), status, code, message, path);
    }
}
