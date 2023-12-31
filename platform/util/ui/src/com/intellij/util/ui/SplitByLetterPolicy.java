// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.util.ui;

import java.io.File;


public final class SplitByLetterPolicy extends FilePathSplittingPolicy{

  SplitByLetterPolicy() {
  }

  @Override
  public String getPresentableName(File file, int count) {
    String filePath = file.getPath();
    if (count >= filePath.length()) return filePath;
    int nameLength = file.getName().length();
    if (count <= nameLength) return filePath.substring(filePath.length() - count);
    int dotsCount = Math.min(3, count - nameLength);
    int shownCount = count - dotsCount;
    int leftCount = (shownCount - nameLength) / 2 + (shownCount - nameLength) % 2;
    int rightCount = shownCount - leftCount;
    return filePath.substring(0, leftCount) + dots(dotsCount) + filePath.substring(filePath.length() - rightCount);
  }

  private static String dots(int count) {
    return switch (count) {
      case 1 -> ".";
      case 2 -> "..";
      default -> "...";
    };
  }



}
