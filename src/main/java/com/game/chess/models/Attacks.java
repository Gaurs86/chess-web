package com.game.chess.models;


import com.game.chess.enums.Directions;

public class Attacks {

  public static final long all = -1L;
  public static final long[] RANKS = new long[8];
  public static final long[] FILES = new long[8];
  public static final long[][] BETWEEN = new long[64][64];
  public static final long[][] RAYS = new long[64][64];
  public static final long[] ATTACKS = new long[88772];
  public static final long[][] RAY_ATTACKS = new long[8][64];
  public static final long[][] LINE_ATTACKS = new long[4][64];
  public static final long[] KNIGHT_ATTACKS = new long[64];
  public static final long[] KING_ATTACKS = new long[64];
  public static final long[] WHITE_PAWN_ATTACKS = new long[64];
  public static final long[] BLACK_PAWN_ATTACKS = new long[64];
  public static final long[] ROOK_ATTACKS = new long[64];
  public static final long[] BISHOP_ATTACKS = new long[64];
  public static final long[] QUEEN_ATTACKS = new long[64];
  public static final int[] KNIGHT_DELTAS = {17, 15, 10, 6, -17, -15, -10, -6};
  public static final int[] BISHOP_DELTAS = {7, -7, 9, -9};
  public static final int[] ROOK_DELTAS = {1, -1, 8, -8};
  public static final int[] KING_DELTAS = {1, 7, 8, 9, -1, -7, -8, -9};
  public static final int[] WHITE_PAWN_DELTAS = {7, 9};
  public static final int[] BLACK_PAWN_DELTAS = {-7, -9};

  private static long slidingAttacks(int square, long occupied, int[] deltas) {
    long attacks = 0L;
    for (int delta : deltas) {
      int sq = square;
      while (true) {
        sq += delta;
        boolean con = (sq < 0 || 64 <= sq || distance(sq, sq - delta) > 2);
        if (!con) {
          attacks |= 1L << sq;
        }
        if (occupiedContains(occupied, sq) || con) {
          break;
        }
      }
    }
    return attacks;
  }


  private static void initialize() {
    for (int i = 0; i < 8; i++) {
      RANKS[i] = 0xffL << (i * 8);
      FILES[i] = 0x0101010101010101L << i;
    }

    for (int sq = 0; sq < 64; sq++) {
      KNIGHT_ATTACKS[sq] = slidingAttacks(sq, all, KNIGHT_DELTAS);
      KING_ATTACKS[sq] = slidingAttacks(sq, all, KING_DELTAS);
      WHITE_PAWN_ATTACKS[sq] = slidingAttacks(sq, all, WHITE_PAWN_DELTAS);
      BLACK_PAWN_ATTACKS[sq] = slidingAttacks(sq, all, BLACK_PAWN_DELTAS);


    }

    for (int a = 0; a < 64; a++) {
      for (int b = 0; b < 64; b++) {
        if (occupiedContains(slidingAttacks(a, 0, ROOK_DELTAS), b)) {
          BETWEEN[a][b] =
              slidingAttacks(a, 1L << b, ROOK_DELTAS) & slidingAttacks(b, 1L << a, ROOK_DELTAS);
          RAYS[a][b] = (1L << a) | (1L << b)
              | slidingAttacks(a, 0, ROOK_DELTAS) & slidingAttacks(b, 0, ROOK_DELTAS);
        } else if (occupiedContains(slidingAttacks(a, 0, BISHOP_DELTAS), b)) {
          BETWEEN[a][b] =
              slidingAttacks(a, 1L << b, BISHOP_DELTAS) & slidingAttacks(b, 1L << a, BISHOP_DELTAS);
          RAYS[a][b] = (1L << a) | (1L << b)
              | slidingAttacks(a, 0, BISHOP_DELTAS) & slidingAttacks(b, 0, BISHOP_DELTAS);
        }
      }
    }

    long nort = 0x0101010101010100L;
    for (int sq = 0; sq < 64; sq++, nort <<= 1)
      RAY_ATTACKS[Directions.North.getValue()][sq] = nort;

    long noea = 0x8040201008040200L;
    for (int f = 0; f < 8; f++, noea = eastOne(noea)) {
      long ne = noea;
      for (int r8 = 0; r8 < 8 * 8; r8 += 8, ne <<= 8)
        RAY_ATTACKS[Directions.NorthEast.getValue()][r8 + f] = ne;
    }

    long nowe = 0x102040810204000L;
    for (int f = 7; f >= 0; f--, nowe = westOne(nowe)) {
      long nw = nowe;
      for (int r8 = 0; r8 < 8 * 8; r8 += 8, nw <<= 8)
        RAY_ATTACKS[Directions.NorthWest.getValue()][r8 + f] = nw;
    }

    long ea = 0xfe;
    for (int f = 0; f < 8; f++, ea = eastOne(ea)) {
      long e = ea;
      for (int r = 0; r < 8; r++, e <<= 8)
        RAY_ATTACKS[Directions.East.getValue()][r * 8 + f] = e;
    }

    long we = 0x7fL;
    for (int f = 7; f >= 0; f--, we = westOne(we)) {
      long w = we;
      for (int r8 = 0; r8 < 8 * 8; r8 += 8, w <<= 8)
        RAY_ATTACKS[Directions.West.getValue()][r8 + f] = w;
    }

    long so = 0x0080808080808080L;

    for (int sq = 63; sq >= 0; sq--, so >>= 1) {
      RAY_ATTACKS[Directions.South.getValue()][sq] = so;

    }

    long sowe = 0x40201008040201L;

    for (int f = 7; f >= 0; f--, sowe = westOne(sowe)) {
      long sw = sowe;

      for (int r = 7; r >= 0; r--, sw >>= 8) {
        RAY_ATTACKS[Directions.SouthWest.getValue()][r * 8 + f] = sw;
      }
    }

    long soea = 0x2040810204080L;
    for (int f = 0; f < 8; f++, soea = eastOne(soea)) {
      long se = soea;

      for (int r = 7; r >= 0; r--, se >>= 8) {
        RAY_ATTACKS[Directions.SouthEast.getValue()][r * 8 + f] = se;
      }
    }


    for (int sq = 0; sq < 64; sq++) {
      LINE_ATTACKS[0][sq] =
          RAY_ATTACKS[Directions.East.getValue()][sq] | RAY_ATTACKS[Directions.West.getValue()][sq];
      LINE_ATTACKS[1][sq] = RAY_ATTACKS[Directions.North.getValue()][sq]
          | RAY_ATTACKS[Directions.South.getValue()][sq];
      LINE_ATTACKS[2][sq] = RAY_ATTACKS[Directions.NorthWest.getValue()][sq]
          | RAY_ATTACKS[Directions.SouthEast.getValue()][sq];
      LINE_ATTACKS[3][sq] = RAY_ATTACKS[Directions.NorthEast.getValue()][sq]
          | RAY_ATTACKS[Directions.SouthWest.getValue()][sq];
      ROOK_ATTACKS[sq] = LINE_ATTACKS[0][sq] | LINE_ATTACKS[1][sq];
      BISHOP_ATTACKS[sq] = LINE_ATTACKS[2][sq] | LINE_ATTACKS[3][sq];
      QUEEN_ATTACKS[sq] = ROOK_ATTACKS[sq] | BISHOP_ATTACKS[sq];

    }



  }

  private static final long notAFile = 0xfefefefefefefefel; // ~0x0101010101010101
  private static final long notHFile = 0x7f7f7f7f7f7f7f7fl; // ~0x8080808080808080

  private static long eastOne(long b) {
    return (b & notHFile) << 1;
  }

  private static long noEaOne(long b) {
    return (b & notHFile) << 9;
  }

  private static long soEaOne(long b) {
    return (b & notHFile) >> 7;
  }

  private static long westOne(long b) {
    return (b & notAFile) >> 1;
  }

  private static long soWeOne(long b) {
    return (b & notAFile) >> 9;
  }

  private static long noWeOne(long b) {
    return (b & notAFile) << 7;
  }


  static {
    initialize();
  }

  private static boolean occupiedContains(long occupied, int square) {
    return (occupied & (1L << square)) != 0L;
  }

  private static int distance(int a, int b) {
    int fileA = a & 7;
    int rankA = a >>> 3;
    int fileB = b & 7;
    int rankB = b >>> 3;
    return Math.max(Math.abs(fileA - fileB), Math.abs(rankA - rankB));
  }



}
