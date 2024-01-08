package com.game.chess.models;


import com.game.chess.enums.MoveType;

public class ChessMoveDetails {

  Long moveId;
  Long playerId;
  Piece pieceMoved;
  Piece pieceCaptured;
  Square from;
  Square to;
  MoveType type;
  Piece promotionPiece;

  public Long getMoveId() {
    return moveId;
  }

  public void setMoveId(Long moveId) {
    this.moveId = moveId;
  }

  public Long getPlayerId() {
    return playerId;
  }

  public void setPlayerId(Long playerId) {
    this.playerId = playerId;
  }

  public Piece getPieceMoved() {
    return pieceMoved;
  }

  public void setPieceMoved(Piece pieceMoved) {
    this.pieceMoved = pieceMoved;
  }

  public Piece getPieceCaptured() {
    return pieceCaptured;
  }

  public void setPieceCaptured(Piece pieceCaptured) {
    this.pieceCaptured = pieceCaptured;
  }

  public Square getFrom() {
    return from;
  }

  public void setFrom(Square from) {
    this.from = from;
  }

  public Square getTo() {
    return to;
  }

  public void setTo(Square to) {
    this.to = to;
  }

  public MoveType getType() {
    return type;
  }

  public void setType(MoveType type) {
    this.type = type;
  }

  public Piece getPromotionPiece() {
    return promotionPiece;
  }

  public void setPromotionPiece(Piece promotionPiece) {
    this.promotionPiece = promotionPiece;
  }

  @Override
  public String toString() {
    return "ChessMoveDetails [moveId=" + moveId + ", playerId=" + playerId + ", pieceMoved="
        + pieceMoved + ", pieceCaptured=" + pieceCaptured + ", from=" + from + ", to=" + to
        + ", type=" + type + ", promotionPiece=" + promotionPiece + "]";
  }



}
