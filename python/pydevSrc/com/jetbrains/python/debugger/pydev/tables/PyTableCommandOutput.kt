// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.jetbrains.python.debugger.pydev.tables

enum class CommandOutputType {
  STREAM, DISPLAY
}

interface TableCommandParameters {}

class PyDevCommandParameters(val start: Int, val end: Int) : TableCommandParameters