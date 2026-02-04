package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameCommand;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Game;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Player;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerId;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerName;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CreateNewGameUseCaseTest {

    @Test
    void should_create_game_for_player() {
        PlayerRepositoryPort playerRepositoryPort = Mockito.mock(PlayerRepositoryPort.class);
        GameRepositoryPort gameRepositoryPort = Mockito.mock(GameRepositoryPort.class);

        Player player = new Player(PlayerId.newId(), new PlayerName("Alan"), 0, 0);

        when(playerRepositoryPort.findOrCreateByName(any(PlayerName.class))).thenReturn(Mono.just(player));
        when(gameRepositoryPort.save(any(Game.class))).thenAnswer(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0)));

        CreateNewGameUseCase useCase = new CreateNewGameUseCase(playerRepositoryPort, gameRepositoryPort);

        StepVerifier.create(useCase.execute(new CreateGameCommand("Alan")))
                .assertNext(res -> {
                    assertFalse(res.gameId().isBlank());
                    assertEquals(player.id().value(), res.playerId());
                    assertFalse(res.status().isBlank());
                })
                .verifyComplete();
    }
}
