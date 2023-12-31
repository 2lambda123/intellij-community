// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.util.xml.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ReferenceEditorWithBrowseButton;
import com.intellij.util.Function;
import com.intellij.util.xml.XmlDomBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextControl extends EditorTextFieldControl<TextPanel> {

  public TextControl(final DomWrapper<String> domWrapper) {
    super(domWrapper);
  }

  public TextControl(final DomWrapper<String> domWrapper, final boolean commitOnEveryChange) {
    super(domWrapper, commitOnEveryChange);
  }

  @Override
  protected EditorTextField getEditorTextField(final @NotNull TextPanel panel) {
    final Component component = panel.getComponent(0);
    if (component instanceof ReferenceEditorWithBrowseButton) {
      return ((ReferenceEditorWithBrowseButton)component).getEditorTextField();
    }
    return (EditorTextField)component;
  }

  @Override
  protected TextPanel createMainComponent(TextPanel boundedComponent, final Project project) {
    if (boundedComponent == null) {
      boundedComponent = new TextPanel();
    }
    boundedComponent.removeAll();
    final Function<String, Document> factory = s -> PsiDocumentManager.getInstance(project)
    .getDocument(PsiFileFactory.getInstance(project).createFileFromText("a.txt", PlainTextLanguage.INSTANCE, "", true, false));
    final TextPanel boundedComponent1 = boundedComponent;
    final EditorTextField editorTextField = new EditorTextField(factory.fun(""), project, FileTypes.PLAIN_TEXT) {
      @Override
      protected @NotNull EditorEx createEditor() {
        final EditorEx editor = super.createEditor();
        return boundedComponent1 instanceof MultiLineTextPanel ? makeBigEditor(editor, ((MultiLineTextPanel)boundedComponent1).getRowCount()) : editor;
      }
      @Override
      protected boolean isOneLineMode() {
        return false;
      }
    };

    if (boundedComponent instanceof BigTextPanel) {
      final ReferenceEditorWithBrowseButton editor = new ReferenceEditorWithBrowseButton(null, editorTextField, factory);
      boundedComponent.add(editor);
      editor.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          EditorTextField textArea = new EditorTextField(editorTextField.getDocument(), project, FileTypes.PLAIN_TEXT) {
            @Override
            protected @NotNull EditorEx createEditor() {
              final EditorEx editor = super.createEditor();
              editor.setEmbeddedIntoDialogWrapper(true);
              return makeBigEditor(editor, 5);
            }

            @Override
            protected boolean isOneLineMode() {
              return false;
            }
          };

          DialogBuilder builder = new DialogBuilder(project);
          builder.setDimensionServiceKey("TextControl");
          builder.setCenterPanel(textArea);
          builder.setPreferredFocusComponent(textArea);
          builder.setTitle(XmlDomBundle.message("big.text.control.window.title"));
          builder.addCloseButton();
          builder.show();
        }
      });
      return boundedComponent;
    }

    boundedComponent.add(editorTextField);
    return boundedComponent;
  }

  private static EditorEx makeBigEditor(final EditorEx editor, int rowCount) {
    editor.setVerticalScrollbarVisible(true);
    final JTextArea area = new JTextArea(10, 50);
    area.setRows(rowCount);
    editor.getComponent().setPreferredSize(area.getPreferredSize());
    return editor;
  }


}
