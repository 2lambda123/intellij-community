// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.util.text;

import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Copy of {@link java.util.StringTokenizer} with added {@link #getCurrentPosition()} and {@link #reset(String)} methods.
 */
public final class StringTokenizer implements Enumeration<String> {
  private int currentPosition;
  private int newPosition;
  private int maxPosition;
  private String str;
  private String delimiters;
  private final boolean retDelims;
  private boolean delimsChanged;

  /**
   * maxDelimChar stores the value of the delimiter character with the
   * highest value. It is used to optimize the detection of delimiter
   * characters.
   */
  private char maxDelimChar;

  /**
   * Set maxDelimChar to the highest char in the delimiter set.
   */
  private void setMaxDelimChar() {
    if (delimiters == null) {
      maxDelimChar = 0;
      return;
    }

    char m = 0;
    for (int i = 0; i < delimiters.length(); i++) {
      char c = delimiters.charAt(i);
      if (m < c) {
        m = c;
      }
    }
    maxDelimChar = m;
  }

  public StringTokenizer(@NotNull String str, @NotNull String delim, boolean returnDelims) {
    currentPosition = 0;
    newPosition = -1;
    delimsChanged = false;
    this.str = str;
    maxPosition = str.length();
    delimiters = delim;
    retDelims = returnDelims;
    setMaxDelimChar();
  }

  public StringTokenizer(@NotNull String str, @NotNull String delim) {
    this(str, delim, false);
  }

  public StringTokenizer(@NotNull String str) {
    this(str, " \t\n\r\f", false);
  }

  private int skipDelimiters(int startPos) {
    if (delimiters == null) {
      throw new NullPointerException();
    }

    int position = startPos;
    while (!retDelims && position < maxPosition) {
      char c = str.charAt(position);
      if (c > maxDelimChar || delimiters.indexOf(c) < 0) {
        break;
      }
      position++;
    }
    return position;
  }

  private int scanToken(int startPos) {
    int position = startPos;
    while (position < maxPosition) {
      char c = str.charAt(position);
      if (c <= maxDelimChar && delimiters.indexOf(c) >= 0) {
        break;
      }
      position++;
    }
    if (retDelims && startPos == position) {
      char c = str.charAt(position);
      if (c <= maxDelimChar && delimiters.indexOf(c) >= 0) {
        position++;
      }
    }
    return position;
  }

  public boolean hasMoreTokens() {
    newPosition = skipDelimiters(currentPosition);
    return newPosition < maxPosition;
  }

  public @NotNull String nextToken() {
    currentPosition = newPosition >= 0 && !delimsChanged ?
                      newPosition : skipDelimiters(currentPosition);

    /* Reset these anyway */
    delimsChanged = false;
    newPosition = -1;

    if (currentPosition >= maxPosition) {
      throw new NoSuchElementException();
    }
    int start = currentPosition;
    currentPosition = scanToken(currentPosition);
    return str.substring(start, currentPosition);
  }

  public @NotNull String nextToken(@NotNull String delim) {
    delimiters = delim;

    /* delimiter string specified, so set the appropriate flag. */
    delimsChanged = true;

    setMaxDelimChar();
    return nextToken();
  }

  @Override
  public boolean hasMoreElements() {
    return hasMoreTokens();
  }

  @Override
  public @NotNull String nextElement() {
    return nextToken();
  }

  public int countTokens() {
    int count = 0;
    int currpos = currentPosition;
    while (currpos < maxPosition) {
      currpos = skipDelimiters(currpos);
      if (currpos >= maxPosition) {
        break;
      }
      currpos = scanToken(currpos);
      count++;
    }
    return count;
  }

  public int getCurrentPosition() {
    return currentPosition;
  }

  public void reset(@NotNull String s) {
    str = s;
    currentPosition = 0;
    newPosition = -1;
    maxPosition = s.length();
  }
}
