package cat.itacademy.s05.t01.blackjack_api.application.dto;

import java.util.List;

public record GameStateResult(
        String gameId,
        String playerId,
        String status,
        int playerScore,
        int dealerScore,
        List<CardDto> playerCards,
        List<CardDto> dealerCards
) { }
