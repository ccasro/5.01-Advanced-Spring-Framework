package cat.itacademy.s05.t01.blackjack_api.application.dto;

import cat.itacademy.s05.t01.blackjack_api.domain.model.Card;

public record CardDto(String rank, String suit, boolean hidden) {
    public static CardDto visible(Card c) {
        return new CardDto(c.rank().name(), c.suit().name(), false);
    }

    public static CardDto hiddenCard() {
        return new CardDto(null, null, true);
    }
}
