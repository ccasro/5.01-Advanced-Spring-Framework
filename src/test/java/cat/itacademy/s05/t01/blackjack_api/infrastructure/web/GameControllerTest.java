package cat.itacademy.s05.t01.blackjack_api.infrastructure.web;

import cat.itacademy.s05.t01.blackjack_api.application.dto.CardDto;
import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameCommand;
import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameResult;
import cat.itacademy.s05.t01.blackjack_api.application.dto.GameStateResult;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.CreateNewGameUseCase;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.GetGameStateUseCase;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.GameNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Card;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Rank;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Suit;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller.GameController;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.CreateGameRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
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

    @Test
    void should_create_game_and_return_201() {
        when(createNewGameUseCase.execute(any()))
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

}
