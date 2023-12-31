// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.codeInsight.template.postfix.templates.editable;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class DefaultPostfixTemplateEditor implements PostfixTemplateEditor {
  @NotNull private final PostfixTemplateProvider myTemplateProvider;
  @NotNull
  private final PostfixTemplate myTemplateToEdit;

  public DefaultPostfixTemplateEditor(@NotNull PostfixTemplateProvider templateProvider, @NotNull PostfixTemplate templateToEdit) {
    myTemplateProvider = templateProvider;
    myTemplateToEdit = templateToEdit instanceof PostfixTemplateWrapper ? ((PostfixTemplateWrapper)templateToEdit).getDelegate()
                                                                        : templateToEdit;
  }

  @NotNull
  @Override
  public PostfixTemplate createTemplate(@NotNull String templateId, @NotNull String templateName) {
    return new PostfixTemplateWrapper(templateId, templateName, "." + templateName, myTemplateToEdit, myTemplateProvider);
  }

  @NotNull
  @Override
  public JComponent getComponent() {
    return new JBPanel();
  }

  @Override
  public void dispose() {

  }
}
