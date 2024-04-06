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

  /**
   * 判断石子是否为空或被俘获
   *
   * @return 石子是否为空或被俘获
   */
  public boolean isEmpty() {
    return this == EMPTY || this == BLACK_CAPTURED || this == WHITE_CAPTURED;
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
    return isBlack();
  }
  /** @return Whether or not this stone is of the white variants. */
  public boolean isWhite() {
    return this == WHITE || this == WHITE_RECURSED;
  }

  public boolean needDrawWhite() {
    return isWhite();
  }

  public Stone unGhosted() {
    return isEmpty() ? EMPTY : this;
    /*
    switch (this) {
      case BLACK:
        return BLACK;
      case WHITE:
        return WHITE;
      default:
        return EMPTY;
    }
     */
  }
}
