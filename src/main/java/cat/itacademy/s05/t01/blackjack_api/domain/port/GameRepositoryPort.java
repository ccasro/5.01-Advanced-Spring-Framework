package cat.itacademy.s05.t01.blackjack_api.domain.port;

import cat.itacademy.s05.t01.blackjack_api.domain.model.Game;
import cat.itacademy.s05.t01.blackjack_api.domain.model.GameId;
import reactor.core.publisher.Mono;

public interface GameRepositoryPort {
    Mono<Game> save(Game game);
    Mono<Game> findById(GameId id);
    Mono<Void> deleteById(GameId id);
}
