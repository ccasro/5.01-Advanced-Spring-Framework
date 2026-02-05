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
        MongoGameDocument doc = new MongoGameDocument(
                game.id().value(),
                game.playerId().value(),
                toCardDocs(game.playerHand().cards()),
                toCardDocs(game.dealerHand().cards()),
                toCardDocs(game.deck().cards()),
                game.status().name()
        );

        return repo.save(doc).map(saved -> game);
    }

    @Override
    public Mono<Game> findById(GameId id) {
        return repo.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(GameId id) {
        return Mono.error(new UnsupportedOperationException("Not implemented yet"));
    }


    private Game toDomain(MongoGameDocument doc) {
        GameId gameId = new GameId(doc.getId());
        PlayerId playerId = new PlayerId(doc.getPlayerId());

        Hand playerHand = Hand.fromCards(fromCardDocs(doc.getPlayerCards()));
        Hand dealerHand = Hand.fromCards(fromCardDocs(doc.getDealerCards()));
        Deck deck = Deck.fromCards(fromCardDocs(doc.getDeckCards()));

        GameStatus status = GameStatus.valueOf(doc.getStatus());

        return Game.rehydrate(gameId, playerId, deck, playerHand, dealerHand, status );
    }

    private static List<CardDoc> toCardDocs(List<Card> cards) {
        return cards.stream()
                .map(card -> new CardDoc(card.rank().name(),card.suit().name()))
                .toList();
    }


    private static List<Card> fromCardDocs(List<CardDoc> docs) {
        return docs.stream()
                .map(d -> new Card(Rank.valueOf(d.getRank()), Suit.valueOf(d.getSuit())))
                .toList();
    }
}
