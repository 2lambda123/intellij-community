// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.codeInsight.documentation.render;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ToggleRenderedDocPresentationAction extends EditorAction {
  public ToggleRenderedDocPresentationAction() {
    super(new Handler());
  }

  private static final class Handler extends EditorActionHandler {
    @Override
    protected boolean isEnabledForCaret(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
      return getItem(editor) != null;
    }

    @Override
    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
      DocRenderItem item = getItem(editor);
      if (item != null) {
        item.toggle();
      }
    }

    private static DocRenderItem getItem(@NotNull Editor editor) {
      return DocRenderItemManager.getInstance().getItemAroundOffset(editor, editor.getCaretModel().getOffset());
    }
  }
}
