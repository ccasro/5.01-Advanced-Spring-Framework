package cat.itacademy.s05.t01.blackjack_api.infrastructure.web;

import cat.itacademy.s05.t01.blackjack_api.application.dto.PlayerResult;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.ChangePlayerNameUseCase;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.PlayerNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller.PlayerController;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = PlayerController.class)
@Import(GlobalExceptionHandler.class)
public class PlayerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    ChangePlayerNameUseCase changePlayerNameUseCase;

    @Test
    void should_return_200_and_player_result_when_name_changed() {
        when(changePlayerNameUseCase.execute(any()))
                .thenReturn(Mono.just(new PlayerResult("p1", "Ccr", 1, 0)));

        webTestClient.put()
                .uri("/player/p1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        { "newName": "Ccr" }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.playerId").isEqualTo("p1")
                .jsonPath("$.name").isEqualTo("Ccr")
                .jsonPath("$.wins").isEqualTo(1)
                .jsonPath("$.losses").isEqualTo(0);
    }

    @Test
    void should_return_400_when_validation_fails_blank_name() {
        webTestClient.put()
                .uri("/player/p1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        { "newName": "" }
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void should_return_404_when_player_not_found() {
        when(changePlayerNameUseCase.execute(any()))
                .thenReturn(Mono.error(new PlayerNotFoundException("missing")));

        webTestClient.put()
                .uri("/player/missing")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        { "newName": "Neo" }
                        """)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.code").isEqualTo("PLAYER_NOT_FOUND")
                .jsonPath("$.path").isEqualTo("/player/missing");
    }
}
