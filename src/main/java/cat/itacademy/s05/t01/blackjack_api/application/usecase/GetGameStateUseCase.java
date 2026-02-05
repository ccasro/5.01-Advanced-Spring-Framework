package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.application.dto.GameStateResult;
import cat.itacademy.s05.t01.blackjack_api.application.mapper.GameStateMapper;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.GameNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.GameId;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import reactor.core.publisher.Mono;

public final class GetGameStateUseCase {

    private final GameRepositoryPort gameRepo;
    private final GameStateMapper mapper;

    public GetGameStateUseCase(GameRepositoryPort gameRepo, GameStateMapper mapper) {
        this.gameRepo = gameRepo;
        this.mapper = mapper;
    }

    public Mono<GameStateResult> execute(String gameId) {
        return gameRepo.findById(new GameId(gameId))
                .switchIfEmpty(Mono.error(new GameNotFoundException(gameId)))
                .map(mapper::toResultForPlayer);
    }
}
