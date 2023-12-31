// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.codeinsights.impl.base.testIntegration

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiPackage
import com.intellij.testIntegration.createTest.CreateTestDialog
import org.jetbrains.annotations.Nls

class KotlinCreateTestDialog(
    project: Project,
    @Nls title: String,
    targetClass: PsiClass?,
    targetPackage: PsiPackage?,
    targetModule: Module
) : CreateTestDialog(project, title, targetClass, targetPackage, targetModule) {
    var explicitClassName: String? = null

    override fun getClassName(): String = explicitClassName ?: super.getClassName()
}
