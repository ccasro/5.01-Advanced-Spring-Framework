package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.application.mapper.GameStateMapper;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.GameNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.*;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GetGameStateUseCaseTest {

    private GameRepositoryPort gameRepo;
    private GetGameStateUseCase useCase;

    @BeforeEach
    void setUp() {
        gameRepo = Mockito.mock(GameRepositoryPort.class);
        useCase = new GetGameStateUseCase(gameRepo, new GameStateMapper());
    }

    @Test
    void should_hide_dealer_second_card_when_game_in_progress() {
        GameId gid = new GameId("g1");
        PlayerId pid = new PlayerId("p1");

        Deck deck = Deck.of(List.of(
                new Card(Rank.TWO, Suit.CLUBS)
        ));

        Hand player = Hand.empty()
                .add(new Card(Rank.TEN, Suit.HEARTS))
                .add(new Card(Rank.TWO, Suit.SPADES));

        Hand dealer = Hand.empty()
                .add(new Card(Rank.TEN, Suit.HEARTS))
                .add(new Card(Rank.ACE, Suit.SPADES));

        Game game = Game.rehydrate(gid, pid, deck, player, dealer, GameStatus.IN_PROGRESS);

        when(gameRepo.findById(gid)).thenReturn(Mono.just(game));

        StepVerifier.create(useCase.execute("g1"))
                .assertNext(res -> {
                    assertEquals("g1", res.gameId());
                    assertEquals("p1", res.playerId());
                    assertEquals("IN_PROGRESS", res.status());

                    assertEquals(12, res.playerScore());
                    assertEquals(10, res.dealerScore());

                    assertEquals(2, res.playerCards().size());
                    assertEquals(2, res.dealerCards().size());

                    var up = res.dealerCards().get(0);
                    assertFalse(up.hidden());
                    assertEquals("TEN", up.rank());
                    assertEquals("HEARTS", up.suit());

                    var hidden = res.dealerCards().get(1);
                    assertTrue(hidden.hidden());
                    assertNull(hidden.rank());
                    assertNull(hidden.suit());
                })
                .verifyComplete();
    }

    @Test
    void should_show_dealer_second_card_when_game_not_in_progress() {
        GameId gid = new GameId("g1");
        PlayerId pid = new PlayerId("p1");

        Deck deck = Deck.of(List.of(new Card(Rank.TWO, Suit.CLUBS)));

        Hand player = Hand.empty()
                .add(new Card(Rank.TEN, Suit.HEARTS))
                .add(new Card(Rank.TWO, Suit.SPADES));

        Hand dealer = Hand.empty()
                .add(new Card(Rank.TEN, Suit.CLUBS))
                .add(new Card(Rank.ACE, Suit.SPADES));

        Game game = Game.rehydrate(gid, pid, deck, player, dealer, GameStatus.DEALER_WINS);

        when(gameRepo.findById(gid)).thenReturn(Mono.just(game));

        StepVerifier.create(useCase.execute("g1"))
                .assertNext(res -> {
                    assertEquals("DEALER_WINS", res.status());
                    assertEquals(2, res.dealerCards().size());

                    assertFalse(res.dealerCards().get(0).hidden());
                    assertFalse(res.dealerCards().get(1).hidden());

                    assertEquals("ACE", res.dealerCards().get(1).rank());
                    assertEquals("SPADES", res.dealerCards().get(1).suit());

                    assertTrue(dealer.isBlackjack());

                    assertEquals(21, res.dealerScore());
                })
                .verifyComplete();
    }

    @Test
    void should_throw_when_game_not_found() {
        GameId gid = new GameId("missing");
        when(gameRepo.findById(gid)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("missing"))
                .expectError(GameNotFoundException.class)
                .verify();
    }
}
