package cat.itacademy.s05.t01.blackjack_api.application.usecase;

import cat.itacademy.s05.t01.blackjack_api.application.dto.GameStateResult;
import cat.itacademy.s05.t01.blackjack_api.application.dto.PlayMoveCommand;
import cat.itacademy.s05.t01.blackjack_api.application.mapper.GameStateMapper;
import cat.itacademy.s05.t01.blackjack_api.domain.exception.GameNotFoundException;
import cat.itacademy.s05.t01.blackjack_api.domain.model.*;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import reactor.core.publisher.Mono;

public final class PlayMoveUseCase {

    private final GameRepositoryPort gameRepo;
    private final PlayerRepositoryPort playerRepo;
    private final GameStateMapper mapper;

    public PlayMoveUseCase(GameRepositoryPort gameRepo, PlayerRepositoryPort playerRepo, GameStateMapper mapper) {
        this.gameRepo = gameRepo;
        this.playerRepo = playerRepo;
        this.mapper = mapper;
    }

    public Mono<GameStateResult> execute(PlayMoveCommand cmd) {
        MoveAction action = MoveAction.valueOf(cmd.action().toUpperCase());

        return gameRepo.findById(new GameId(cmd.gameId()))
                .switchIfEmpty(Mono.error(new GameNotFoundException(cmd.gameId())))
                .map(game -> applyMove(game, action))
                .flatMap(this::persistAndUpdateStats)
                .map(mapper::toResultForPlayer);
    }

    private Game applyMove(Game game, MoveAction action) {
        return switch (action) {
            case HIT -> game.hit();
            case STAND -> game.stand();
        };
    }

    private Mono<Void> updatePlayerStatsIfFinished(Game game) {
        if (game.status() == GameStatus.IN_PROGRESS) return Mono.empty();

        return playerRepo.findById(game.playerId())
                .flatMap(player -> {
                    Player updated = switch (game.status()) {
                        case PLAYER_WINS -> player.registerWin();
                        case DEALER_WINS -> player.registerLoss();
                        case PUSH -> player;
                        default -> player;
                    };
                    return playerRepo.save(updated).then();
                });
    }

    private Mono<Game> persistAndUpdateStats(Game updated) {
        return gameRepo.save(updated)
                .flatMap(saved -> updatePlayerStatsIfFinished(saved).thenReturn(saved));
    }
}
