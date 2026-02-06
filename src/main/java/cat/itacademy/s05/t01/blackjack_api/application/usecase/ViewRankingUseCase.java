package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.application.dto.RankingEntryResult;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Player;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import reactor.core.publisher.Flux;

public final class ViewRankingUseCase {

    private final PlayerRepositoryPort playerRepo;

    public ViewRankingUseCase(PlayerRepositoryPort playerRepo) {
        this.playerRepo = playerRepo;
    }

    public Flux<RankingEntryResult> execute() {
        return playerRepo.findRanking()
                .index()
                .map(tuple -> toEntry(tuple.getT1(), tuple.getT2()));
    }

    private RankingEntryResult toEntry(long idx, Player p) {
        return new RankingEntryResult(
                (int) idx + 1,
                p.id().value(),
                p.name().value(),
                p.wins(),
                p.losses(),
                p.score()
        );
    }
}
