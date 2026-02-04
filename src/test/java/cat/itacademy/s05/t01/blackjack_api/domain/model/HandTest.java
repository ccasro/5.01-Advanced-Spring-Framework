package cat.itacademy.s05.t01.blackjack_api.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HandTest {

    @Test
    void empty_hand_has_score_0(){
        Hand h = Hand.empty();
        assertEquals(0,h.score());
    }

    @Test
    void ace_can_be_1_or_11(){
        Hand h = Hand.empty()
                .add(new Card(Rank.ACE, Suit.SPADES))
                .add(new Card(Rank.KING, Suit.CLUBS));

        assertEquals(21, h.score());
        assertTrue(h.isBlackjack());

        Hand h2 = Hand.empty()
                .add(new Card(Rank.ACE, Suit.SPADES))
                .add(new Card(Rank.ACE, Suit.HEARTS))
                .add(new Card(Rank.NINE, Suit.DIAMONDS));

        assertEquals(21, h.score());
        assertFalse(h2.isBust());
    }

    @Test
    void hand_is_blackjack_with_21_and_two_cards(){
        Hand h = Hand.empty()
                .add(new Card(Rank.ACE, Suit.SPADES))
                .add(new Card(Rank.KING, Suit.CLUBS));

        assertTrue(h.isBlackjack());

        Hand h2 = Hand.empty()
                .add(new Card(Rank.ACE, Suit.SPADES))
                .add(new Card(Rank.ACE, Suit.HEARTS))
                .add(new Card(Rank.NINE, Suit.DIAMONDS));

        assertFalse(h2.isBlackjack());
    }

    @Test
    void hand_is_bust_over_21(){
        Hand h = Hand.empty()
                .add(new Card(Rank.EIGHT, Suit.SPADES))
                .add(new Card(Rank.KING, Suit.CLUBS))
                .add(new Card(Rank.KING, Suit.HEARTS));

        assertEquals(28, h.score());
        assertTrue(h.isBust());
    }

    @Test
    void multiple_aces_are_adjusted(){
        Hand h = Hand.empty()
                .add(new Card(Rank.ACE, Suit.SPADES))
                .add(new Card(Rank.ACE, Suit.CLUBS))
                .add(new Card(Rank.ACE, Suit.HEARTS))
                .add(new Card(Rank.NINE, Suit.CLUBS));

        assertEquals(12, h.score());
        assertFalse(h.isBust());
    }
}
