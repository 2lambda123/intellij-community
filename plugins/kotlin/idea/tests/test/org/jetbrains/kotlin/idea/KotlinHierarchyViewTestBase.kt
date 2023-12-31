// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.kotlin.idea

import com.intellij.ide.hierarchy.HierarchyTreeStructure
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.codeInsight.hierarchy.HierarchyViewTestFixture
import org.jetbrains.kotlin.idea.test.KotlinLightCodeInsightFixtureTestCase
import org.junit.internal.runners.JUnit38ClassRunner
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(JUnit38ClassRunner::class)
abstract class KotlinHierarchyViewTestBase : KotlinLightCodeInsightFixtureTestCase() {
    private val hierarchyFixture = HierarchyViewTestFixture()

    @Throws(Exception::class)
    protected open fun doHierarchyTest(
        treeStructureComputable: Computable<out HierarchyTreeStructure>,
        vararg fileNames: String
    ) {
        myFixture.configureByFiles(*fileNames)
        val expectedStructure = loadExpectedStructure()
        HierarchyViewTestFixture.doHierarchyTest(treeStructureComputable.compute(), expectedStructure)
    }

    @Throws(IOException::class)
    private fun loadExpectedStructure(): String {
        val verificationFile = File(testDataDirectory, getTestName(false) + "_verification.xml")
        return FileUtil.loadFile(verificationFile)
    }
}