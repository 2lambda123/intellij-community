// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.internal.statistic.service.fus;

import com.google.gson.GsonBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.io.HttpRequests;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

// StatisticsWhiteListGroups service returns actual "approved" UsagesCollectors(groups)
// the result is gson file:
// {
//  "groups" : [{
//    "id" : "statistics.Productivity",
//    "builds" : [{ "from" : "173.4127.37" }]
//  }, {
//    "id" : "spring-example"
//  }]
//}
public class FUStatisticsWhiteListGroupsService {
  private static final Logger LOG = Logger.getInstance("com.intellij.internal.statistic.service.whiteList.FUStatisticsWhiteListGroupsService");

  @NotNull
  public static Set<String> getApprovedGroups(@NotNull String serviceUrl) {
    String content = null;
    try {
      content = HttpRequests.request(serviceUrl)
                            .productNameAsUserAgent()
                            .readString(null);
    }  catch (IOException e) {
      LOG.error(e);
    }
    if (content == null) return Collections.emptySet();

    WLGroups groups = new GsonBuilder().create().fromJson(content, WLGroups.class);
    return groups.groups.stream().map(group -> group.id).collect(Collectors.toSet());
  }

  private static class WLGroups {
    @NotNull
    public final ArrayList<WLGroup> groups = new ArrayList<>();
  }

  private static class WLGroup {
    @NotNull
    public final String id;
    @NotNull
    public final ArrayList<WLBuild> builds = new ArrayList<>();

    public WLGroup(@NotNull String id) {
      this.id = id;
    }
  }

  private static class WLBuild {
    public final String from;

    private WLBuild(String from) {this.from = from;}
  }
}
