// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package git4idea.branch;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.vcs.log.VcsLogUi;
import com.intellij.vcs.log.data.VcsLogData;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Service(Service.Level.PROJECT)
public final class DeepComparatorHolder implements Disposable {
  private final @NotNull Project myProject;
  private final @NotNull Map<VcsLogUi, DeepComparator> myComparators;

  private DeepComparatorHolder(@NotNull Project project) {
    myProject = project;
    myComparators = new HashMap<>();
  }

  public @NotNull DeepComparator getInstance(@NotNull VcsLogData dataProvider, @NotNull VcsLogUi ui) {
    DeepComparator comparator = myComparators.get(ui);
    if (comparator == null) {
      comparator = new DeepComparator(myProject, GitRepositoryManager.getInstance(myProject), dataProvider, ui, this);
      myComparators.put(ui, comparator);
      if (ui instanceof Disposable) {
        Disposer.register((Disposable)ui, new Disposable() {
          @Override
          public void dispose() {
            DeepComparator removed = myComparators.remove(ui);
            if (removed != null) Disposer.dispose(removed); // check for null in case we dispose DeepComparatorHolder before ui
          }
        });
      }
    }
    return comparator;
  }

  @Override
  public void dispose() {
    myComparators.clear();
  }
}
