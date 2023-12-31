// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package com.intellij.psi.templateLanguages;

import com.intellij.lang.ASTNode;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dmitry Avdeev
 */
public final class TemplateLanguageUtil {
  private TemplateLanguageUtil() {
  }

  public static @Nullable PsiFile getTemplateFile(PsiFile file) {
    final FileViewProvider viewProvider = file.getViewProvider();
    if (viewProvider instanceof TemplateLanguageFileViewProvider) {
      return viewProvider.getPsi(((TemplateLanguageFileViewProvider)viewProvider).getTemplateDataLanguage());
    } else {
      return null;
    }
  }

  public static PsiFile getBaseFile(@NotNull PsiFile file) {
    FileViewProvider viewProvider = file.getViewProvider();
    return viewProvider.getPsi(viewProvider.getBaseLanguage());
  }

  public static boolean isInsideTemplateFile(@NotNull PsiElement element) {
    return element.getContainingFile().getViewProvider() instanceof TemplateLanguageFileViewProvider;
  }

  public static boolean isTemplateDataFile(@NotNull PsiFile file) {
    FileViewProvider viewProvider = file.getViewProvider();
    return viewProvider instanceof TemplateLanguageFileViewProvider &&
                file == viewProvider.getPsi(((TemplateLanguageFileViewProvider)viewProvider).getTemplateDataLanguage());
  }

  public static @Nullable ASTNode getSameLanguageTreePrev(@NotNull ASTNode node) {
    ASTNode current = node.getTreePrev();
    while (current instanceof OuterLanguageElement) {
      current = current.getTreePrev();
    }
    return current;
  }

  public static @Nullable ASTNode getSameLanguageTreeNext(@NotNull ASTNode node) {
    ASTNode current = node.getTreeNext();
    while (current instanceof OuterLanguageElement) {
      current = current.getTreeNext();
    }
    return current;
  }

  public static PsiElement getSameLanguageTreePrev(@NotNull PsiElement element) {
    PsiElement current = element.getNextSibling();
    while (current instanceof OuterLanguageElement) {
      current = current.getPrevSibling();
    }
    return current;
  }

  public static PsiElement getSameLanguageTreeNext(@NotNull PsiElement element) {
    PsiElement current = element.getNextSibling();
    while (current instanceof OuterLanguageElement) {
      current = current.getNextSibling();
    }
    return current;
  }
}
