package com.game.chess.enums;

import java.util.List;

public enum Status {
  Created(10), Started(20), Aborted(25), Mate(30), Resign(31), Stalemate(32), Draw(34);

  private final int id;

  Status(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return toString().substring(0, 1).toLowerCase() + toString().substring(1);
  }

  public boolean is(Status s) {
    return this == s;
  }


  public boolean isGreaterThanOrEqual(Status s) {
    return id >= s.id;
  }

  public boolean isGreaterThan(Status s) {
    return id > s.id;
  }

  public boolean isLessThanOrEqual(Status s) {
    return id <= s.id;
  }

  public boolean isLessThan(Status s) {
    return id < s.id;
  }

  public static final List<Status> all = List.of(values());


}
