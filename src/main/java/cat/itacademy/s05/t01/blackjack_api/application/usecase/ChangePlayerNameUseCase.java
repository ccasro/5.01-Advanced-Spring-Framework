package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.application.dto.ChangePlayerNameCommand;
import cat.itacademy.s05.t01.blackjack_api.application.dto.PlayerResult;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.InvalidPlayerNameException;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.PlayerNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Player;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerId;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerName;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import reactor.core.publisher.Mono;

public final class ChangePlayerNameUseCase {

    private final PlayerRepositoryPort playerRepo;

    public ChangePlayerNameUseCase(PlayerRepositoryPort playerRepo) {
        this.playerRepo = playerRepo;
    }

    public Mono<PlayerResult> execute(ChangePlayerNameCommand cmd) {
        final PlayerName newName;
        try {
            newName = new PlayerName(cmd.newName());
        } catch (IllegalArgumentException e) {
            return Mono.error(new InvalidPlayerNameException(e.getMessage()));
        }

        PlayerId pId = new PlayerId(cmd.playerId());

        return playerRepo.findById(pId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(cmd.playerId())))
                .map(player -> player.rename(newName))
                .flatMap(playerRepo::save)
                .map(this::toResult);
    }

    private PlayerResult toResult(Player p) {
        return new PlayerResult(p.id().value(), p.name().value(), p.wins(), p.losses(), p.score());
    }
}
