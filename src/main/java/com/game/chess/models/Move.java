package com.game.chess.models;

import java.util.Optional;
import com.game.chess.enums.PieceType;



public class Move {

  private final Piece piece;
  private final Square orig;
  private final Square dest;
  private final Optional<Square> capture;
  private final Optional<Piece> promotion;
  private final Optional<Castle> castle;
  private final boolean enpassant;

  public Move(Piece piece, Square orig, Square dest, Optional<Square> capture,
      Optional<Piece> promotion, Optional<Castle> castle, boolean enpassant) {
    super();
    this.piece = piece;
    this.orig = orig;
    this.dest = dest;
    this.capture = capture;
    this.promotion = promotion;
    this.castle = castle;
    this.enpassant = enpassant;
  }

  public boolean isEnpassant() {
    return enpassant;
  }

  public Piece getPiece() {
    return piece;
  }

  public Square getOrig() {
    return orig;
  }

  public Square getDest() {
    return dest;
  }

  public Optional<Square> getCapture() {
    return capture;
  }

  public Optional<Piece> getPromotion() {
    return promotion;
  }

  public Optional<Castle> getCastle() {
    return castle;
  }

  public boolean isCastles() {
    return castle.isPresent();

  }

  public boolean isPromotes() {
    return promotion.isPresent();
  }

  public boolean isCaptures() {
    return capture.isPresent();
  }

  public Move withPromotion(PieceType orElse) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    return "Move [piece=" + piece + ", orig=" + orig + ", dest=" + dest + ", capture=" + capture
        + ", promotion=" + promotion + ", castle=" + castle + ", enpassant=" + enpassant + "]";
  }



}
