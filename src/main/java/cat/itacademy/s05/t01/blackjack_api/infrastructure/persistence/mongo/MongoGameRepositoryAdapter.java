package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mongo;

import cat.itacademy.s05.t01.blackjack_api.domain.model.*;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class MongoGameRepositoryAdapter implements GameRepositoryPort {

    private final SpringDataMongoGameRepository repo;

    public MongoGameRepositoryAdapter(SpringDataMongoGameRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Game> save(Game game) {
        MongoGameDocument doc = MongoGameMapper.toDocument(game);
        return repo.save(doc).thenReturn(game);
    }

    @Override
    public Mono<Game> findById(GameId id) {
        return repo.findById(id.value())
                .map(MongoGameMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(GameId id) {
        return Mono.error(new UnsupportedOperationException("Not implemented yet"));
    }
}
