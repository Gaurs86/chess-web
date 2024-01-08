package com.game.chess.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.game.chess.enums.Color;
import com.game.chess.enums.PieceType;


public class Board {

  private final Bitboard occupiedBB;
  private final Bitboard[] colorBB;
  private final Bitboard[] pieceBB;


  public Board(Bitboard occupiedBB, Bitboard[] colorBB, Bitboard[] pieceBB) {
    super();
    this.occupiedBB = occupiedBB;
    this.colorBB = colorBB;
    this.pieceBB = pieceBB;
  }


  public static Board init(Map<Square, Piece> pieces) {
    
    
  
    Bitboard[] piece = new Bitboard[PieceType.all.size()];
    Bitboard[] colorBitBoard = new Bitboard[Color.all.size()];
    
    Bitboard occupied = Bitboard.apply(0xffff00000000ffffL);
    
    colorBitBoard[Color.White.getValue()] = Bitboard.apply(0XffffL);
    colorBitBoard[Color.Black.getValue()] = Bitboard.apply(0xffff000000000000L);
    
    piece[PieceType.Pawn.getValue()] = Bitboard.apply(0xff00000000ff00L);
    piece[PieceType.Rook.getValue()] = Bitboard.apply(0x8100000000000081L);
    piece[PieceType.Bishop.getValue()] = Bitboard.apply(0x2400000000000024L);
    piece[PieceType.Knight.getValue()] = Bitboard.apply(0x4200000000000042L);
    piece[PieceType.King.getValue()] = Bitboard.apply(0x1000000000000010L);
    piece[PieceType.Queen.getValue()] = Bitboard.apply(0x800000000000008L);
    
    
    

    return new Board(occupied, colorBitBoard, piece);
 }



  public Optional<Color> colorAt(Square s) {
    if (getWhites().contains(s)) {
      return Optional.of(Color.White);
    } else if (getBlacks().contains(s)) {
      return Optional.of(Color.Black);
    } else {
      return Optional.empty();
    }
  }

  public Optional<PieceType> pieceTypeAt(Square s) {

    for (int i = 0; i < pieceBB.length; i++) {
      if (pieceBB[i].contains(s)) {
        return Optional.of(PieceType.pieceTypeMap.get(i));
      }
    }
    return Optional.empty();
  }



  public Bitboard getPieceWithColorBB(Piece piece) {
    Bitboard colorBoard = colorBB[piece.getColor().getValue()];
    Bitboard pieceBoard = pieceBB[piece.getType().getValue()];
    return colorBoard.and(pieceBoard);
  }


  public Bitboard getOccupiedBB() {
    return occupiedBB;
  }


  public Bitboard[] getColorBB() {
    return colorBB;
  }

  public Bitboard[] getPieceBB() {
    return pieceBB;
  }


  public Bitboard getColorBB(Color c) {
    return colorBB[c.getValue()];
  }

  public Bitboard getPieceBB(PieceType type) {
    return pieceBB[type.getValue()];
  }

  public Bitboard getPieceWithColorBB(Color c, PieceType type) {
    return getPieceWithColorBB(new Piece(c, type));
  }

  public Bitboard getPawns() {
    return pieceBB[PieceType.Pawn.getValue()];
  }

  public Bitboard getKnights() {
    return pieceBB[PieceType.Knight.getValue()];
  }

  public Bitboard getBishops() {
    return pieceBB[PieceType.Bishop.getValue()];
  }

  public Bitboard getRooks() {
    return pieceBB[PieceType.Rook.getValue()];
  }

  public Bitboard getQueens() {
    return pieceBB[PieceType.Queen.getValue()];
  }

  public Bitboard getKings() {
    return pieceBB[PieceType.King.getValue()];
  }

  public Bitboard getWhites() {
    return colorBB[Color.White.getValue()];
  }

  public Bitboard getBlacks() {
    return colorBB[Color.black.getValue()];
  }

  public Bitboard getSliders() {
    return getBishops().xor(getRooks()).xor(getQueens());
  }

  public boolean isOccupied(Square s) {
    return occupiedBB.contains(s);
  }

  public int getNbPieces() {
    return occupiedBB.count();
  }

  public Optional<Piece> pieceAt(Square s) {
    Optional<Color> colorOption = colorAt(s);
    Optional<PieceType> typeOption = pieceTypeAt(s);

    if (colorOption.isPresent() && typeOption.isPresent()) {
      Color color = colorOption.get();
      PieceType type = typeOption.get();
      return Optional.of(new Piece(color, type));
    } else {
      return Optional.empty();
    }
  }

  public List<Square> getKings(Color color) {
    return kingOf(color).getSquares();
  }

  public Bitboard kingOf(Color c) {
    return getKings().and(colorBB[c.getValue()]);
  }

  public Optional<Square> kingPosOf(Color c) {
    return kingOf(c).getSingleSquare();
  }

  public Bitboard attackers(Square s, Color attacker) {
    return attackers(s, attacker, occupiedBB);
  }

  public boolean attacks(Square s, Color attacker) {
    return attackers(s, attacker).nonEmpty();
  }

  public Bitboard attackers(Square s, Color attacker, Bitboard occupied) {
    Bitboard attackersBB =
        colorBB[attacker.getValue()].and(s.rookAttacks(occupied).and(getRooks().xor(getQueens()))
            .or(s.bishopAttacks(occupied).and(getBishops().xor(getQueens())))
            .or(s.knightAttacks().and(getKnights())).or(s.kingAttacks().and(getKings()))
            .or(s.pawnAttacks(attacker.negate()).and(getPawns())));


    return attackersBB;
  }

  public boolean isCheck(Color color) {
    for (Square kingSquare : getKings(color)) {
      if (attacks(kingSquare, color.negate())) {
        return true;
      }
    }
    return false;
  }

  public Bitboard sliderBlockers(Square ourKing, Color us) {
    Bitboard snipers = colorBB[us.negate().getValue()]
        .and((ourKing.rookAttacks(Bitboard.empty()).and(getRooks().xor(getQueens())))
            .or(ourKing.bishopAttacks(Bitboard.empty()).and(getBishops().xor(getQueens()))));



    return snipers.fold(Bitboard.empty(), (blockers, sniper) -> {
      Bitboard between = Bitboard.between(ourKing, sniper).and(occupiedBB);
      if (between.moreThanOne()) {
        return blockers;
      } else {
        return blockers.or(between);
      }
    });
  }


  private Board discard(Square s) {
    return discard(s.bb());
  }

  private Board discard(Bitboard mask) {
    
    Bitboard notMask = mask.not();

    Bitboard[] newColorBB = Arrays.copyOf(colorBB, colorBB.length);
    Arrays.setAll(newColorBB, c -> colorBB[c].and(notMask));

    Bitboard[] newPieceBB = Arrays.copyOf(pieceBB, pieceBB.length);
    Arrays.setAll(newPieceBB, i -> pieceBB[i].and(notMask));

    Bitboard newOccupiedBB = occupiedBB.and(notMask);

    return new Board(newOccupiedBB, newColorBB, newPieceBB);
  }


  public Optional<Board> put(Piece piece, Square at) {
    return isOccupied(at) ? Optional.empty() : Optional.of(putOrReplace(piece, at));
  }

  public Optional<Board> replace(Piece piece, Square at) {
    return isOccupied(at) ? Optional.of(putOrReplace(piece, at)) : Optional.empty();
  }
  
  private Board putOrReplace(Piece p, Square s) {
    return putOrReplace(s, p.getType(), p.getColor());
  }

  private Board putOrReplace(Square s, PieceType type, Color color) {
    Board b = discard(s);
    Bitboard m = s.bb();


    b.getColorBB()[color.getValue()] = b.getColorBB(color).or(m);
    b.getPieceBB()[type.getValue()] = b.getPieceBB(type).or(m);

    return new Board(b.occupiedBB.or(m), b.getColorBB(), b.getPieceBB());
  }




  public Optional<Board> take(Square at) {
    return isOccupied(at) ? Optional.of(discard(at)) : Optional.empty();
  }

  public Optional<Board> move(Square orig, Square dest) {
    return isOccupied(dest) ? Optional.empty()
        : pieceAt(orig).map(piece -> discard(orig).putOrReplace(piece, dest));
  }

  public Optional<Board> taking(Square orig, Square dest, Optional<Square> taking) {
    Optional<Piece> pieceOption = pieceAt(orig);
    Square takenSquare = taking.orElse(dest);

    if (pieceOption.isPresent() && isOccupied(takenSquare)) {
      Piece piece = pieceOption.get();
      return Optional.of(discard(orig).discard(takenSquare).putOrReplace(piece, dest));
    } else {
      return Optional.empty();
    }

  }

  public Optional<Board> promote(Square orig, Square dest, Piece piece) {
    return take(orig).map(board -> board.putOrReplace(piece, dest));
  }

  public boolean isOccupied(Piece p) {
    return getPieceWithColorBB(p).nonEmpty();
  }


  public Map<Square, Piece> getPieceMap() {
    Map<Square, Piece> m = new HashMap<>();

    for (Color c : Color.values()) {
      for (PieceType type : PieceType.values()) {
        Bitboard b = colorBB[c.getValue()].and(pieceBB[type.getValue()]);

        b.getSquares().forEach(s -> m.put(s, new Piece(c, type)));
      }
    }
    return m;
  }


  public boolean kingsAndBishopsOnly() {
    return (getKings().or(getBishops())).equals(getOccupiedBB());
  }

  public boolean kingsAndKnightsOnly() {
    return (getKings().or(getKnights())).equals(getOccupiedBB());
  }

  public boolean onlyKnights() {
    return getKnights().equals(getOccupiedBB());
  }

  public Bitboard minors() {
    return getBishops().or(getKnights());
  }

  public boolean kingsAndMinorsOnly() {
    return (getKings().or(minors())).equals(getOccupiedBB());
  }

  public boolean kingsRooksAndMinorsOnly() {
    return (getKings().or(getRooks().or(minors()))).equals(getOccupiedBB());
  }

  public boolean kingsAndBishopsOnlyOf(Color color) {
    return onlyOf(color, getKings().or(getBishops()));
  }

  public boolean kingsAndMinorsOnlyOf(Color color) {
    return onlyOf(color, getKings().or(minors()));
  }

  public boolean kingsOnly() {
    return getKings().equals(getOccupiedBB());
  }

  public boolean kingsOnlyOf(Color color) {
    return onlyOf(color, getKings());
  }

  public boolean kingsAndKnightsOnlyOf(Color color) {
    return onlyOf(color, getKings().or(getKnights()));
  }

  public boolean onlyOf(Color color, Bitboard roles) {
    Bitboard colorPieces = getColorBB(color);
    return roles.and(colorPieces).equals(colorPieces);
  }

  public Bitboard nonKingsOf(Color color) {
    return getColorBB(color).and(getKings().not());
  }

  public Bitboard nonKing() {
    return getOccupiedBB().and(getKings().not());
  }


  public int count(Piece piece) {
    return getPieceWithColorBB(piece).count();
  }

  public int count(Color color) {
    return getColorBB(color).count();
  }


  @Override
  public String toString() {
    return "Board [occupiedBB=" + occupiedBB + ", colorBB=" + Arrays.toString(colorBB)
        + ", pieceBB=" + Arrays.toString(pieceBB) + "]";
  }



}
