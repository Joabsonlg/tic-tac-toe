package com.joabsonlg.tictactoewebsocket.enumeration;

public enum GameState {
    WAITING_FOR_PLAYER("Waiting for player."),
    PLAYER1_TURN("Player 1's turn."),
    PLAYER2_TURN("Player 2's turn."),
    PLAYER1_WON("Player 1 won."),
    PLAYER2_WON("Player 2 won."),
    TIE("Tie.");

    String description;

    GameState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
