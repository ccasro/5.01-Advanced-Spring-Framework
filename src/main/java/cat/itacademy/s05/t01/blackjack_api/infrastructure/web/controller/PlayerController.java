package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller;


import cat.itacademy.s05.t01.blackjack_api.application.dto.ChangePlayerNameCommand;
import cat.itacademy.s05.t01.blackjack_api.application.dto.PlayerResult;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.ChangePlayerNameUseCase;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.ChangePlayerNameRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/player")
@AllArgsConstructor
public class PlayerController {

    private final ChangePlayerNameUseCase changeName;

    @PutMapping("/{playerId}")
    public Mono<PlayerResult> changePlayerName(@PathVariable String playerId,
                                               @Valid @RequestBody ChangePlayerNameRequest request) {
        return changeName.execute(new ChangePlayerNameCommand(playerId, request.newName()));
    }
}
