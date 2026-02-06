package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePlayerNameRequest(@NotBlank(message = "Player name must not be blank") @Size(max = 30, message = "Player name length must be <= 30") String newName) {
}
