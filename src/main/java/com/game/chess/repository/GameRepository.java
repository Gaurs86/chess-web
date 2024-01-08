package com.game.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.game.chess.entity.Game;

public interface GameRepository extends JpaRepository<Game, Integer> {

}
