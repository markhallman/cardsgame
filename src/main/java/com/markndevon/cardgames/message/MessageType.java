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
    GameUpdateMessage,
    StartGameRequest, //client requests the server starts the game
    DealMessage,
    PlayCardMessage,
    UpdateCurrentTrickMessage,
    LastTrickPlayedMessage,
    UpdateScoreBoardMessage,
    UpdateLegalPlaysMessage,
    PassCardsMessage,
    ShowPassCardsDialogMessage,

    LobbyUpdateMessage,
    GameDoneMessage, HandResolvedMessage;

    @Override
    public String toString() {
        return name();
    }
}
