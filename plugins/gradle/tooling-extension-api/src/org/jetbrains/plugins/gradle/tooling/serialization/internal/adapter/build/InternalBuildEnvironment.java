// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.gradle.tooling.serialization.internal.adapter.build;

import org.gradle.tooling.model.build.BuildEnvironment;
import org.gradle.tooling.model.build.GradleEnvironment;
import org.gradle.tooling.model.build.JavaEnvironment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.tooling.serialization.internal.adapter.InternalBuildIdentifier;
import org.jetbrains.plugins.gradle.tooling.serialization.internal.adapter.InternalJavaEnvironment;
import org.jetbrains.plugins.gradle.tooling.serialization.internal.adapter.Supplier;
import org.jetbrains.plugins.gradle.tooling.serialization.internal.adapter.Suppliers;

import java.io.File;
import java.io.Serializable;

@ApiStatus.Internal
public final class InternalBuildEnvironment implements BuildEnvironment, Serializable {
  private final Supplier<InternalBuildIdentifier> buildIdentifier;
  private final Supplier<File> gradleUserHome;
  private final Supplier<String> gradleVersion;
  private final Supplier<InternalJavaEnvironment> java;

  public InternalBuildEnvironment(
    @NotNull Supplier<InternalBuildIdentifier> buildIdentifier,
    @NotNull Supplier<File> gradleUserHome,
    @NotNull Supplier<String> gradleVersion,
    @NotNull Supplier<InternalJavaEnvironment> java
  ) {
    this.buildIdentifier = Suppliers.wrap(buildIdentifier);
    this.gradleUserHome = Suppliers.wrap(gradleUserHome);
    this.gradleVersion = Suppliers.wrap(gradleVersion);
    this.java = Suppliers.wrap(java);
  }

  @Override
  public InternalBuildIdentifier getBuildIdentifier() {
    return this.buildIdentifier.get();
  }

  @Override
  public GradleEnvironment getGradle() {
    return new GradleEnvironment() {
      @Override
      public File getGradleUserHome() {
        return InternalBuildEnvironment.this.gradleUserHome.get();
      }

      @Override
      public String getGradleVersion() {
        return InternalBuildEnvironment.this.gradleVersion.get();
      }
    };
  }

  @Override
  public JavaEnvironment getJava() {
    return java.get();
  }
}
