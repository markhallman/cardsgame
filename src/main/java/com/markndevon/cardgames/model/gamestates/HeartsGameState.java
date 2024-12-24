package com.markndevon.cardgames.model.gamestates;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.PassCardsMessage;
import com.markndevon.cardgames.message.PlayCardMessage;
import com.markndevon.cardgames.message.UpdateScoreBoardMessage;
import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.model.scoreboard.HeartsScoreboard;
import com.markndevon.cardgames.model.scoreboard.ScoreBoard;
import com.markndevon.cardgames.model.util.Util;
import com.markndevon.cardgames.model.util.hearts.PassDeterminer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
    State information for a game of hearts
*/
public class HeartsGameState extends GameState {
    private final List<Card> currentTrick = new ArrayList<>();
    private final Map<Player.PlayerDescriptor, Card> currentTrickMap = new LinkedHashMap<>();
    private final List<Card> kitty = new ArrayList<>();
    private Player currentPlayer;
    private Player lastStartingPlayer = null;
    private Player trickLeader;
    private int tricksPlayed = 0;
    private Player playerWhoTookTheKitty = null;
    private int handCount = 0;
    private AtomicInteger passCountDown;
    private final Object passCountDownMutex = new Object();
    private boolean heartsBroken = false;
    private boolean kittyWon = false;
    private final PassDeterminer passDeterminer;

    @Autowired
    private final Logger logger;


    public HeartsGameState(Player[] players, HeartsRulesConfig rulesConfig, int gameId, Logger logger) {
        super(players, rulesConfig, gameId, logger);
        this.passDeterminer = new PassDeterminer(players.length, rulesConfig);
        this.logger = logger;
    }

    public void start() {
        dealNewHand();
        determineStartingPlayer();
        logger.log("Starting game...");

        if(!currentPlayer.isHumanControlled()){
            playCard(currentPlayer, currentPlayer.getNextPlay(this));
        }

        // passCountDown = new AtomicInteger(players.length);
        // possiblyPassCards();
    }

    //region getters
    public Player[] getPlayers() {
        return players;
    }

    public Player.PlayerDescriptor[] getPlayerDescriptors() {
        return playerDescriptors;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }

    public List<Card> getKitty() {
        return kitty;
    }

    public List<Card> getCurrentTrick() {
        return currentTrick;
    }

    public Map<Player.PlayerDescriptor, Card> getCurrentTrickMap() {
        return currentTrickMap;
    }

    public Player getCurrentPlayer() { return currentPlayer; }
    public int getCurrentPlayerID() {
        return currentPlayer.getId();
    }

    public ScoreBoard getScoreBoard(){
        return scoreBoard;
    }

    public boolean isKittyWon() {
        return kittyWon;
    }

    public Player getPlayerWhoTookTheKitty() {
        return playerWhoTookTheKitty;
    }

    public boolean isHeartsBroken() {
        return heartsBroken;
    }

    //endregion

    private void dealNewHand() {
        final List<Card> extraCards = Util.dealCardsAndReturnExtra(players);
        kitty.addAll(extraCards);
    }

    private void determineStartingPlayer() {
        if(((HeartsRulesConfig) rulesConfig).getStartCardRules() == HeartsRulesConfig.START_CARD_RULES_2_CLUBS) {
            currentPlayer = getStartingPlayer2ClubsRules();
        } else if(lastStartingPlayer == null) {
            currentPlayer = players[0];
        } else {
            currentPlayer = players[(lastStartingPlayer.getId() + 1) % players.length];
        }

        trickLeader = currentPlayer;
    }

    private void gameOver() {
        //todo
    }
    @Override
    public boolean isLegal(Player player, Card card) {
        final HeartsRulesConfig heartsRulesConfig = (HeartsRulesConfig) getRulesConfig();
        // checks for first card legality. See RulesConfig
        // if this is the first card played this hand:
        // - must be 2 of clubs, or not a point, or anarchy
        // if this is the first card played this trick:
        // - may not be a heart, or it has been two turns
        if(currentTrick.isEmpty()) {
            if(tricksPlayed == 0) {

                final int firstCardRule = heartsRulesConfig.getStartCardRules();
                switch(firstCardRule) {
                    case HeartsRulesConfig.START_CARD_RULES_2_CLUBS -> { return isTwoOfClubs(card); }
                    case HeartsRulesConfig.START_CARD_RULES_NO_POINTS -> { return isPoint(card); }
                    case HeartsRulesConfig.START_CARD_RULES_ANARCHY -> { return true; }
                    default -> throw new IllegalStateException("Undefined start card rule " + firstCardRule);
                }
            } else if(heartsRulesConfig.isHeartsMustBeBroken() && !heartsBroken && isHeart(card) && !onlyHearts(player.getHand())) {
                return false; // can't lead a heart if hearts have not yet been broken
            } else if(heartsRulesConfig.isHeartsMustBeBroken() && heartsBroken) {
                return true;
            } else if(!heartsRulesConfig.isHeartsMustBeBroken() && !heartsBroken && tricksPlayed < 2 && isHeart(card)) {
                return false; // a heart can't be lead if hearts have not been broken, and it is not yet the third trick
            }

            return true;
        }

        final Card.Suit leadSuit = currentTrick.get(0).getSuit();

        if(card.getSuit() != leadSuit) {
            if(Util.hasSuit(player.getHand(), leadSuit)) {
                return false; // player must follow suit if they can
            } else if(tricksPlayed == 0 && isPoint(card) && !heartsRulesConfig.isPointsAllowedFirstTrick()) {
                return false; // player may not play points on the first trick
            }

            heartsBroken |= isHeart(card);
        }

        return true;
    }

    //region HeartsLogic
    public boolean playCard(final Player player, final Card card) {
        if(!player.equals(currentPlayer) || !isLegal(currentPlayer, card)) {
            return false;
        }
        logger.log("Player " + player + " playing card " + card);

        currentPlayer.removeCard(card);
        currentTrick.add(card);
        currentTrickMap.put(player.getPlayerDescriptor(), card);

        logger.log("Player " + currentPlayer + " played card " + card);

        currentPlayer = players[(currentPlayer.getId() + 1) % players.length];

        dumpGameInfo();

        return true;
    }

    public List<Card> getLegalPlays(Player player){
        List<Card> cards = new ArrayList<>();
        for (Card card : player.getHand()){
            if(isLegal(player, card)){
                cards.add(card);
            }
        }
        return cards;
    }

    public void possiblyResolveTrick() {
        if(currentTrick.size() == players.length) {
            logger.log("Trick finished, resolving. All cards played=" + currentTrick);

            currentPlayer = getTrickWinner();
            logger.log("Trick winner=" + currentPlayer);

            // may add the kitty to currentTrick
            possiblyResolveKitty();
            currentPlayer.addCardsWon(currentTrick);

            logger.log(currentPlayer + " has now taken " + currentPlayer.getCardsWon());


            // broadcast the current trick before we clear it
            // TODO: how should server communicate info back to client?
            /*
                        // broadcast the current trick before we clear it
            final Map<Player.PlayerDescriptor, Card> currentTrickMapCopy = new LinkedHashMap<>(currentTrickMap);
            final LastTrickPlayedMessage lastTrickPlayedMessage = new LastTrickPlayedMessage(currentTrickMapCopy);
            server.broadcastMessage(lastTrickPlayedMessage);
             */

            // reset
            currentTrick.clear();
            currentTrickMap.clear();
            trickLeader = currentPlayer;
            tricksPlayed++;
        }
    }

    private void possiblyResolveKitty() {
        HeartsRulesConfig heartsRulesConfig = (HeartsRulesConfig) rulesConfig;
        if((!kittyWon && heartsRulesConfig.isKittyWonFirstTrick()) || (!kittyWon && Util.hasSuit(currentTrick, Card.Suit.HEART))) {
            kittyWon = true;
            currentTrick.addAll(kitty);
            playerWhoTookTheKitty = currentPlayer;
        }
    }
    public void possiblyResolveHand() {
        if(Arrays.stream(players).allMatch(player -> player.getHand().isEmpty())) {
            logger.log("Hand finished, resolving.");
            updateScoreBoardAtHandEnd();

            // TODO: Need to communicate back to client
        }
    }
/*
    TODO: Add passing back in
    private void possiblyPassCards() {
        final PassDeterminer.PassingRule passingRule = passDeterminer.getPassingRule(handCount);
        final PassDeterminer.PassingRule.PassDirection passDirection = passingRule.getPassDirection();
        if(passDirection != PassDeterminer.PassingRule.PassDirection.NONE) {
            passCountDown.set(players.length);
            for(int i = 0; i < players.length; i++) {
                final Player playerPassing = players[i];

                final Player playerReceiving;
                // java modulo is dumb and says -1 % 4 = -1, not 3
                if(passDirection == PassDeterminer.PassingRule.PassDirection.LEFT) {
                    playerReceiving = players[(i + passingRule.getXPlayers()) % players.length];
                } else {
                    final int dumMod = (i - passingRule.getXPlayers()) % players.length;
                    final int idx = dumMod < 0 ? players.length + dumMod : dumMod;
                    playerReceiving = players[idx];
                }


            }
        }
    }

    public void passCards(final String passer, final String receiver, final List<Card> cards) {
        Player passerPlayer = null;
        Player receiverPlayer = null;
        for(final Player player : players) {
            if(player.getName().equals(passer)) {
                passerPlayer = player;
            } else if(player.getName().equals(receiver)) {
                receiverPlayer = player;
            }
        }

        if(passerPlayer == null || receiverPlayer == null) {
            throw new IllegalArgumentException("Could not find player with name " + passer + " or " + receiver);
        }

        passCards(passerPlayer, receiverPlayer, cards);
    }
    public void passCards(final Player passer, final Player receiver, final List<Card> cards) {
        logger.log("Passing cards from " + passer + " to " + receiver + ": " + cards);
        passer.removeCards(cards);
        receiver.addCards(cards);

        final PassCardsMessage message = new PassCardsMessage(passer.getName(), receiver.getName(), cards);

        // TODO: broadcast information about card pass
        final int count = passCountDown.decrementAndGet();
        logger.log("Pass count down: " + count);
        if(count == 0) {
            synchronized (passCountDownMutex) {
                logger.log("Finished pass count down, notifying");
                passCountDownMutex.notifyAll();
            }
        }
    }
*/
    public void reset() {
        Arrays.stream(players).forEach(Player::clearTricksWon);
        heartsBroken = false;
        tricksPlayed = 0;
        kitty.clear();
        kittyWon = false;
        handCount++;
        dealNewHand();
        passCountDown.set(players.length);
        logger.log("Hand reset, resolving.");
        //possiblyPassCards();
    }

    // Assumes cards in order
    private Player getTrickWinner() {
        final int winnerIndex = Util.getHighCard(currentTrick);
        return players[(trickLeader.getId() + winnerIndex) % players.length];
    }

    private void updateScoreBoardAtHandEnd() {
        HeartsRulesConfig heartsRulesConfig = (HeartsRulesConfig) rulesConfig;
        ((HeartsScoreboard)scoreBoard).updateScore(buildUpdateScoreMap()); // TODO: Not sure if continually casting is right here?
        scoreBoard.saveHandScore();

        //TODO: Web socket communication
        UpdateScoreBoardMessage finalHandScore = new UpdateScoreBoardMessage(scoreBoard.getScore());

        //server.broadcastMessage(finalHandScore);

        logger.log("Score updated: " + scoreBoard.getScore());
        if(scoreBoard.getScore().values().stream().anyMatch(i -> i >= heartsRulesConfig.getPointsToLose())) {
            gameOver();
        }
    }

    private Map<Player.PlayerDescriptor, List<Card>> buildUpdateScoreMap() {
        final Map<Player.PlayerDescriptor, List<Card>> updateScoreMap = new HashMap<>();

        for(final Player player : players) {
            updateScoreMap.put(player.getPlayerDescriptor(), player.getCardsWon());
        }

        return updateScoreMap;
    }

    private Player getStartingPlayer2ClubsRules() {
        Card startCard = new Card(Card.Suit.CLUB, Card.Value.TWO);
        while(kitty.contains(startCard)) {
            if(startCard.getValue() == Card.Value.ACE) {
                startCard = new Card(Card.Suit.values()[startCard.getSuit().ordinal() + 1], Card.Value.TWO);
            } else {
                startCard = new Card(startCard.getSuit(), Card.Value.values()[startCard.getValue().ordinal() + 1]);
            }
        }

        final Card finalStartCard = startCard;

        return Arrays.stream(players)
                .filter(player -> player.getHand().contains(finalStartCard))
                .toList()
                .get(0);
    }

    //endregion

    //todo move to Util?
    //region CardEquals
    public static boolean isTwoOfClubs(final Card card) {
        return card.getSuit().equals(Card.Suit.CLUB) && card.getValue() == Card.Value.TWO;
    }

    public static boolean isQueenOfSpades(final Card card) {
        return card.getSuit().equals(Card.Suit.SPADE) && card.getValue() == Card.Value.QUEEN;
    }

    public static boolean isJackOfDiamonds(final Card card) {
        return card.getSuit().equals(Card.Suit.DIAMOND) && card.getValue() == Card.Value.JACK;
    }

    public static boolean isPoint(final Card card) {
        return isHeart(card) || isQueenOfSpades(card);
    }

    public static boolean isHeart(final Card card) {
        return card.getSuit().equals(Card.Suit.HEART);
    }

    public static boolean onlyHearts(final List<Card> cards) {
        return cards.stream().allMatch(card -> card.getSuit() != Card.Suit.HEART);
    }
    //endregion

    public void dumpGameInfo() {
        logger.log("Dumping game information");
        for(final Player player : players) {
            logger.log("Player " + player + " hand: " + player.getHand());
            logger.log("Player " + player + " cards taken: " + player.getCardsWon());
        }

        logger.log("Current player was " + currentPlayer);
        logger.log("Current trick was " + currentTrick);
    }
}
