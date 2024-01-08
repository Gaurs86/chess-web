package com.game.chess.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class Player {

  @Id
  @Column(name = "player_id")
  private int playerId;

  @Column(name = "color")
  private String color;

  @Column(name = "game_id")
  private int gameId;



  public int getPlayerId() {
    return playerId;
  }

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }



  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  @Override
  public String toString() {
    return "Player [playerId=" + playerId + ", color=" + color + ", gameId=" + gameId + "]";
  }





}
