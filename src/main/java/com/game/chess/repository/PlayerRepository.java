package com.game.chess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.game.chess.entity.Player;



public interface PlayerRepository extends JpaRepository<Player, Integer> {

  Optional<List<Player>> findByGameId(int gameId);



}
