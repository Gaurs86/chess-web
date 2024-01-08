package com.game.chess.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.game.chess.enums.Color;
import com.game.chess.enums.PieceType;
import com.game.chess.enums.Side;
import com.game.chess.enums.Status;
import com.game.chess.service.InsufficientMatingMaterial;


public final class ChessBoard {

  private final Board board;
  private final Castles castles;
  private final Color currentPlayer;
  private final int halfMoveClock;
  private final int fullMoveNumber;
  private final List<Move> moves;
  private final Optional<Move> lastMove;
  private final UnmovedRooks unmovedrooks;



  public ChessBoard(Board board, Castles castle, Color currentPlayer) {
    super();
    this.board = board;
    this.castles = castle;
    this.currentPlayer = currentPlayer;
    this.halfMoveClock = 0;
    this.fullMoveNumber = 0;
    this.moves = new ArrayList<Move>();
    this.lastMove = Optional.empty();
    this.unmovedrooks = UnmovedRooks.CORNERS;
  }



  public ChessBoard(Board board, Castles castles, Color currentPlayer, int halfMoveClock,
      int fullMoveNumber, List<Move> moves, Optional<Move> lastMove, UnmovedRooks unmovedrooks) {
    super();
    this.board = board;
    this.castles = castles;
    this.currentPlayer = currentPlayer;
    this.halfMoveClock = halfMoveClock;
    this.fullMoveNumber = fullMoveNumber;
    this.moves = moves;
    this.lastMove = lastMove;
    this.unmovedrooks = unmovedrooks;
  }



  public Board getBoard() {
    return board;
  }



  public Color getCurrentPlayer() {
    return currentPlayer;
  }



  public int getHalfMoveClock() {
    return halfMoveClock;
  }



  public int getFullMoveNumber() {
    return fullMoveNumber;
  }



  public List<Move> getMoves() {
    return moves;
  }



  public Optional<Move> getLastMove() {
    return lastMove;
  }



  public Castles getCastles() {
    return castles;
  }



  public UnmovedRooks getUnmovedrooks() {
    return unmovedrooks;
  }



  public static ChessBoard init() {

    Map<Square, Piece> pieces = new HashMap<Square, Piece>();
    pieces.put(new Square(File.A, Rank.First), new Piece(Color.White, PieceType.Rook));
    pieces.put(new Square(File.B, Rank.First), new Piece(Color.White, PieceType.Knight));
    pieces.put(new Square(File.C, Rank.First), new Piece(Color.White, PieceType.Bishop));
    pieces.put(new Square(File.D, Rank.First), new Piece(Color.White, PieceType.Queen));
    pieces.put(new Square(File.E, Rank.First), new Piece(Color.White, PieceType.King));
    pieces.put(new Square(File.F, Rank.First), new Piece(Color.White, PieceType.Bishop));
    pieces.put(new Square(File.G, Rank.First), new Piece(Color.White, PieceType.Knight));
    pieces.put(new Square(File.H, Rank.First), new Piece(Color.White, PieceType.Rook));

    for (File file : File.all) {
      pieces.put(new Square(file, Rank.Second), new Piece(Color.White, PieceType.Pawn));
      pieces.put(new Square(file, Rank.Seventh), new Piece(Color.Black, PieceType.Pawn));
    }

    pieces.put(new Square(File.A, Rank.Eight), new Piece(Color.Black, PieceType.Rook));
    pieces.put(new Square(File.B, Rank.Eight), new Piece(Color.Black, PieceType.Knight));
    pieces.put(new Square(File.C, Rank.Eight), new Piece(Color.Black, PieceType.Bishop));
    pieces.put(new Square(File.D, Rank.Eight), new Piece(Color.Black, PieceType.Queen));
    pieces.put(new Square(File.E, Rank.Eight), new Piece(Color.Black, PieceType.King));
    pieces.put(new Square(File.F, Rank.Eight), new Piece(Color.Black, PieceType.Bishop));
    pieces.put(new Square(File.G, Rank.Eight), new Piece(Color.Black, PieceType.Knight));
    pieces.put(new Square(File.H, Rank.Eight), new Piece(Color.Black, PieceType.Rook));

    Board board = Board.init(pieces);
    Castles castles = Castles.init;
    Color currentPlayer = Color.White;

    return new ChessBoard(board, castles, currentPlayer);


  }


  public Map<Square, List<Move>> moves() {
    Map<Square, List<Move>> moveMap = new HashMap<>();
    for (Move move : validMoves()) {
      moveMap.computeIfAbsent(move.getOrig(), k -> new ArrayList<>()).add(move);
    }
    return moveMap;
  }

  public boolean playerCanCapture() {
    return validMoves().stream().anyMatch(Move::isCaptures);
  }

 



  public Optional<Square> checkSquare() {
    return check() ? getOurKing() : Optional.empty();
  }



  public boolean end() {
    return checkmate() || staleMate() || autoDraw();
  }

  public boolean staleMate() {
    return !check() && validMoves().isEmpty();
  }

  public boolean checkmate() {
    return check() && validMoves().isEmpty();
  }


  public boolean check() {
    return checkOf(currentPlayer);
  }


  public boolean checkOf(Color c) {
    return board.isCheck(c);
  }

  public boolean autoDraw() {
    return fiftyMoves() || isInsufficientMaterial();
  }


  private boolean isInsufficientMaterial() {

    return InsufficientMatingMaterial.isInsufficientMatingMaterial(board, currentPlayer);
  }

  private boolean fiftyMoves() {
    return halfMoveClock >= 100;
  }

  public Optional<Status> status() {
    if (checkmate()) {
      return Optional.of(Status.Mate);
    } else if (staleMate()) {
      return Optional.of(Status.Stalemate);
    } else if (autoDraw()) {
      return Optional.of(Status.Draw);
    } else {
      return Optional.empty();
    }
  }



  public Optional<Color> winner() {
    if (checkmate()) {
      return Optional.of(currentPlayer.negate());
    } else {
      return Optional.empty();
    }
  }



  public Optional<Square> getOurKing() {
    return board.kingPosOf(currentPlayer);
  }

  public Optional<Square> getTheirKing() {
    return board.kingPosOf(currentPlayer.negate());
  }

  public Bitboard getCheckers() {
    return getOurKing().map(king -> board.attackers(king, currentPlayer.negate()))
        .orElse(Bitboard.empty());

  }


  public Bitboard getUs() {
    return board.getColorBB(currentPlayer);

  }

  public Bitboard getThem() {
    return board.getColorBB(currentPlayer.negate());
  }

  public List<Move> generateMovesAt(Square s) {
    List<Move> moves = new ArrayList<Move>();

    Optional<Piece> optionalPiece = board.pieceAt(s);

    if (!optionalPiece.isEmpty()) {

      Piece piece = optionalPiece.get();
      if (piece.getColor() == currentPlayer) {
        Bitboard targets = getUs().not();
        Bitboard bb = s.bb();

        switch (piece.getType()) {
          case Pawn:
            moves.addAll(genEnPassant(getUs().and(bb)));
            moves.addAll(genPawn(bb, targets));
            break;
          case Knight:
            moves.addAll(genKnight(getUs().and(bb), targets));
            break;
          case Bishop:
            moves.addAll(genBishop(getUs().and(bb), targets));
            break;
          case Rook:
            moves.addAll(genRook(getUs().and(bb), targets));
            break;
          case Queen:
            moves.addAll(genQueen(getUs().and(bb), targets));
            break;
          case King:
            moves.addAll(genKingAt(targets, s));
            break;

        }


      }


    }

    return moves;
  }

  private List<Move> genKingAt(Bitboard mask, Square square) {
    List<Move> moves = genUnsafeKing(square, mask);
    moves.addAll(genCastling(square));

    return moves;
  }

  private List<Move> genUnsafeKing(Square king, Bitboard mask) {
    return (king.kingAttacks().and(mask))
        .flatMap(to -> normalMove(king, to, PieceType.King, board.isOccupied(to))
            .map(Collections::singletonList).orElse(Collections.emptyList()));

  }

  public List<Move> genSafeKing(Bitboard mask) {
    return getOurKing().map(king -> genSafeKing(king, mask)).orElse(null);
  }

  public List<Move> genSafeKing(Square king, Bitboard mask) {
    List<Move> result = new ArrayList<>();
    for (Square to : king.kingAttacks().and(mask).getSquares()) {
      if (board.attackers(to, currentPlayer.negate()).isEmpty()) {
        Move move = normalMove(king, to, PieceType.King, board.isOccupied(to)).get();
        result.add(move);
      }
    }
    return result;
  }



  private Optional<Move> normalMove(Square orig, Square dest, PieceType role, boolean capture) {
    Optional<Square> taken = capture ? Optional.of(dest) : Optional.empty();


    return Optional.of(new Move(new Piece(currentPlayer, role), orig, dest, taken, Optional.empty(),
        Optional.empty(), false));
  }



  public List<Move> genCastling(Square king) {
    if (!castles.can(currentPlayer) || king.getRankEnum() != currentPlayer.getBackRank()) {
      return Collections.emptyList();
    } else {
      Bitboard rooks =
          Bitboard.rank(currentPlayer.getBackRank()).and(board.getRooks()).and(unmovedrooks.bb());
      List<Move> castlingMoves = new ArrayList<>();

      for (Square rook : rooks.getSquares()) {
        boolean canQueenSide =
            rook.getValue() < king.getValue() && castles.can(currentPlayer, Side.QueenSide);
        boolean canKingSide =
            rook.getValue() > king.getValue() && castles.can(currentPlayer, Side.KingSide);

        if (canQueenSide || canKingSide) {
          File toKingFile = canQueenSide ? File.C : File.G;
          File toRookFile = canQueenSide ? File.D : File.F;
          Square kingTo = new Square(toKingFile, king.getRankEnum());
          Square rookTo = new Square(toRookFile, rook.getRankEnum());


          Bitboard path = Bitboard.between(king, rook);

          if ((path.and(board.getOccupiedBB()).and(rook.bb().not())).isEmpty()) {
            Bitboard kingPath = Bitboard.between(king, kingTo).or(king.bb());

            kingPath.getSquares().forEach(square -> {
              if (castleCheckSafeSquare(board, square, currentPlayer,
                  board.getOccupiedBB().xor(king.bb()))) {
                if (castleCheckSafeSquare(board, kingTo, currentPlayer,
                    board.getOccupiedBB().xor(king.bb()).xor(rook.bb()).xor(rookTo.bb()))) {
                  List<Move> moves = castle(king, kingTo, rook, rookTo);
                  castlingMoves.addAll(moves);
                }
              }
            });
          }
        }
      }

      return castlingMoves;
    }


  }

  private boolean castleCheckSafeSquare(Board board, Square kingTo, Color color,
      Bitboard occupied) {
    return board.attackers(kingTo, color.negate(), occupied).isEmpty();
  }

  private List<Move> castle(Square king, Square kingTo, Square rook, Square rookTo) {

    Optional<Board> after = board.take(king).flatMap(b1 -> b1.take(rook))
        .flatMap(b2 -> b2.put(currentPlayer.king(), kingTo))
        .flatMap(b3 -> b3.put(currentPlayer.rook(), rookTo));



    // Define the destination squares for the move
    List<Square> destInput = List.of(rook, kingTo);

    // Generate a list of Move objects representing the castling move
    List<Move> result = new ArrayList<>();

    if (after.isPresent()) {
      Board afterBoard = after.get();
      for (Square inputKingSquare : destInput) {
        Move move = new Move(currentPlayer.king(), king, inputKingSquare, Optional.empty(),
            Optional.empty(), Optional.empty(), false);
        result.add(move);
      }
    }

    return result;
  }

  public Optional<Square> enPassantSquare() {
    return potentialEpSquare().flatMap(ep -> validMoves().stream().filter(Move::isEnpassant)
        .filter(move -> move.getDest().equals(ep)).map(Move::getDest).findFirst());
  }

  public List<Move> genEnPassant(Bitboard pawns) {
    List<Move> result = new ArrayList<>();

    potentialEpSquare().ifPresent(ep -> {
      Bitboard pawnsCanEnPassant = pawns.and(ep.pawnAttacks(currentPlayer.negate()));

      pawnsCanEnPassant.getSquares().forEach(square -> {
        enpassant(square, ep).ifPresent(result::add);
      });
    });

    return result;
  }

  public Optional<Move> enpassant(Square orig, Square dest) {
    Square capture = new Square(dest.getFileEnum(), orig.getRankEnum());

    return board.taking(orig, dest, Optional.of(capture))
        .map(after -> new Move(currentPlayer.pawn(), orig, dest, Optional.of(capture),
            Optional.empty(), Optional.empty(), true));
  }


  public Optional<Square> potentialEpSquare() {
    return lastMove.flatMap(move -> {

      Optional<Piece> pieceOptional = board.pieceAt(move.getDest());

      if (pieceOptional.isPresent()) {
        Piece piece = pieceOptional.get();

        Square orig = move.getOrig();
        Square dest = move.getOrig();
        if (piece.getColor() != currentPlayer && piece.getType() == PieceType.Pawn
            && orig.yDist(dest) == 2 && orig.getRankEnum() != piece.getColor().getBackRank()) {
          return Optional.of(dest.prevRank(currentPlayer.negate()).get());
        }
      }

      return Optional.empty();
    });
  }

  private Optional<Move> promotion(Square orig, Square dest, PieceType promotion, boolean capture) {
    Optional<Square> taken = capture ? Optional.of(dest) : Optional.empty();

    Piece promotionPiece = new Piece(currentPlayer, promotion);
    return board.promote(orig, dest, promotionPiece)
        .map(board -> new Move(new Piece(currentPlayer, PieceType.Pawn), orig, dest, taken,
            Optional.of(promotionPiece), Optional.empty(), false));
  }

  private List<Move> genPawnMoves(Square from, Square to, boolean capture) {
    List<Move> moves = new ArrayList<>();
    if (from.getRankEnum() == currentPlayer.getSeventhRank()) {
      PieceType.promotableRoles()
          .forEach(promotion -> promotion(from, to, promotion, capture).ifPresent(moves::add));
    } else {
      normalMove(from, to, PieceType.Pawn, capture).ifPresent(moves::add);
    }
    return moves;
  }

  public List<Move> genKnight(Bitboard knights, Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    knights.getSquares().forEach(from -> {
      Bitboard knightAttacks = from.knightAttacks().and(mask);
      knightAttacks.getSquares().forEach(to -> {
        moves.add(normalMove(from, to, PieceType.Knight, board.isOccupied(to)).get());
      });
    });
    return moves;
  }

  public List<Move> genBishop(Bitboard bishops, Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    bishops.getSquares().forEach(from -> {
      Bitboard bishopAttacks = from.bishopAttacks(board.getOccupiedBB()).and(mask);
      bishopAttacks.getSquares().forEach(to -> {
        moves.add(normalMove(from, to, PieceType.Bishop, board.isOccupied(to)).get());
      });
    });
    return moves;
  }

  public List<Move> genRook(Bitboard rooks, Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    rooks.getSquares().forEach(from -> {
      Bitboard rookAttacks = from.rookAttacks(board.getOccupiedBB()).and(mask);
      rookAttacks.getSquares().forEach(to -> {
        moves.add(normalMove(from, to, PieceType.Rook, board.isOccupied(to)).get());
      });
    });
    return moves;
  }

  public List<Move> genQueen(Bitboard queens, Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    queens.getSquares().forEach(from -> {
      Bitboard queenAttacks = from.queenAttacks(board.getOccupiedBB()).and(mask);
      queenAttacks.getSquares().forEach(to -> {
        moves.add(normalMove(from, to, PieceType.Queen, board.isOccupied(to)).get());
      });
    });
    return moves;
  }

  public List<Move> genNonKingAndNonPawn(Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    moves.addAll(genKnight(getUs().and(board.getKnights()), mask));
    moves.addAll(genBishop(getUs().and(board.getBishops()), mask));
    moves.addAll(genRook(getUs().and(board.getRooks()), mask));
    moves.addAll(genQueen(getUs().and(board.getQueens()), mask));
    return moves;
  }

  public List<Move> genNonKing(Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    moves.addAll(genPawn(getUs().and(board.getPawns()), mask));
    moves.addAll(genNonKingAndNonPawn(mask));

    return moves;
  }

  public List<Move> genPawn(Bitboard pawns, Bitboard mask) {


    List<Move> moves = new ArrayList<>();

    Bitboard capturers = pawns;

    capturers.getSquares().forEach(from -> {
      Bitboard pawnAttacks = from.pawnAttacks(currentPlayer).and(getThem()).and(mask);
      pawnAttacks.getSquares().forEach(to -> {
        moves.addAll(genPawnMoves(from, to, true));
      });
    });

    Bitboard singleMoves =
        board.getOccupiedBB().not().and((isWhiteTurn() ? (board.getWhites().and(pawns)).shiftLeft(8)
            : (board.getBlacks().and(pawns)).shiftRightUnsigned(8)));

    Bitboard doubleMoves = board.getOccupiedBB().not()
        .and((isWhiteTurn() ? singleMoves.shiftLeft(8) : singleMoves.shiftRightUnsigned(8))
            .and(Bitboard.rank(currentPlayer.getFourthRank())));

    singleMoves.and(mask).getSquares().forEach(to -> {
      Square from = Square.fromValue(to.getValue() + (isWhiteTurn() ? -8 : 8));
      moves.addAll(genPawnMoves(from, to, false));
    });

    doubleMoves.and(mask).getSquares().forEach(to -> {
      Square from = Square.fromValue(to.getValue() + (isWhiteTurn() ? -16 : 16));
      moves.add(normalMove(from, to, PieceType.Pawn, false).get());
    });



    return moves;
  }


  private boolean isWhiteTurn() {
    return currentPlayer.equals(Color.White);
  }


  public List<Move> validMoves() {
    List<Move> enPassantMoves = genEnPassant(getUs().and(board.getPawns()));


    List<Move> result = getOurKing().map(king -> {
      List<Move> candidates;
      if (getCheckers().isEmpty()) {


        Bitboard targets = getUs().not();
        candidates = new ArrayList<>();
        candidates.addAll(genNonKing(targets));
        candidates.addAll(genSafeKing(king, targets));
        candidates.addAll(genCastling(king));
        candidates.addAll(enPassantMoves);
      } else {
        candidates = genEvasions(king, getCheckers());
        candidates.addAll(enPassantMoves);
      }

      Bitboard sliderBlockers = board.sliderBlockers(king, currentPlayer);

      if (!sliderBlockers.isEmpty() || !enPassantMoves.isEmpty()) {
        return candidates.stream().filter(move -> isSafe(king, sliderBlockers, move)).toList();
      } else {
        return candidates;
      }
    }).orElse(Collections.emptyList());


    return result;
  }

  private boolean isSafe(Square king, Bitboard blockers, Move move) {

    Bitboard boardRooksQueens = board.getRooks().xor(board.getQueens());
    Bitboard boardBishopsQueens = board.getBishops().xor(board.getQueens());

    if (move.isEnpassant()) {
      Bitboard newOccupied = (board.getOccupiedBB().xor(move.getOrig().bb())
          .xor(move.getDest().withRankOf(move.getOrig()).bb())).or(move.getDest().bb());
      return king.rookAttacks(newOccupied).and(getThem()).and(boardRooksQueens).isEmpty()
          && king.bishopAttacks(newOccupied).and(getThem()).and(boardBishopsQueens).isEmpty();
    } else if (!move.isCastles() || !move.isPromotes()) {
      return !(getUs().and(blockers)).contains(move.getOrig())
          || Bitboard.aligned(move.getOrig(), move.getDest(), king);
    } else {
      return true;
    }
  }



  private List<Move> genEvasions(Square king, Bitboard checkers) {
    Bitboard boardSliders = checkers.and(board.getSliders());

    Bitboard attacked = Bitboard.empty();

    for (Square s : boardSliders.getSquares()) {
      attacked = attacked.or(Bitboard.ray(king, s)).xor(s.bb());
    }
    List<Move> safeKings = genSafeKing(king, getUs().xor(attacked).not());

    Square s = checkers.getSingleSquare().get();
    List<Move> blockers = genNonKing(Bitboard.between(king, s).or(checkers));

    safeKings.addAll(blockers);

    return safeKings;
  }



  @Override
  public String toString() {
    return "ChessGame [board=" + board + ", castle=" + castles + ", currentPlayer=" + currentPlayer
        + ", halfMoveClock=" + halfMoveClock + ", fullMoveNumber=" + fullMoveNumber + ", moves="
        + moves + ", lastMove=" + lastMove + "]";
  }

  public String printBoard() {
    
    StringBuilder boardStr = new StringBuilder();
    Map<Square, Piece> map = board.getPieceMap();
    for (Rank rank : Rank.allReversed) {
      for (File file : File.all) {

        Square s = new Square(file, rank);
        if (map.containsKey(s)) {
          Piece p = map.get(s);
          boardStr.append(p.forsyth() + " ");
        } else {
          boardStr.append("-" + " ");
        }

      }
      boardStr.append("\n");
    }
    
    return boardStr.toString();
  }
  


}
