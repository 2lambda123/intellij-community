// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.application.options.colors;

import com.intellij.codeHighlighting.RainbowHighlighter;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public final class RainbowColorsInSchemeState {
  private final EditorColorsScheme myEditedScheme;
  private final EditorColorsScheme myOriginalScheme;

  public RainbowColorsInSchemeState(@NotNull EditorColorsScheme editedScheme,
                                    @NotNull EditorColorsScheme originalScheme) {
    myEditedScheme = editedScheme;
    myOriginalScheme = originalScheme;
  }

  public void apply(@Nullable EditorColorsScheme scheme) {
    if (scheme != null && scheme != myEditedScheme) {
      RainbowHighlighter.transferRainbowState(scheme, myEditedScheme);
      for (TextAttributesKey key : RainbowHighlighter.RAINBOW_COLOR_KEYS) {
        Color color = myEditedScheme.getAttributes(key).getForegroundColor();
        if (!color.equals(scheme.getAttributes(key).getForegroundColor()) ) {
          scheme.setAttributes(key, RainbowHighlighter.createRainbowAttribute(color));
        }
      }
      updateRainbowMarkup();
    }
  }

  private static void updateRainbowMarkup() {
    Editor[] allEditors = EditorFactory.getInstance().getAllEditors();
    for (Editor editor : allEditors) {
      final Project project = editor.getProject();
      if (project != null) {
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (file != null) {
          DaemonCodeAnalyzer.getInstance(project).restart(file);
        }
      }
    }
  }

  public boolean isModified(@Nullable Language language) {
    return (language == null && isRainbowColorsModified())
           || RainbowHighlighter.isRainbowEnabled(myEditedScheme, language) != RainbowHighlighter.isRainbowEnabled(myOriginalScheme, language);
  }

  private boolean isRainbowColorsModified() {
    for (TextAttributesKey key : RainbowHighlighter.RAINBOW_COLOR_KEYS) {
      if (!myEditedScheme.getAttributes(key).equals(myOriginalScheme.getAttributes(key)) ) {
        return true;
      }
    }
    return false;
  }
}
