// This is a generated file. Not intended for manual editing.
package org.editorconfig.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.editorconfig.language.psi.EditorConfigElementTypes.*;
import org.editorconfig.language.psi.base.EditorConfigHeaderElementBase;
import org.editorconfig.language.psi.*;

public class EditorConfigPatternImpl extends EditorConfigHeaderElementBase implements EditorConfigPattern {

  public EditorConfigPatternImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EditorConfigVisitor visitor) {
    visitor.visitPattern(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EditorConfigVisitor) accept((EditorConfigVisitor)visitor);
    else super.accept(visitor);
  }

}
