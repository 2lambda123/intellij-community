// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.util.indexing;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class IndexedFileImpl extends UserDataHolderBase implements IndexedFile {
  protected final VirtualFile myFile;

  private volatile Project myProject;

  private String myFileName;
  private FileType mySubstituteFileType;

  private final @Nullable FileType myType;

  public IndexedFileImpl(@NotNull VirtualFile file, Project project) {
    this(file, null, project);
  }

  public IndexedFileImpl(@NotNull VirtualFile file, @Nullable FileType type, Project project) {
    myFile = file;
    myProject = project;
    myType = type;
  }

  @Override
  public @NotNull FileType getFileType() {
    if (mySubstituteFileType == null) {
      mySubstituteFileType = SubstitutedFileType.substituteFileType(myFile, myType != null ? myType : myFile.getFileType(), getProject());
    }
    return mySubstituteFileType;
  }

  public void setSubstituteFileType(@NotNull FileType substituteFileType) {
    mySubstituteFileType = substituteFileType;
  }

  @Override
  public @NotNull VirtualFile getFile() {
    return myFile;
  }

  @Override
  public @NotNull String getFileName() {
    if (myFileName == null) {
      myFileName = myFile.getName();
    }
    return myFileName;
  }

  @Override
  public Project getProject() {
    return myProject;
  }

  public void setProject(Project project) {
    myProject = project;
  }

  @Override
  public String toString() {
    return "IndexedFileImpl(" + getFileName() + ")";
  }
}
