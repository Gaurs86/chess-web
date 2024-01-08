package com.game.chess.models;

import java.time.Instant;
import java.util.List;

import com.game.chess.entity.Player;
import com.game.chess.enums.Status;



public class ChessGameDetails {


  int gameId;
  List<Player> players;
  ChessBoard game;
  Status status;
  Instant createdAt;
  Instant movedAt;

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  public ChessBoard getGame() {
    return game;
  }

  public void setGame(ChessBoard game) {
    this.game = game;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getMovedAt() {
    return movedAt;
  }

  public void setMovedAt(Instant movedAt) {
    this.movedAt = movedAt;
  }



}
