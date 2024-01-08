package com.game.chess.models;


import com.game.chess.enums.Color;
import com.game.chess.enums.Side;


public final class Castles {
  private final long value;

  private Castles(long getValue) {
    this.value = getValue;
  }

  public static Castles apply(long getValue) {
    return new Castles(getValue);
  }

  public static Castles apply(Bitboard bb) {
    return init.and(bb);
  }

  public boolean can(Color color) {
    return Bitboard.rank(color.getBackRank()).intersects(value);
  }

  public boolean can(Color color, Side side) {
    return contains(color.at(side));
  }

  public boolean isEmpty() {
    return value == 0L;
  }

  public boolean whiteKingSide() {
    return contains(Square.H1);
  }

  public boolean whiteQueenSide() {
    return contains(Square.A1);
  }

  public boolean blackKingSide() {
    return contains(Square.H8);
  }

  public boolean blackQueenSide() {
    return contains(Square.A8);
  }

  public Castles without(Color color) {
    return new Castles(value & Bitboard.rank(color.getLastRank()).getValue());
  }

  public Castles without(Color color, Side side) {
    return new Castles(value & ~color.at(side).getValue());
  }

  public Castles add(Color color, Side side) {
    return new Castles(value | color.at(side).getValue());
  }

  public Castles update(Color color, boolean kingSide, boolean queenSide) {
    return new Castles((value & ~color.getValue()) | (kingSide ? color.kingSide().getValue() : 0L)
        | (queenSide ? color.queenSide().getValue() : 0L));
  }

  public boolean[] toSeq() {
    return new boolean[] {whiteKingSide(), whiteQueenSide(), blackKingSide(), blackQueenSide()};
  }

  public Castles unaryNot() {
    return new Castles(~value);
  }

  public Castles and(long other) {
    return new Castles(value & other);
  }

  public Castles xor(long other) {
    return new Castles(value ^ other);
  }

  public Castles or(long other) {
    return new Castles(value | other);
  }

  public Castles and(Bitboard o) {
    return new Castles(value & o.getValue());
  }

  public Castles xor(Bitboard o) {
    return new Castles(value ^ o.getValue());
  }

  public Castles or(Bitboard o) {
    return new Castles(value | o.getValue());
  }

  public long getValue() {
    return value;
  }

  public boolean contains(Square square) {
    return (value & square.bl()) != 0L;
  }

  public Castles addSquare(Square square) {
    return new Castles(value | square.getValue());
  }


  public static Castles apply(boolean whiteKingSide, boolean whiteQueenSide, boolean blackKingSide,
      boolean blackQueenSide) {
    return new Castles((whiteKingSide ? Color.White.kingSide().getValue() : 0L)
        | (whiteQueenSide ? Color.White.queenSide().getValue() : 0L)
        | (blackKingSide ? Color.Black.kingSide().getValue() : 0L)
        | (blackQueenSide ? Color.Black.queenSide().getValue() : 0L));
  }

  public static final Castles init = Castles.apply(0x8100000000000081L);
  public static final Castles none = Castles.apply(0L);
  public static final Castles black = Castles.apply(0x8100000000000000L);

  public static final Castles charToSquare(char c) {
    return switch (c) {
      case 'k' -> Castles.apply(Square.H8.getValue());
      case 'q' -> Castles.apply(Square.A8.getValue());
      case 'K' -> Castles.apply(Square.H1.getValue());
      case 'Q' -> Castles.apply(Square.A1.getValue());
      default -> Castles.none;
    };
  }


}
