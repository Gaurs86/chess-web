package com.game.chess.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.game.chess.models.Board;
import com.game.chess.models.ChessGameDetails;

@Component
public class ChessGameStateManager {

  private final ConcurrentHashMap<Integer, ChessGameDetails> gameStates;

  public ChessGameStateManager() {
    this.gameStates = new ConcurrentHashMap<Integer, ChessGameDetails>();
  }

  public ChessGameDetails getGameState(Integer gameId) {
    return gameStates.get(gameId);
  }

  public void putGameState(Integer gameId, ChessGameDetails gameState) {
    gameStates.put(gameId, gameState);
  }

  public void removeGameState(Integer gameId) {
    gameStates.remove(gameId);
  }

}
