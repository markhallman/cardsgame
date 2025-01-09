package com.markndevon.cardgames.model.util;

import com.markndevon.cardgames.model.Card;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ResourceManager {

    private static final String DEFAULT_CARD_BACK = "yellow_back.png";
    private static final String CARDS_IMAGE_DIR = "/static/images/cards/";
    private static final String SCOREBOARD_IMAGE_DIR = "/static/images/scoreboard/";
    private static final String RULES_CONFIG_IMAGE_DIR = "/static/images/rulesConfig/";
    private static final String CHAT_BUTTON_IMAGE_DIR = "/static/images/messageBoard/";

    private static final String PLAYER_AVATAR_IMAGE_DIR = "/static/images/icons/";

    private static final String ICON_DIRECTORY = "/static/images/icons/";
    private static final BufferedImage CARD_BACK_IMAGE = readImageTryCatch(CARDS_IMAGE_DIR + DEFAULT_CARD_BACK);

    public static BufferedImage getScoreboardButtonImage(){ return readImageTryCatch(getScoreboardButtonUrl()); }
    public static BufferedImage getRulesConfigButtonImage(){ return readImageTryCatch(getRulesConfigButtonUrl()); }
    public static BufferedImage getChatButtonImage() {
        return readImageTryCatch(getChatButtonURL());
    }
    public static BufferedImage getChatButtonWithNotificationImage() {
        return readImageTryCatch(getChatButtonWithNotificationURL());
    }
    public static BufferedImage getApplicationIconImage() { return readImageTryCatch(getApplicationIconUrl()); }
    public static BufferedImage getUserAvatarImage(String playerNumber) { return readImageTryCatch(getUserAvatarUrl(playerNumber));}

    public static BufferedImage getAIAvatarImage() { return readImageTryCatch(getAIAvatarUrl()); }

    public static BufferedImage getArrowImage(final String arrowDirection){
        if(arrowDirection.equals("LEFT")){
            return readImageTryCatch(SCOREBOARD_IMAGE_DIR + "Arrow-Left.png");
        } else if(arrowDirection.equals("RIGHT")){
            return readImageTryCatch(SCOREBOARD_IMAGE_DIR + "Arrow-Right.png");
        }
        throw new IllegalArgumentException("arrowDirection must be specified to be either RIGHT or LEFT");
    }

    public static BufferedImage getCardImage(final Card card) {
        return getCardImage(card, false);
    }

    public static BufferedImage getCardImage(final Card card, final boolean rotate) {
        return getCardImage(getCardUrl(card, rotate));
    }

    public static BufferedImage getCardImage(final String file) {
        return readImageTryCatch(file);
    }

    public static BufferedImage getCardBackImage() {
        return CARD_BACK_IMAGE;
    }


    private static BufferedImage readImageTryCatch(final String file) {
        try {
            System.out.println(file);
            return ImageIO.read(Objects.requireNonNull(ResourceManager.class.getResourceAsStream(file)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCardUrl(final Card card, final boolean rotate) {
        return CARDS_IMAGE_DIR + getFileName(card) + (rotate ? "R" : "") + ".png";
    }


    private static String getScoreboardButtonUrl(){ return SCOREBOARD_IMAGE_DIR + "ScoreboardButton.png"; }
    private static String getRulesConfigButtonUrl(){ return RULES_CONFIG_IMAGE_DIR + "RulesConfigButton.png"; }
    private static String getChatButtonURL() {
        return CHAT_BUTTON_IMAGE_DIR + "ChatButton.png";
    }
    private static String getChatButtonWithNotificationURL() {
        return CHAT_BUTTON_IMAGE_DIR + "ChatButtonNotification.png";
    }

    private static String getApplicationIconUrl(){return ICON_DIRECTORY + "cardHand.jpeg"; }

    private static String getAIAvatarUrl(){return PLAYER_AVATAR_IMAGE_DIR + "questionMark.png"; }
    private static String getUserAvatarUrl(String playerNumber){
        String playerAvatarUrl;
        switch(playerNumber){
            case "1","one","One" -> playerAvatarUrl = PLAYER_AVATAR_IMAGE_DIR + "playerOne.png";
            case "2","two","Two" -> playerAvatarUrl = PLAYER_AVATAR_IMAGE_DIR + "playerTwo.png";
            case "3","three","Three" -> playerAvatarUrl = PLAYER_AVATAR_IMAGE_DIR + "playerThree.png";
            case "4","four","Four" -> playerAvatarUrl = PLAYER_AVATAR_IMAGE_DIR + "playerFour.png";
            default -> throw new IllegalArgumentException("NO avatar for playerNumber " + playerNumber);
        }
        return playerAvatarUrl;
    }

    private static String getFileName(final Card card) {
        final Card.Suit suit = card.getSuit();
        final String suitString;
        switch(suit) {
            case CLUB -> suitString = "C";
            case HEART -> suitString = "H";
            case SPADE -> suitString = "S";
            case DIAMOND -> suitString = "D";
            default -> throw new IllegalStateException("Unknown suit " + suit);
        }

        return card.getValue().getVal() + suitString;
    }
}
