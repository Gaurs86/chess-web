package com.game.chess.enums;

public enum Directions {

  North(0), NorthEast(1), East(2), SouthEast(3), South(4), SouthWest(5), West(6), NorthWest(7);

  private final int value;

  Directions(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public boolean isNegative() {

    if (this == West || this == South || this == SouthEast || this == SouthWest) {
      return true;
    }

    return false;
  }

}
