package com.game.chess.service;

import java.util.Optional;
import com.game.chess.enums.Color;
import com.game.chess.enums.PieceType;
import com.game.chess.models.Board;
import com.game.chess.models.Square;

public class InsufficientMatingMaterial {

  // Verify if there are at least two bishops of opposite color
  // no matter which sides they are on
  public static boolean bishopsOnOppositeColors(Board board) {
    return board.getBishops().map(Square::isLight).size() == 2;

  }
  //
  // /*
  // * Returns true if a pawn cannot progress forward because it is blocked by a pawn
  // * and it doesn't have any capture
  // */
  // public static boolean pawnBlockedByPawn(Square pawn, CBoard board) {
  // return board.getPawns()
  //
  // if (optionalPawn.isPresent()) {
  // Piece p = optionalPawn.get();
  // if (p.is(PieceType.PAWN)) {
  // if (Situation.generateMovesAt(board, pawn).isEmpty()) {
  // Optional<Square> blockingPosition = posAheadOfPawn(pawn, p.getColor());
  // return blockingPosition.flatMap(board::getPiece)
  // .filter(piece -> piece.is(PieceType.PAWN))
  // .isPresent();
  // }
  // }
  // }
  // return false;
  // }

  /*
   * Determines whether a board position is an automatic draw due to neither player being able to
   * mate the other as informed by the traditional chess rules.
   */
  public static boolean isDraw(Board board) {
    return board.kingsAndMinorsOnly() && (board.getNbPieces() <= 3
        || (board.kingsAndBishopsOnly() && !bishopsOnOppositeColors(board)));
  }

  /*
   * Determines whether a color does not have mating material. In general: King by itself is not
   * mating material King + knight mates against king + any(rook, bishop, knight, pawn) King +
   * bishop mates against king + any(bishop, knight, pawn) King + bishop(s) versus king + bishop(s)
   * depends upon bishop square colors
   */
  public static boolean isInsufficientMatingMaterial(Board board, Color color) {
    if (board.kingsOnlyOf(color)) {
      return true;
    } else if (board.kingsAndKnightsOnlyOf(color)) {
      return board.nonKingsOf(color).count() == 1
          && board.onlyOf(color.negate(), board.getKings().or(board.getQueens()));
    } else if (board.kingsAndBishopsOnlyOf(color)) {
      return !(bishopsOnOppositeColors(board)
          || board.getPieceWithColorBB(color.negate(), PieceType.Knight)
              .or(board.getPieceWithColorBB(color.negate(), PieceType.Pawn)).nonEmpty());
    }
    return false;
  }

  /**
   * Determines the position one ahead of a pawn based on the color of the piece. White pawns move
   * up and black pawns move down.
   */
  public static Optional<Square> posAheadOfPawn(Square square, Color color) {
    return color.isWhite() ? square.up() : square.down();
  }
}
