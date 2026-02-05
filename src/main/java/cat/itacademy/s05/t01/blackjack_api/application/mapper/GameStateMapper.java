package cat.itacademy.s05.t01.blackjack_api.application.mapper;

import cat.itacademy.s05.t01.blackjack_api.application.dto.CardDto;
import cat.itacademy.s05.t01.blackjack_api.application.dto.GameStateResult;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Card;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Game;
import cat.itacademy.s05.t01.blackjack_api.domain.model.GameStatus;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Hand;

import java.util.ArrayList;
import java.util.List;

public class GameStateMapper {

    public GameStateResult toResultForPlayer(Game game) {
        boolean hideDealer = game.status() == GameStatus.IN_PROGRESS;

        var playerCards = toVisibleCards(game.playerHand().cards());
        var dealerCards = dealerCardsForPlayerView(game.dealerHand().cards(), hideDealer);

        int dealerScore = hideDealer
                ? scoreFirstDealerCard(game.dealerHand())
                : game.dealerHand().score();

        return new GameStateResult(
                game.id().value(),
                game.playerId().value(),
                game.status().name(),
                game.playerHand().score(),
                dealerScore,
                playerCards,
                dealerCards
        );
    }

    private List<CardDto> toVisibleCards(List<Card> cards) {
        return cards.stream()
                .map(CardDto::visible)
                .toList();
    }

    private List<CardDto> dealerCardsForPlayerView(List<Card> cards, boolean hideDealer) {
        if (!hideDealer) {
            return toVisibleCards(cards);
        }

        if (cards.isEmpty()) return List.of();

        var result = new ArrayList<CardDto>(cards.size());
        result.add(CardDto.visible(cards.get(0)));
        for (int i = 1; i < cards.size(); i++) {
            result.add(CardDto.hiddenCard());
        }
        return result;
    }

    private int scoreFirstDealerCard(Hand dealerHand) {
        var cards = dealerHand.cards();
        if (cards.isEmpty()) return 0;
        return Hand.empty().add(cards.get(0)).score();
    }
}
