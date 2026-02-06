package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.domain.model.Player;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerId;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerName;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ViewRankingUseCaseTest {

    @Mock
    PlayerRepositoryPort playerRepo;

    @InjectMocks
    ViewRankingUseCase useCase;

    @Test
    void should_return_ranked_players_with_positions() {
        var p1 = new Player(new PlayerId("p1"), new PlayerName("Ccr"), 5, 1);
        var p2 = new Player(new PlayerId("p2"), new PlayerName("Jcr"), 6, 2);

        when(playerRepo.findRanking()).thenReturn(Flux.just(p1,p2));

        StepVerifier.create(useCase.execute())
                .assertNext(rank -> {
                    assertEquals(1, rank.position());
                    assertEquals("p1", rank.playerId());
                })
                .assertNext(rank -> {
                    assertEquals(2,rank.position());
                    assertEquals("p2", rank.playerId());
                })
                .verifyComplete();
    }
}
