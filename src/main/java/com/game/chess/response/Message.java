package com.game.chess.response;

public class Message {

  private String type;

  private Payload data;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Payload getData() {
    return data;
  }

  public void setData(Payload data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "Message [type=" + type + ", data=" + data + "]";
  }
  
  

}
