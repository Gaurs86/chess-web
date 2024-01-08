package com.game.chess.service;

import java.util.Optional;
import com.game.chess.enums.PieceType;
import com.game.chess.enums.Side;
import com.game.chess.models.Bitboard;
import com.game.chess.models.Castles;
import com.game.chess.models.ChessBoard;
import com.game.chess.models.Move;
import com.game.chess.models.Piece;
import com.game.chess.models.Square;
import com.game.chess.models.UnmovedRooks;

public class MoveHandler {

  public void updateBoard(ChessBoard game, Move move) {

    Castles castleRights = game.getCastles();
    UnmovedRooks unmovedRooks = game.getUnmovedrooks();


    Square dest = move.getDest();
    Piece piece = move.getPiece();

    if (move.isCaptures()) {
      Optional<Optional<Side>> result = unmovedRooks.side(dest);

      if (result.isPresent()) {
        Optional<Side> sideResult = result.get();
        unmovedRooks = unmovedRooks.and(dest.bb().not());
        if (sideResult.isPresent()) {
          Side side = sideResult.get();
          castleRights = castleRights.without(piece.getColor().negate(), side);
        } else {
          // There is only one unmoved rook left, so just remove the color from castleRights
          castleRights = castleRights.without(piece.getColor().negate());
        }

      }
    }

    if ((PieceType.Rook).equals(move.getPiece().getType())
        && unmovedRooks.contains(move.getOrig())) {

    }

    if (move.getPiece().is(PieceType.King)) {
      unmovedRooks = unmovedRooks.without(move.getPiece().getColor());
      castleRights = castleRights.without(move.getPiece().getColor());

    }


  }

}
