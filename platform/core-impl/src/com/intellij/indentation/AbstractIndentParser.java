// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.indentation;

import com.intellij.core.CoreBundle;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractIndentParser implements PsiParser {
  protected IndentPsiBuilder myBuilder;

  @Override
  public @NotNull ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
    myBuilder = createPsiBuilder(builder);
    parseRoot(root);
    return myBuilder.getTreeBuilt();
  }

  protected @NotNull IndentPsiBuilder createPsiBuilder(@NotNull PsiBuilder builder) {
    return new IndentPsiBuilder(builder);
  }

  protected abstract void parseRoot(IElementType root);

  public PsiBuilder.Marker mark(boolean couldBeRolledBack) {
    if (couldBeRolledBack) {
      return myBuilder.markWithRollbackPossibility();
    }
    return myBuilder.mark();
  }

  public PsiBuilder.Marker mark() {
    return mark(false);
  }

  public void done(final @NotNull PsiBuilder.Marker marker, final @NotNull IElementType elementType) {
    marker.done(elementType);
  }

  public static void collapse(final @NotNull PsiBuilder.Marker marker, final @NotNull IElementType elementType) {
    marker.collapse(elementType);
  }

  protected static void drop(final @NotNull PsiBuilder.Marker marker) {
    marker.drop();
  }

  protected void rollbackTo(final @NotNull PsiBuilder.Marker marker) {
    marker.rollbackTo();
  }

  protected boolean eof() {
    return myBuilder.eof();
  }

  protected int getCurrentOffset() {
    return myBuilder.getCurrentOffset();
  }

  public int getCurrentIndent() {
    return myBuilder.getCurrentIndent();
  }

  protected void error(@NotNull @NlsContexts.ParsingError String message) {
    myBuilder.error(message);
  }

  public @Nullable IElementType getTokenType() {
    return myBuilder.getTokenType();
  }

  protected static boolean tokenIn(final @Nullable IElementType elementType, IElementType... tokens) {
    return ArrayUtil.indexOfIdentity(tokens, elementType) != -1;
  }

  protected boolean currentTokenIn(IElementType... tokens) {
    return tokenIn(getTokenType(), tokens);
  }

  protected boolean currentTokenIn(final @NotNull TokenSet tokenSet) {
    return tokenSet.contains(getTokenType());
  }

  protected @NotNull String getTokenText() {
    String result = myBuilder.getTokenText();
    if (result == null) {
      result = "";
    }
    return result;
  }

  protected boolean expect(final @NotNull IElementType elementType) {
    return expect(elementType, CoreBundle.message("parsing.error.expected.element", elementType));
  }

  protected boolean expect(final @NotNull IElementType elementType, @NotNull @NlsContexts.ParsingError String expectedMessage) {
    if (getTokenType() == elementType) {
      advance();
      return true;
    }
    error(expectedMessage);
    return false;
  }

  public @Nullable IElementType lookAhead(int step) {
    return myBuilder.lookAhead(step);
  }

  public @Nullable IElementType rawLookup(int step) {
    return myBuilder.rawLookup(step);
  }

  public boolean isNewLine() {
    return myBuilder.isNewLine();
  }

  public void advance() {
    myBuilder.advanceLexer();
  }

  public void recalculateCurrentIndent() {
   myBuilder.recalculateCurrentIndent(getEolElementType(), getIndentElementType());
  }

  protected void advanceUntil(TokenSet tokenSet) {
    while (getTokenType() != null && !isNewLine() && !tokenSet.contains(getTokenType())) {
      advance();
    }
  }

  protected void advanceUntilEol() {
    advanceUntil(TokenSet.EMPTY);
  }

  protected void errorUntil(TokenSet tokenSet, @NotNull @NlsContexts.ParsingError String message) {
    PsiBuilder.Marker errorMarker = mark();
    advanceUntil(tokenSet);
    errorMarker.error(message);
  }

  protected void errorUntilEol(@NotNull @NlsContexts.ParsingError String message) {
    PsiBuilder.Marker errorMarker = mark();
    advanceUntilEol();
    errorMarker.error(message);
  }

  protected void errorUntilEof() {
    PsiBuilder.Marker errorMarker = mark();
    while (!eof()) {
      advance();
    }
    errorMarker.error(CoreBundle.message("parsing.error.unexpected.token"));
  }

  protected void expectEolOrEof() {
    if (!isNewLine() && !eof()) {
      errorUntilEol(CoreBundle.message("parsing.error.end.of.line.expected"));
    }
  }

  protected abstract IElementType getIndentElementType();
  protected abstract IElementType getEolElementType();
}
