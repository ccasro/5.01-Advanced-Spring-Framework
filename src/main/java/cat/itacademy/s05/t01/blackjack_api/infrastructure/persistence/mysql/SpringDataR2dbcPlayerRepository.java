package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mysql;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SpringDataR2dbcPlayerRepository extends ReactiveCrudRepository<PlayerRow, Long> {
    Mono<PlayerRow> findByName(String name);
}
