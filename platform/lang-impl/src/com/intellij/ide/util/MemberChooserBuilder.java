// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

/*
 * @author max
 */
package com.intellij.ide.util;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;

import javax.swing.*;

public final class MemberChooserBuilder<T extends ClassMember> {
  private final Project myProject;
  private boolean myAllowEmptySelection = false;
  private boolean myAllowMultiSelection = true;
  private boolean myIsInsertOverrideVisible = false;
  private boolean myIsCopyJavadocVisible = false;
  private JComponent myHeaderPanel;
  private @NlsContexts.DialogTitle String myTitle;

  public MemberChooserBuilder(final Project project) {
    myProject = project;
  }

  public MemberChooser<T> createBuilder(T[] elements) {
    final MemberChooser<T> chooser =
      new MemberChooser<>(elements, myAllowEmptySelection, myAllowMultiSelection, myProject, myIsInsertOverrideVisible, myHeaderPanel);

    if (myTitle != null) {
      chooser.setTitle(myTitle);
    }

    chooser.setCopyJavadocVisible(myIsCopyJavadocVisible);

    return chooser;
  }

  public void allowEmptySelection(final boolean allowEmptySelection) {
    myAllowEmptySelection = allowEmptySelection;
  }

  public void allowMultiSelection(final boolean allowMultiSelection) {
    myAllowMultiSelection = allowMultiSelection;
  }

  public void overrideAnnotationVisible(final boolean isInsertOverrideVisible) {
    myIsInsertOverrideVisible = isInsertOverrideVisible;
  }

  public void setHeaderPanel(final JComponent headerPanel) {
    myHeaderPanel = headerPanel;
  }

  public void copyJavadocVisible(final boolean isCopyJavadocVisible) {
    myIsCopyJavadocVisible = isCopyJavadocVisible;
  }

  public void setTitle(final @NlsContexts.DialogTitle String title) {
    myTitle = title;
  }
}