package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller;


import cat.itacademy.s05.t01.blackjack_api.application.dto.ChangePlayerNameCommand;
import cat.itacademy.s05.t01.blackjack_api.application.dto.PlayerResult;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.ChangePlayerNameUseCase;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.ChangePlayerNameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Player", description = "Player operations")
@RestController
@RequestMapping("/player")
@AllArgsConstructor
public class PlayerController {

    private final ChangePlayerNameUseCase changeName;

    @Operation(
            summary = "Change player name",
            description = "Changes the name of an existing player. The new name must be valid and follow validation constraints."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Player updated"),
            @ApiResponse(responseCode = "400", description = "Validation error (invalid name)"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @PutMapping("/{playerId}")
    public Mono<PlayerResult> changePlayerName(@PathVariable String playerId,
                                               @Valid @RequestBody ChangePlayerNameRequest request) {
        return changeName.execute(new ChangePlayerNameCommand(playerId, request.newName()));
    }
}
