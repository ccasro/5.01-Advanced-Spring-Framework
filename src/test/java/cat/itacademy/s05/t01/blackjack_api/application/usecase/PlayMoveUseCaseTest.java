package cat.itacademy.s05.t01.blackjack_api.application.usecase;


import cat.itacademy.s05.t01.blackjack_api.application.dto.GameStateResult;
import cat.itacademy.s05.t01.blackjack_api.application.dto.PlayMoveCommand;
import cat.itacademy.s05.t01.blackjack_api.application.mapper.GameStateMapper;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.GameNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.InvalidMoveException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.*;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayMoveUseCaseTest {

    @Mock
    GameRepositoryPort gameRepo;
    @Mock
    PlayerRepositoryPort playerRepo;
    @Mock
    GameStateMapper mapper;

    @InjectMocks
    PlayMoveUseCase useCase;

    @Test
    void gameExists_hit_returnsMappedResult_andSaves() {

        var cmd = new PlayMoveCommand("game-123", "HIT");

        GameId gameId = new GameId("game-123");

        Game game = mock(Game.class);
        Game updated = mock(Game.class);
        Game saved = mock(Game.class);

        when(gameRepo.findById(gameId)).thenReturn(Mono.just(game));
        when(game.hit()).thenReturn(updated);
        when(gameRepo.save(updated)).thenReturn(Mono.just(saved));

        when(saved.status()).thenReturn(GameStatus.IN_PROGRESS);

        GameStateResult expected = mock(GameStateResult.class);
        when(mapper.toResultForPlayer(saved)).thenReturn(expected);

        StepVerifier.create(useCase.execute(cmd))
                .expectNext(expected)
                .verifyComplete();

        verify(gameRepo).findById(gameId);
        verify(game).hit();
        verify(gameRepo).save(updated);
        verify(mapper).toResultForPlayer(saved);

        verifyNoInteractions(playerRepo);
    }

    @Test
    void gameExists_stand_returnsMappedResult_andSaves() {

        var cmd = new PlayMoveCommand("game-456", "STAND");
        GameId gameId = new GameId("game-456");

        Game game = mock(Game.class);
        Game updated = mock(Game.class);
        Game saved = mock(Game.class);

        when(gameRepo.findById(gameId)).thenReturn(Mono.just(game));
        when(game.stand()).thenReturn(updated);
        when(gameRepo.save(updated)).thenReturn(Mono.just(saved));

        when(saved.status()).thenReturn(GameStatus.IN_PROGRESS);

        GameStateResult expected = mock(GameStateResult.class);
        when(mapper.toResultForPlayer(saved)).thenReturn(expected);

        StepVerifier.create(useCase.execute(cmd))
                .expectNext(expected)
                .verifyComplete();

        verify(gameRepo).findById(gameId);
        verify(game).stand();
        verify(gameRepo).save(updated);
        verify(mapper).toResultForPlayer(saved);

        verifyNoInteractions(playerRepo);
    }

    @Test
    void gameNotFound_throwsGameNotFoundException() {

        var cmd = new PlayMoveCommand("missing-id", "HIT");
        when(gameRepo.findById(new GameId("missing-id"))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(cmd))
                .expectErrorSatisfies(err -> assertThat(err).isInstanceOf(GameNotFoundException.class))
                .verify();

        verify(gameRepo).findById(new GameId("missing-id"));
        verify(gameRepo, never()).save(any());
        verifyNoInteractions(playerRepo);
        verifyNoInteractions(mapper);
    }

    @Test
    void invalidMove_throwsInvalidMoveException_whenGameIsNotInProgress() {

        var cmd = new PlayMoveCommand("game-999", "HIT");
        Game game = mock(Game.class);

        when(gameRepo.findById(new GameId("game-999"))).thenReturn(Mono.just(game));
        when(game.hit()).thenThrow(new InvalidMoveException("Game is not in progress"));


        StepVerifier.create(useCase.execute(cmd))
                .expectError(InvalidMoveException.class)
                .verify();

        verify(gameRepo).findById(new GameId("game-999"));
        verify(game).hit();
        verify(gameRepo, never()).save(any());

        verifyNoInteractions(playerRepo);
        verifyNoInteractions(mapper);
    }
}
