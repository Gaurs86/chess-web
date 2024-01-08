package com.game.chess.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.game.chess.enums.Color;
import com.game.chess.enums.Status;
import com.game.chess.models.Board;
import com.game.chess.models.ChessBoard;

@Service
public class GameLogicService {
  
  private final BoardService boardService;
  private final MovesValidationService moveService;
  
  
  
  public GameLogicService(BoardService boardService, MovesValidationService moveService) {
    super();
    this.boardService = boardService;
    this.moveService = moveService;
  }



  public boolean isCheckmate(ChessBoard chessBoard) {
    
    return check(chessBoard.getBoard(), chessBoard.getCurrentPlayer()) && moveService.validMoves(chessBoard).isEmpty();
    
  }
  
  public boolean isStalemate(ChessBoard chessBoard) {
    return !check(chessBoard.getBoard(), chessBoard.getCurrentPlayer()) && moveService.validMoves(chessBoard).isEmpty();
  }



  private boolean check(Board board, Color currentPlayer) {
    return boardService.isCheck(board, currentPlayer);
  }
  
  
  public boolean isAutoDraw(ChessBoard chessBoard) {
    return fiftyMoves(chessBoard.getHalfMoveClock()) || isInsufficientMaterial(chessBoard.getBoard(), chessBoard.getCurrentPlayer());
  }


  private boolean isInsufficientMaterial(Board board, Color currentPlayer) {

    return InsufficientMatingMaterial.isInsufficientMatingMaterial(board, currentPlayer);
  }

  private boolean fiftyMoves(int halfMoveClock) {
    return halfMoveClock >= 100;
  }
  
  
  public boolean end(ChessBoard chessBoard) {
    return isCheckmate(chessBoard) || isStalemate(chessBoard) || isAutoDraw(chessBoard);
  }

  

  public Optional<Status> status(ChessBoard chessBoard) {
    if (isCheckmate(chessBoard)) {
      return Optional.of(Status.Mate);
    } else if (isStalemate(chessBoard)) {
      return Optional.of(Status.Stalemate);
    } else if (isAutoDraw(chessBoard)) {
      return Optional.of(Status.Draw);
    } else {
      return Optional.empty();
    }
  }



  public Optional<Color> winner(ChessBoard chessBoard) {
    if (isCheckmate(chessBoard)) {
      return Optional.of(chessBoard.getCurrentPlayer().negate());
    } else {
      return Optional.empty();
    }
  }

  
  

}
