// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.jetbrains.python.testing.pyTestFixtures

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.jetbrains.python.inspections.PyInspectionExtension
import com.jetbrains.python.psi.PyElement
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.PyNamedParameter
import com.jetbrains.python.psi.types.TypeEvalContext

/**
 * Fixture-based parameters should be skipped by inspection.
 * Fixture-based unresolved reference should be ignored by inspection.
 * @see PyTestUnpassedFixtureInspection
 */
class PyTestFixtureInspectionExtension : PyInspectionExtension() {
  override fun ignoreUnused(local: PsiElement, evalContext: TypeEvalContext) =
    local is PyNamedParameter && local.isFixture(evalContext)

  override fun ignoreShadowed(element: PsiElement) = element is PyFunction && element.isFixture()

  override fun ignoreUnresolvedReference(node: PyElement, reference: PsiReference, context: TypeEvalContext) =
    getFixtureLink(node, context) != null
}