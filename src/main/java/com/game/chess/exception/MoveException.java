package com.game.chess.exception;

public class MoveException extends RuntimeException {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public MoveException(String message) {
    super(message);
  }
}
