package com.game.chess.models;

import com.game.chess.enums.Side;


public class Castle {
  private final Square king;
  private final Square kingTo;
  private final Square rook;
  private final Square rookTo;

  public Castle(Square king, Square kingTo, Square rook, Square rookTo) {
    this.king = king;
    this.kingTo = kingTo;
    this.rook = rook;
    this.rookTo = rookTo;
  }

  public Square getKing() {
    return king;
  }

  public Square getKingTo() {
    return kingTo;
  }

  public Square getRook() {
    return rook;
  }

  public Square getRookTo() {
    return rookTo;
  }

  public Side getSide() {
    return kingTo.getFileEnum() == File.C ? Side.QueenSide : Side.KingSide;
  }

  public boolean isStandard() {
    return king.getFileEnum() == File.E
        && (rook.getFileEnum() == File.A || rook.getFileEnum() == File.H);
  }
}
