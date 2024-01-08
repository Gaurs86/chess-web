package com.game.chess.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.springframework.stereotype.Component;


public class Bitboard {
  private final long value;

  public Bitboard(long value) {
    this.value = value;
  }

  public static Bitboard apply(long l) {
    return new Bitboard(l);
  }

  public static Bitboard apply(Iterable<Square> xs) {
    long result = 0L;
    for (Square s : xs) {
      result |= s.bl();
    }
    return new Bitboard(result);
  }


  public static final Bitboard empty() {
    return new Bitboard(0L);
  }

  public static final Bitboard all() {
    return new Bitboard(-1L);
  }

  public static final Bitboard firstRank() {
    return new Bitboard(0xffL);
  }

  public static final Bitboard lastRank() {
    return new Bitboard(0xffL << 56);
  }

  public static final Bitboard lightSquares() {
    return new Bitboard(0x55aa55aa55aa55aaL);
  }

  public static final Bitboard darkSquares() {
    return new Bitboard(0xaa55aa55aa55aa55L);
  }


  public static Bitboard file(File f) {
    return new Bitboard(Attacks.FILES[f.getValue()]);
  }

  public static Bitboard rank(Rank r) {
    return new Bitboard(Attacks.RANKS[r.getValue()]);
  }

  public static Bitboard ray(Square from, Square to) {
    return new Bitboard(Attacks.RAYS[from.getValue()][to.getValue()]);
  }

  public static boolean aligned(Square a, Square b, Square c) {
    return ray(a, b).contains(c);
  }

  public static Bitboard between(Square a, Square b) {
    return new Bitboard(Attacks.BETWEEN[a.getValue()][b.getValue()]);
  }

  public long getValue() {
    return value;
  }

  public Bitboard not() {
    return new Bitboard(~value);
  }

  public Bitboard and(long o) {
    return new Bitboard(value & o);
  }

  public Bitboard xor(long o) {
    return new Bitboard(value ^ o);
  }

  public Bitboard or(long o) {
    return new Bitboard(value | o);
  }

  public Bitboard shiftLeft(int o) {
    return new Bitboard(value << o);
  }

  public Bitboard shiftRightUnsigned(int o) {
    return new Bitboard(value >>> o);
  }

  public Bitboard and(Bitboard o) {
    return new Bitboard(value & o.value);
  }

  public Bitboard xor(Bitboard o) {
    return new Bitboard(value ^ o.value);
  }

  public Bitboard or(Bitboard o) {
    return new Bitboard(value | o.value);
  }

  public boolean isEmpty() {
    return value == 0L;
  }

  public boolean nonEmpty() {
    return !isEmpty();
  }

  public boolean contains(Square square) {
    return (value & (1L << square.getValue())) != 0L;
  }

  public Bitboard add(Square square) {
    return new Bitboard(value | square.bl());
  }

  public Bitboard remove(Square square) {
    return new Bitboard(value & ~square.bl());
  }

  public Bitboard move(Square from, Square to) {
    return new Bitboard(value & ~from.bl() | to.bl());
  }

  public boolean moreThanOne() {
    return (value & (value - 1L)) != 0L;
  }


  public int count() {
    return Long.bitCount(value);
  }

  public List<Square> getSquares() {
    long b = value;
    List<Square> result = new ArrayList<>();
    while (b != 0L) {
      result.add(Square.fromValue(Long.numberOfTrailingZeros(b)));
      b &= (b - 1L);
    }
    return result;
  }

  public Optional<Square> getSingleSquare() {
    if (moreThanOne()) {
      return Optional.empty();
    } else {
      return first();
    }
  }

  public Optional<Square> first() {
    long trailingZeros = Long.numberOfTrailingZeros(value);
    if (trailingZeros < 64) {
      return Optional.of(Square.fromValue((int) trailingZeros));
    } else {
      return Optional.empty();
    }
  }

  public Optional<Square> last() {
    long leadingZeros = Long.numberOfLeadingZeros(value);
    if (leadingZeros < 64) {
      return Optional.of(Square.fromValue(63 - (int) leadingZeros));
    } else {
      return Optional.empty();
    }
  }


  public boolean intersects(long o) {
    return (value & o) != 0L;
  }

  public boolean isDisjoint(long o) {
    return (value & o) == 0L;
  }

  public <B> List<B> flatMap(Function<Square, Iterable<B>> f) {
    long b = value;
    List<B> result = new ArrayList<>();

    while (b != 0L) {
      Square square = Square.fromBitboard(Long.lowestOneBit(b));
      result.addAll((Collection<? extends B>) f.apply(square));
      b &= (b - 1L);
    }

    return result;
  }

  public <B> B fold(B init, BiFunction<B, Square, B> f) {
    long b = value;
    B result = init;
    while (b != 0L) {
      result = f.apply(result, Square.fromValue(Long.numberOfTrailingZeros(b)));
      b &= (b - 1L);
    }
    return result;
  }

  public <B> List<B> map(Function<Square, B> f) {
    long b = value;
    List<B> result = new ArrayList<>();

    while (b != 0L) {
      Square lsbSquare = Square.fromValue(Long.numberOfTrailingZeros(b));
      result.add(f.apply(lsbSquare));
      b &= (b - 1L);
    }

    return result;
  }

  @Override
  public String toString() {
    return printBitboard(value);
  }

  public String printBitboard(long bitboard) {

    String ans = "";
    for (int rank = 7; rank >= 0; rank--) {
      for (int file = 0; file < 8; file++) {
        int square = rank * 8 + file;
        long mask = 1L << square;

        char piece = (bitboard & mask) != 0 ? '1' : '0';

        ans += piece + " ";
      }
      ans += "\n";
    }

    return ans;
  }



}


