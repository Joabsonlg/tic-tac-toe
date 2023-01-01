package com.joabsonlg.tictactoewebsocket.manager;

import com.joabsonlg.tictactoewebsocket.enumeration.GameState;
import com.joabsonlg.tictactoewebsocket.model.TicTacToe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager class for the Tic-Tac-Toe games.
 * Handles adding and removing players from games, and storing and retrieving the current games.
 *
 * @author Joabson Arley do Nascimento
 */
public class TicTacToeManager {

    /**
     * Map of active Tic-Tac-Toe games, with the game ID as the key.
     */
    private final Map<String, TicTacToe> games;

    /**
     * Map of players waiting to join a Tic-Tac-Toe game, with the player's name as the key.
     */
    protected final Map<String, String> waitingPlayers;

    /**
     * Constructs a new TicTacToeManager.
     */
    public TicTacToeManager() {
        games = new ConcurrentHashMap<>();
        waitingPlayers = new ConcurrentHashMap<>();
    }

    /**
     * Attempts to add a player to an existing Tic-Tac-Toe game, or creates a new game if no open games are available.
     *
     * @param player the name of the player
     * @return the Tic-Tac-Toe game the player was added to
     */
    public synchronized TicTacToe joinGame(String player) {
        if (games.values().stream().anyMatch(game -> game.getPlayer1().equals(player) || (game.getPlayer2() != null && game.getPlayer2().equals(player)))) {
            return games.values().stream().filter(game -> game.getPlayer1().equals(player) || game.getPlayer2().equals(player)).findFirst().get();
        }

        for (TicTacToe game : games.values()) {
            if (game.getPlayer1() != null && game.getPlayer2() == null) {
                game.setPlayer2(player);
                game.setGameState(GameState.PLAYER1_TURN);
                return game;
            }
        }

        TicTacToe game = new TicTacToe(player, null);
        games.put(game.getGameId(), game);
        waitingPlayers.put(player, game.getGameId());
        return game;
    }

    /**
     * Removes a player from their Tic-Tac-Toe game. If the player was the only player in the game,
     * the game is removed.
     *
     * @param player the name of the player
     */
    public synchronized TicTacToe leaveGame(String player) {
        String gameId = getGameByPlayer(player) != null ? getGameByPlayer(player).getGameId() : null;
        if (gameId != null) {
            waitingPlayers.remove(player);
            TicTacToe game = games.get(gameId);
            if (player.equals(game.getPlayer1())) {
                if (game.getPlayer2() != null) {
                    game.setPlayer1(game.getPlayer2());
                    game.setPlayer2(null);
                    game.setGameState(GameState.WAITING_FOR_PLAYER);
                    game.setBoard(new String[3][3]);
                    waitingPlayers.put(game.getPlayer1(), game.getGameId());
                } else {
                    games.remove(gameId);
                    return null;
                }
            } else if (player.equals(game.getPlayer2())) {
                game.setPlayer2(null);
                game.setGameState(GameState.WAITING_FOR_PLAYER);
                game.setBoard(new String[3][3]);
                waitingPlayers.put(game.getPlayer1(), game.getGameId());
            }
            return game;
        }
        return null;
    }

    /**
     * Returns the Tic-Tac-Toe game with the given game ID.
     *
     * @param gameId the ID of the game
     * @return the Tic-Tac-Toe game with the given game ID, or null if no such game exists
     */
    public TicTacToe getGame(String gameId) {
        return games.get(gameId);
    }

    /**
     * Returns the Tic-Tac-Toe game the given player is in.
     *
     * @param player the name of the player
     * @return the Tic-Tac-Toe game the given player is in, or null if the player is not in a game
     */
    public TicTacToe getGameByPlayer(String player) {
        return games.values().stream().filter(game -> game.getPlayer1().equals(player) || (game.getPlayer2() != null &&
                game.getPlayer2().equals(player))).findFirst().orElse(null);
    }

    /**
     * Removes the Tic-Tac-Toe game with the given game ID.
     *
     * @param gameId the ID of the game to remove
     */
    public void removeGame(String gameId) {
        games.remove(gameId);
    }
}
