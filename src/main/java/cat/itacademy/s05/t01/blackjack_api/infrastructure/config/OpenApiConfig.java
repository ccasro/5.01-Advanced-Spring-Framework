package cat.itacademy.s05.t01.blackjack_api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI blackjackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blackjack API")
                        .version("1.0.0")
                        .description("Reactive Blackjack API using Spring WebFlux, MongoDB and MySQL (R2DBC)."));
    }
}
