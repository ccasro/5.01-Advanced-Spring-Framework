package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record PlayMoveRequest(@NotBlank String action) {
}
