package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.domain.model.GameId;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import reactor.core.publisher.Mono;

public final class DeleteGameUseCase {

    private final GameRepositoryPort gameRepo;

    public DeleteGameUseCase(GameRepositoryPort gameRepo) {
        this.gameRepo = gameRepo;
    }

    public Mono<Void> execute(String gameId) {
        return gameRepo.deleteById(new GameId(gameId));
    }
}
