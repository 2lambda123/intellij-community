// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.toolWindow

interface ToolWindowStripeManager {
  fun allowToShowOnStripe(id: String, isDefaultLayout: Boolean, isNewUi: Boolean): Boolean
}