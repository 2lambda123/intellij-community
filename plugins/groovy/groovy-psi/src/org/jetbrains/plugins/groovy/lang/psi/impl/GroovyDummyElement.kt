// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.plugins.groovy.lang.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.ParsingDiagnostics
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.FileElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import org.jetbrains.plugins.groovy.GroovyLanguage
import org.jetbrains.plugins.groovy.lang.parser.GroovyParser

internal class GroovyDummyElement(private val childType: IElementType, text: CharSequence) : FileElement(GroovyDummyElementType, text) {

  private object GroovyDummyElementType : IFileElementType("GROOVY_DUMMY_ELEMENT", GroovyLanguage) {

    override fun doParseContents(chameleon: ASTNode, psi: PsiElement): ASTNode {
      val builder = PsiBuilderFactory.getInstance().createBuilder(psi.project, chameleon)
      val dummyElement = chameleon as GroovyDummyElement
      val startTime = System.nanoTime()
      val result = GroovyParser().parse(dummyElement.childType, builder)
      ParsingDiagnostics.registerParse(builder, language, System.nanoTime() - startTime)
      return result
    }
  }
}
