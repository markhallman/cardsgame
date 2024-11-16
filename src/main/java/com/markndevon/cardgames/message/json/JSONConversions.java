package com.markndevon.cardgames.message.json;

import com.hearts.message.DealMessage;
import com.hearts.message.GameDoneMessage;
import com.hearts.play.Card;
import com.hearts.player.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class JSONConversions {
    public static DealMessage parseDealMessage(final JSONObject json) {
        final List<Card> cards = new ArrayList<>();

        final JSONArray jsonArray = (JSONArray) json.get(JSONConstants.CARD_ARRAY_KEY);
        for (final Object card : jsonArray) {
            cards.add(parseCard((JSONObject) card));
        }

        return new DealMessage(cards);
    }

    public static Card parseCard(final JSONObject json) {
        final String suitStr = (String) json.get("suit");
        final String valueStr = (String) json.get("value");
        return new Card(Card.Suit.valueOf(suitStr), Card.Value.valueOf(valueStr));
    }

    public static List<Card> parseCardList(final JSONObject json) {
        final JSONArray cardArray = (JSONArray) json.get(JSONConstants.CARD_ARRAY_KEY);
        final List<Card> cards = new ArrayList<>();

        if(cardArray == null || cardArray.isEmpty()) {
            return cards;
        }

        for (final Object card : cardArray) {
            cards.add(parseCard((JSONObject) card));
        }

        return cards;
    }

    public static JSONObject encodeCard(final Card card) {
        final JSONObject playerObj = new JSONObject();
        playerObj.put("suit", card.getSuit().name());
        playerObj.put("value", card.getValue().name());
        return playerObj;
    }

    public static Map<Player.PlayerDescriptor, Integer> parseScoreMap(final JSONObject json) {
        Map<Player.PlayerDescriptor, Integer> scoreMap = new HashMap<>();
        JSONParser parser = new JSONParser();

        final JSONObject mapObj = (JSONObject) json.get(JSONConstants.MAP_KEY);
        for(final Object o : mapObj.keySet()) {
            JSONObject obj;
            try {
                obj = (JSONObject) parser.parse((String) o);
            } catch (ParseException ex){
                throw new RuntimeException("Failure parsing server scoreboard update", ex);
            }

            final Player.PlayerDescriptor key = parsePlayerDescriptor(obj);
            final int value =((Long) (mapObj).get(o)).intValue();
            scoreMap.put(key, value);
        }

        return scoreMap;
    }

    public static Map<Player.PlayerDescriptor, Card> parseCurrentTrick(final JSONObject json) {
        final Map<Player.PlayerDescriptor, Card> currentTrick = new LinkedHashMap<>();
        final JSONParser parser = new JSONParser();

        final JSONObject mapObj = (JSONObject) json.get(JSONConstants.MAP_KEY);
        for(final Object o : mapObj.keySet()) {
            JSONObject obj;
            try {
                obj = (JSONObject) parser.parse((String) o);
            } catch (ParseException ex){
                throw new RuntimeException("Failure parsing current trick", ex);
            }

            final Player.PlayerDescriptor key = parsePlayerDescriptor(obj);
            final Card card = parseCard((JSONObject) mapObj.get(o));
            currentTrick.put(key, card);
        }

        return currentTrick;
    }

    public static Player.PlayerDescriptor parsePlayerDescriptor(final JSONObject json) {
        System.out.println("Player descriptor parsing " + json);
        final String name = (String) json.get(JSONConstants.PLAYER_NAME);
        final long id = (long) json.get(JSONConstants.PLAYER_ID);
        final boolean isHumanControlled = (boolean) json.get(JSONConstants.IS_HUMAN_CONTROLLED);
        return new Player.PlayerDescriptor(name, (int) id, isHumanControlled);
    }

    public static JSONObject encodePlayerDescriptor(final Player.PlayerDescriptor player) {
        final JSONObject playerObj = new JSONObject();
        playerObj.put(JSONConstants.PLAYER_NAME, player.getName());
        playerObj.put(JSONConstants.PLAYER_ID, player.getId());
        playerObj.put(JSONConstants.IS_HUMAN_CONTROLLED, player.isHumanControlled());
        return playerObj;
    }

    public static JSONObject encodePlayer(final Player player) {
        return encodePlayerDescriptor(player.getPlayerDescriptor());
    }

    public static Player.PlayerDescriptor[] getPlayerDescriptorArray(final JSONObject json) {
        final JSONArray playerArray = (JSONArray) json.get(JSONConstants.PLAYER_ARRAY_KEY);
        final List<Player.PlayerDescriptor> players = new ArrayList<>();

        for (final Object player : playerArray) {
            players.add(parsePlayerDescriptor((JSONObject) player));
        }

        return players.toArray(new Player.PlayerDescriptor[0]);
    }

    public static GameDoneMessage parseGameDoneMessage(final JSONObject json) {
        final Map<String, Integer> scoreBoard = new HashMap<>();

        for (String name : (Iterable<String>) json.keySet()) {
            scoreBoard.put(name, Integer.parseInt((String) json.get(name)));
        }

        return new GameDoneMessage(scoreBoard);
    }
}
