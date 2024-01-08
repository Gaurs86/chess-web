package com.game.chess.enums;

import java.util.List;
import java.util.Optional;
import com.game.chess.models.Piece;
import com.game.chess.models.Rank;
import com.game.chess.models.Square;

public enum Color {

  White("white", 0), Black("black", 1);


  private final String name;
  private final int value;

  private Color(String name, int value) {
    this.value = value;
    this.name = name;
  }

  public int getValue() {
    return value;
  }

  public String getName() {
    return name;
  }

  public boolean isWhite() {
    return this == White;
  }

  public boolean isBlack() {
    return this == Black;
  }

  public Color negate() {
    return isWhite() ? Black : White;
  }

  public Rank getBackRank() {
    return this == White ? Rank.First : Rank.Eight;
  }

  public Rank getThirdRank() {
    return this == White ? Rank.Third : Rank.Sixth;
  }

  public Rank getFourthRank() {
    return this == White ? Rank.Fourth : Rank.Fifth;
  }

  public Rank getFifthRank() {
    return this == White ? Rank.Fifth : Rank.Fourth;
  }

  public Rank getSixthRank() {
    return this == White ? Rank.Sixth : Rank.Third;
  }

  public Rank getSeventhRank() {
    return this == White ? Rank.Seventh : Rank.Second;
  }

  public Rank getLastRank() {
    return this == White ? Rank.Eight : Rank.First;
  }

  public Rank getPassablePawnRank() {
    return getFifthRank();
  }

  public Rank getPromotablePawnRank() {
    return getLastRank();
  }

  public static Color fromWhite(boolean white) {
    return white ? White : Black;
  }

  public static final Color white = White;
  public static final Color black = Black;

  public static final List<Color> all = List.of(White, Black);


  public Square at(Side side) {
    switch (this) {
      case White:
        return switch (side) {
          case KingSide -> Square.H1;
          case QueenSide -> Square.A1;
        };
      case Black:
        return switch (side) {
          case KingSide -> Square.H8;
          case QueenSide -> Square.A8;
        };
    }
    return null;
  }

  public Square kingSide() {
    return at(Side.KingSide);
  }

  public Square queenSide() {
    return at(Side.QueenSide);
  }

  public Piece minus(PieceType type) {
    return new Piece(this, type);
  }

  public Piece pawn() {
    return minus(PieceType.Pawn);
  }

  public Piece bishop() {
    return minus(PieceType.Bishop);
  }

  public Piece knight() {
    return minus(PieceType.Knight);
  }

  public Piece rook() {
    return minus(PieceType.Rook);
  }

  public Piece queen() {
    return minus(PieceType.Queen);
  }

  public Piece king() {
    return minus(PieceType.King);
  }



}
