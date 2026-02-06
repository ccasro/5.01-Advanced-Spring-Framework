package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mysql;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataR2dbcPlayerRepository extends ReactiveCrudRepository<PlayerRow, Long> {
    Mono<PlayerRow> findByName(String name);
    Mono<PlayerRow> findByExternalId(String externalId);

    @Query("""
    SELECT *
    FROM players
    ORDER BY (wins - losses) DESC, wins DESC, losses ASC, name ASC
    """)
    Flux<PlayerRow> findRanking();
}
