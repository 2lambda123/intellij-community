// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.refactoring.move

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.intellij.refactoring.move.MoveCallback
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedDeclaration

interface MoveKotlinDeclarationsHandlerActions {
    fun invokeMoveKotlinNestedClassesRefactoring(
        project: Project,
        elementsToMove: List<KtClassOrObject>,
        originalClass: KtClassOrObject,
        targetClass: KtClassOrObject,
        moveCallback: MoveCallback?
    )

    fun invokeMoveKotlinTopLevelDeclarationsRefactoring(
        project: Project,
        elementsToMove: Set<KtNamedDeclaration>,
        targetPackageName: String,
        targetDirectory: PsiDirectory?,
        targetFile: KtFile?,
        freezeTargets: Boolean,
        moveToPackage: Boolean,
        moveCallback: MoveCallback?
    )

    fun invokeKotlinSelectNestedClassChooser(nestedClass: KtClassOrObject, targetContainer: PsiElement?)

    fun invokeKotlinAwareMoveFilesOrDirectoriesRefactoring(
        project: Project,
        initialDirectory: PsiDirectory?,
        elements: List<PsiFileSystemItem>,
        moveCallback: MoveCallback?
    )

    fun showErrorHint(project: Project, editor: Editor?, message: String, title: String, helpId: String?)
}