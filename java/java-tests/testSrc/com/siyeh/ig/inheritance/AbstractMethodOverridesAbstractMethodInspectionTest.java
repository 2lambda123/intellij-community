/*
 * Copyright 2000-2017 JetBrains s.r.o.
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
package com.siyeh.ig.inheritance;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.siyeh.ig.LightJavaInspectionTestCase;
import org.jetbrains.annotations.Nullable;

/**
 * @author Bas Leijdekkers
 */
public class AbstractMethodOverridesAbstractMethodInspectionTest extends LightJavaInspectionTestCase {

  public void testAbstractMethodOverridesAbstractMethod() {
    doTest();
  }
  
  public void testFix() {
    myFixture.addClass("""
      /**
       * @see Sub#run
       */
      abstract class Super {
        abstract void run();
      }
      """);
    myFixture.configureByText("Sub.java", """
      /**
       * @see Sub#run
       */
      abstract class Sub extends Super {
        @Override abstract void run<caret>();
      }
      """);
    IntentionAction intention = myFixture.findSingleIntention("Remove redundant abstract method declaration");
    assertEquals(
      """
        /**
         * @see Super#run
         */
        abstract class Sub extends Super {
        }

        ----------
        /**
         * @see Super#run
         */
        abstract class Super {
          abstract void run();
        }
        """, myFixture.getIntentionPreviewText(intention));
    myFixture.launchAction(intention);
    myFixture.checkResult("""
        /**
         * @see Super#run
         */
        abstract class Sub extends Super {
        }
        """);
  }

  @Nullable
  @Override
  protected InspectionProfileEntry getInspection() {
    final AbstractMethodOverridesAbstractMethodInspection inspection = new AbstractMethodOverridesAbstractMethodInspection();
    inspection.ignoreJavaDoc = true;
    return inspection;
  }
}
