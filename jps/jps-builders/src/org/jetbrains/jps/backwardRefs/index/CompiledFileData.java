// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.jps.backwardRefs.index;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.backwardRefs.CompilerRef;
import org.jetbrains.jps.backwardRefs.SignatureData;

import java.util.Collection;
import java.util.Map;

public final class CompiledFileData {
  private final Map<CompilerRef, Collection<CompilerRef>> myBackwardHierarchyMap;
  private final Map<CompilerRef, Collection<CompilerRef>> myCasts;
  private final Map<CompilerRef, Integer> myReferences;
  private final Map<CompilerRef, Void> myDefinitions;
  private final Map<SignatureData, Collection<CompilerRef>> mySignatureData;
  private final Map<CompilerRef, Void> myImplicitToString;

  public CompiledFileData(@NotNull Map<CompilerRef, Collection<CompilerRef>> backwardHierarchyMap,
                          @NotNull Map<CompilerRef, Collection<CompilerRef>> casts,
                          @NotNull Map<CompilerRef, Integer> references,
                          @NotNull Map<CompilerRef, Void> definitions,
                          @NotNull Map<SignatureData, Collection<CompilerRef>> signatureData,
                          @NotNull Map<CompilerRef, Void> implicitToString) {
    myBackwardHierarchyMap = backwardHierarchyMap;
    myCasts = casts;
    myReferences = references;
    myDefinitions = definitions;
    mySignatureData = signatureData;
    myImplicitToString = implicitToString;
  }

  public @NotNull Map<CompilerRef, Collection<CompilerRef>> getBackwardHierarchy() {
    return myBackwardHierarchyMap;
  }

  public @NotNull Map<CompilerRef, Integer> getReferences() {
    return myReferences;
  }

  public @NotNull Map<CompilerRef, Void> getDefinitions() {
    return myDefinitions;
  }

  public @NotNull Map<SignatureData, Collection<CompilerRef>> getSignatureData() {
    return mySignatureData;
  }

  public @NotNull Map<CompilerRef, Collection<CompilerRef>> getCasts() {
    return myCasts;
  }

  public @NotNull Map<CompilerRef, Void> getImplicitToString() {
    return myImplicitToString;
  }
}
