package com.game.chess.enums;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.game.chess.models.Move;

public enum PieceType {


  Pawn('p', 0), Bishop('b', 1), Knight('n', 2), Rook('r', 3), Queen('q', 4), King('k', 5);



  private final char forsyth;
  private final int value;

  public final static Map<Integer, PieceType> pieceTypeMap = new HashMap<Integer, PieceType>();

  static {
    for (PieceType p : values()) {
      pieceTypeMap.put(p.getValue(), p);
    }
  }

  private PieceType(char forsyth, int value) {
    this.forsyth = forsyth;
    this.value = value;
  }

  public char getForsyth() {
    return forsyth;
  }

  public char getForsythUpper() {
    return Character.toUpperCase(forsyth);
  }

  public char getForsythBy(Color color) {
    return color == Color.White ? getForsythUpper() : forsyth;
  }

  public String getName() {
    return name().toLowerCase();
  }

  public int getValue() {
    return value;
  }

  public static List<PieceType> promotableRoles() {

    return List.of(PieceType.Rook, PieceType.Bishop, PieceType.Knight, PieceType.Queen);
  }

  public static final List<PieceType> all = List.of(Rook, Pawn, Bishop, Knight, King, Queen);



}
