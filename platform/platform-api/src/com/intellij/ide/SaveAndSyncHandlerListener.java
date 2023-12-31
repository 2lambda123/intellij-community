// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ide;

import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface SaveAndSyncHandlerListener {
  @Topic.AppLevel
  Topic<SaveAndSyncHandlerListener> TOPIC = new Topic<>("SaveAndSyncHandler events", SaveAndSyncHandlerListener.class);

  default void beforeRefresh() {}

  default void beforeSave(SaveAndSyncHandler.SaveTask task, boolean forceExecuteImmediately) {}
}