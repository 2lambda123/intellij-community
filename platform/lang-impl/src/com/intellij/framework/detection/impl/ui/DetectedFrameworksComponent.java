// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.framework.detection.impl.ui;

import com.intellij.framework.detection.DetectedFrameworkDescription;
import com.intellij.framework.detection.DetectionExcludesConfiguration;
import com.intellij.framework.detection.FrameworkDetectionContext;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public final class DetectedFrameworksComponent {
  private JPanel myMainPanel;
  private final DetectedFrameworksTree myTree;
  private JPanel myTreePanel;
  private JComboBox<GroupByOption> myGroupByComboBox;
  private JLabel myDescriptionLabel;
  private JPanel myOptionsPanel;

  public DetectedFrameworksComponent(final FrameworkDetectionContext context) {
    myTree = new DetectedFrameworksTree(context, GroupByOption.TYPE) {
      @Override
      protected void onNodeStateChanged(CheckedTreeNode node) {
        super.onNodeStateChanged(node);
        updateOptionsPanel();
      }
    };
    myTreePanel.add(ScrollPaneFactory.createScrollPane(myTree), BorderLayout.CENTER);
    myGroupByComboBox.setModel(new EnumComboBoxModel<>(GroupByOption.class));
    myGroupByComboBox.setRenderer(SimpleListCellRenderer.create("", GroupByOption::getPresentableName));
    myGroupByComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        myTree.changeGroupBy((GroupByOption)myGroupByComboBox.getSelectedItem());
      }
    });
    myTree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        updateOptionsPanel();
      }
    });
    updateOptionsPanel();
  }

  public DetectedFrameworksTree getTree() {
    return myTree;
  }

  private void updateOptionsPanel() {
    final DetectedFrameworkTreeNodeBase[] nodes = myTree.getSelectedNodes(DetectedFrameworkTreeNodeBase.class, null);
    if (nodes.length == 1) {
      final DetectedFrameworkTreeNodeBase node = nodes[0];
      String description = node.isChecked() ? node.getCheckedDescription() : node.getUncheckedDescription();
      if (description != null) {
        ((CardLayout)myOptionsPanel.getLayout()).show(myOptionsPanel, "description");
        myDescriptionLabel.setText(UIUtil.toHtml(description));
        return;
      }
    }
    ((CardLayout)myOptionsPanel.getLayout()).show(myOptionsPanel, "empty");
  }

  public List<DetectedFrameworkDescription> getSelectedFrameworks() {
    return Arrays.asList(myTree.getCheckedNodes(DetectedFrameworkDescription.class, null));
  }

  public JComponent getMainPanel() {
    return myMainPanel;
  }

  public void processUncheckedNodes(final DetectionExcludesConfiguration excludesConfiguration) {
    getTree().processUncheckedNodes(node -> node.disableDetection(excludesConfiguration));
  }

  public enum GroupByOption {
    TYPE(ProjectBundle.messagePointer("list.item.group.by.type")),
    DIRECTORY(ProjectBundle.messagePointer("list.item.group.by.directory"));
    private final Supplier<@NlsContexts.ListItem String> myPresentableName;

    GroupByOption(Supplier<String> presentableName) {
      myPresentableName = presentableName;
    }

    @NlsContexts.ListItem String getPresentableName() {
      return myPresentableName.get();
    }
  }
}
