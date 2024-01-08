package com.game.chess.request;

public class JoinRequestDTO {

  int gameId;
  int playerId;
  String playerName;
  String color;

  @Override
  public String toString() {
    return "JoinRequestDTO [gameId=" + gameId + ", playerId=" + playerId + ", color=" + color + "]";
  }

  public int getGameId() {
    return gameId;
  }

  public int getPlayerId() {
    return playerId;
  }

  public String getColor() {
    return color;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public void setColor(String color) {
    this.color = color;
  }
  
  



}
