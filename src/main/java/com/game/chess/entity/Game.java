package com.game.chess.entity;



import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "games")
public class Game {

  @Id
  @Column(name = "game_id")
  private int gameId;

  @Column(name = "status")
  private String status;

  @Column(name = "current_turn")
  private String currentTurn;

  @Column(name = "start_time")
  private String startTime;

  @Column(name = "end_time")
  private String endTime;


  public int getId() {
    return gameId;
  }

  public void setId(int id) {
    this.gameId = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCurrentTurn() {
    return currentTurn;
  }

  public void setCurrentTurn(String currentTurn) {
    this.currentTurn = currentTurn;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }



}
