package com.game.chess.models;



import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.game.chess.enums.Color;
import com.game.chess.enums.PieceType;

public class Piece {

  private final Color color;
  private final PieceType type;



  public Piece(Color color, PieceType type) {
    super();
    this.color = color;
    this.type = type;
  }

  public Color getColor() {
    return color;
  }

  public PieceType getType() {
    return type;
  }

  public boolean is(Color c) {
    return c == color;
  }

  public boolean is(PieceType t) {
    return t == type;
  }

  public boolean isNot(PieceType t) {
    return t != type;
  }

  public Piece not() {
    return new Piece(color.negate(), type);
  }

  public boolean oneOf(Set<PieceType> types) {
    return types.contains(type);
  }

  public boolean isMinor() {
    return oneOf(Set.of(PieceType.Knight, PieceType.Bishop));
  }

  public boolean isMajor() {
    return oneOf(Set.of(PieceType.Queen, PieceType.Rook));
  }

  public char forsyth() {
    return (color == Color.White) ? type.getForsythUpper() : type.getForsyth();
  }

  public boolean eyes(Square from, Square to, Bitboard mask) {
    switch (type) {
      case King:
        return from.kingAttacks().contains(to);
      case Queen:
        return from.queenAttacks(mask).contains(to);
      case Rook:
        return from.rookAttacks(mask).contains(to);
      case Bishop:
        return from.bishopAttacks(mask).contains(to);
      case Knight:
        return from.knightAttacks().contains(to);
      case Pawn:
        return from.pawnAttacks(color).contains(to);
      default:
        return false;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Piece piece = (Piece) obj;
    return color == piece.color && type == piece.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(color, type);
  }

  @Override
  public String toString() {
    return color.toString().toLowerCase() + "-" + type.toString().toLowerCase();
  }

  private static final Map<Character, Piece> ALL_BY_FEN =
      Map.ofEntries(Map.entry('P', new Piece(Color.White, PieceType.Pawn)),
          Map.entry('N', new Piece(Color.White, PieceType.Knight)),
          Map.entry('B', new Piece(Color.White, PieceType.Bishop)),
          Map.entry('R', new Piece(Color.White, PieceType.Rook)),
          Map.entry('Q', new Piece(Color.White, PieceType.Queen)),
          Map.entry('K', new Piece(Color.White, PieceType.King)),
          Map.entry('p', new Piece(Color.Black, PieceType.Pawn)),
          Map.entry('n', new Piece(Color.Black, PieceType.Knight)),
          Map.entry('b', new Piece(Color.Black, PieceType.Bishop)),
          Map.entry('r', new Piece(Color.Black, PieceType.Rook)),
          Map.entry('q', new Piece(Color.Black, PieceType.Queen)),
          Map.entry('k', new Piece(Color.Black, PieceType.King)));

  public static Piece fromChar(char c) {
    return ALL_BY_FEN.get(c);
  }

}
