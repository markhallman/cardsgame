package com.markndevon.cardgames.message;

public enum MessageType {
    ActiveGamesMessage,
    ActivePlayersMessage,
    ChatMessage,
    CreatePlayerMessage,

    JoinSuccessMessage,
    PlayerJoinedMessage,
    KittyMessage,
    GameStartedMessage, //server informs the client the game has started
    StartGameRequest, //client requests the server starts the game
    DealMessage,
    PlayCardMessage,
    UpdateCurrentTrickMessage,
    LastTrickPlayedMessage,
    UpdateScoreBoardMessage,
    UpdateLegalPlaysMessage,
    PassCardsMessage,
    ShowPassCardsDialogMessage,
    GameDoneMessage, HandResolvedMessage;

    @Override
    public String toString() {
        return name();
    }
}
