package com.joabsonlg.tictactoewebsocket.controller;

import com.joabsonlg.tictactoewebsocket.enumeration.GameState;
import com.joabsonlg.tictactoewebsocket.manager.TicTacToeManager;
import com.joabsonlg.tictactoewebsocket.model.TicTacToe;
import com.joabsonlg.tictactoewebsocket.model.dto.JoinMessage;
import com.joabsonlg.tictactoewebsocket.model.dto.PlayerMessage;
import com.joabsonlg.tictactoewebsocket.model.dto.TicTacToeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Controller class for handling WebSocket messages and managing the Tic-Tac-Toe games.
 *
 * @author Joabson Arley do Nascimento
 */
@Controller
public class MessageController {

    /**
     * Template for sending messages to clients through the message broker.
     */
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Manager for the Tic-Tac-Toe games.
     */
    private final TicTacToeManager ticTacToeManager = new TicTacToeManager();

    /**
     * Handles a request from a client to join a Tic-Tac-Toe game.
     * If a game is available and the player is successfully added to the game,
     * the current state of the game is sent to all subscribers of the game's topic.
     *
     * @param message the message from the client containing the player's name
     * @return the current state of the game, or an error message if the player was unable to join
     */
    @MessageMapping("/game.join")
    @SendTo("/topic/game.state")
    public Object joinGame(@Payload JoinMessage message, SimpMessageHeaderAccessor headerAccessor) {
        TicTacToe game = ticTacToeManager.joinGame(message.getPlayer());
        if (game == null) {
            TicTacToeMessage errorMessage = new TicTacToeMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Não foi possível entrar no jogo. Talvez o jogo já esteja cheio ou ocorreu um erro interno.");
            return errorMessage;
        }
        headerAccessor.getSessionAttributes().put("gameId", game.getGameId());
        headerAccessor.getSessionAttributes().put("player", message.getPlayer());

        TicTacToeMessage gameMessage = gameToMessage(game);
        gameMessage.setType("game.joined");
        return gameMessage;
    }

    /**
     * Handles a request from a client to leave a Tic-Tac-Toe game.
     * If the player is successfully removed from the game, a message is sent to subscribers
     * of the game's topic indicating that the player has left.
     *
     * @param message the message from the client containing the player's name
     */
    @MessageMapping("/game.leave")
    public void leaveGame(@Payload PlayerMessage message) {
        TicTacToe game = ticTacToeManager.leaveGame(message.getPlayer());
        if (game != null) {
            TicTacToeMessage gameMessage = gameToMessage(game);
            gameMessage.setType("game.left");
            messagingTemplate.convertAndSend("/topic/game." + game.getGameId(), gameMessage);
        }
    }

    /**
     * Handles a request from a client to make a move in a Tic-Tac-Toe game.
     * If the move is valid, the game state is updated and sent to all subscribers of the game's topic.
     * If the game is over, a message is sent indicating the result of the game.
     *
     * @param message the message from the client containing the player's name, game ID, and move
     */
    @MessageMapping("/game.move")
    public void makeMove(@Payload TicTacToeMessage message) {
        String player = message.getSender();
        String gameId = message.getGameId();
        int move = message.getMove();
        TicTacToe game = ticTacToeManager.getGame(gameId);

        if (game == null || game.isGameOver()) {
            TicTacToeMessage errorMessage = new TicTacToeMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Game not found or is already over.");
            this.messagingTemplate.convertAndSend("/topic/game." + gameId, errorMessage);
            return;
        }

        if (game.getGameState().equals(GameState.WAITING_FOR_PLAYER)) {
            TicTacToeMessage errorMessage = new TicTacToeMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Game is waiting for another player to join.");
            this.messagingTemplate.convertAndSend("/topic/game." + gameId, errorMessage);
            return;
        }

        if (game.getTurn().equals(player)) {
            game.makeMove(player, move);

            TicTacToeMessage gameStateMessage = new TicTacToeMessage(game);
            gameStateMessage.setType("game.move");
            this.messagingTemplate.convertAndSend("/topic/game." + gameId, gameStateMessage);

            if (game.isGameOver()) {
                TicTacToeMessage gameOverMessage = gameToMessage(game);
                gameOverMessage.setType("game.gameOver");
                this.messagingTemplate.convertAndSend("/topic/game." + gameId, gameOverMessage);
                ticTacToeManager.removeGame(gameId);
            }
        }
    }

    @EventListener
    public void SessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String gameId = headerAccessor.getSessionAttributes().get("gameId").toString();
        String player = headerAccessor.getSessionAttributes().get("player").toString();
        TicTacToe game = ticTacToeManager.getGame(gameId);
        if (game != null) {
            if (game.getPlayer1().equals(player)) {
                game.setPlayer1(null);
                if (game.getPlayer2() != null) {
                    game.setGameState(GameState.PLAYER2_WON);
                    game.setWinner(game.getPlayer2());
                } else {
                    ticTacToeManager.removeGame(gameId);
                }
            } else if (game.getPlayer2() != null && game.getPlayer2().equals(player)) {
                game.setPlayer2(null);
                if (game.getPlayer1() != null) {
                    game.setGameState(GameState.PLAYER1_WON);
                    game.setWinner(game.getPlayer1());
                } else {
                    ticTacToeManager.removeGame(gameId);
                }
            }
            TicTacToeMessage gameMessage = gameToMessage(game);
            gameMessage.setType("game.gameOver");
            messagingTemplate.convertAndSend("/topic/game." + gameId, gameMessage);
            ticTacToeManager.removeGame(gameId);
        }
    }

    private TicTacToeMessage gameToMessage(TicTacToe game) {
        TicTacToeMessage message = new TicTacToeMessage();
        message.setGameId(game.getGameId());
        message.setPlayer1(game.getPlayer1());
        message.setPlayer2(game.getPlayer2());
        message.setBoard(game.getBoard());
        message.setTurn(game.getTurn());
        message.setGameState(game.getGameState());
        message.setWinner(game.getWinner());
        return message;
    }
}
