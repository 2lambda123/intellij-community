// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.xdebugger.impl.ui.attach.dialog.extensions

import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project

interface XAttachHostSettingsProvider {

  companion object {
    var EP: ExtensionPointName<XAttachHostSettingsProvider> = ExtensionPointName.create(
      "com.intellij.xdebugger.attachHostSettingsProvider")
  }

  fun openHostsSettings(project: Project) {}

  fun openAndCreateTemplate(project: Project) {}

  fun addSettingsChangedListener(project: Project, disposable: Disposable, runnable: Runnable) {}
}