package cat.itacademy.s05.t01.blackjack_api.domain.model;

import java.util.UUID;

public record GameId(String value) {
    public GameId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("GameId cannot be blank");
    }
    public static GameId newId() { return new GameId(UUID.randomUUID().toString()); }
    @Override
    public String toString() { return value; }
}
