package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mysql;

import cat.itacademy.s05.t01.blackjack_api.domain.model.Player;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerId;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerName;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MySqlPlayerRepositoryAdapter implements PlayerRepositoryPort {

    private final SpringDataR2dbcPlayerRepository repo;

    public MySqlPlayerRepositoryAdapter(SpringDataR2dbcPlayerRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Player> save(Player player) {
        PlayerRow row = new PlayerRow(
                null,
                player.id().value(),
                player.name().value(),
                player.wins(),
                player.losses()
        );
        return repo.save(row).map(this::toDomain);
    }

    @Override
    public Mono<Player> findById(PlayerId id) {
        return Mono.error(new UnsupportedOperationException("Not implemented yet"));
    }

    @Override
    public Mono<Player> findOrCreateByName(PlayerName name) {
        return repo.findByName(name.value())
                .map(this::toDomain)
                .switchIfEmpty(Mono.defer(() -> {
                    Player newPlayer = Player.newPlayer(name);
                    return save(newPlayer);
                }));
    }

    @Override
    public Flux<Player> findRanking() {
        return Flux.error(new UnsupportedOperationException("Not implemented yet"));
    }

    private Player toDomain(PlayerRow row) {
        return new Player(
                new PlayerId(row.getExternalId()),
                new PlayerName(row.getName()),
                row.getWins(),
                row.getLosses()
        );
    }
}
