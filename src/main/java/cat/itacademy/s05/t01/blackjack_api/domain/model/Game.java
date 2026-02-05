package cat.itacademy.s05.t01.blackjack_api.domain.model;

import cat.itacademy.s05.t01.blackjack_api.domain.exception.InvalidMoveException;

import java.util.Objects;

public final class Game {
    private final GameId id;
    private final PlayerId playerId;
    private final Deck deck;
    private final Hand playerHand;
    private final Hand dealerHand;
    private final GameStatus status;

    private Game(GameId id, PlayerId playerId, Deck deck, Hand playerHand, Hand dealerHand, GameStatus status){
        this.id = Objects.requireNonNull(id);
        this.playerId = Objects.requireNonNull(playerId);
        this.deck = Objects.requireNonNull(deck);
        this.playerHand = Objects.requireNonNull(playerHand);
        this.dealerHand = Objects.requireNonNull(dealerHand);
        this.status = Objects.requireNonNull(status);
    }

    public static Game newGame(PlayerId playerId, Deck deck) {
        var d1 = deck.draw(); var c1 = d1.card(); deck = d1.nextDeck();
        var d2 = deck.draw(); var c2 = d2.card(); deck = d2.nextDeck();
        var d3 = deck.draw(); var c3 = d3.card(); deck = d3.nextDeck();
        var d4 = deck.draw(); var c4 = d4.card(); deck = d4.nextDeck();

        Hand player = Hand.empty().add(c1).add(c3);
        Hand dealer = Hand.empty().add(c2).add(c4);

        Game g = new Game(GameId.newId(), playerId, deck, player, dealer, GameStatus.IN_PROGRESS);

        return g.resolveNaturals();
    }

    public static Game rehydrate(GameId id, PlayerId playerId, Deck deck, Hand playerHand, Hand dealerHand, GameStatus status ){
        return new Game(id, playerId, deck, playerHand, dealerHand, status);
    }

    public static Game newGame(PlayerId playerId){
        return newGame(playerId, Deck.standardShuffled());
    }

    private Game resolveNaturals() {
        boolean playerBJ = playerHand.isBlackjack();
        boolean dealerBJ = dealerHand.isBlackjack();
        if (playerBJ && dealerBJ) return withStatus(GameStatus.PUSH);
        if (playerBJ) return withStatus(GameStatus.PLAYER_WINS);
        if (dealerBJ) return withStatus(GameStatus.DEALER_WINS);
        return this;
    }

    public Game hit() {
        ensureInProgress();
        var draw = deck.draw();
        Hand nextPlayer = playerHand.add(draw.card());
        Game next = new Game(id, playerId, draw.nextDeck(), nextPlayer, dealerHand, status);

        if(nextPlayer.isBust()) return next.withStatus(GameStatus.PLAYER_BUST).finalizeOutcome();
        return next;
    }

    public Game stand() {
        ensureInProgress();
        Game afterDealer = dealerPlay();
        return afterDealer.finalizeOutcome();
    }

    private Game dealerPlay(){
        Deck d = deck;
        Hand dealer = dealerHand;

        while (dealer.score() < 17) {
            var draw = d.draw();
            dealer = dealer.add(draw.card());
            d = draw.nextDeck();
        }

        Game g = new Game(id, playerId, d, playerHand, dealer, status);
        if (dealer.isBust()) return g.withStatus(GameStatus.DEALER_BUST);
        return g;
    }


    private Game finalizeOutcome() {
        if (status != GameStatus.IN_PROGRESS && status != GameStatus.DEALER_BUST && status != GameStatus.PLAYER_BUST) {
            return this;
        }

        if (status == GameStatus.PLAYER_BUST)
            return withStatus(GameStatus.DEALER_WINS);

        if (status == GameStatus.DEALER_BUST)
            return withStatus(GameStatus.PLAYER_WINS);

        int ps = playerHand.score();
        int ds = dealerHand.score();

        if (ps > ds) return withStatus(GameStatus.PLAYER_WINS);
        if (ds > ps) return withStatus(GameStatus.DEALER_WINS);
        return withStatus(GameStatus.PUSH);
    }

    private Game withStatus(GameStatus st){
        return new Game(id,playerId,deck,playerHand,dealerHand,st);
    }

    private void ensureInProgress() {
        if (status != GameStatus.IN_PROGRESS) throw new InvalidMoveException("Game is not in progress");
    }

    public GameId id() { return id; }
    public PlayerId playerId() { return playerId; }
    public Deck deck() { return deck; }
    public Hand playerHand() { return playerHand; }
    public Hand dealerHand() { return dealerHand; }
    public GameStatus status() { return status; }
}
