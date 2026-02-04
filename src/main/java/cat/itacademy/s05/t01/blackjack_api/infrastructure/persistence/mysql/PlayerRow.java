package cat.itacademy.s05.t01.blackjack_api.infrastructure.persistence.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRow {

    @Id
    private Long id;

    private String externalId;
    private String name;
    private int wins;
    private int losses;
}
