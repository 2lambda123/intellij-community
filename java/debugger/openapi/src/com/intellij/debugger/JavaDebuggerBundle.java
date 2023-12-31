// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger;

import com.intellij.DynamicBundle;
import com.intellij.execution.configurations.RemoteConnection;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Supplier;

public final class JavaDebuggerBundle {
  private static final @NonNls String BUNDLE = "messages.JavaDebuggerBundle";
  private static final DynamicBundle INSTANCE = new DynamicBundle(JavaDebuggerBundle.class, BUNDLE);

  private JavaDebuggerBundle() {}

  public static @NotNull @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
    return INSTANCE.getMessage(key, params);
  }

  public static @NotNull Supplier<@Nls String> messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
                                                              Object @NotNull ... params) {
    return INSTANCE.getLazyMessage(key, params);
  }

  public static String getAddressDisplayName(final RemoteConnection connection) {
    return connection.isUseSockets() ? StringUtil.notNullize(connection.getDebuggerHostName()) + ":" + connection.getDebuggerAddress()
                                     : connection.getDebuggerAddress();
  }

  public static String getTransportName(final RemoteConnection connection) {
    return connection.isUseSockets() ? message("transport.name.socket") : message("transport.name.shared.memory");
  }
}