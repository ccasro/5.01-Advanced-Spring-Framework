package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameCommand;
import cat.itacademy.s05.t01.blackjack_api.application.dto.CreateGameResult;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.InvalidPlayerNameException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.Game;
import cat.itacademy.s05.t01.blackjack_api.domain.model.PlayerName;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import reactor.core.publisher.Mono;

public final class CreateNewGameUseCase {

    private final PlayerRepositoryPort playerRepo;
    private final GameRepositoryPort gameRepo;

    public CreateNewGameUseCase(PlayerRepositoryPort playerRepo, GameRepositoryPort gameRepo) {
        this.playerRepo = playerRepo;
        this.gameRepo = gameRepo;
    }

    public Mono<CreateGameResult> execute(CreateGameCommand cmd) {
        final PlayerName name;
        try {
            name = new PlayerName(cmd.playerName());
        } catch (IllegalArgumentException e) {
            return Mono.error(new InvalidPlayerNameException(e.getMessage()));
        }

        return playerRepo.findOrCreateByName(name)
                .flatMap(player -> {
                    Game game = Game.newGame(player.id());
                    return gameRepo.save(game)
                            .map(saved -> new CreateGameResult(
                                    saved.id().value(),
                                    player.id().value(),
                                    saved.status().name()
                            ));
                });
    }
}
