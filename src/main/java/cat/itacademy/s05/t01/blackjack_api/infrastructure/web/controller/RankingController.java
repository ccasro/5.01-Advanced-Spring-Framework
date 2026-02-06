package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller;

import cat.itacademy.s05.t01.blackjack_api.application.dto.RankingEntryResult;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.ViewRankingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "Ranking", description = "Ranking operations")
@RestController
@AllArgsConstructor
public class RankingController {

    private final ViewRankingUseCase viewRanking;

    @Operation(
            summary = "Get players ranking",
            description = "Returns the ranking of players ordered by score (and tie-breakers)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking returned")
    })
    @GetMapping("/ranking")
    public Flux<RankingEntryResult> getRanking() {
        return viewRanking.execute();
    }
}
