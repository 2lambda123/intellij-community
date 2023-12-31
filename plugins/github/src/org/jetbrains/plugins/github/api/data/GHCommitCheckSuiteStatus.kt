// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.github.api.data

import com.intellij.collaboration.api.dto.GraphQLNodesDTO

class GHCommitCheckSuiteStatus(checkRuns: GraphQLNodesDTO<GHCommitCheckRunStatus>) {
  val checkRuns: List<GHCommitCheckRunStatus> = checkRuns.nodes
}