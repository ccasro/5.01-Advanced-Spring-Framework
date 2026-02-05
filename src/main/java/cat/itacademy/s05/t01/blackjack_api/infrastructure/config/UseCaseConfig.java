package cat.itacademy.s05.t01.blackjack_api.infrastructure.config;

import cat.itacademy.s05.t01.blackjack_api.application.mapper.GameStateMapper;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.CreateNewGameUseCase;
import cat.itacademy.s05.t01.blackjack_api.application.usecase.GetGameStateUseCase;
import cat.itacademy.s05.t01.blackjack_api.domain.port.GameRepositoryPort;
import cat.itacademy.s05.t01.blackjack_api.domain.port.PlayerRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    CreateNewGameUseCase createNewGameUseCase(PlayerRepositoryPort playerRepo, GameRepositoryPort gameRepo) {
        return new CreateNewGameUseCase(playerRepo,gameRepo);
    }

    @Bean
    GetGameStateUseCase getGameStateUseCase(GameRepositoryPort gameRepo){
        return new GetGameStateUseCase(gameRepo, new GameStateMapper());
    }
}
