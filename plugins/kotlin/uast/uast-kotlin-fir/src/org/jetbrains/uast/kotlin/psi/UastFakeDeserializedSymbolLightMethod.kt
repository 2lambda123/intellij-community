// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.uast.kotlin.psi

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClass
import com.intellij.psi.impl.light.LightModifierList
import com.intellij.psi.impl.light.LightParameterListBuilder
import org.jetbrains.kotlin.analysis.api.annotations.annotations
import org.jetbrains.kotlin.analysis.api.symbols.KtFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.pointers.KtSymbolPointer
import org.jetbrains.kotlin.analysis.api.types.KtTypeNullability
import org.jetbrains.kotlin.asJava.toLightAnnotation
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.utils.SmartList
import org.jetbrains.uast.UastLazyPart
import org.jetbrains.uast.getOrBuild
import org.jetbrains.uast.kotlin.internal.analyzeForUast

/**
 * A fake light method from binary, which is not materialized for some reason (e.g., `inline`)
 *
 * Due to its origin, BINARY, we don't have source PSI, but at least we have a pointer to
 * Analysis API symbol if it's resolved.
 */
internal class UastFakeDeserializedSymbolLightMethod(
    private val original: KtSymbolPointer<KtFunctionSymbol>,
    name: String,
    containingClass: PsiClass,
    private val context: KtElement,
) : UastFakeLightMethodBase(
    context.manager,
    context.language,
    name,
    LightParameterListBuilder(context.manager, context.language),
    LightModifierList(context.manager),
    containingClass
) {
    private val _isSuspend = UastLazyPart<Boolean>()

    override fun isSuspendFunction(): Boolean =
        _isSuspend.getOrBuild {
            analyzeForUast(context) {
                val functionSymbol = original.restoreSymbol() ?: return@analyzeForUast false
                functionSymbol.isSuspend
            }
        }

    private val _isUnit = UastLazyPart<Boolean>()

    override fun isUnitFunction(): Boolean =
        _isUnit.getOrBuild {
            analyzeForUast(context) {
                val functionSymbol = original.restoreSymbol() ?: return@analyzeForUast false
                functionSymbol.returnType.isUnit
            }
        }

    override fun computeNullability(): KtTypeNullability? {
        return analyzeForUast(context) {
            val functionSymbol = original.restoreSymbol() ?: return@analyzeForUast null
            functionSymbol.returnType.nullability
        }
    }

    override fun computeAnnotations(annotations: SmartList<PsiAnnotation>) {
        analyzeForUast(context) {
            val functionSymbol = original.restoreSymbol() ?: return
            functionSymbol.annotations.forEach { annoApp ->
                annoApp.psi?.toLightAnnotation()?.let { annotations.add(it) }
            }
        }
    }
}
