package cat.itacademy.s05.t01.blackjack_api.domain.model;

import java.util.Objects;

public final class Player {
    private final PlayerId id;
    private final PlayerName name;
    private final int wins;
    private final int losses;

    public Player(PlayerId id, PlayerName name, int wins, int losses) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        if (wins < 0 || losses < 0) throw new IllegalArgumentException("wins/losses cannot be negative");
        this.wins = wins;
        this.losses = losses;
    }

    public static Player newPlayer(PlayerName name) {
        return new Player(PlayerId.newId(), name, 0, 0);
    }

    public Player rename(PlayerName newName) {
        return new Player(id, newName, wins, losses);
    }

    public Player registerWin() {
        return new Player(id, name, wins + 1, losses);
    }

    public Player registerLoss() {
        return new Player(id, name, wins, losses + 1);
    }

    public PlayerId id() { return id; }
    public PlayerName name() { return name; }
    public int wins() { return wins; }
    public int losses() { return losses; }
    public int score() { return wins - losses; }
}
