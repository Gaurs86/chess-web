package com.game.chess.request;

public class CreateGameRequestDTO {

  int playerId;
  String playerName;
  String color;

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

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public void setColor(String color) {
    this.color = color;
  }

  @Override
  public String toString() {
    return "CreateGameRequestDTO [playerId=" + playerId + ", playerName=" + playerName + ", color="
        + color + "]";
  }

  
  



}
