package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mongo;

import cat.itacademy.s05.t01.blackjack_api.domain.model.Game;
import cat.itacademy.s05.t01.blackjack_api.domain.model.GameId;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MongoGameRepositoryAdapter implements GameRepositoryPort {

    private final SpringDataMongoGameRepository repo;

    public MongoGameRepositoryAdapter(SpringDataMongoGameRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Game> save(Game game) {
        MongoGameDocument doc = new MongoGameDocument(
                game.id().value(),
                game.playerId().value(),
                game.playerHand().cards().stream()
                        .map(c -> new CardDoc(c.rank().name(), c.suit().name()))
                        .toList(),
                game.dealerHand().cards().stream()
                        .map(c -> new CardDoc(c.rank().name(), c.suit().name()))
                        .toList(),
                game.status().name()
        );

        return repo.save(doc).map(saved -> game);
    }

    @Override
    public Mono<Game> findById(GameId id) {
        return Mono.error(new UnsupportedOperationException("Not implemented yet"));
    }

    @Override
    public Mono<Void> deleteById(GameId id) {
        return Mono.error(new UnsupportedOperationException("Not implemented yet"));
    }
}
