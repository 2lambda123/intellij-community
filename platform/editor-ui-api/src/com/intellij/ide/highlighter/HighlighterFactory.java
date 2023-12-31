// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ide.highlighter;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public final class HighlighterFactory {
  private HighlighterFactory() {}

  public static @NotNull EditorHighlighter createHighlighter(SyntaxHighlighter highlighter, @NotNull EditorColorsScheme settings) {
    return EditorHighlighterFactory.getInstance().createEditorHighlighter(highlighter, settings);
  }

  public static @NotNull EditorHighlighter createHighlighter(Project project, @NotNull String fileName) {
    return EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileName);
  }

  public static @NotNull EditorHighlighter createHighlighter(Project project, @NotNull VirtualFile file) {
    return EditorHighlighterFactory.getInstance().createEditorHighlighter(project, file);
  }

  public static @NotNull EditorHighlighter createHighlighter(Project project, @NotNull FileType fileType) {
    return EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileType);
  }

  public static @NotNull EditorHighlighter createHighlighter(@NotNull EditorColorsScheme settings, @NotNull String fileName, Project project) {
    return EditorHighlighterFactory.getInstance().createEditorHighlighter(settings, fileName, project);
  }

  public static @NotNull EditorHighlighter createHighlighter(@NotNull FileType fileType, @NotNull EditorColorsScheme settings, Project project) {
    return EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, settings, project);
  }

  public static @NotNull EditorHighlighter createHighlighter(@NotNull VirtualFile vFile, @NotNull EditorColorsScheme settings, Project project) {
    return EditorHighlighterFactory.getInstance().createEditorHighlighter(vFile, settings, project);
  }
}