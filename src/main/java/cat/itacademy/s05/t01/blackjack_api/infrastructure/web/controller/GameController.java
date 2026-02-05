package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller;


import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameCommand;
import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameResult;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.CreateNewGameUseCase;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.CreateGameRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game")
@AllArgsConstructor
public class GameController {

    private final CreateNewGameUseCase createNewGame;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreateGameResult> create(@Valid @RequestBody CreateGameRequest request) {
        return createNewGame.execute(new CreateGameCommand(request.playerName()));
    }
}
