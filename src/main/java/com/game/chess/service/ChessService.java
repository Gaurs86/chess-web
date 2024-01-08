package com.game.chess.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.game.chess.entity.Game;
import com.game.chess.entity.Player;
import com.game.chess.enums.PieceType;
import com.game.chess.enums.Status;
import com.game.chess.models.Bitboard;
import com.game.chess.models.ChessBoard;
import com.game.chess.models.ChessGameDetails;
import com.game.chess.models.Square;
import com.game.chess.repository.GameRepository;
import com.game.chess.repository.PlayerRepository;
import com.game.chess.response.Message;
import com.game.chess.response.Payload;
import chessutils.ChessUtils;

@Service
public class ChessService {

  private static final Logger logger = LoggerFactory.getLogger(ChessService.class);
  private final PlayerRepository playerRepository;
  private final GameRepository gameRepository;
  private final ChessGameStateManager gameStateManager;
  private final ChessGame chessGameService;
  private final BoardService boardService;
  private final MovesValidationService moveService;



  @Autowired
  public ChessService(PlayerRepository playerRepository, GameRepository gameRepository,
      ChessGameStateManager gameStateManager, ChessGame chessGame, BoardService boardService, MovesValidationService moveService) {
    super();
    this.playerRepository = playerRepository;
    this.gameRepository = gameRepository;
    this.gameStateManager = gameStateManager;
    this.chessGameService = chessGame;
    this.boardService = boardService;
    this.moveService = moveService;
  }



  public ChessGameDetails createNewGame(Player player) {



    int gameId = ChessUtils.generateGameId();
    logger.info("Creating a new game with ID: {}", gameId);

    List<Player> players = new ArrayList<Player>();

    players.add(player);

    ChessGameDetails chessGame = new ChessGameDetails();

    chessGame.setGameId(gameId);
    chessGame.setCreatedAt(Instant.now());
    chessGame.setPlayers(players);
    chessGame.setStatus(Status.Created);
    chessGame.setGame(ChessBoard.init());


    gameStateManager.putGameState(gameId, chessGame);

    Game game = new Game();
    game.setId(gameId);
    game.setStatus(Status.Created.toString());


    player.setGameId(gameId);

    gameRepository.save(game);

    playerRepository.save(player);

    logger.info("New game created successfully. Game ID: {}, Player: {}", gameId, player);
    return chessGame;
  }



  public ChessGameDetails joinGame(Player player, int gameId) {

    Game game = gameRepository.findById(gameId)
        .orElseThrow(() -> new RuntimeException("No Game found with gameId : " + gameId));

    ChessGameDetails chessGameDetails = gameStateManager.getGameState(gameId);

    Player player1 = chessGameDetails.getPlayers().get(0);

    player.setColor(player1.getColor().equalsIgnoreCase("white") ? "black" : "white");
    player.setGameId(gameId);;
    playerRepository.save(player);
    
    
    chessGameDetails.getPlayers().add(player);
    chessGameDetails.setStatus(Status.Started);
    game.setStatus(Status.Started.toString());

    gameRepository.save(game);
    logger.info("Player {} joined the game (ID: {}). Game started.", player, gameId);

    return chessGameDetails;
  }

  public Message handleMove(int gameId, Message move) {



    if ("move".equals(move.getType())) {

      String uci = move.getData().getUci();
      String promotion = move.getData().getPromotion();

      Square from = Square.fromKey(uci.substring(0, 2)).get();
      Square to = Square.fromKey(uci.substring(2, 4)).get();

      Optional<PieceType> promotionPiece = PieceType.all.stream()
          .filter(type -> type.getName().equalsIgnoreCase(promotion)).findFirst();

      ChessGameDetails chessGame = gameStateManager.getGameState(gameId);

      Optional<ChessBoard> chessBoardOptional =
          chessGameService.move(chessGame.getGame(), from, to, promotionPiece);
      
      

      if (chessBoardOptional.isPresent()) {
        ChessBoard chessBoard = chessBoardOptional.get();
        
        chessGame.setGame(chessBoard);

        String fen = boardService.createFen(chessBoard.getBoard());
        String san = "";
        Payload data = move.getData();

        data.setFen(fen);
        logger.info("Board after move : {}", chessGame.getGame().printBoard());
        Map<Square, Bitboard> destinations = moveService.destinations(chessBoard);

        Map<String, String> dests = new HashMap<String, String>();

        destinations.forEach((s, b) -> {
          dests.put(s.toString(), b.getSquares().toString());

        });
        data.setDests(dests);
        logger.info("Board after move : {}", chessGame.getGame().printBoard());
        
        boolean check = boardService.isCheck(chessBoard.getBoard(), chessBoard.getCurrentPlayer());
        logger.info("Board after move : {}", chessGame.getGame().printBoard());
        logger.info("Move details - FEN: {}, Destinations: {}, Check: {}, SAN: {}", fen, dests,
            check, san);
        data.setCheck(check);
        data.setSan(san);
      }

    } else if (move.getType().equalsIgnoreCase("game")) {

      ChessGameDetails chessGame = gameStateManager.getGameState(gameId);

      ChessBoard chessBoard = chessGame.getGame();

      String fen = boardService.createFen(chessBoard.getBoard());
      Payload data = move.getData();

      data.setFen(fen);

      Map<Square, Bitboard> destinations = moveService.destinations(chessBoard);

      Map<String, String> dests = new HashMap<String, String>();

      destinations.forEach((s, b) -> {
        dests.put(s.toString(), b.getSquares().toString());

      });
      data.setDests(dests);



    }



    return move;

  }


  public ChessGameDetails getGameStatus(int gameId) {
    logger.info("Fetching game status for Game ID: {}", gameId);
    return gameStateManager.getGameState(gameId);
  }



}
