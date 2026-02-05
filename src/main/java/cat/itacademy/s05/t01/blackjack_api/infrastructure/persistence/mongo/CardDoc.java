package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDoc {
    private String rank;
    private String suit;
}
