// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.util.containers;

import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

public abstract class ImmutableUserMap {
  public static final ImmutableUserMap EMPTY = new ImmutableUserMap() {
    @Override
    public <T> T get(final @NotNull Key<T> key) {
      return null;
    }
  };

  private ImmutableUserMap() {
  }

  public abstract <T> T get(@NotNull Key<T> key);

  public final <T> ImmutableUserMap put(final @NotNull Key<T> key, final T value) {
    return new ImmutableUserMapImpl<>(key, value, this);
  }

  private static final class ImmutableUserMapImpl<V> extends ImmutableUserMap {
    private final Key<V> myKey;
    private final V myValue;
    private final ImmutableUserMap myNext;

    private ImmutableUserMapImpl(final Key<V> key, final V value, final ImmutableUserMap next) {
      myKey = key;
      myNext = next;
      myValue = value;
    }

    @Override
    public <T> T get(final @NotNull Key<T> key) {
      if (key.equals(myKey)) return (T)myValue;
      return myNext.get(key);
    }

  }
}
