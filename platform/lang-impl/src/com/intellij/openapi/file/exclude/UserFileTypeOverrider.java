// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.file.exclude;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.impl.FileTypeOverrider;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Substitutes type for files which users explicitly marked with "Override File Type" action
 */
final class UserFileTypeOverrider implements FileTypeOverrider {
  @Nullable
  @Override
  public FileType getOverriddenFileType(@NotNull VirtualFile file) {
    String overriddenType = OverrideFileTypeManager.getInstance().getFileValue(file);
    if (overriddenType != null) {
      return FileTypeManager.getInstance().findFileTypeByName(overriddenType);
    }
    return null;
  }
}
