package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface SpringDataMongoGameRepository extends ReactiveMongoRepository<MongoGameDocument, String> {
}
