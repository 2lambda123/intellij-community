// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.spellchecker.inspection;

import com.intellij.openapi.application.PluginPathManager;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

public class JavaSpellcheckerInspectionTest extends LightJavaCodeInsightFixtureTestCase {
  @Override
  protected String getBasePath() {
    return PluginPathManager.getPluginHomePathRelative("java-i18n") + "/testData/inspections/spellchecker";
  }

  @Override
  protected @NotNull LightProjectDescriptor getProjectDescriptor() {
    return JAVA_21;
  }

  public void testCorrectJava() { doTest(); }
  public void testTypoInJava() { doTest(); }
  public void testVarArg() { doTest(); }
  public void testJapanese() { doTest(); }

  public void testClassName() { doTest(); }
  public void testFieldName() { doTest(); }
  public void testMethodName() { doTest(); }
  public void testConstructorIgnored() { doTest();}
  public void testLocalVariableName() { doTest(); }
  public void testDocComment() { doTest(); }
  public void testStringLiteral() { doTest(); }
  public void testStringLiteralEscaping() { doTest(); }
  public void testSuppressions() { doTest(); }

  // suppression by @NonNls
  public void testMethodReturnTypeWithNonNls() { doTest(); }
  public void testMethodReturnTypeWithNonNlsReturnsLiteral() { doTest(); }
  public void testNonNlsField() { doTest(); }
  public void testNonNlsField2() { doTest(); }
  public void testNonNlsLocalVariable() { doTest(); }
  public void testNonNlsLocalVariableAndComment() { doTest(); }
  public void testFieldComment() { doTest(); }
  public void testDoNotCheckDerivedNames() { doTest(); }
  public void testSkipDateTime() { doTest(); }

  private void doTest() {
    myFixture.enableInspections(SpellcheckerInspectionTestCase.getInspectionTools());
    myFixture.testHighlighting(false, false, true, getTestName(false) + ".java");
  }
}
