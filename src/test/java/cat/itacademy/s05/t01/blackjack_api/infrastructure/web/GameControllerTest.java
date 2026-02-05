package cat.itacademy.s05.t01.blackjack_api.infrastructure.web;

import cat.itacademy.s05.t01.blackjack_api.application.dto.*;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.CreateNewGameUseCase;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.GetGameStateUseCase;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.PlayMoveUseCase;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.GameNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.InvalidMoveException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Card;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Rank;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Suit;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller.GameController;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.CreateGameRequest;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.PlayMoveRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = GameController.class)
public class GameControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    CreateNewGameUseCase createNewGameUseCase;

    @MockitoBean
    GetGameStateUseCase getGameStateUseCase;

    @MockitoBean
    PlayMoveUseCase playMoveUseCase;

    @Test
    void should_create_game_and_return_201() {
        when(createNewGameUseCase.execute(any(CreateGameCommand.class)))
                .thenReturn(Mono.just( new CreateGameResult("g1","p1","IN_PROGRESS")));

        webTestClient.post()
                .uri("/game/new")
                .bodyValue(new CreateGameRequest("Ccr"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.gameId").isEqualTo("g1")
                .jsonPath("$.playerId").isEqualTo("p1")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }

    @Test
    void should_return_400_when_name_is_blank() {
        webTestClient.post()
                .uri("/game/new")
                .bodyValue(new CreateGameCommand("  "))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                .jsonPath("$.path").isEqualTo("/game/new");
    }

    @Test
    void should_return_game_state_200_and_hide_dealer_second_card() {
        var result = new GameStateResult(
                "g1", "p1", "IN_PROGRESS",
                12, 10,
                List.of(
                        CardDto.visible(
                                new Card(Rank.TEN, Suit.HEARTS)
                        )
                ),
                List.of(
                        CardDto.visible(
                                new Card(Rank.ACE, Suit.SPADES)
                        ),
                        CardDto.hiddenCard()
                )
        );

        when(getGameStateUseCase.execute("g1"))
                .thenReturn(Mono.just(result));

        webTestClient.get()
                .uri("/game/g1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gameId").isEqualTo("g1")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS")
                .jsonPath("$.dealerCards[0].hidden").isEqualTo(false)
                .jsonPath("$.dealerCards[0].rank").isEqualTo("ACE")
                .jsonPath("$.dealerCards[0].suit").isEqualTo("SPADES")
                .jsonPath("$.dealerCards[1].hidden").isEqualTo(true)
                .jsonPath("$.dealerCards[1].rank").isEmpty()
                .jsonPath("$.dealerCards[1].suit").isEmpty();
    }

    @Test
    void should_return_404_when_game_not_found() {
        when(getGameStateUseCase.execute("missing"))
                .thenReturn(Mono.error(new GameNotFoundException("missing")));

        webTestClient.get()
                .uri("/game/missing")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.code").isEqualTo("GAME_NOT_FOUND")
                .jsonPath("$.path").isEqualTo("/game/missing");
    }

    @Test
    void should_return_404_when_game_not_found_when_play_move() {
        when(playMoveUseCase.execute(any()))
                .thenReturn(Mono.error(new GameNotFoundException("missing")));

        webTestClient.post()
                .uri("/game/missing/play")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PlayMoveRequest("HIT"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.code").isEqualTo("GAME_NOT_FOUND")
                .jsonPath("$.path").isEqualTo("/game/missing/play");
    }

    @Test
    void should_return_409_when_invalid_move() {
        when(playMoveUseCase.execute(any()))
                .thenReturn(Mono.error(new InvalidMoveException("Game is not in progress")));

        webTestClient.post()
                .uri("/game/g1/play")
                .bodyValue(new PlayMoveRequest("HIT"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_MOVE");
    }

    @Test
    void should_play_hit_and_return_200() {
        var result = new GameStateResult(
                "g1",
                "p1",
                "IN_PROGRESS",
                16,
                11,
                List.of(
                        CardDto.visible(new Card(Rank.TEN, Suit.HEARTS)),
                        CardDto.visible(new Card(Rank.SIX, Suit.CLUBS))
                ),
                List.of(
                        CardDto.visible(new Card(Rank.ACE, Suit.SPADES)),
                        CardDto.hiddenCard()
                )
        );

        when(playMoveUseCase.execute(any()))
                .thenReturn(Mono.just(result));

        webTestClient.post()
                .uri("/game/g1/play")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PlayMoveRequest("HIT"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.gameId").isEqualTo("g1")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS")
                .jsonPath("$.playerScore").isEqualTo(16)
                .jsonPath("$.dealerScore").isEqualTo(11)
                .jsonPath("$.dealerCards[1].hidden").isEqualTo(true);
    }

    @Test
    void should_play_stand_and_return_200_with_final_status() {
        var result = new GameStateResult(
                "g1",
                "p1",
                "DEALER_WINS",
                16,
                20,
                List.of(
                        CardDto.visible(new Card(Rank.TEN, Suit.HEARTS)),
                        CardDto.visible(new Card(Rank.SIX, Suit.CLUBS))
                ),
                List.of(
                        CardDto.visible(new Card(Rank.ACE, Suit.SPADES)),
                        CardDto.visible(new Card(Rank.NINE, Suit.CLUBS))
                )
        );

        when(playMoveUseCase.execute(any()))
                .thenReturn(Mono.just(result));

        webTestClient.post()
                .uri("/game/g1/play")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PlayMoveRequest("STAND"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gameId").isEqualTo("g1")
                .jsonPath("$.status").isEqualTo("DEALER_WINS")
                .jsonPath("$.dealerScore").isEqualTo(20)
                .jsonPath("$.dealerCards[0].hidden").isEqualTo(false)
                .jsonPath("$.dealerCards[1].hidden").isEqualTo(false);
    }
}

