package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.application.dto.ChangePlayerNameCommand;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.InvalidPlayerNameException;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.PlayerNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Player;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerId;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerName;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChangePlayerNameUseCaseTest {

    @Mock
    private PlayerRepositoryPort playerRepo;

    @InjectMocks
    ChangePlayerNameUseCase useCase;

    @Test
    void should_change_player_name_when_player_exists() {
        PlayerId id = new PlayerId("p1");

        Player existing = new Player(id, new PlayerName("OldName"), 2, 1);

        when(playerRepo.findById(id)).thenReturn(Mono.just(existing));
        when(playerRepo.save(any(Player.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        ChangePlayerNameCommand cmd = new ChangePlayerNameCommand("p1", "NewName");

        StepVerifier.create(useCase.execute(cmd))
                .assertNext(res -> {
                    assertThat(res.playerId()).isEqualTo("p1");
                    assertThat(res.name()).isEqualTo("NewName");
                    assertThat(res.wins()).isEqualTo(2);
                    assertThat(res.losses()).isEqualTo(1);
                })
                .verifyComplete();

        verify(playerRepo).findById(id);

        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepo).save(captor.capture());
        assertThat(captor.getValue().name().value()).isEqualTo("NewName");

        verifyNoMoreInteractions(playerRepo);
    }

    @Test
    void should_return_player_not_found_when_repo_returns_empty() {
        PlayerId id = new PlayerId("missing");
        when(playerRepo.findById(id)).thenReturn(Mono.empty());

        ChangePlayerNameCommand cmd = new ChangePlayerNameCommand("missing", "NewName");

        StepVerifier.create(useCase.execute(cmd))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(PlayerNotFoundException.class);
                    assertThat(err.getMessage()).contains("missing");
                })
                .verify();

        verify(playerRepo).findById(id);
        verifyNoMoreInteractions(playerRepo);
    }

    @Test
    void should_return_invalid_player_name_when_new_name_is_invalid() {
        ChangePlayerNameCommand cmd = new ChangePlayerNameCommand("p1", "");

        StepVerifier.create(useCase.execute(cmd))
                .expectError(InvalidPlayerNameException.class)
                .verify();

        verifyNoInteractions(playerRepo);
    }
}
