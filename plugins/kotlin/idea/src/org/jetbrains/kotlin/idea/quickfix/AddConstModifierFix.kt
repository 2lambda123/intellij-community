// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.idea.codeinsight.api.classic.quickfixes.CleanupFix
import org.jetbrains.kotlin.idea.codeinsight.utils.replaceReferencesToGetterByReferenceToField
import org.jetbrains.kotlin.idea.util.application.runWriteActionIfPhysical
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.checkers.ConstModifierChecker
import org.jetbrains.kotlin.resolve.source.PsiSourceElement

class AddConstModifierFix(property: KtProperty) : AddModifierFixFE10(property, KtTokens.CONST_KEYWORD), CleanupFix {
    override fun startInWriteAction(): Boolean = false

    override fun invokeImpl(project: Project, editor: Editor?, file: PsiFile) {
        val property = element as? KtProperty ?: return
        addConstModifier(property)
    }

    companion object {
        private val removeAnnotations = listOf(FqName("kotlin.jvm.JvmStatic"), FqName("kotlin.jvm.JvmField"))

        fun addConstModifier(property: KtProperty) {
            val annotationsToRemove = removeAnnotations.mapNotNull { property.findAnnotation(it) }
            replaceReferencesToGetterByReferenceToField(property, JavaFileType.INSTANCE)
            runWriteActionIfPhysical(property) {
                property.addModifier(KtTokens.CONST_KEYWORD)
                annotationsToRemove.forEach(KtAnnotationEntry::delete)
            }
        }
    }
}

object ConstFixFactory : KotlinSingleIntentionActionFactory() {
    override fun createAction(diagnostic: Diagnostic): IntentionAction? {
        val expr = diagnostic.psiElement as? KtReferenceExpression ?: return null
        val targetDescriptor = expr.resolveToCall()?.resultingDescriptor as? VariableDescriptor ?: return null
        val declaration = (targetDescriptor.source as? PsiSourceElement)?.psi as? KtProperty ?: return null
        if (ConstModifierChecker.canBeConst(declaration, declaration, targetDescriptor)) {
            return AddConstModifierFix(declaration)
        }
        return null
    }
}