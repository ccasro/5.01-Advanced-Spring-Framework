package cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller;

import cat.itacademy.s05.t01.blackjack_api.application.dto.RankingEntryResult;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.ViewRankingUseCase;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
public class RankingController {

    private final ViewRankingUseCase viewRanking;

    @GetMapping("/ranking")
    public Flux<RankingEntryResult> getRanking() {
        return viewRanking.execute();
    }
}
