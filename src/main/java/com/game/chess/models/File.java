package com.game.chess.models;

import java.util.List;

public class File {
  private final int value;

  private File(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public boolean greaterThan(File other) {
    return this.value > other.value;
  }

  public boolean lessThan(File other) {
    return this.value < other.value;
  }

  public boolean greaterThanOrEqual(File other) {
    return this.value >= other.value;
  }

  public boolean lessThanOrEqual(File other) {
    return this.value <= other.value;
  }

  public char toChar() {
    return (char) (97 + value);
  }

  public char toUpperCaseChar() {
    return (char) (65 + value);
  }

  public String toUpperCaseString() {
    return Character.toString(toUpperCaseChar());
  }

  public static File of(Square square) {
    return new File(square.getValue() & 7);
  }

  public static File fromChar(char ch) {
    int intValue = ch - 97;
    return apply(intValue);
  }

  public static File apply(int value) {
    if (0 <= value && value < 8) {
      return new File(value);
    } else {
      return null; // Or throw an exception, depending on your design
    }
  }

  public static final File A = new File(0);
  public static final File B = new File(1);
  public static final File C = new File(2);
  public static final File D = new File(3);
  public static final File E = new File(4);
  public static final File F = new File(5);
  public static final File G = new File(6);
  public static final File H = new File(7);

  public static final List<File> all = List.of(A, B, C, D, E, F, G, H);
}
