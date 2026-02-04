package cat.itacademy.s05.t01.blackjack_api.domain.model;

public record PlayerName(String value) {
    public PlayerName {
        if (value == null) throw new IllegalArgumentException("PlayerName cannot be null");
        String v = value.trim();
        if (v.isEmpty()) throw new IllegalArgumentException("PlayerName cannot be blank");
        if (v.length() > 30) throw new IllegalArgumentException("PlayerName too long (max 30)");
        value = v;
    }
}
