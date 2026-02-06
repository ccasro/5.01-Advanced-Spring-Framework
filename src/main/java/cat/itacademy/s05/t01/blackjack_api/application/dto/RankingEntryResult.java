package cat.itacademy.s05.t01.blackjack_api.application.dto;

public record RankingEntryResult(
        int position,
        String playerId,
        String name,
        int wins,
        int losses,
        int score
) {
}
