package cat.itacademy.s05.t01.blackjack_api.domain.exception;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String playerId) {
        super("Player not found: " + playerId);
    }
}
