package com.game.chess.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.game.chess.enums.Color;
import com.game.chess.enums.PieceType;
import com.game.chess.enums.Side;
import com.game.chess.models.Bitboard;
import com.game.chess.models.Board;
import com.game.chess.models.Castle;
import com.game.chess.models.Castles;
import com.game.chess.models.ChessBoard;
import com.game.chess.models.File;
import com.game.chess.models.Move;
import com.game.chess.models.Piece;
import com.game.chess.models.Square;
import com.game.chess.models.UnmovedRooks;

@Service
public class MovesValidationService {

  private final static Logger logger = LoggerFactory.getLogger(MovesValidationService.class);

  @Cacheable("destinations")
  public Map<Square, Bitboard> destinations(ChessBoard chessBoard) {
    return calculateDestinations(chessBoard);
  }

  private Map<Square, Bitboard> calculateDestinations(ChessBoard chessBoard) {
    Map<Square, Bitboard> destinationMap = new HashMap<>();
    for (Move move : validMoves(chessBoard)) {
      destinationMap.merge(move.getOrig(), move.getDest().bb(), Bitboard::or);
    }
    return destinationMap;
  }


  public List<Move> generateMovesAt(ChessBoard chessBoard, Square s) {

    List<Move> moves = new ArrayList<>();

    Board board = chessBoard.getBoard();
    Color currentPlayer = chessBoard.getCurrentPlayer();
    Optional<Move> lastMove = chessBoard.getLastMove();
    Castles castles = chessBoard.getCastles();
    UnmovedRooks unmoovedrooks = chessBoard.getUnmovedrooks();

    Bitboard us = board.getColorBB(currentPlayer);


    Optional<Piece> optionalPiece = board.pieceAt(s);

    if (!optionalPiece.isEmpty()) {

      Piece piece = optionalPiece.get();
      if (piece.getColor() == currentPlayer) {
        Bitboard targets = us.not();
        Bitboard bb = s.bb();

        switch (piece.getType()) {
          case Pawn -> {
            moves.addAll(genEnPassant(board, currentPlayer, us.and(bb), lastMove));
            moves.addAll(genPawn(board, currentPlayer, bb, targets));
          }
          case Knight -> moves.addAll(genKnight(board, currentPlayer, us.and(bb), targets));
          case Bishop -> moves.addAll(genBishop(board, currentPlayer, us.and(bb), targets));
          case Rook -> moves.addAll(genRook(board, currentPlayer, us.and(bb), targets));
          case Queen -> moves.addAll(genQueen(board, currentPlayer, us.and(bb), targets));
          case King -> {
            moves.addAll(genUnsafeKing(board, currentPlayer, s, targets));
            moves.addAll(genCastling(board, castles, unmoovedrooks, currentPlayer, s));
          }

        }


      }


    }

    return moves;
  }

  public List<Move> validMoves(ChessBoard chessBoard) {

    Board board = chessBoard.getBoard();
    Color currentPlayer = chessBoard.getCurrentPlayer();
    Optional<Move> lastMove = chessBoard.getLastMove();
    Castles castles = chessBoard.getCastles();
    UnmovedRooks unmoovedrooks = chessBoard.getUnmovedrooks();

    Bitboard us = board.getColorBB(currentPlayer);
    Optional<Square> getOurKing = board.kingPosOf(currentPlayer);


    List<Move> enPassantMoves =
        genEnPassant(board, currentPlayer, us.and(board.getPawns()), lastMove);


    List<Move> result = getOurKing.map(king -> {
      List<Move> candidates;
      if (getCheckers(board, currentPlayer).isEmpty()) {


        Bitboard targets = us.not();
        candidates = new ArrayList<>();
        candidates.addAll(genNonKing(board, currentPlayer, us, targets));
        candidates.addAll(genSafeKing(board, currentPlayer, king, targets));
        candidates.addAll(genCastling(board, castles, unmoovedrooks, currentPlayer, king));
        candidates.addAll(enPassantMoves);
      } else {
        candidates = genEvasions(board, currentPlayer, king, getCheckers(board, currentPlayer));
        candidates.addAll(enPassantMoves);
      }

      Bitboard sliderBlockers = board.sliderBlockers(king, currentPlayer);

      if (!sliderBlockers.isEmpty() || !enPassantMoves.isEmpty()) {
        return candidates.stream()
            .filter(move -> isSafe(board, currentPlayer, king, sliderBlockers, move)).toList();
      } else {
        return candidates;
      }
    }).orElse(Collections.emptyList());


    return result;
  }



  private List<Move> genUnsafeKing(Board board, Color currentPlayer, Square king, Bitboard mask) {
    return (king.kingAttacks().and(mask))
        .flatMap(to -> normalMove(king, to, PieceType.King, currentPlayer, board.isOccupied(to))
            .map(Collections::singletonList).orElse(Collections.emptyList()));

  }


  private List<Move> genEnPassant(Board board, Color currentPlayer, Bitboard pawns,
      Optional<Move> lastMove) {
    List<Move> result = new ArrayList<>();

    potentialEpSquare(board, currentPlayer, lastMove).ifPresent(ep -> {
      Bitboard pawnsCanEnPassant = pawns.and(ep.pawnAttacks(currentPlayer.negate()));

      pawnsCanEnPassant.getSquares().forEach(square -> {
        enpassant(board, currentPlayer, square, ep).ifPresent(result::add);
      });
    });

    return result;
  }

  private Optional<Square> potentialEpSquare(Board board, Color currentPlayer,
      Optional<Move> lastMove) {
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

  private Optional<Move> enpassant(Board board, Color currentPlayer, Square orig, Square dest) {
    Square capture = new Square(dest.getFileEnum(), orig.getRankEnum());

    return board.taking(orig, dest, Optional.of(capture))
        .map(after -> new Move(currentPlayer.pawn(), orig, dest, Optional.of(capture),
            Optional.empty(), Optional.empty(), true));
  }


  private Bitboard getCheckers(Board board, Color currentPlayer) {

    Optional<Square> ourKing = board.kingPosOf(currentPlayer);
    return ourKing.map(king -> board.attackers(king, currentPlayer.negate()))
        .orElse(Bitboard.empty());

  }

  private List<Move> genNonKing(Board board, Color currentPlayer, Bitboard us, Bitboard mask) {


    List<Move> moves = new ArrayList<>();
    moves.addAll(genPawn(board, currentPlayer, us.and(board.getPawns()), mask));
    moves.addAll(genNonKingAndNonPawn(board, currentPlayer, us, mask));

    return moves;
  }

  private List<Move> genNonKingAndNonPawn(Board board, Color currentPlayer, Bitboard us,
      Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    moves.addAll(genKnight(board, currentPlayer, us.and(board.getKnights()), mask));
    moves.addAll(genBishop(board, currentPlayer, us.and(board.getBishops()), mask));
    moves.addAll(genRook(board, currentPlayer, us.and(board.getRooks()), mask));
    moves.addAll(genQueen(board, currentPlayer, us.and(board.getQueens()), mask));
    return moves;
  }

  private List<Move> genKnight(Board board, Color currentPlayer, Bitboard knights, Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    knights.getSquares().forEach(from -> {
      Bitboard knightAttacks = from.knightAttacks().and(mask);
      knightAttacks.getSquares().forEach(to -> {
        moves
            .add(normalMove(from, to, PieceType.Knight, currentPlayer, board.isOccupied(to)).get());
      });
    });
    return moves;
  }


  private List<Move> genBishop(Board board, Color currentPlayer, Bitboard bishops, Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    bishops.getSquares().forEach(from -> {
      Bitboard bishopAttacks = from.bishopAttacks(board.getOccupiedBB()).and(mask);
      bishopAttacks.getSquares().forEach(to -> {
        moves
            .add(normalMove(from, to, PieceType.Bishop, currentPlayer, board.isOccupied(to)).get());
      });
    });
    return moves;
  }

  private List<Move> genRook(Board board, Color currentPlayer, Bitboard rooks, Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    rooks.getSquares().forEach(from -> {
      Bitboard rookAttacks = from.rookAttacks(board.getOccupiedBB()).and(mask);
      rookAttacks.getSquares().forEach(to -> {
        moves.add(normalMove(from, to, PieceType.Rook, currentPlayer, board.isOccupied(to)).get());
      });
    });
    return moves;
  }

  private List<Move> genQueen(Board board, Color currentPlayer, Bitboard queens, Bitboard mask) {
    List<Move> moves = new ArrayList<>();
    queens.getSquares().forEach(from -> {
      Bitboard queenAttacks = from.queenAttacks(board.getOccupiedBB()).and(mask);
      queenAttacks.getSquares().forEach(to -> {
        moves.add(normalMove(from, to, PieceType.Queen, currentPlayer, board.isOccupied(to)).get());
      });
    });
    return moves;
  }

  private List<Move> genPawn(Board board, Color currentPlayer, Bitboard pawns, Bitboard mask) {


    List<Move> moves = new ArrayList<>();

    Bitboard capturers = pawns;

    Bitboard them = board.getColorBB(currentPlayer.negate());

    capturers.getSquares().forEach(from -> {
      Bitboard pawnAttacks = from.pawnAttacks(currentPlayer).and(them).and(mask);
      pawnAttacks.getSquares().forEach(to -> {
        moves.addAll(genPawnMoves(board, from, to, currentPlayer, true));
      });
    });

    Bitboard singleMoves = board.getOccupiedBB().not()
        .and((currentPlayer.equals(Color.White) ? (board.getWhites().and(pawns)).shiftLeft(8)
            : (board.getBlacks().and(pawns)).shiftRightUnsigned(8)));

    Bitboard doubleMoves =
        board.getOccupiedBB().not()
            .and((currentPlayer.equals(Color.White) ? singleMoves.shiftLeft(8)
                : singleMoves.shiftRightUnsigned(8))
                    .and(Bitboard.rank(currentPlayer.getFourthRank())));

    singleMoves.and(mask).getSquares().forEach(to -> {
      Square from = Square.fromValue(to.getValue() + (currentPlayer.equals(Color.White) ? -8 : 8));
      moves.addAll(genPawnMoves(board, from, to, currentPlayer, false));
    });

    doubleMoves.and(mask).getSquares().forEach(to -> {
      Square from =
          Square.fromValue(to.getValue() + (currentPlayer.equals(Color.White) ? -16 : 16));
      moves.add(normalMove(from, to, PieceType.Pawn, currentPlayer, false).get());
    });



    return moves;
  }


  private List<Move> genPawnMoves(Board board, Square from, Square to, Color currentPlayer,
      boolean capture) {
    List<Move> moves = new ArrayList<>();
    if (from.getRankEnum() == currentPlayer.getSeventhRank()) {
      PieceType.promotableRoles()
          .forEach(promotion -> promotion(board, from, to, promotion, currentPlayer, capture)
              .ifPresent(moves::add));
    } else {
      normalMove(from, to, PieceType.Pawn, currentPlayer, capture).ifPresent(moves::add);
    }
    return moves;
  }

  private List<Move> genSafeKing(Board board, Color currentPlayer, Square king, Bitboard mask) {
    List<Move> result = new ArrayList<>();
    for (Square to : king.kingAttacks().and(mask).getSquares()) {
      if (board.attackers(to, currentPlayer.negate()).isEmpty()) {
        Move move = normalMove(king, to, PieceType.King, currentPlayer, board.isOccupied(to)).get();
        result.add(move);
      }
    }
    return result;
  }


  private Optional<Move> promotion(Board board, Square orig, Square dest, PieceType promotion,
      Color currentPlayer, boolean capture) {
    Optional<Square> taken = capture ? Optional.of(dest) : Optional.empty();

    Piece promotionPiece = new Piece(currentPlayer, promotion);
    return board.promote(orig, dest, promotionPiece)
        .map(b -> new Move(new Piece(currentPlayer, PieceType.Pawn), orig, dest, taken,
            Optional.of(promotionPiece), Optional.empty(), false));
  }



  private Optional<Move> normalMove(Square orig, Square dest, PieceType role, Color currentPlayer,
      boolean capture) {
    Optional<Square> taken = capture ? Optional.of(dest) : Optional.empty();


    return Optional.of(new Move(new Piece(currentPlayer, role), orig, dest, taken, Optional.empty(),
        Optional.empty(), false));
  }

  private boolean isSafe(Board board, Color currentPlayer, Square king, Bitboard blockers,
      Move move) {

    Bitboard boardRooksQueens = board.getRooks().xor(board.getQueens());
    Bitboard boardBishopsQueens = board.getBishops().xor(board.getQueens());

    Bitboard us = board.getColorBB(currentPlayer);
    Bitboard them = board.getColorBB(currentPlayer.negate());
    if (move.isEnpassant()) {
      Bitboard newOccupied = (board.getOccupiedBB().xor(move.getOrig().bb())
          .xor(move.getDest().withRankOf(move.getOrig()).bb())).or(move.getDest().bb());
      return king.rookAttacks(newOccupied).and(them).and(boardRooksQueens).isEmpty()
          && king.bishopAttacks(newOccupied).and(them).and(boardBishopsQueens).isEmpty();
    } else if (!move.isCastles() || !move.isPromotes()) {
      return !(us.and(blockers)).contains(move.getOrig())
          || Bitboard.aligned(move.getOrig(), move.getDest(), king);
    } else {
      return true;
    }
  }



  private List<Move> genEvasions(Board board, Color currentPlayer, Square king, Bitboard checkers) {
    Bitboard boardSliders = checkers.and(board.getSliders());

    Bitboard attacked = Bitboard.empty();

    Bitboard us = board.getColorBB(currentPlayer);

    for (Square s : boardSliders.getSquares()) {
      attacked = attacked.or(Bitboard.ray(king, s)).xor(s.bb());
    }
    List<Move> safeKings = genSafeKing(board, currentPlayer, king, us.xor(attacked).not());

    Square s = checkers.getSingleSquare().get();
    List<Move> blockers =
        genNonKing(board, currentPlayer, us, Bitboard.between(king, s).or(checkers));

    safeKings.addAll(blockers);

    return safeKings;
  }


  private List<Move> genCastling(Board board, Castles castles, UnmovedRooks unmovedrooks,
      Color currentPlayer, Square king) {
    logger.info("Generating castling moves for {} player.", currentPlayer);
    if (!castles.can(currentPlayer)) {
      logger.info("Castling is not allowed for {} player. castlesValue: {}", currentPlayer,
          Bitboard.apply(castles.getValue()).toString());
      return Collections.emptyList();
    } else if (king.getRankEnum().getValue() != currentPlayer.getBackRank().getValue()) {
      logger.info("king is not on the back rank : king rank : {}, {} back rank : {}", king.getRankEnum().toChar(),
          currentPlayer, currentPlayer.getBackRank().toChar());

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
                  List<Move> moves = castle(board, currentPlayer, king, kingTo, rook, rookTo);
                  castlingMoves.addAll(moves);
                  logger.info("Generated castling moves: {}", moves);
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

  private List<Move> castle(Board board, Color currentPlayer, Square king, Square kingTo,
      Square rook, Square rookTo) {

    Optional<Board> after = board.take(king).flatMap(b1 -> b1.take(rook))
        .flatMap(b2 -> b2.put(currentPlayer.king(), kingTo))
        .flatMap(b3 -> b3.put(currentPlayer.rook(), rookTo));



    // Define the destination squares for the move
    List<Square> destInput = List.of(rook, kingTo);

    // Generate a list of Move objects representing the castling move
    List<Move> result = new ArrayList<>();

    if (after.isPresent()) {
      for (Square inputKingSquare : destInput) {
        Move move = new Move(currentPlayer.king(), king, inputKingSquare, Optional.empty(),
            Optional.empty(), Optional.of(new Castle(king, kingTo, rook, rookTo)), false);
        result.add(move);
      }
    }

    return result;
  }

}
