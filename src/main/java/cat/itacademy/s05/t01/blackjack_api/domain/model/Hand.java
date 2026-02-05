package cat.itacademy.s05.t01.blackjack_api.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private final List<Card> cards;

    private Hand(List<Card> cards) {
        this.cards = List.copyOf(cards);
    }

    public static Hand empty() { return new Hand(List.of()); }

    public static Hand fromCards(List<Card> cards) {
        return new Hand(cards);
    }

    public Hand add(Card card) {
        var next = new ArrayList<>(cards);
        next.add(card);
        return new Hand(next);
    }

    public List<Card> cards() { return List.copyOf(cards); }

    public int score() {
        int total = 0;
        int aces = 0;

        for (Card c: cards) {
            total += c.rank().getDefaultValue();
            if (c.rank().isAce()) aces++;
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    public boolean isBlackjack() { return cards.size() == 2 && score() == 21; }
    public boolean isBust() { return score() > 21; }
}
