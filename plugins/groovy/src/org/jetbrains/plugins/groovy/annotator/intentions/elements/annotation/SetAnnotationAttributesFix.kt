// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.groovy.annotator.intentions.elements.annotation

import com.intellij.modcommand.ModPsiUpdater
import com.intellij.modcommand.PsiUpdateModCommandQuickFix
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.groovy.GroovyBundle
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.auxiliary.modifiers.annotation.GrAnnotation

abstract class SetAnnotationAttributesFix : PsiUpdateModCommandQuickFix() {

  abstract fun getNecessaryAttributes(place: PsiElement): Pair<GrAnnotation, Map<String, Any?>>?

  final override fun applyFix(project: Project, element: PsiElement, updater: ModPsiUpdater) {
    val (annotation: GrAnnotation, attributes: Map<String, Any?>) = getNecessaryAttributes(element)?.takeIf { it.second.isNotEmpty() } ?: return
    val factory = GroovyPsiElementFactory.getInstance(project)
    for ((attrName: String, attrValue: Any?) in attributes) {
      val psiAttributeValue = when (attrValue) {
        is Boolean, is String, is Nothing? -> factory.createLiteralFromValue(attrValue)
        else -> null
      } ?: continue
      annotation.setDeclaredAttributeValue(attrName, psiAttributeValue)
    }
  }

  final override fun getFamilyName(): String {
    return GroovyBundle.message("intention.family.name.add.attributes.to.annotation")
  }
}