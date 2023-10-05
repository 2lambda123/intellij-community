// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ide.customize.transferSettings

enum class TransferableIdeId {
  DummyIde,
  VSCode,
  VisualStudio,
  VisualStudioForMac
}

enum class TransferableIdeVersionId {
  Unknown,
  V2012,
  V2015,
  V2013,
  V2017,
  V2019,
  V2022
}

enum class TransferableLafId {
  Light,
  Darcula,
  HighContrast
}

enum class TransferableKeymapId {
  Default,
  VsCode,
  VsCodeMac,
  VsForMac,
  VisualStudio2022
}

enum class TransferableIdeFeatureId {
  CSharp,
  ChineseLanguage,
  Dart,
  DatabaseSupport,
  Debugger,
  Docker,
  DotNetDecompiler,
  DummyBuiltInFeature,
  DummyPlugin,
  EditorConfig,
  Flutter,
  Git,
  Gradle,
  IdeaVim,
  Ideolog,
  Java,
  Kotlin,
  Kubernetes,
  LanguageSupport,
  LiveTemplates,
  Lombok,
  Maven,
  Monokai,
  NodeJsSupport,
  NuGet,
  Prettier,
  ReSharper,
  RunConfigurations,
  RustSupport,
  Scala,
  Solarized,
  SpellChecker,
  TeamCity,
  TestExplorer,
  TsLint,
  Unity,
  Vue,
  WebSupport,
  Wsl,
  XamlStyler
}
