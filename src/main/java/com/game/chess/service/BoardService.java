package com.game.chess.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.game.chess.enums.Color;
import com.game.chess.enums.Directions;
import com.game.chess.enums.PieceType;
import com.game.chess.models.Attacks;
import com.game.chess.models.Bitboard;
import com.game.chess.models.Board;
import com.game.chess.models.File;
import com.game.chess.models.Piece;
import com.game.chess.models.Rank;
import com.game.chess.models.Square;



@Service
public class BoardService {

  public Optional<Board> put(Board board, Piece piece, Square at) {
    return board.getOccupiedBB().contains(at) ? Optional.empty()
        : Optional.of(putOrReplace(board, piece, at));
  }

  public Optional<Board> replace(Board board, Piece piece, Square at) {
    return board.getOccupiedBB().contains(at) ? Optional.of(putOrReplace(board, piece, at))
        : Optional.empty();
  }



  public Optional<Board> take(Board board, Square at) {
    return board.getOccupiedBB().contains(at) ? Optional.of(discard(board, at)) : Optional.empty();
  }

  public Optional<Board> move(Board board, Square orig, Square dest) {
    return board.getOccupiedBB().contains(dest) ? Optional.empty()
        : board.pieceAt(orig).map(piece -> {
          Board b = discard(board, orig);
          return putOrReplace(b, piece, dest);
        });
  }

  public Optional<Board> taking(Board board, Square orig, Square dest, Optional<Square> taking) {
    Optional<Piece> pieceOption = board.pieceAt(orig);
    Square takenSquare = taking.orElse(dest);

    if (pieceOption.isPresent() && board.getOccupiedBB().contains(takenSquare)) {
      Piece piece = pieceOption.get();

      Board b1 = discard(board, orig);
      Board b2 = discard(b1, takenSquare);
      Board b3 = putOrReplace(b2, piece, dest);
      return Optional.of(b3);
    } else {
      return Optional.empty();
    }

  }

  public Optional<Board> promote(Board board, Square orig, Square dest, Piece piece) {
    return take(board, orig).map(b -> putOrReplace(b, piece, dest));
  }

  private Board discard(Board board, Square s) {
    return discard(board, s.bb());
  }


  private Board discard(Board board, Bitboard mask) {

    Bitboard colorBB[] = board.getColorBB();
    Bitboard pieceBB[] = board.getPieceBB();
    Bitboard occupiedBB = board.getOccupiedBB();

    Bitboard notMask = mask.not();

    Arrays.setAll(colorBB, c -> colorBB[c].and(notMask));
    Arrays.setAll(pieceBB, i -> pieceBB[i].and(notMask));

    return new Board(occupiedBB.and(notMask), colorBB, pieceBB);
  }



  private Board putOrReplace(Board board, Piece p, Square s) {
    return putOrReplace(board, s, p.getType(), p.getColor());
  }

  private Board putOrReplace(Board board, Square s, PieceType type, Color color) {
    Board b = discard(board, s);
    Bitboard m = s.bb();


    b.getColorBB()[color.getValue()] = b.getColorBB(color).or(m);
    b.getPieceBB()[type.getValue()] = b.getPieceBB(type).or(m);

    return new Board(b.getOccupiedBB().or(m), b.getColorBB(), b.getPieceBB());
  }



  public boolean isCheck(Board board, Color color) {
    for (Square kingSquare : board.getKings(color)) {
      if (attacks(board, kingSquare, color.negate())) {
        return true;
      }
    }
    return false;
  }

  public Bitboard attackers(Board board, Square s, Color attacker) {

    Bitboard occupied = board.getOccupiedBB();
    return board.getColorBB(attacker)
        .and(rookAttacks(s, occupied).and(board.getRooks().xor(board.getQueens()))
            .or(bishopAttacks(s, occupied).and(board.getBishops().xor(board.getQueens())))
            .or(knightAttacks(s).and(board.getKnights())).or(kingAttacks(s).and(board.getKings()))
            .or(pawnAttacks(s, attacker.negate()).and(board.getPawns())));


  }

  public boolean attacks(Board board, Square s, Color attacker) {
    return attackers(board, s, attacker).nonEmpty();
  }



  public Bitboard bishopAttacks(Square s, Bitboard occupied) {

    Bitboard attacks = Bitboard.apply(getAntiDiagonalAttacks(s, occupied.getValue())
        | getDiagonalAttacks(s, occupied.getValue()));

    return attacks;
  }

  public Bitboard rookAttacks(Square s, Bitboard occupied) {
    return Bitboard
        .apply(getFileAttacks(s, occupied.getValue()) | getRankAttacks(s, occupied.getValue()));
  }

  public Bitboard queenAttacks(Square s, Bitboard occupied) {
    return bishopAttacks(s, occupied).xor(rookAttacks(s, occupied));
  }

  public Bitboard pawnAttacks(Square s, Color color) {

    if (color.equals(Color.White)) {
      return Bitboard.apply(Attacks.WHITE_PAWN_ATTACKS[s.getValue()]);
    } else {
      return Bitboard.apply(Attacks.BLACK_PAWN_ATTACKS[s.getValue()]);
    }
  }

  public Bitboard kingAttacks(Square s) {
    return new Bitboard(Attacks.KING_ATTACKS[s.getValue()]);
  }

  public Bitboard knightAttacks(Square s) {
    return new Bitboard(Attacks.KNIGHT_ATTACKS[s.getFile()]);
  }

  private long getRayAttacks(Square sq, long occupied, Directions dir) {


    long attacks = Attacks.RAY_ATTACKS[dir.getValue()][sq.getValue()];
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

  private long getDiagonalAttacks(Square s, long occupied) {
    return getRayAttacks(s, occupied, Directions.NorthEast)
        | getRayAttacks(s, occupied, Directions.SouthWest);
  }

  private long getAntiDiagonalAttacks(Square s, long occupied) {

    return getRayAttacks(s, occupied, Directions.NorthWest)
        | getRayAttacks(s, occupied, Directions.SouthEast);
  }

  private long getRankAttacks(Square s, long occupied) {
    return getRayAttacks(s, occupied, Directions.East)
        | getRayAttacks(s, occupied, Directions.West);
  }

  private long getFileAttacks(Square s, long occupied) {
    return getRayAttacks(s, occupied, Directions.North)
        | getRayAttacks(s, occupied, Directions.South);
  }


  public String createFen(Board board) {
    StringBuilder fen = new StringBuilder();


    for (Rank rank : Rank.allReversed) {
      int emptyCount = 0;

      for (File file : File.all) {
        Square sq = new Square(file, rank);


        if (board.getOccupiedBB().contains(sq)) {
          if (emptyCount > 0) {
            fen.append(emptyCount);
            emptyCount = 0;
          }

          fen.append(board.pieceAt(sq).get().forsyth());
        } else {
          emptyCount++;
        }
      }

      if (emptyCount > 0) {
        fen.append(emptyCount);
      }

      if (rank.getValue() > 0) {
        fen.append("/");
      }
    }

    return fen.toString();
  }



}
