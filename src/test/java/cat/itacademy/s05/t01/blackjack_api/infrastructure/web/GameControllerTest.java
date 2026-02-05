package cat.itacademy.s05.t01.blackjack_api.infrastructure.web;

import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameCommand;
import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameResult;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.CreateNewGameUseCase;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller.GameController;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.dto.CreateGameRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = GameController.class)
public class GameControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    CreateNewGameUseCase createNewGameUseCase;

    @Test
    void should_create_game_and_return_201() {
        Mockito.when(createNewGameUseCase.execute(any()))
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
                .expectStatus().isBadRequest();
    }
}
