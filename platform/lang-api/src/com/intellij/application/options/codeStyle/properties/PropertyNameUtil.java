// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.application.options.codeStyle.properties;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

final class PropertyNameUtil {

  private PropertyNameUtil() {
  }

  static String getPropertyName(@NotNull String fieldName) {
    return StringUtil.toLowerCase(preprocess(fieldName));
  }

  private static @NotNull String preprocess(@NotNull String fieldName) {
    if (fieldName.startsWith("SPACE_WITHIN")) {
      return fieldName.replace("SPACE_WITHIN", "SPACES_WITHIN");
    }
    else if (fieldName.startsWith("SPACE_AROUND")) {
      return fieldName.replace("SPACE_AROUND", "SPACES_AROUND");
    }
    return fieldName;
  }
}
