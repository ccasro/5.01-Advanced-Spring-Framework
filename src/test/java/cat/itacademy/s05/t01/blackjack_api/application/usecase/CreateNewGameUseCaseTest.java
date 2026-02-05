package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameCommand;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.InvalidPlayerNameException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Game;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Player;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerId;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerName;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateNewGameUseCaseTest {

    private PlayerRepositoryPort playerRepo;
    private GameRepositoryPort gameRepo;
    private CreateNewGameUseCase useCase;

    @BeforeEach
    void setUp() {
        playerRepo = Mockito.mock(PlayerRepositoryPort.class);
        gameRepo = Mockito.mock(GameRepositoryPort.class);
        useCase = new CreateNewGameUseCase(playerRepo, gameRepo);
    }

    @Test
    void should_create_game_for_player() {
        Player player = new Player(PlayerId.newId(), new PlayerName("Alan"), 0, 0);

        when(playerRepo.findOrCreateByName(any(PlayerName.class))).thenReturn(Mono.just(player));
        when(gameRepo.save(any(Game.class))).thenAnswer(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0)));

        StepVerifier.create(useCase.execute(new CreateGameCommand("Alan")))
                .assertNext(res -> {
                    assertFalse(res.gameId().isBlank());
                    assertEquals(player.id().value(), res.playerId());
                    assertFalse(res.status().isBlank());
                })
                .verifyComplete();

        verify(playerRepo).findOrCreateByName(new PlayerName("Alan"));
        verify(gameRepo).save(any(Game.class));
    }

    @Test
    void should_fail_when_name_invalid() {
        StepVerifier.create(useCase.execute(new CreateGameCommand(" ")))
                .expectError(InvalidPlayerNameException.class)
                .verify();
    }
}
