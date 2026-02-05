package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller;


import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameCommand;
import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameResult;
import cat.itacademy.s05.t01.blackjack_api.application.dto.GameStateResult;
import cat.itacademy.s05.t01.blackjack_api.application.dto.PlayMoveCommand;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.CreateNewGameUseCase;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.GetGameStateUseCase;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.PlayMoveUseCase;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.CreateGameRequest;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.PlayMoveRequest;
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
    private final GetGameStateUseCase getGameState;
    private final PlayMoveUseCase playMove;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreateGameResult> create(@Valid @RequestBody CreateGameRequest request) {
        return createNewGame.execute(new CreateGameCommand(request.playerName()));
    }

    @GetMapping("/{id}")
    public Mono<GameStateResult> get(@PathVariable String id) {
        return getGameState.execute(id);
    }

    @PostMapping("/{id}/play")
    public Mono<GameStateResult> play(@PathVariable String id, @Valid @RequestBody PlayMoveRequest request){
        return playMove.execute(new PlayMoveCommand(id, request.action()));
    }
}
