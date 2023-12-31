// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.kotlin.idea.codeInsight.inspections.shared

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.base.resources.KotlinBundle
import org.jetbrains.kotlin.idea.codeinsight.api.classic.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.idea.codeinsight.utils.isSetterParameter
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

internal class RemoveSetterParameterTypeInspection: AbstractApplicabilityBasedInspection<KtParameter>(KtParameter::class.java) {
    override fun inspectionText(element: KtParameter): String = KotlinBundle.message("redundant.setter.parameter.type")

    override val defaultFixText: String
        get() = KotlinBundle.message("remove.explicit.type.specification")

    override fun applyTo(element: KtParameter, project: Project, editor: Editor?) {
        element.typeReference = null
    }

    override fun isApplicable(element: KtParameter): Boolean {
        if (!element.isSetterParameter) return false
        val typeReference = element.typeReference ?: return false
        return typeReference.endOffset > typeReference.startOffset
    }
}