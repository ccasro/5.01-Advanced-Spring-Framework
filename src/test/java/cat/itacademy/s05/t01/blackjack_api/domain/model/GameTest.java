package cat.itacademy.s05.t01.blackjack_api.domain.model;

import cat.itacademy.s05.t01.blackjack_api.domain.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private static Deck deck(Card... cards) {
        return Deck.of(List.of(cards));
    }

    @Test
    void new_game_starts_in_progress_or_finishes_on_naturals() {
        Deck d = deck(
                new Card(Rank.TWO, Suit.CLUBS),
                new Card(Rank.THREE, Suit.HEARTS),
                new Card(Rank.FOUR, Suit.SPADES),
                new Card(Rank.FIVE, Suit.DIAMONDS)
        );

        Game g = Game.newGame(PlayerId.newId(), d);

        assertNotNull(g.id());
        assertEquals(2, g.playerHand().cards().size());
        assertEquals(2,g.dealerHand().cards().size());
        assertNotNull(g.status());
    }

    @Test
    void player_natural_blackjack_wins(){
        Deck d = deck(
                new Card(Rank.ACE, Suit.CLUBS),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.KING, Suit.CLUBS),
                new Card(Rank.SEVEN, Suit.DIAMONDS)
        );

        Game g = Game.newGame(PlayerId.newId(), d);

        assertEquals(GameStatus.PLAYER_WINS, g.status());
    }

    @Test
    void both_natural_blackjack_is_push() {
        Deck d = deck(
                new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.KING, Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.DIAMONDS)
        );

        Game g = Game.newGame(PlayerId.newId(), d);

        assertEquals(GameStatus.PUSH, g.status());
    }

    @Test
    void player_bust_finishes_game_as_dealer_wins() {
        Deck d = deck(
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.NINE, Suit.SPADES),
                new Card(Rank.THREE, Suit.DIAMONDS),
                new Card(Rank.FIVE, Suit.CLUBS)
        );

        Game g = Game.newGame(PlayerId.newId(), d);

        assertEquals(GameStatus.IN_PROGRESS, g.status());

        g = g.hit();

        assertEquals(GameStatus.DEALER_WINS, g.status());
    }

    @Test
    void dealer_bust_finishes_game_as_player_wins() {
        Deck d = deck(
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.NINE, Suit.SPADES),
                new Card(Rank.EIGHT, Suit.DIAMONDS),
                new Card(Rank.NINE, Suit.CLUBS)
        );

        Game g = Game.newGame(PlayerId.newId(), d);

        assertEquals(GameStatus.IN_PROGRESS, g.status());

        g = g.stand();

        assertEquals(GameStatus.PLAYER_WINS, g.status());
    }

    @Test
    void dealer_hits_until_17_or_more() {
        Deck d = deck(
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.TWO, Suit.CLUBS)
        );

        Game g = Game.newGame(PlayerId.newId(), d);
        assertEquals(GameStatus.IN_PROGRESS, g.status());

        g = g.stand();

        assertTrue(g.dealerHand().score() >= 17);
        assertEquals(3, g.dealerHand().cards().size()); // robó 1 carta
    }

    @Test
    void dealer_stands_on_17() {
        Deck d = deck(
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.FIVE, Suit.CLUBS)
        );

        Game g = Game.newGame(PlayerId.newId(), d);

        g = g.stand();

        assertEquals(17, g.dealerHand().score());
        assertEquals(2, g.dealerHand().cards().size());
    }

    @Test
    void cannot_hit_when_game_finished() {
        Deck d = deck(
                new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.KING, Suit.CLUBS),
                new Card(Rank.SEVEN, Suit.DIAMONDS)
        );

        Game g = Game.newGame(PlayerId.newId(), d);
        assertNotEquals(GameStatus.IN_PROGRESS, g.status());

        assertThrows(InvalidMoveException.class, g::hit);
    }
}
