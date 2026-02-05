package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mongo;

import cat.itacademy.s05.t01.blackjack_api.domain.model.*;

import java.util.List;

public final class MongoGameMapper {

    private MongoGameMapper() {}

    public static MongoGameDocument toDocument(Game game) {
        return new MongoGameDocument(
                game.id().value(),
                game.playerId().value(),
                toCardDocs(game.playerHand().cards()),
                toCardDocs(game.dealerHand().cards()),
                toCardDocs(game.deck().cards()),
                game.status().name()
        );
    }

    public static Game toDomain(MongoGameDocument doc) {
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
                .map(card -> new CardDoc(card.rank().name(), card.suit().name()))
                .toList();
    }

    private static List<Card> fromCardDocs(List<CardDoc> docs) {
        return docs.stream()
                .map(d -> new Card(Rank.valueOf(d.getRank()), Suit.valueOf(d.getSuit())))
                .toList();
    }


}
