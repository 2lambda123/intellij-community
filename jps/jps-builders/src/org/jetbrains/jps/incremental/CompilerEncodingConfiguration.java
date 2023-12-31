// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.jps.incremental;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.BuildRootIndex;
import org.jetbrains.jps.builders.java.JavaBuilderExtension;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.model.JpsEncodingConfigurationService;
import org.jetbrains.jps.model.JpsEncodingProjectConfiguration;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;
import org.jetbrains.jps.service.JpsServiceManager;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.File;
import java.util.*;

public final class CompilerEncodingConfiguration {
  private final JpsModel myJpsModel;
  private final Map<String, String> myUrlToCharset;
  private final String myProjectCharset;
  private final BuildRootIndex myRootsIndex;
  private Map<JpsModule, Set<String>> myModuleCharsetMap;

  public CompilerEncodingConfiguration(JpsModel jpsModel, BuildRootIndex index) {
    myJpsModel = jpsModel;
    JpsEncodingProjectConfiguration configuration = JpsEncodingConfigurationService.getInstance().getEncodingConfiguration(jpsModel.getProject());
    myUrlToCharset = configuration != null ? configuration.getUrlToEncoding() : Collections.emptyMap();
    myProjectCharset = JpsEncodingConfigurationService.getInstance().getProjectEncoding(jpsModel);
    myRootsIndex = index;
  }

  public Map<JpsModule, Set<String>> getModuleCharsetMap() {
    if (myModuleCharsetMap == null) {
      myModuleCharsetMap = computeModuleCharsetMap();
    }
    return myModuleCharsetMap;
  }

  private Map<JpsModule, Set<String>> computeModuleCharsetMap() {
    final Map<JpsModule, Set<String>> map = new HashMap<>();
    final Iterable<JavaBuilderExtension> builderExtensions = JpsServiceManager.getInstance().getExtensions(JavaBuilderExtension.class);
    for (Map.Entry<String, String> entry : myUrlToCharset.entrySet()) {
      final String fileUrl = entry.getKey();
      final String charset = entry.getValue();
      File file = JpsPathUtil.urlToFile(fileUrl);
      if (charset == null || (!file.isDirectory() && !shouldHonorEncodingForCompilation(builderExtensions, file))) {
        continue;
      }
      final JavaSourceRootDescriptor rootDescriptor = myRootsIndex.findJavaRootDescriptor(null, file);
      if (rootDescriptor == null) {
        continue;
      }
      final JpsModule module = rootDescriptor.target.getModule();
      Set<String> set = map.get(module);
      if (set == null) {
        set = new LinkedHashSet<>();
        map.put(module, set);
        // need to search parents only once because
        // file parent's charset, if explicitly defined, has higher priority than the charset assigned to individual files
        // we deliberately check parents until the source roots
        final File sourceRoot = rootDescriptor.root;
        File current = FileUtilRt.getParentFile(file);
        String parentCharset = null;
        while (current != null) {
          final String currentCharset = lookupCharsetMap(current);
          if (currentCharset != null) {
            parentCharset = currentCharset;
          }
          if (FileUtil.filesEqual(current, sourceRoot)) {
            break;
          }
          current = FileUtilRt.getParentFile(current);
        }
        if (parentCharset != null) {
          set.add(parentCharset);
        }
      }
      set.add(charset);
    }

    // now check source root charsets and up and augment the map so that every module has its preferred charset assigned
    // here apply usual rules from encoding configuration: sub-paths inherit charset assigned to parents unless explicitly overridden.

    for (JpsModule module : myJpsModel.getProject().getModules()) {
      for (JpsModuleSourceRoot srcRoot : module.getSourceRoots()) {
        final String encoding = getEncoding(srcRoot.getFile());
        if (encoding != null) {
          Set<String> charsets = map.get(module);
          if (charsets == null) {
            charsets = new LinkedHashSet<>();
            map.put(module, charsets);
          }
          charsets.add(encoding);
        }
      }
    }

    return map;
  }

  public @Nullable String getEncoding(@Nullable File file) {
    while (file != null) {
      final String charset = lookupCharsetMap(file);
      if (charset != null) {
        return charset;
      }
      file = FileUtilRt.getParentFile(file);
    }
    return myProjectCharset;
  }

  private @Nullable String lookupCharsetMap(File file) {
    return myUrlToCharset.get(JpsPathUtil.pathToUrl(FileUtilRt.toSystemIndependentName(file.getAbsolutePath())));
  }

  private static boolean shouldHonorEncodingForCompilation(Iterable<? extends JavaBuilderExtension> builders, File file) {
    for (JavaBuilderExtension extension : builders) {
      if (extension.shouldHonorFileEncodingForCompilation(file)) {
        return true;
      }
    }
    return false;
  }

  public @Nullable String getPreferredModuleChunkEncoding(@NotNull ModuleChunk moduleChunk) {
    for (JpsModule module : moduleChunk.getModules()) {
      final String encoding = getPreferredModuleEncoding(module);
      if (encoding != null) {
        return encoding;
      }
    }
    return myProjectCharset;
  }

  public String getPreferredModuleEncoding(JpsModule module) {
    final Set<String> encodings = getModuleCharsetMap().get(module);
    return ContainerUtil.getFirstItem(encodings, null);
  }

  public @NotNull Set<String> getAllModuleChunkEncodings(@NotNull ModuleChunk moduleChunk) {
    final Map<JpsModule, Set<String>> map = getModuleCharsetMap();
    Set<String> encodings = new HashSet<>();
    for (JpsModule module : moduleChunk.getModules()) {
      final Set<String> moduleEncodings = map.get(module);
      if (moduleEncodings != null) {
        encodings.addAll(moduleEncodings);
      }
    }
    return encodings;
  }
}
