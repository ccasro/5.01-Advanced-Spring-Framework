package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.error;


import cat.itacademy.s05.t01.blackjack_api.domain.exception.GameNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.InvalidMoveException;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.InvalidPlayerNameException;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.PlayerNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestControllerAdvice
@Order(-2)
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGameNotFound(
            GameNotFoundException ex,
            ServerHttpRequest request
    ) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(404, "GAME_NOT_FOUND", ex.getMessage(), request.getPath().value())));
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handlePlayerNotFound(
            PlayerNotFoundException ex,
            ServerHttpRequest request
    ) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        404, "PLAYER_NOT_FOUND", ex.getMessage(), request.getPath().value()
                )));
    }

    @ExceptionHandler(InvalidPlayerNameException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidPlayerName(
            InvalidPlayerNameException ex,
            ServerHttpRequest request
    ) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "INVALID_PLAYER_NAME", ex.getMessage(), request.getPath().value())));
    }

    @ExceptionHandler(InvalidMoveException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidMove(
            InvalidMoveException ex,
            ServerHttpRequest request
    ) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(409, "INVALID_MOVE", ex.getMessage(), request.getPath().value())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebFluxValidation(
            WebExchangeBindException ex,
            ServerHttpRequest request
    ) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));

        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "VALIDATION_ERROR", msg, request.getPath().value())));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleConstraintViolation(
            ConstraintViolationException ex,
            ServerHttpRequest request
    ) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "VALIDATION_ERROR", ex.getMessage(), request.getPath().value())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgument(
            IllegalArgumentException ex,
            ServerHttpRequest request
    ) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "BAD_REQUEST", ex.getMessage(), request.getPath().value())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(
            Exception ex,
            ServerHttpRequest request
    ) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "INTERNAL_ERROR", "Unexpected error", request.getPath().value())));
    }

    private String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }
}
