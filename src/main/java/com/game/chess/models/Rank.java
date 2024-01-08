package com.game.chess.models;

import java.util.List;
import java.util.Objects;



public class Rank {




  private final int value;

  private Rank(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public boolean greaterThan(Rank other) {
    return this.value > other.value;
  }

  public boolean lessThan(Rank other) {
    return this.value < other.value;
  }

  public boolean greaterThanOrEqual(Rank other) {
    return this.value >= other.value;
  }

  public boolean lessThanOrEqual(Rank other) {
    return this.value <= other.value;
  }

  public char toChar() {
    return (char) (49 + value);
  }

  public static Rank apply(int index) {
    if (index >= 0 && index < 8) {
      return new Rank(index);
    } else {
      return null; // Or throw an exception, depending on your design
    }
  }

  public static Rank of(Square square) {
    return new Rank(square.getValue() >> 3);
  }

  public static Rank fromChar(char ch) {
    int intValue = ch - 49;
    return apply(intValue);
  }

  public static final Rank First = new Rank(0);
  public static final Rank Second = new Rank(1);
  public static final Rank Third = new Rank(2);
  public static final Rank Fourth = new Rank(3);
  public static final Rank Fifth = new Rank(4);
  public static final Rank Sixth = new Rank(5);
  public static final Rank Seventh = new Rank(6);
  public static final Rank Eight = new Rank(7);

  public static final List<Rank> all =
      List.of(First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eight);
  public static final List<Rank> allReversed =
      List.of(Eight, Seventh, Sixth, Fifth, Fourth, Third, Second, First);
  
  
  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Rank other = (Rank) obj;
    return value == other.value;
  }
}
