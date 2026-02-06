package cat.itacademy.s05.t01.blackjack_api.application.dto;

public record ChangePlayerNameCommand(String playerId, String newName) {
}
