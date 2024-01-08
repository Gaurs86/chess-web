package com.game.chess.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.game.chess.enums.CheckStatus;
import com.game.chess.enums.Color;
import com.game.chess.enums.PieceType;
import com.game.chess.enums.Side;
import com.game.chess.exception.MoveException;
import com.game.chess.models.Board;
import com.game.chess.models.Castle;
import com.game.chess.models.Castles;
import com.game.chess.models.ChessBoard;
import com.game.chess.models.Move;
import com.game.chess.models.Piece;
import com.game.chess.models.Square;
import com.game.chess.models.UnmovedRooks;

@Service
public class ChessGame {


  private final MovesValidationService moveService;
  private static final Logger logger = LoggerFactory.getLogger(ChessGame.class);


  @Autowired
  public ChessGame(MovesValidationService moveService) {
    super();
    this.moveService = moveService;
  }


  public Optional<ChessBoard> move(ChessBoard chessBoard, Square from, Square to,
      Optional<PieceType> promotion) {
    
    logger.info("Move recived from {} to {} with promotion: {}", from, to, promotion);
    logger.info("Board before move : {}", chessBoard.printBoard());
    
    List<Move> moves1 = moveService.generateMovesAt(chessBoard, from);
    logger.info("Generated moves: {}", moves1);
    
    Optional<Move> foundMove =
        moves1.stream().filter(move -> move.getDest().equals(to)).findFirst();
    logger.info("Found move: {}", foundMove);

    try {
      Piece piece = chessBoard.getBoard().pieceAt(from)
          .orElseThrow(() -> new MoveException("No piece on " + from.key()));

      logger.info("Selected piece: {}", piece);
      if (piece.getColor() != chessBoard.getCurrentPlayer()) {
        throw new MoveException("Not my piece on " + from.key());
      }

      Move m1 = foundMove.orElseThrow(
          () -> new MoveException("Piece on " + from.key() + " cannot move to " + to.key()));


      Optional<Board> after;

      if (m1.isCaptures()) {
        Optional<Square> taken = m1.getCapture();
        after = chessBoard.getBoard().taking(from, to, taken);
      } else if(m1.isCastles()) {
        
        Castle castle = m1.getCastle().get();
        
        Square king = castle.getKing();
        Square rook = castle.getRook();
        Square kingTo = castle.getKingTo();
        Square rookTo = castle.getRookTo();
        Color currentPlayer = chessBoard.getCurrentPlayer();
        
        after = chessBoard.getBoard().take(king).flatMap(b1 -> b1.take(rook))
            .flatMap(b2 -> b2.put(currentPlayer.king(), kingTo))
            .flatMap(b3 -> b3.put(currentPlayer.rook(), rookTo));
        
      } else {
        after = chessBoard.getBoard().move(from, to);
      }

      Board afterBoard = after.get();

      
      Color curPlayer = chessBoard.getCurrentPlayer().negate();

      int halfMoveClock = chessBoard.getHalfMoveClock();
      if (m1.isCaptures() || m1.isPromotes() || piece.is(PieceType.Pawn)) {
        halfMoveClock = 0;
      } else {
        halfMoveClock++;
      }

      Castles castleRights = chessBoard.getCastles();
      UnmovedRooks unmovedRooks = chessBoard.getUnmovedrooks();

      // If the rook is captured
      // remove the captured rook from unmovedRooks
      // check the captured rook's side and remove it from castlingRights
      if (m1.isCaptures()) {
        Optional<Optional<Side>> sideResult = unmovedRooks.side(to);
        if (sideResult.isPresent()) {

          Optional<Side> sideOptional = sideResult.get();
          unmovedRooks = unmovedRooks.and(to.bb().not());
          if (sideOptional.isPresent()) {
            Side side = sideOptional.get();
            castleRights = castleRights.without(piece.getColor().negate(), side);
          } else {
            // There is only one unmoved rook left so just remove the color from castlingRights
            castleRights = castleRights.without(piece.getColor().negate());
          }


        }
      }

      // If a Rook is moved
      // Remove that rook from unmovedRooks.
      // check the captured rook's side and remove it from castlingRights
      if (piece.is(PieceType.Rook) && unmovedRooks.contains(from)) {
        Optional<Optional<Side>> sideResult = unmovedRooks.side(from);
        if (sideResult.isPresent()) {
          unmovedRooks = unmovedRooks.and(from.bb().not());
          Optional<Side> sideOptional = sideResult.get();

          if (sideOptional.isPresent()) {
            castleRights = castleRights.without(piece.getColor(), sideOptional.get());
          } else {
            // There is only one unmoved rook left so just remove the color from castlingRights
            castleRights = castleRights.without(piece.getColor());
          }

        }
      }

      // If the King is moved
      // remove castlingRights and unmovedRooks for the moving side
      else if (piece.is(PieceType.King)) {
        unmovedRooks = unmovedRooks.without(piece.getColor());
        castleRights = castleRights.without(piece.getColor());
      }


      List<Move> moves = new ArrayList<Move>();

      moves.addAll(chessBoard.getMoves());
      moves.add(m1);
      ChessBoard updatedChessBoard = new ChessBoard(afterBoard, castleRights, curPlayer,
          halfMoveClock, 0, moves, Optional.of(m1), unmovedRooks);
      
      logger.info("Board after move : {}", updatedChessBoard.printBoard());



      // if (!ChessUtils.isValidPromotion(promotion)) {
      // throw new MoveException("Cannot promote to " + promotion + " in this game mode");
      // }

      // Move m2 = normalizeCastle(m1);

      return Optional.of(updatedChessBoard);
    } catch (MoveException e) {
      logger.error("Move exception: {}", e.getMessage());
      return Optional.empty();
    }
  }


  private Move normalizeCastle(Move move) {
    if (move.isCastles()) {
      Castle rawCastle = move.getCastle().get();
      Square rookOrig = rawCastle.getKing();
      return new Move(move.getPiece(), rookOrig, move.getDest(), move.getCapture(),
          move.getPromotion(), Optional.empty(), move.isEnpassant());
    } else {
      return move;
    }
  }


}
