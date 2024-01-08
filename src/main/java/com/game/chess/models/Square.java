package com.game.chess.models;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


import com.game.chess.enums.Color;
import com.game.chess.enums.Directions;



public class Square {
  private final int value;

  private Square(int value) {
    this.value = value;
  }

  public Square(File file, Rank rank) {
    this.value = rank.getValue() * 8 + file.getValue();
  }


  public int getValue() {
    return value;
  }

  public int getFile() {
    return value % 8;
  }

  public int getRank() {
    return value / 8;
  }

  public Optional<Square> down() {
    return at(getFile(), getRank() - 1);
  }

  public Optional<Square> left() {
    return at(getFile() - 1, getRank());
  }

  public Optional<Square> downLeft() {
    return at(getFile() - 1, getRank() - 1);
  }

  public Optional<Square> downRight() {
    return at(getFile() + 1, getRank() - 1);
  }

  public Optional<Square> up() {
    return at(getFile(), getRank() + 1);
  }

  public Optional<Square> right() {
    return at(getFile() + 1, getRank());
  }

  public Optional<Square> upLeft() {
    return at(getFile() - 1, getRank() + 1);
  }

  public Optional<Square> upRight() {
    return at(getFile() + 1, getRank() + 1);
  }

  public boolean onSameFile(Square other) {
    return getFile() == other.getFile();
  }

  public boolean onSameRank(Square other) {
    return getRank() == other.getRank();
  }

  public boolean onSameLine(Square other) {
    return onSameFile(other) || onSameRank(other);
  }

  public boolean onSameDiagonal(Square other) {
    return getFile() - getRank() == other.getFile() - other.getRank()
        || getFile() + getRank() == other.getFile() + other.getRank();
  }

  public int xDist(Square other) {
    return Math.abs(getFile() - other.getFile());
  }

  public int yDist(Square other) {
    return Math.abs(getRank() - other.getRank());
  }

  public boolean isLight() {
    return Bitboard.lightSquares().contains(this);
  }

  public File getFileEnum() {
    return File.of(this);
  }

  public Rank getRankEnum() {
    return Rank.of(this);
  }

  public char asChar() {
    if (value <= 25) {
      return (char) (97 + value); // a ...
    } else if (value <= 51) {
      return (char) (39 + value); // A ...
    } else if (value <= 61) {
      return (char) (value - 4); // 0 ...
    } else if (value == 62) {
      return '!';
    } else {
      return '?';
    }
  }

  public String key() {
    return String.valueOf(getFileEnum().toChar()) + getRankEnum().toChar();
  }

  public Square withRank(Rank r) {
    return new Square(getFileEnum(), r);
  }

  public Square withFile(File f) {
    return new Square(f, getRankEnum());
  }

  public Square withRankOf(Square o) {
    return withRank(o.getRankEnum());
  }

  public Square withFileOf(Square o) {
    return withFile(o.getFileEnum());
  }

  public Bitboard bb() {
    return new Bitboard(1L << value);
  }

  public long bl() {
    return 1L << value;
  }

  public static Square apply(File file, Rank rank) {
    return new Square(file, rank);
  }

  public static Optional<Square> at(int x, int y) {
    return (x >= 0 && x < 8 && y >= 0 && y < 8) ? Optional.of(new Square(x + 8 * y))
        : Optional.empty();
  }


  public static Optional<Square> fromKey(String key) {
    return Optional.ofNullable(allKeys.get(key));
  }

  public static Optional<Square> fromChar(char c) {
    return Optional.ofNullable(charMap.get(c));
  }

  public static Optional<Character> keyToChar(String key) {
    return fromKey(key).map(Square::asChar);
  }

  public static final Square A1 = new Square(0);
  public static final Square B1 = new Square(1);
  public static final Square C1 = new Square(2);
  public static final Square D1 = new Square(3);
  public static final Square E1 = new Square(4);
  public static final Square F1 = new Square(5);
  public static final Square G1 = new Square(6);
  public static final Square H1 = new Square(7);
  public static final Square A2 = new Square(8);
  public static final Square B2 = new Square(9);
  public static final Square C2 = new Square(10);
  public static final Square D2 = new Square(11);
  public static final Square E2 = new Square(12);
  public static final Square F2 = new Square(13);
  public static final Square G2 = new Square(14);
  public static final Square H2 = new Square(15);
  public static final Square A3 = new Square(16);
  public static final Square B3 = new Square(17);
  public static final Square C3 = new Square(18);
  public static final Square D3 = new Square(19);
  public static final Square E3 = new Square(20);
  public static final Square F3 = new Square(21);
  public static final Square G3 = new Square(22);
  public static final Square H3 = new Square(23);
  public static final Square A4 = new Square(24);
  public static final Square B4 = new Square(25);
  public static final Square C4 = new Square(26);
  public static final Square D4 = new Square(27);
  public static final Square E4 = new Square(28);
  public static final Square F4 = new Square(29);
  public static final Square G4 = new Square(30);
  public static final Square H4 = new Square(31);
  public static final Square A5 = new Square(32);
  public static final Square B5 = new Square(33);
  public static final Square C5 = new Square(34);
  public static final Square D5 = new Square(35);
  public static final Square E5 = new Square(36);
  public static final Square F5 = new Square(37);
  public static final Square G5 = new Square(38);
  public static final Square H5 = new Square(39);
  public static final Square A6 = new Square(40);
  public static final Square B6 = new Square(41);
  public static final Square C6 = new Square(42);
  public static final Square D6 = new Square(43);
  public static final Square E6 = new Square(44);
  public static final Square F6 = new Square(45);
  public static final Square G6 = new Square(46);
  public static final Square H6 = new Square(47);
  public static final Square A7 = new Square(48);
  public static final Square B7 = new Square(49);
  public static final Square C7 = new Square(50);
  public static final Square D7 = new Square(51);
  public static final Square E7 = new Square(52);
  public static final Square F7 = new Square(53);
  public static final Square G7 = new Square(54);
  public static final Square H7 = new Square(55);
  public static final Square A8 = new Square(56);
  public static final Square B8 = new Square(57);
  public static final Square C8 = new Square(58);
  public static final Square D8 = new Square(59);
  public static final Square E8 = new Square(60);
  public static final Square F8 = new Square(61);
  public static final Square G8 = new Square(62);
  public static final Square H8 = new Square(63);

  private static final List<Square> all =
      List.of(A1, B1, C1, D1, E1, F1, G1, H1, A2, B2, C2, D2, E2, F2, G2, H2, A3, B3, C3, D3, E3,
          F3, G3, H3, A4, B4, C4, D4, E4, F4, G4, H4, A5, B5, C5, D5, E5, F5, G5, H5, A6, B6, C6,
          D6, E6, F6, G6, H6, A7, B7, C7, D7, E7, F7, G7, H7, A8, B8, C8, D8, E8, F8, G8, H8);

  private static final Map<String, Square> allKeys = new HashMap<>();
  private static final Map<Character, Square> charMap = new HashMap<>();

  static {
    for (Square square : all) {
      allKeys.put(square.key(), square);
      charMap.put(square.asChar(), square);
    }
  }

  public static Square fromValue(int value) {
    return new Square(value);
  }


  public Bitboard bishopAttacks(Bitboard occupied) {

    Bitboard attacks = Bitboard.apply(
        getAntiDiagonalAttacks(occupied.getValue()) | getDiagonalAttacks(occupied.getValue()));

    return attacks;
  }

  public Bitboard rookAttacks(Bitboard occupied) {
    return Bitboard
        .apply(getFileAttacks(occupied.getValue()) | getRankAttacks(occupied.getValue()));
  }

  public Bitboard queenAttacks(Bitboard occupied) {
    return bishopAttacks(occupied).xor(rookAttacks(occupied));
  }

  public Bitboard pawnAttacks(Color color) {

    if (color.equals(Color.White)) {
      return Bitboard.apply(Attacks.WHITE_PAWN_ATTACKS[value]);
    } else {
      return Bitboard.apply(Attacks.BLACK_PAWN_ATTACKS[value]);
    }
  }

  public Bitboard kingAttacks() {
    return new Bitboard(Attacks.KING_ATTACKS[value]);
  }

  public Bitboard knightAttacks() {
    return new Bitboard(Attacks.KNIGHT_ATTACKS[value]);
  }

  public boolean isBefore(Square s) {

    return this.getFile() < s.getFile();
  }

  public static Square fromBitboard(long bit) {
    int position = Long.numberOfTrailingZeros(bit);
    File file = File.apply(position % 8);
    Rank rank = Rank.apply(position / 8);

    return new Square(file, rank);

  }

  public Optional<Square> prevRank(Color color) {
    return color.isWhite() ? down() : up();

  }

  @Override
  public String toString() {
    return key();
  }

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
    Square other = (Square) obj;
    return value == other.value;
  }

  private long getRayAttacks(long occupied, Directions dir) {


    long attacks = Attacks.RAY_ATTACKS[dir.getValue()][value];
    long blocker = attacks & occupied;

    if (blocker != 0L) {


      int s;
      if (dir.isNegative()) {
        s = 63 - Long.numberOfLeadingZeros(blocker);
      } else {
        s = Long.numberOfTrailingZeros(blocker);

      }
      attacks ^= Attacks.RAY_ATTACKS[dir.getValue()][s];
    }

    return attacks;

  }

  private long getDiagonalAttacks(long occupied) {
    return getRayAttacks(occupied, Directions.NorthEast)
        | getRayAttacks(occupied, Directions.SouthWest);
  }

  private long getAntiDiagonalAttacks(long occupied) {

    return getRayAttacks(occupied, Directions.NorthWest)
        | getRayAttacks(occupied, Directions.SouthEast);
  }

  private long getRankAttacks(long occupied) {
    return getRayAttacks(occupied, Directions.East) | getRayAttacks(occupied, Directions.West);
  }

  private long getFileAttacks(long occupied) {
    return getRayAttacks(occupied, Directions.North) | getRayAttacks(occupied, Directions.South);
  }



}
