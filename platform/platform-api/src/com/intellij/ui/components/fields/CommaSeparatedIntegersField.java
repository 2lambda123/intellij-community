// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ui.components.fields;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.valueEditors.CommaSeparatedIntegersValueEditor;
import com.intellij.ui.components.fields.valueEditors.ValueEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * A validating text component for comma-separated integer values. Extra spaces before and/or after comma are ignored.
 */
public class CommaSeparatedIntegersField extends JBTextField {

  private final ValueEditor<List<Integer>> myValueEditor;

  @SuppressWarnings("unused") // Default constructor
  public CommaSeparatedIntegersField() {
    this(null, Integer.MIN_VALUE, Integer.MAX_VALUE, null);
  }

  public CommaSeparatedIntegersField(@Nullable String valueName, int minValue, int maxValue, @Nullable @NlsContexts.StatusText String optionalText) {
    myValueEditor = new CommaSeparatedIntegersValueEditor(this, valueName, minValue, maxValue);
    if (optionalText != null) {
      getEmptyText().setText(optionalText);
    }
  }

  public void setValue(@NotNull List<Integer> newValue) {
    myValueEditor.setValue(newValue);
  }

  public @NotNull List<Integer> getValue() {
    return myValueEditor.getValue();
  }

  public void setDefaultValue(@NotNull List<Integer> defaultValue) {
    myValueEditor.setDefaultValue(defaultValue);
  }

  public @NotNull List<Integer> getDefaultValue() {
    return myValueEditor.getDefaultValue();
  }

  public @Nullable String getValueName() {
    return myValueEditor.getValueName();
  }

  public void validateContent() throws ConfigurationException {
    myValueEditor.validateContent();
  }

  public boolean isEmpty() {
    return getValue().isEmpty();
  }

  public void clear() {
    setValue(Collections.emptyList());
  }

  public ValueEditor<List<Integer>> getValueEditor() {
    return myValueEditor;
  }
}
