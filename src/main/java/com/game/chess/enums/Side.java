package com.game.chess.enums;


import java.util.List;
import java.util.Optional;

import com.game.chess.models.File;
import com.game.chess.models.Square;

public enum Side {
  KingSide, QueenSide;

  public static Optional<Side> kingRookSide(Square king, Square rook) {
    if (king.onSameRank(rook)) {
      return Optional.of(king.isBefore(rook) ? Side.QueenSide : Side.KingSide);
    } else {
      return Optional.empty();
    }
  }

  public File castledKingFile() {
    return this == KingSide ? File.G : File.C;
  }

  public File castledRookFile() {
    return this == KingSide ? File.F : File.D;
  }

  public Side unary_() {
    return this == KingSide ? QueenSide : KingSide;
  }

  public static List<Side> all() {
    return List.of(KingSide, QueenSide);
  }

  public <A> A fold(A k, A q) {
    return this == KingSide ? k : q;
  }
}
