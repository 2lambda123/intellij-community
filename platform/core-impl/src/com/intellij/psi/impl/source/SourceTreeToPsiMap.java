// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.psi.impl.source;

import com.intellij.extapi.psi.ASTDelegatePsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SourceTreeToPsiMap {
  private SourceTreeToPsiMap() { }

  public static @Nullable PsiElement treeElementToPsi(@Nullable ASTNode element) {
    return element == null ? null : element.getPsi();
  }

  public static @NotNull <T extends PsiElement> T treeToPsiNotNull(@NotNull ASTNode element) {
    PsiElement psi = element.getPsi();
    assert psi != null : element;
    //noinspection unchecked
    return (T)psi;
  }

  public static @Nullable ASTNode psiElementToTree(@Nullable PsiElement psiElement) {
    return psiElement == null ? null : psiElement.getNode();
  }

  public static @NotNull TreeElement psiToTreeNotNull(@NotNull PsiElement psiElement) {
    ASTNode node = psiElement.getNode();
    assert node instanceof TreeElement : psiElement + ", " + node;
    return (TreeElement)node;
  }

  public static boolean hasTreeElement(@Nullable PsiElement psiElement) {
    return psiElement instanceof TreeElement || psiElement instanceof ASTDelegatePsiElement || psiElement instanceof PsiFileImpl;
  }
}
