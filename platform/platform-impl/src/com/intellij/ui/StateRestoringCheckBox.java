// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

/*
 * @author Eugene Zhuravlev
 */
package com.intellij.ui;

import com.intellij.openapi.util.NlsContexts;

import javax.swing.*;

/**
 * When enabled the checkbox behaves like the ordinary checkbox
 * If to use special methods to enable/disable it,
 * it will manage different selected/unselected states for each mode - enabled or disabled
 */
public final class StateRestoringCheckBox extends JCheckBox {
  private boolean myIsSelectedWhenSelectable;

  public StateRestoringCheckBox() {
    super();
    myIsSelectedWhenSelectable = isSelected();
  }

  public StateRestoringCheckBox(Icon icon) {
    super(icon);
    myIsSelectedWhenSelectable = isSelected();
  }

  public StateRestoringCheckBox(Icon icon, boolean selected) {
    super(icon, selected);
    myIsSelectedWhenSelectable = isSelected();
  }

  public StateRestoringCheckBox(@NlsContexts.Checkbox String text) {
    super(text);
    myIsSelectedWhenSelectable = isSelected();
  }

  public StateRestoringCheckBox(Action a) {
    super(a);
    myIsSelectedWhenSelectable = isSelected();
  }

  public StateRestoringCheckBox(@NlsContexts.Checkbox String text, boolean selected) {
    super(text, selected);
    myIsSelectedWhenSelectable = isSelected();
  }

  public StateRestoringCheckBox(@NlsContexts.Checkbox String text, Icon icon) {
    super(text, icon);
    myIsSelectedWhenSelectable = isSelected();
  }

  public StateRestoringCheckBox(@NlsContexts.Checkbox String text, Icon icon, boolean selected) {
    super(text, icon, selected);
    myIsSelectedWhenSelectable = isSelected();
  }

  /**
   * The method should be used instead of setEnabled(false) or disable() in order to support selected state saving/recovering
   * Remembers the selected state of the checkbox when the checkbox is enabled, disables it
   * and sets the selected state according to tha parameter pased
   * @param isSelected the parameter telling whetheer the checkbox is selected when disabled
   */
  public void makeUnselectable(boolean isSelected) {
    if (isEnabled()) {
      myIsSelectedWhenSelectable = isSelected();
      setEnabled(false);
    }
    setSelected(isSelected);
  }

  /**
   * The method should be used instead of setEnabled(true) or enable() in order to support selected state saving/recovering
   * Enables the checkbox and restores the selected state of the checkbox to the one, that it had before the makeUnselectable() method was called
   * that was before the checkbox was disabled
   */
  public void makeSelectable() {
    if (!isEnabled()) {
      setEnabled(true);
      setSelected(myIsSelectedWhenSelectable);
    }
  }

  public boolean isSelectedWhenSelectable() {
    if (isEnabled()) {
      return isSelected();
    }
    return myIsSelectedWhenSelectable;
  }
}
