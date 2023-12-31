// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.refactoring.util;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NonCodeUsageInfo extends MoveRenameUsageInfo{
  public final String newText;

  private NonCodeUsageInfo(@NotNull PsiElement element, int startOffset, int endOffset, PsiElement referencedElement, String newText){
    super(element, null, startOffset, endOffset, referencedElement, true);
    this.newText = newText;
  }

  public static @Nullable NonCodeUsageInfo create(@NotNull PsiFile file,
                                                  int startOffset,
                                                  int endOffset,
                                                  PsiElement referencedElement,
                                                  String newText) {
    PsiElement element = file.findElementAt(startOffset);
    while(element != null){
      TextRange range = element.getTextRange();
      if (range.getEndOffset() < endOffset){
        element = element.getParent();
      }
      else{
        break;
      }
    }

    if (element == null) return null;

    int elementStart = element.getTextRange().getStartOffset();
    startOffset -= elementStart;
    endOffset -= elementStart;
    return new NonCodeUsageInfo(element, startOffset, endOffset, referencedElement, newText);
  }

  @Override
  public @Nullable PsiReference getReference() {
    return null;
  }

  public NonCodeUsageInfo replaceElement(PsiElement newElement) {
    return new NonCodeUsageInfo(newElement, getRangeInElement().getStartOffset(), getRangeInElement().getEndOffset(), getReferencedElement(), newText);
  }
}
