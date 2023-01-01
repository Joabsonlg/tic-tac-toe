package com.joabsonlg.tictactoewebsocket.manager;

import com.joabsonlg.tictactoewebsocket.enumeration.GameState;
import com.joabsonlg.tictactoewebsocket.model.TicTacToe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TicTacToeManagerTest {

    @Mock TicTacToeManager ticTacToeManager;

    //// joinGame() ////

    /**
     * Tests the {@link TicTacToeManager#joinGame(String)} method when the player is the first to join a game.
     */
    @Test
    void testJoinGame_firstPlayer() {
        TicTacToeManager manager = new TicTacToeManager();
        TicTacToe game = manager.joinGame("player1");
        Assertions.assertEquals("player1", game.getPlayer1());
        Assertions.assertNull(game.getPlayer2());
        Assertions.assertEquals(GameState.WAITING_FOR_PLAYER, game.getGameState());
    }

    /**
     * Tests the {@link TicTacToeManager#joinGame(String)} method when the player is the second to join a game.
     */
    @Test
    void testJoinGame_secondPlayer() {
        TicTacToeManager manager = new TicTacToeManager();
        manager.joinGame("player1");
        TicTacToe game = manager.joinGame("player2");
        Assertions.assertEquals("player1", game.getPlayer1());
        Assertions.assertEquals("player2", game.getPlayer2());
        Assertions.assertEquals(GameState.PLAYER1_TURN, game.getGameState());
    }

    /**
     * Tests the {@link TicTacToeManager#joinGame(String)} method when the player is already in a game.
     */
    @Test
    void testJoinGame_alreadyInGame() {
        TicTacToeManager manager = new TicTacToeManager();
        TicTacToe game = manager.joinGame("player1");
        TicTacToe game2 = manager.joinGame("player1");
        Assertions.assertEquals(game, game2);
        Assertions.assertEquals("player1", game2.getPlayer1());
        Assertions.assertNull(game2.getPlayer2());
        Assertions.assertEquals(GameState.WAITING_FOR_PLAYER, game2.getGameState());
    }


    //// leaveGame() ////

    /**
     * Tests the {@link TicTacToeManager#leaveGame(String)} method when the player to be removed is player 1.
     */
    @Test
    void testLeaveGame_player1() {
        TicTacToeManager manager = new TicTacToeManager();
        TicTacToe game = manager.joinGame("player1");
        manager.leaveGame("player1");
        Assertions.assertNull(manager.getGame(game.getGameId()));
    }

    /**
     * Tests the {@link TicTacToeManager#leaveGame(String)} method when the player to be removed is player 2.
     */
    @Test
    void testLeaveGame_player2() {
        TicTacToeManager manager = new TicTacToeManager();
        TicTacToe game = manager.joinGame("player1");
        manager.joinGame("player2");
        manager.leaveGame("player2");
        Assertions.assertNotNull(manager.getGame(game.getGameId()));
        Assertions.assertEquals("player1", manager.getGame(game.getGameId()).getPlayer1());
        Assertions.assertNull(manager.getGame(game.getGameId()).getPlayer2());
    }

    /**
     * Tests the {@link TicTacToeManager#leaveGame(String)} method when the player to be removed is a waiting player.
     */
    @Test
    void testLeaveGame_waitingPlayer() {
        TicTacToeManager manager = new TicTacToeManager();
        manager.joinGame("player1");
        manager.leaveGame("player1");
        Assertions.assertTrue(manager.waitingPlayers.isEmpty());
    }

    /**
     * Tests the {@link TicTacToeManager#leaveGame(String)} method when the player to be removed is not in any game.
     */
    @Test
    void testLeaveGame_nonexistentPlayer() {
        TicTacToeManager manager = new TicTacToeManager();
        TicTacToe game = manager.joinGame("player1");
        manager.leaveGame("player2");
        Assertions.assertNotNull(manager.getGame(game.getGameId()));
        Assertions.assertEquals("player1", manager.getGame(game.getGameId()).getPlayer1());
        Assertions.assertNull(manager.getGame(game.getGameId()).getPlayer2());
    }

    //// removeGame() ////

    /**
     * Tests the {@link TicTacToeManager#removeGame(String)} method when the game to be removed exists.
     */
    @Test
    void testRemoveGame_exists() {
        TicTacToeManager manager = new TicTacToeManager();
        TicTacToe game = manager.joinGame("player1");
        manager.removeGame(game.getGameId());
        Assertions.assertNull(manager.getGame(game.getGameId()));
    }

    /**
     * Tests the {@link TicTacToeManager#removeGame(String)} method when the game to be removed does not exist.
     */
    @Test
    void testRemoveGame_doesNotExist() {
        TicTacToeManager manager = new TicTacToeManager();
        TicTacToe game = manager.joinGame("player1");
        manager.removeGame("invalid_game_id");
        Assertions.assertNotNull(manager.getGame(game.getGameId()));
    }

    //// getGame() ////

    /**
     * Tests the {@link TicTacToeManager#getGame(String)} method when the game exists.
     */
    @Test
    void testGetGame_exists() {
        TicTacToeManager manager = new TicTacToeManager();
        TicTacToe game = manager.joinGame("player1");
        Assertions.assertEquals(game, manager.getGame(game.getGameId()));
    }

    /**
     * Tests the {@link TicTacToeManager#getGame(String)} method when the game does not exist.
     */
    @Test
    void testGetGame_doesNotExist() {
        TicTacToeManager manager = new TicTacToeManager();
        TicTacToe game = manager.joinGame("player1");
        Assertions.assertNull(manager.getGame("invalid_game_id"));
    }
}
