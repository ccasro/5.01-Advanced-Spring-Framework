package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MongoGameDocument {

    @Id
    private String id;

    private String playerId;
    private List<CardDoc> playerCards;
    private List<CardDoc> dealerCards;
    private String status;
}
