package featurecat.lizzie.rules;

public enum Stone {
  BLACK,
  WHITE,
  EMPTY,
  BLACK_RECURSED,
  WHITE_RECURSED,
  BLACK_CAPTURED,
  WHITE_CAPTURED;

  /**
   * used to find the opposite color stone
   *
   * @return the opposite stone type
   */
  public Stone opposite() {
    switch (this) {
      case BLACK:
        return WHITE;
      case WHITE:
        return BLACK;
      default:
        return this;
    }
  }

  /**
   * used to keep track of which stones were visited during removal of dead stones
   *
   * @return the recursed version of this stone color
   */
  public Stone recursed() {
    switch (this) {
      case BLACK:
        return BLACK_RECURSED;
      case WHITE:
        return WHITE_RECURSED;
      default:
        return this;
    }
  }

  public boolean isEmpty() {
    switch (this) {
      case EMPTY:
        return true;
      case BLACK_CAPTURED:
        return true;
      case WHITE_CAPTURED:
        return true;
      default:
        return false;
    }
  }
  /**
   * used to keep track of which stones were visited during removal of dead stones
   *
   * @return the unrecursed version of this stone color
   */
  public Stone unrecursed() {
    switch (this) {
      case BLACK_RECURSED:
        return BLACK;
      case WHITE_RECURSED:
        return WHITE;
      default:
        return this;
    }
  }

  /** @return Whether or not this stone is of the black variants. */
  public boolean isBlack() {
    return this == BLACK || this == BLACK_RECURSED;
  }

  public boolean isBlackColor() {
    return this == BLACK || this == BLACK_RECURSED || this == Stone.BLACK_CAPTURED;
  }

  public boolean needDrawBlack() {
    return this == BLACK || this == BLACK_RECURSED;
  }
  /** @return Whether or not this stone is of the white variants. */
  public boolean isWhite() {
    return this != EMPTY && !this.isBlack();
  }

  public boolean needDrawWhite() {
    return this == WHITE || this == WHITE_RECURSED;
  }

  public Stone unGhosted() {
    switch (this) {
      case BLACK:
        return BLACK;
      case WHITE:
        return WHITE;
      default:
        return EMPTY;
    }
  }
}
