package cat.itacademy.s05.t01.blackjack_api.domain.port;


import cat.itacademy.s05.t01.blackjack_api.domain.model.Player;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerId;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerRepositoryPort {
    Mono<Player> save(Player player);
    Mono<Player> findById(PlayerId id);
    Mono<Player> findOrCreateByName(PlayerName name);
    Flux<Player> findRanking();
}
