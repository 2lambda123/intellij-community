/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.slicer;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

class CanItBeNullAction extends GroupByNullnessActionBase {
  CanItBeNullAction(@NotNull SliceTreeBuilder treeBuilder) {
    super(treeBuilder);
  }

  @Override
  protected boolean isAvailable() {
    SliceRootNode rootNode = myTreeBuilder.getRootSliceNode();
    PsiElement element = rootNode.getRootUsage().getUsageInfo().getElement();
    PsiType type;
    if (element instanceof PsiVariable variable) {
      type = variable.getType();
    }
    else if (element instanceof PsiExpression expression) {
      type = expression.getType();
    }
    else {
      type = null;
    }
    return type instanceof PsiClassType || type instanceof PsiArrayType;
  }
}
