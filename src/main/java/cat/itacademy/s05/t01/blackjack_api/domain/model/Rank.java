package cat.itacademy.s05.t01.blackjack_api.domain.model;

public enum Rank {
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
    EIGHT(8), NINE(9), TEN(10),
    JACK(10), QUEEN(10), KING(10),
    ACE(11);

    private final int defaultValue;

    Rank(int defaultValue) { this.defaultValue = defaultValue; }

    public int getDefaultValue() { return defaultValue; }

    public boolean isAce() { return this == ACE; }
}
