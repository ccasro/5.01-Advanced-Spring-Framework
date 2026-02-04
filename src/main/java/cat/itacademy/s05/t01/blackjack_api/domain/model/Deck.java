package cat.itacademy.s05.t01.blackjack_api.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Deck {

    private final List<Card> cards;

    private Deck(List<Card> cards){
        this.cards = List.copyOf(cards);
    }

    public static Deck standardShuffled(){
        List<Card> all = new ArrayList<>();

        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                all.add(new Card(r, s));
            }
        }
        Collections.shuffle(all);

        return new Deck(all);
    }

    public DrawResult draw(){
        if (cards.isEmpty()) throw new IllegalStateException("Deck is empty");
        Card top = cards.get(0);
        List<Card> rest = cards.subList(1, cards.size());

        return new DrawResult(top, new Deck(rest));
    }

    public static Deck of(List<Card> cards) {
        if (cards == null || cards.isEmpty())
            throw new IllegalArgumentException("Deck cannot be empty");
        return new Deck(cards);
    }

    public record DrawResult(Card card, Deck nextDeck) {}
}
