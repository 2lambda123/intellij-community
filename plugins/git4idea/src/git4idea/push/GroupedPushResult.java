// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package git4idea.push;

import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

final class GroupedPushResult {

  final @NotNull Map<GitRepository, GitPushRepoResult> successful;
  final @NotNull Map<GitRepository, GitPushRepoResult> errors;
  final @NotNull Map<GitRepository, GitPushRepoResult> rejected;
  final @NotNull Map<GitRepository, GitPushRepoResult> customRejected;

  private GroupedPushResult(@NotNull Map<GitRepository, GitPushRepoResult> successful,
                            @NotNull Map<GitRepository, GitPushRepoResult> errors,
                            @NotNull Map<GitRepository, GitPushRepoResult> rejected,
                            @NotNull Map<GitRepository, GitPushRepoResult> customRejected) {
    this.successful = successful;
    this.errors = errors;
    this.rejected = rejected;
    this.customRejected = customRejected;
  }

  static @NotNull GroupedPushResult group(@NotNull Map<GitRepository, GitPushRepoResult> results) {
    Map<GitRepository, GitPushRepoResult> successful = new HashMap<>();
    Map<GitRepository, GitPushRepoResult> rejected = new HashMap<>();
    Map<GitRepository, GitPushRepoResult> customRejected = new HashMap<>();
    Map<GitRepository, GitPushRepoResult> errors = new HashMap<>();
    for (Map.Entry<GitRepository, GitPushRepoResult> entry : results.entrySet()) {
      GitRepository repository = entry.getKey();
      GitPushRepoResult result = entry.getValue();

      Map<GitRepository, GitPushRepoResult> map = switch (result.getType()) {
        case REJECTED_NO_FF -> rejected;
        case ERROR -> errors;
        case REJECTED_STALE_INFO, REJECTED_OTHER -> customRejected;
        default -> successful;
      };
      map.put(repository, result);
    }
    return new GroupedPushResult(successful, errors, rejected, customRejected);
  }
}
