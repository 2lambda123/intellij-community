// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.psi.impl.source.tree.injected;

import com.intellij.openapi.editor.SoftWrap;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.ex.SoftWrapChangeListener;
import com.intellij.openapi.editor.ex.SoftWrapModelEx;
import com.intellij.openapi.editor.impl.softwrap.SoftWrapDrawingType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collections;
import java.util.List;

final class SoftWrapModelWindow implements SoftWrapModelEx {
  SoftWrapModelWindow() {}

  @Override
  public List<? extends SoftWrap> getRegisteredSoftWraps() {
    return Collections.emptyList();
  }

  @Override
  public int getSoftWrapIndex(int offset) {
    return -1;
  }

  @Override
  public int paint(@NotNull Graphics g, @NotNull SoftWrapDrawingType drawingType, int x, int y, int lineHeight) {
    return 0;
  }

  @Override
  public int getMinDrawingWidthInPixels(@NotNull SoftWrapDrawingType drawingType) {
    return 0;
  }

  @Override
  public boolean addSoftWrapChangeListener(@NotNull SoftWrapChangeListener listener) {
    return false;
  }

  @Override
  public boolean isRespectAdditionalColumns() {
    return false;
  }

  @Override
  public void forceAdditionalColumnsUsage() {
  }

  @Override
  public boolean isSoftWrappingEnabled() {
    return false;
  }

  @Override
  public @Nullable SoftWrap getSoftWrap(int offset) {
    return null;
  }

  @Override
  public @NotNull List<? extends SoftWrap> getSoftWrapsForRange(int start, int end) {
    return Collections.emptyList();
  }

  @Override
  public @NotNull List<? extends SoftWrap> getSoftWrapsForLine(int documentLine) {
    return Collections.emptyList();
  }

  @Override
  public boolean isVisible(SoftWrap softWrap) {
    return false;
  }

  @Override
  public void beforeDocumentChangeAtCaret() {
  }

  @Override
  public boolean isInsideSoftWrap(@NotNull VisualPosition position) {
    return false;
  }

  @Override
  public boolean isInsideOrBeforeSoftWrap(@NotNull VisualPosition visual) {
    return false;
  }

  @Override
  public void release() {
  }
}
