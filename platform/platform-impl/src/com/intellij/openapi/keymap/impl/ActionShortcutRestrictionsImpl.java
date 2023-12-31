// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.keymap.impl;

import com.intellij.openapi.actionSystem.IdeActions;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ActionShortcutRestrictionsImpl extends ActionShortcutRestrictions {
  private static final ShortcutRestrictions MOUSE_SINGLE_CLICK_ONLY = new ShortcutRestrictions(true, true, false, false, false, false);
  private static final ShortcutRestrictions FIXED_SHORTCUT = new ShortcutRestrictions(false, false, false, false, false, false);
  private static final ShortcutRestrictions SWING_SHORTCUT = new ShortcutRestrictions(true, false, false, true, false, false);

  @Override
  public @NotNull ShortcutRestrictions getForActionId(@NonNls String actionId) {
    if (actionId == null) return ShortcutRestrictions.NO_RESTRICTIONS;
    if (actionId.startsWith("Swing-")) return SWING_SHORTCUT;

    if (IdeActions.ACTION_EDITOR_ADD_OR_REMOVE_CARET.equals(actionId) ||
        IdeActions.ACTION_EDITOR_CREATE_RECTANGULAR_SELECTION.equals(actionId) ||
        IdeActions.ACTION_EDITOR_ADD_RECTANGULAR_SELECTION_ON_MOUSE_DRAG.equals(actionId) ||
        IdeActions.ACTION_EDITOR_CREATE_RECTANGULAR_SELECTION_ON_MOUSE_DRAG.equals(actionId)) {
      return MOUSE_SINGLE_CLICK_ONLY;
    }
    if (IdeActions.ACTION_EXPAND_LIVE_TEMPLATE_BY_TAB.equals(actionId)) {
      return FIXED_SHORTCUT;
    }
    return ShortcutRestrictions.NO_RESTRICTIONS;
  }
}
