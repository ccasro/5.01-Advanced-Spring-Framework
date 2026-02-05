package cat.itacademy.s05.t01.blackjack_api.domain.exception;

public class InvalidPlayerNameException extends RuntimeException {
    public InvalidPlayerNameException(String message) {
        super(message);
    }
}
