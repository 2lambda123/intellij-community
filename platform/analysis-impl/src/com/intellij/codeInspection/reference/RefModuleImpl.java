// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.codeInspection.reference;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class RefModuleImpl extends RefEntityImpl implements RefModule {
  private final Module myModule;

  RefModuleImpl(@NotNull Module module, @NotNull RefManager manager) {
    super(module.getName(), manager);
    myModule = module;
    ((RefProjectImpl)manager.getRefProject()).add(this);
  }

  @Override
  public void accept(final @NotNull RefVisitor refVisitor) {
    ApplicationManager.getApplication().runReadAction(() -> refVisitor.visitModule(this));
  }

  @Override
  public @NotNull Module getModule() {
    return myModule;
  }

  @Override
  public boolean isValid() {
    return !myModule.isDisposed();
  }

  @Override
  public Icon getIcon(final boolean expanded) {
    return PlatformIcons.CLOSED_MODULE_GROUP_ICON;
  }

  static @Nullable RefEntity moduleFromName(final RefManager manager, final String name) {
    return manager.getRefModule(ModuleManager.getInstance(manager.getProject()).findModuleByName(name));
  }
}
