// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.plugins.javaFX.packaging.preloader;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.packaging.artifacts.Artifact;
import com.intellij.packaging.ui.ArtifactPropertiesEditor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.javaFX.JavaFXBundle;
import org.jetbrains.plugins.javaFX.packaging.JavaFxApplicationClassBrowser;

import javax.swing.*;

public final class JavaFxPreloaderArtifactPropertiesEditor extends ArtifactPropertiesEditor {
  private final JavaFxPreloaderArtifactProperties myProperties;
  private JPanel myPanel;
  private TextFieldWithBrowseButton myPreloaderTf;

  public JavaFxPreloaderArtifactPropertiesEditor(JavaFxPreloaderArtifactProperties properties, Project project, Artifact artifact) {
    super();
    myProperties = properties;
    JavaFxApplicationClassBrowser.preloaderClassBrowser(project, artifact).setField(myPreloaderTf);
  }

  @Override
  public String getTabName() {
    return JavaFXBundle.message("javafx.preloader.tab.name");
  }

  @Override
  public @Nullable JComponent createComponent() {
    return myPanel;
  }

  @Override
  public boolean isModified() {
    return !Comparing.strEqual(myPreloaderTf.getText(), myProperties.getPreloaderClass());
  }

  @Override
  public void apply() {
    myProperties.setPreloaderClass(myPreloaderTf.getText());
  }

  @Override
  public @Nullable String getHelpId() {
    return "Project_Structure_Artifacts_Java_FX_tab";
  }

  @Override
  public void reset() {
    myPreloaderTf.setText(myProperties.getPreloaderClass());
  }
}