package com.siyeh.igfixes.dataflow.too_broad_scope;

public class ForStatement {

  void noCondition() {
    <caret>  for (var i = 1; ; i++) {
      if (i == 10){
        break;
      }
    }
  }
}