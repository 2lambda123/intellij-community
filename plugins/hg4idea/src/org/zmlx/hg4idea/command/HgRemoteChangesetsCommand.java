// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.zmlx.hg4idea.command;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zmlx.hg4idea.HgBundle;
import org.zmlx.hg4idea.HgProjectConfigurable;
import org.zmlx.hg4idea.HgProjectSettings;
import org.zmlx.hg4idea.HgVcs;
import org.zmlx.hg4idea.action.HgCommandResultNotifier;
import org.zmlx.hg4idea.execution.HgCommandResult;
import org.zmlx.hg4idea.execution.HgRemoteCommandExecutor;
import org.zmlx.hg4idea.util.HgErrorUtil;
import org.zmlx.hg4idea.util.HgUtil;

import javax.swing.event.HyperlinkEvent;
import java.util.List;

import static org.zmlx.hg4idea.HgNotificationIdsHolder.CHANGESETS_ERROR;

/**
 * Common ancestor for HgIncomingCommand and HgOutgoingCommand - changeset commands which need connection to the server.
 *
 * @author Kirill Likhodedov
 */
public abstract class HgRemoteChangesetsCommand extends HgChangesetsCommand {

  private static final Logger LOG = Logger.getInstance(HgRemoteChangesetsCommand.class);

  public HgRemoteChangesetsCommand(Project project, @NonNls String command) {
    super(project, command);
  }

  @Override
  protected void addArguments(List<String> args) {
    args.add("--newest-first");
  }

  @Override
  protected boolean isSilentCommand() {
    return true;
  }

  protected @Nullable String getRepositoryUrl(VirtualFile root) {
    return HgUtil.getRepositoryDefaultPath(project, root);
  }

  @Override
  protected HgCommandResult executeCommandInCurrentThread(VirtualFile repo, List<String> args) {
    String repositoryURL = getRepositoryUrl(repo);
    if (repositoryURL == null) {
      LOG.info("executeCommand no default path configured");
      return null;
    }
    HgRemoteCommandExecutor executor = new HgRemoteCommandExecutor(project, repositoryURL);
    HgCommandResult result = executor.executeInCurrentThread(repo, command, args);
    if (result == HgCommandResult.CANCELLED || HgErrorUtil.isAuthorizationError(result)) {
      final HgVcs vcs = HgVcs.getInstance(project);
      if (vcs == null) {
        return result;
      }
      new HgCommandResultNotifier(project).notifyError(CHANGESETS_ERROR,
                                                       result,
                                                       HgBundle.message("hg4idea.changesets.error"),
                                                       HgBundle.message("hg4idea.changesets.error.msg", repositoryURL),
                                                       new NotificationListener() {
                                                         @Override
                                                         public void hyperlinkUpdate(@NotNull Notification notification,
                                                                                     @NotNull HyperlinkEvent event) {
                                                           ShowSettingsUtil.getInstance()
                                                             .showSettingsDialog(project, HgProjectConfigurable.class);
                                                         }
                                                       });
      final HgProjectSettings projectSettings = vcs.getProjectSettings();
      projectSettings.setCheckIncomingOutgoing(false);
    }
    return result;
  }
}
