package com.game.chess.models;

import com.game.chess.enums.GameState;
import com.game.chess.enums.PieceColor;

public class Game {
		
	Player[] players;
	Board board;
	GameState gameState;
	PieceColor turn;
	
	public Game(Player player1, Player player2) {
		
		this.gameState = GameState.NEW;
		turn=PieceColor.WHITE;
	}
		
	

}
