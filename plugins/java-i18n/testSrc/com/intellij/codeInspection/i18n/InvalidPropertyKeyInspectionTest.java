/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.codeInspection.i18n;

import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.testFramework.JavaInspectionTestCase;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;

public class InvalidPropertyKeyInspectionTest extends JavaInspectionTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    ModuleRootModificationUtil.updateModel(getModule(), DefaultLightProjectDescriptor::addJetBrainsAnnotations);
  }

  private void doTest() {
    LocalInspectionToolWrapper tool = new LocalInspectionToolWrapper(new InvalidPropertyKeyInspection());
    doTest("invalidPropertyKey/" + getTestName(true), tool);
  }

  public void testSimple() {
    doTest();
  }

  public void testImplicit() {
    doTest();
  }

  public void testImplicit2() {
    doTest();
  }

  public void testImplicit3() {
    doTest();
  }

  @Override
  protected String getTestDataPath() {
    return PluginPathManager.getPluginHomePath("java-i18n") + "/testData/inspections";
  }
}
