// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ide.errorTreeView;

import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Zhuravlev
 */
public class NavigatableMessageElement extends ErrorTreeElement implements NavigatableErrorTreeElement {
  private final GroupingElement myParent;
  private final String[] myMessage;
  private final @NotNull Navigatable myNavigatable;
  private final String myExportText;
  private final String myRendererTextPrefix;

  public NavigatableMessageElement(@NotNull ErrorTreeElementKind kind,
                                   @Nullable GroupingElement parent,
                                   String[] message,
                                   @NotNull Navigatable navigatable,
                                   String exportText,
                                   String rendererTextPrefix) {
    super(kind);
    myParent = parent;
    myMessage = message;
    myNavigatable = navigatable;
    myExportText = exportText;
    myRendererTextPrefix = rendererTextPrefix;
  }

  @Override
  public @NotNull Navigatable getNavigatable() {
    return myNavigatable;
  }

  @Override
  public String[] getText() {
    return myMessage;
  }

  @Override
  public Object getData() {
    return myParent == null ? null : myParent.getData();
  }

  public @Nullable GroupingElement getParent() {
    return myParent;
  }

  @Override
  public String getExportTextPrefix() {
    return getKind().getPresentableText() + myExportText;
  }

  public String getRendererTextPrefix() {
    return myRendererTextPrefix;
  }
}
