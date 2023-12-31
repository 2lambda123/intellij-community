// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.search.declarationsSearch

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.SearchScope
import com.intellij.util.*
import org.jetbrains.kotlin.psi.psiUtil.contains
import java.util.*

interface DeclarationSearchRequest<in T> {
    val project: Project
    val searchScope: SearchScope
}

interface SearchRequestWithElement<T : PsiElement> : DeclarationSearchRequest<T> {
    val originalElement: T
    override val project: Project get() = originalElement.project
}

abstract class DeclarationsSearch<T : PsiElement, R : DeclarationSearchRequest<T>> : QueryFactory<T, R>() {
    init {
        registerExecutor(
            object : QueryExecutorBase<T, R>(true) {
                override fun processQuery(queryParameters: R, consumer: Processor<in T>) {
                    doSearch(queryParameters, consumer)
                }
            }
        )
    }

    final override fun registerExecutor(executor: QueryExecutor<T, R>) {
        super.registerExecutor(executor)
    }

    protected abstract fun doSearch(request: R, consumer: Processor<in T>)
    protected open fun isApplicable(request: R): Boolean = true

    fun search(request: R): Query<T> = if (isApplicable(request)) createUniqueResultsQuery(request) else EmptyQuery.getEmptyQuery()
}

class HierarchySearchRequest<T : PsiElement>(
    override val originalElement: T,
    override val searchScope: SearchScope,
    val searchDeeply: Boolean = true
) : SearchRequestWithElement<T> {
    fun <U : PsiElement> copy(newOriginalElement: U): HierarchySearchRequest<U> =
        HierarchySearchRequest(newOriginalElement, searchScope, searchDeeply)
}

interface HierarchyTraverser<T> {
    fun nextElements(current: T): Iterable<T>
    fun shouldDescend(element: T): Boolean

    fun forEach(initialElement: T, body: (T) -> Unit) {
        val stack = Stack<T>()
        val processed = HashSet<T>()

        stack.push(initialElement)
        while (!stack.isEmpty()) {
            ProgressIndicatorProvider.checkCanceled()

            val current = stack.pop()!!
            if (!processed.add(current)) continue

            for (next in nextElements(current)) {
                ProgressIndicatorProvider.checkCanceled()

                body(next)

                if (shouldDescend(next)) {
                    stack.push(next)
                }
            }
        }
    }
}

fun <T : PsiElement> Processor<in T>.consumeHierarchy(request: SearchRequestWithElement<T>, traverser: HierarchyTraverser<T>) {
    traverser.forEach(request.originalElement) { element ->
        if (element in request.searchScope) {
            process(element)
        }
    }
}

abstract class HierarchySearch<T : PsiElement>(
    private val traverser: HierarchyTraverser<T>
) : DeclarationsSearch<T, HierarchySearchRequest<T>>() {
    protected open fun doSearchAll(request: HierarchySearchRequest<T>, consumer: Processor<in T>) {
        consumer.consumeHierarchy(request, traverser)
    }

    protected abstract fun doSearchDirect(request: HierarchySearchRequest<T>, consumer: Processor<in T>)

    override fun doSearch(request: HierarchySearchRequest<T>, consumer: Processor<in T>) {
        if (request.searchDeeply) {
            doSearchAll(request, consumer)
        } else {
            doSearchDirect(request, consumer)
        }
    }
}
