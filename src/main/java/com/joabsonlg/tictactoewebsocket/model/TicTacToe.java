package com.joabsonlg.tictactoewebsocket.model;

import com.joabsonlg.tictactoewebsocket.enumeration.GameState;

import java.util.Objects;
import java.util.UUID;

public class TicTacToe {
    private String gameId;
    private String[][] board;
    private String player1;
    private String player2;
    private String winner;
    private String turn;
    private GameState gameState;

    public TicTacToe(String player1, String player2) {
        this.gameId = UUID.randomUUID().toString();
        this.player1 = player1;
        this.player2 = player2;
        this.turn = player1;
        this.board = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.board[i][j] = " ";
            }
        }
        gameState = GameState.WAITING_FOR_PLAYER;
    }

    public void makeMove(String player, int move) {
        int row = move / 3;
        int col = move % 3;
        if (Objects.equals(board[row][col], " ")) {
            board[row][col] = player;
            turn = player.equals(player1) ? player2 : player1;
            checkWinner();
            updateGameState();
        }
    }

    private void checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (Objects.equals(board[i][0], board[i][1]) && Objects.equals(board[i][0], board[i][2])) {
                if (!Objects.equals(board[i][0], " ")) {
                    setWinner(Objects.equals(board[i][0], player1) ? player1 : player2);
                    return;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (Objects.equals(board[0][i], board[1][i]) && Objects.equals(board[0][i], board[2][i])) {
                if (!Objects.equals(board[0][i], " ")) {
                    setWinner(Objects.equals(board[0][i], player1) ? player1 : player2);
                    return;
                }
            }
        }

        if (Objects.equals(board[0][0], board[1][1]) && Objects.equals(board[0][0], board[2][2])) {
            if (!Objects.equals(board[0][0], " ")) {
                setWinner(Objects.equals(board[0][0], player1) ? player1 : player2);
                return;
            }
        }
    }

    private void updateGameState() {
        if (winner != null) {
            gameState = winner.equals(player1) ? GameState.PLAYER1_WON : GameState.PLAYER2_WON;
        } else if (isBoardFull()) {
            gameState = GameState.TIE;
        } else {
            gameState = turn.equals(player1) ? GameState.PLAYER1_TURN : GameState.PLAYER2_TURN;
        }
    }

    private void setWinner(String winner) {
        this.winner = winner;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Objects.equals(board[i][j], " ")) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isGameOver() {
        return winner != null || isBoardFull();
    }

    /**
     * Getters and Setters
     */
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getWinner() {
        return winner;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}

