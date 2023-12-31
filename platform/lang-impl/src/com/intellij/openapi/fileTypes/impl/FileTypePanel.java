// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.fileTypes.impl;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.fileTypes.FileTypesBundle;
import com.intellij.ui.ContextHelpLabel;
import com.intellij.ui.components.JBLabel;

import javax.swing.*;

final class FileTypePanel {
  JPanel myWholePanel;
  JButton myAssociateButton;
  JPanel myAssociatePanel;
  JBLabel myAssociateMessageLabel;
  @SuppressWarnings("unused") private JLabel myAssociateContextHelpLabel;
  JPanel myUpperPanel;

  private void createUIComponents() {
    myAssociateContextHelpLabel = ContextHelpLabel.create(
      FileTypesBundle.message("filetype.associate.context.help.text", ApplicationInfo.getInstance().getFullApplicationName())
    );
  }
}
