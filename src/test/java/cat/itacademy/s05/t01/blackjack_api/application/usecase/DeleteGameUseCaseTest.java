package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.domain.exception.GameNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.GameId;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteGameUseCaseTest {

    @Mock
    GameRepositoryPort gameRepo;

    @InjectMocks
    DeleteGameUseCase useCase;

    @Test
    void should_delete_game() {
        when(gameRepo.deleteById(new GameId("g1")))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("g1"))
                .verifyComplete();

        verify(gameRepo).deleteById(new GameId("g1"));
        verifyNoMoreInteractions(gameRepo);
    }

    @Test
    void should_propagate_game_not_found_exception() {
        when(gameRepo.deleteById(new GameId("g2")))
                .thenReturn(Mono.error(new GameNotFoundException("g2")));

        StepVerifier.create(useCase.execute("g2"))
                .expectError(GameNotFoundException.class)
                .verify();
    }
}
