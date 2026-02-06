package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller;


import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameCommand;
import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameResult;
import cat.itacademy.s05.t01.blackjack_api.application.dto.GameStateResult;
import cat.itacademy.s05.t01.blackjack_api.application.dto.PlayMoveCommand;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.CreateNewGameUseCase;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.DeleteGameUseCase;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.GetGameStateUseCase;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.PlayMoveUseCase;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.CreateGameRequest;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.PlayMoveRequest;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Game", description = "Blackjack game operations")
@RestController
@RequestMapping("/game")
@AllArgsConstructor
public class GameController {

    private final CreateNewGameUseCase createNewGame;
    private final GetGameStateUseCase getGameState;
    private final PlayMoveUseCase playMove;
    private final DeleteGameUseCase deleteGame;

    @Operation(summary = "Create a new Blackjack game")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Game created",  content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreateGameResult> create(@Valid @RequestBody CreateGameRequest request) {
        return createNewGame.execute(new CreateGameCommand(request.playerName()));
    }

    @Operation(
            summary = "Get game details",
            description = "Returns the current state of a Blackjack game. Dealer cards may be partially hidden while the game is in progress."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Game state returned"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @GetMapping("/{id}")
    public Mono<GameStateResult> get(@PathVariable String id) {
        return getGameState.execute(id);
    }

    @Operation(
            summary = "Play a move in an existing game",
            description = "Plays a move (HIT or STAND) in an existing game and returns the updated game state. If the game ends, player ranking stats are updated."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Move applied and game state returned"),
            @ApiResponse(responseCode = "400", description = "Validation error (e.g., missing action)"),
            @ApiResponse(responseCode = "404", description = "Game not found"),
            @ApiResponse(responseCode = "409", description = "Invalid move (game not in progress)")
    })
    @PostMapping("/{id}/play")
    public Mono<GameStateResult> play(@PathVariable String id, @Valid @RequestBody PlayMoveRequest request){
        return playMove.execute(new PlayMoveCommand(id, request.action()));
    }

    @Operation(
            summary = "Delete a game",
            description = "Deletes an existing Blackjack game by id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Game deleted"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return deleteGame.execute(id);
    }
}
