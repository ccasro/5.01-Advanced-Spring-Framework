package cat.itacademy.s05.t01.blackjack_api.domain.model;

import java.util.UUID;

public record PlayerId(String value) {
    public PlayerId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("PlayerId cannot be blank");
    }
    public static PlayerId newId() { return new PlayerId(UUID.randomUUID().toString()); }
    @Override
    public String toString() { return value; }
}
