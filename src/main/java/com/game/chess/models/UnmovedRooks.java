package com.game.chess.models;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import com.game.chess.enums.Color;
import com.game.chess.enums.Side;

public final class UnmovedRooks {
  private final long value;

  private UnmovedRooks(long value) {
    this.value = value;
  }


  public static final UnmovedRooks DEFAULT =
      new UnmovedRooks(Bitboard.rank(Rank.First).getValue() | Bitboard.rank(Rank.Eight).getValue());
  public static final UnmovedRooks CORNERS = new UnmovedRooks(0x8100000000000081L);
  public static final UnmovedRooks NONE = new UnmovedRooks(0L);


  public static UnmovedRooks apply(Bitboard b) {
    return new UnmovedRooks(b.getValue());
  }

  public static UnmovedRooks apply(long l) {
    return new UnmovedRooks(l);
  }

  public static UnmovedRooks apply(Iterable<Square> xs) {
    long result = StreamSupport.stream(xs.spliterator(), false).mapToLong(Square::bl).reduce(0L,
        (b, s) -> b | s);
    return new UnmovedRooks(result);
  }

  // guess unmovedRooks from board
  // we assume rooks are on their initial position
  public static UnmovedRooks from(Board board) {
    long wr = board.getRooks().getValue() & board.getWhites().getValue()
        & Bitboard.rank(Color.White.getBackRank()).getValue();
    long br = board.getRooks().getValue() & board.getBlacks().getValue()
        & Bitboard.rank(Color.Black.getBackRank()).getValue();
    return new UnmovedRooks(wr | br);
  }

  public long value() {
    return value;
  }

  public Bitboard bb() {
    return new Bitboard(value);
  }

  public boolean isEmpty() {
    return value == 0L;
  }

  public List<Square> toList() {
    return bb().getSquares();
  }

  public UnmovedRooks without(Color color) {
    long rankMask = Bitboard.rank(color.getLastRank()).getValue();
    return new UnmovedRooks(value & rankMask);
  }

  // Try to guess the side of the rook at position `square`
  // If the position is not an unmovedRook, return None
  // If the position is an unmovedRook but there is no other rook on the
  // same rank, return Some(None) (because we cannot guess)
  // If there are two rooks on the same rank, return the side of the rook
  public Optional<Optional<Side>> side(Square square) {
    Bitboard rook = square.bb();
    if (rook.isDisjoint(value)) {
      return Optional.empty();
    } else {
      Optional<Square> otherRookOptional =
          Bitboard.rank(square.getRankEnum()).and(rook.not()).and(value).first();

      if (otherRookOptional.isPresent()) {
        Square otherRook = otherRookOptional.get();
        if (otherRook.getFile() > square.getFile()) {
          return Optional.of(Optional.of(Side.QueenSide));
        } else {
          return Optional.of(Optional.of(Side.KingSide));
        }
      } else {
        return Optional.of(Optional.empty());
      }


    }
  }

  public boolean contains(Square square) {
    return (value & (1L << square.getValue())) != 0L;
  }

  public UnmovedRooks unaryNot() {
    return new UnmovedRooks(~value);
  }

  public UnmovedRooks and(long other) {
    return new UnmovedRooks(value & other);
  }

  public UnmovedRooks xor(long other) {
    return new UnmovedRooks(value ^ other);
  }

  public UnmovedRooks or(long other) {
    return new UnmovedRooks(value | other);
  }


  public UnmovedRooks and(Bitboard o) {
    return new UnmovedRooks(value & o.getValue());
  }


  public UnmovedRooks xor(Bitboard o) {
    return new UnmovedRooks(value ^ o.getValue());
  }


  public UnmovedRooks or(Bitboard o) {
    return new UnmovedRooks(value | o.getValue());
  }
}
