package com.game.chess;


import java.util.Scanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.game.chess.models.ChessBoard;
import com.game.chess.service.ChessGame;

@SpringBootApplication()
public class ChessApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChessApplication.class, args);

  }

}
