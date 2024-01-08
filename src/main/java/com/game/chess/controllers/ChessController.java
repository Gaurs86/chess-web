package com.game.chess.controllers;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.chess.entity.Player;
import com.game.chess.request.CreateGameRequestDTO;
import com.game.chess.request.JoinRequestDTO;
import com.game.chess.response.Message;
import com.game.chess.models.ChessGameDetails;
import com.game.chess.service.ChessGame;
import com.game.chess.service.ChessService;
import jakarta.websocket.server.PathParam;



@RestController
@RequestMapping("/api")
public class ChessController {

  private final ChessService chessService;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private static final Logger logger = LoggerFactory.getLogger(ChessController.class);



  @Autowired
  public ChessController(ChessService chessService, SimpMessagingTemplate simpMessagingTemplate) {
    super();
    this.chessService = chessService;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @PostMapping("/create-game")
  public ChessGameDetails createGame(@RequestBody CreateGameRequestDTO request) {
    logger.info("Create game controller entered: {}", request);

    Player player = new Player();
    player.setColor(request.getColor());
    player.setPlayerId((int)Math.floor(Math.random()*1000));

    return chessService.createNewGame(player);
  }

  @PostMapping("/join-game")
  public ChessGameDetails joinGame(@RequestBody JoinRequestDTO request) {
    logger.info("Join game controller entered: {}", request);

    Player player = new Player();
    if(request.getColor()!=null) {
      player.setColor(request.getColor());
    }
    
    player.setPlayerId((int)Math.floor(Math.random()*1000));

    ChessGameDetails gameDetails = chessService.joinGame(player, request.getGameId());
    
    simpMessagingTemplate.convertAndSend("/chess/status/"+request.getGameId(),gameDetails);
    
    return gameDetails;

  }
  
  
  @MessageMapping("/status/{gameId}")
  @SendTo("/chess/status/{gameId}")
  public ChessGameDetails gameStatus(@PathVariable int gameId) {
    logger.info("Game status requested for Game ID: {}", gameId);
    
    return chessService.getGameStatus(gameId);
    
    
  }
  
  @MessageMapping("/move/{gameId}")
  @SendTo("/chess/game/{gameId}")
  public Message move(@DestinationVariable(value = "gameId") int gameId, @Payload Message move) {

    logger.info("Move controller entered: Move - {}", move.toString());

    return chessService.handleMove(gameId, move);



  }
  
  
  @PostMapping("/play/{gameId}")
  public Message play(@PathVariable (value = "gameId") int gameId, @RequestBody Message move) {

    logger.info("Move controller entered: Move - {}", move.toString());

    Message message = chessService.handleMove(gameId, move);
    
    return message;

  }






}
