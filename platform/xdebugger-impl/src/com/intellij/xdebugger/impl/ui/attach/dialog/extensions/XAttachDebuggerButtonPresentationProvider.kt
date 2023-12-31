// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.xdebugger.impl.ui.attach.dialog.extensions

import com.intellij.xdebugger.XDebuggerBundle
import com.intellij.xdebugger.attach.XAttachDebugger
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Nls

@ApiStatus.Experimental
interface XAttachDebuggerButtonPresentationProvider {
  @Nls
  fun getCustomActionPresentation(): String
}

@Nls
fun XAttachDebugger?.getActionPresentation(): String {
  if (this == null) return XDebuggerBundle.message("xdebugger.attach.button.no.debugger.name")
  if (this is XAttachDebuggerButtonPresentationProvider) return getCustomActionPresentation()
  return XDebuggerBundle.message("xdebugger.attach.button.name", debuggerDisplayName)
}