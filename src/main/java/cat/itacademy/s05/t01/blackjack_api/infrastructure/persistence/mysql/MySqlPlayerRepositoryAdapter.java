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
        return repo.findByExternalId(player.id().value())
                .defaultIfEmpty(new PlayerRow(
                        null,
                        player.id().value(),
                        player.name().value(),
                        player.wins(),
                        player.losses()
                ))
                .flatMap(existing -> {
                    existing.setName(player.name().value());
                    existing.setWins(player.wins());
                    existing.setLosses(player.losses());
                    return repo.save(existing).map(this::toDomain);
                });
    }

    @Override
    public Mono<Player> findById(PlayerId id) {
        return repo.findByExternalId(id.value())
                .map(this::toDomain);
    }

    @Override
    public Mono<Player> findOrCreateByName(PlayerName name) {
        return repo.findByName(name.value())
                .map(this::toDomain)
                .switchIfEmpty(Mono.defer(() -> save(Player.newPlayer(name))));
    }

    @Override
    public Flux<Player> findRanking() {
        return repo.findRanking().map(this::toDomain);
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
