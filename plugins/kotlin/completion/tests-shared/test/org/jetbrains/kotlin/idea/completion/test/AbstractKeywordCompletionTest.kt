// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.completion.test

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import org.jetbrains.kotlin.idea.completion.KeywordLookupObject
import org.jetbrains.kotlin.idea.test.KotlinLightProjectDescriptor
import org.jetbrains.kotlin.idea.test.KotlinProjectDescriptorWithFacet
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms

abstract class AbstractKeywordCompletionTest : KotlinFixtureCompletionBaseTestCase() {
    override fun getPlatform() = JvmPlatforms.unspecifiedJvmPlatform

    override fun defaultCompletionType() = CompletionType.BASIC

    override fun complete(completionType: CompletionType, invocationCount: Int): Array<LookupElement>? {
        val items = myFixture.complete(completionType) ?: return null
        return items.filter { it.`object` is KeywordLookupObject }.toTypedArray()
    }

    override fun getProjectDescriptor(): KotlinLightProjectDescriptor = when {
        "LangLevel10" in fileName() -> KotlinProjectDescriptorWithFacet.KOTLIN_10.apply { replicateToFacetSettings() }
        "LangLevel11" in fileName() -> KotlinProjectDescriptorWithFacet.KOTLIN_11.apply { replicateToFacetSettings() }
        else -> KotlinProjectDescriptorWithFacet.createKotlinWithMultiplatformAndStdlib()
    }

    override fun defaultInvocationCount() = 1
}