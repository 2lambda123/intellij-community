// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.navigationToolbar

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.KotlinIconProvider
import org.jetbrains.kotlin.idea.projectView.KtDeclarationTreeNode.Companion.tryGetRepresentableText
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile

class KotlinNavBarModelExtension : AbstractNavBarModelExtensionCompatBase() {
    override fun getPresentableText(item: Any?): String? =
        (item as? KtDeclaration)?.let { tryGetRepresentableText(it, renderArguments = false) }

    override fun adjustElementImpl(psiElement: PsiElement?): PsiElement? {
        if (psiElement is KtDeclaration) {
            return psiElement
        }

        val containingFile = psiElement?.containingFile as? KtFile ?: return psiElement
        return KotlinIconProvider.getSingleClass(containingFile) ?: psiElement
    }
}
