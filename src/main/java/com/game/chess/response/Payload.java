package com.game.chess.response;

import java.util.Map;

public class Payload {

  int ply;
  String uci;
  String san;
  String fen;
  String promotion;
  String gameStatus;
  boolean check;
  Map<String, String> dests;


  public int getPly() {
    return ply;
  }

  public void setPly(int ply) {
    this.ply = ply;
  }

  public String getUci() {
    return uci;
  }

  public void setUci(String uci) {
    this.uci = uci;
  }

  public String getSan() {
    return san;
  }

  public void setSan(String san) {
    this.san = san;
  }

  public String getFen() {
    return fen;
  }

  public void setFen(String fen) {
    this.fen = fen;
  }

  public boolean isCheck() {
    return check;
  }

  public void setCheck(boolean check) {
    this.check = check;
  }

  public Map<String, String> getDests() {
    return dests;
  }

  public void setDests(Map<String, String> dests) {
    this.dests = dests;
  }

  public String getPromotion() {
    return promotion;
  }

  public void setPromotion(String promotion) {
    this.promotion = promotion;
  }

  public String getGameStatus() {
    return gameStatus;
  }

  public void setGameStatus(String gameStatus) {
    this.gameStatus = gameStatus;
  }

  @Override
  public String toString() {
    return "Payload [ply=" + ply + ", uci=" + uci + ", san=" + san + ", fen=" + fen + ", promotion="
        + promotion + ", gameStatus=" + gameStatus + ", check=" + check + ", dests=" + dests + "]";
  }
  
  
  
  
  
  



}
