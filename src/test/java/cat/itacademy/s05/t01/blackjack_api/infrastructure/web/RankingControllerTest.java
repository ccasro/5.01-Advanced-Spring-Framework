package cat.itacademy.s05.t01.blackjack_api.infrastructure.web;

import cat.itacademy.s05.t01.blackjack_api.application.dto.RankingEntryResult;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.ViewRankingUseCase;
import cat.itacademy.s05.t01.blackjack_api.infrastructure.web.controller.RankingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = RankingController.class)
public class RankingControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    ViewRankingUseCase viewRanking;

    @Test
    void should_return_200_and_ranking_list() {
        when(viewRanking.execute()).thenReturn(Flux.just(
                new RankingEntryResult(1, "p1", "Ccr", 5, 1, 4),
                new RankingEntryResult(2, "p2", "Jcr", 3, 2, 1)
        ));

        webTestClient.get()
                .uri("/ranking")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].position").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Ccr")
                .jsonPath("$[1].position").isEqualTo(2);
    }

    @Test
    void should_return_200_and_empty_list_when_no_players() {
        when(viewRanking.execute()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/ranking")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("[]");
    }

}
