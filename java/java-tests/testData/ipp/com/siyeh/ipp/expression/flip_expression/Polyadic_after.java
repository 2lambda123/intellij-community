package com.siyeh.ipp.expression.flip_expression;

public class Polyadic {
  int x() {
    return 1 - 3 <caret>- 2;
  }

}