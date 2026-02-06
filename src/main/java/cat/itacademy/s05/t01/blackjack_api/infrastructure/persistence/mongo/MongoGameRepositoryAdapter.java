package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mongo;

import cat.itacademy.s05.t01.blackjack_api.domain.exception.GameNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.*;
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
        return repo.findById(id.value())
                .switchIfEmpty(Mono.error(new GameNotFoundException(id.value())))
                .flatMap(doc -> repo.deleteById(id.value()));
    }
}
