// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.psi.impl.source.resolve.reference.impl.providers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.impl.http.HttpVirtualFile;
import com.intellij.psi.PsiFileSystemItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

final class HttpFileReferenceHelper extends FileReferenceHelper {
  @Override
  public @Nullable PsiFileSystemItem findRoot(@NotNull Project project, @NotNull VirtualFile file) {
    VirtualFile root = file;
    VirtualFile parent;
    while ((parent = root.getParent()) != null) {
      root = parent;
    }
    return getPsiFileSystemItem(project, root);
  }

  @Override
  public @NotNull Collection<PsiFileSystemItem> getContexts(@NotNull Project project, @NotNull VirtualFile file) {
    PsiFileSystemItem item = getPsiFileSystemItem(project, file);
    return item == null ? Collections.emptyList() : Collections.singleton(item);
  }

  @Override
  public boolean isMine(@NotNull Project project, @NotNull VirtualFile file) {
    return file instanceof HttpVirtualFile;
  }
}