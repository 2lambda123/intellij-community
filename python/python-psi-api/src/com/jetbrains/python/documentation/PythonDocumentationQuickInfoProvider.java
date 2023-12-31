/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
package com.jetbrains.python.documentation;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.types.TypeEvalContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows you to inject quick info into python documentation provider
 *
 * @author Ilya.Kazakevich
 */
public interface PythonDocumentationQuickInfoProvider {
  ExtensionPointName<PythonDocumentationQuickInfoProvider> EP_NAME =
    ExtensionPointName.create("Pythonid.pythonDocumentationQuickInfoProvider");

  /**
   * Return quick info for <strong>original</strong> element.
   *
   * @param originalElement original element
   * @return info (if exists) or null (if another provider should be checked)
   */
  @Nullable
  @Nls
  default String getQuickInfo(@NotNull PsiElement originalElement) {
    return null;
  }

  @ApiStatus.Experimental
  @Nullable
  @Nls
  default String getHoverAdditionalQuickInfo(@NotNull TypeEvalContext context, @Nullable PsiElement originalElement) {
    return null;
  }
}
