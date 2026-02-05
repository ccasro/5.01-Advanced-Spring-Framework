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
                game.status().name()
        );
    }

    private static List<CardDoc> toCardDocs(List<Card> cards) {
        return cards.stream()
                .map(card -> new CardDoc(card.rank().name(), card.suit().name()))
                .toList();
    }
}
