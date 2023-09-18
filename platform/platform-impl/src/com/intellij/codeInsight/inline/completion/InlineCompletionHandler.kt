// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.codeInsight.inline.completion

import com.intellij.codeInsight.inline.completion.logs.InlineCompletionEventListener
import com.intellij.codeInsight.inline.completion.logs.InlineCompletionEventType
import com.intellij.codeInsight.inline.completion.logs.InlineCompletionUsageTracker
import com.intellij.codeInsight.lookup.LookupEvent
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.EDT
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiFile
import com.intellij.util.EventDispatcher
import com.intellij.util.application
import com.intellij.util.concurrency.annotations.RequiresBlockingContext
import com.intellij.util.concurrency.annotations.RequiresEdt
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.atomic.AtomicBoolean

@ApiStatus.Experimental
class InlineCompletionHandler(scope: CoroutineScope) {
  private val executor = SafeInlineCompletionExecutor(scope)
  private var lastInvocationTime = 0L
  private val eventListeners = EventDispatcher.create(InlineCompletionEventListener::class.java)

  init {
    addEventListener(InlineCompletionUsageTracker.Listener())
  }

  private fun getProvider(event: InlineCompletionEvent): InlineCompletionProvider? {
    if (application.isUnitTestMode && testProvider != null) {
      return testProvider
    }

    return InlineCompletionProvider.extensions().firstOrNull { it.isEnabled(event) }?.also {
      LOG.trace("Selected inline provider: $it")
    }
  }

  fun addEventListener(listener: InlineCompletionEventListener) {
    eventListeners.addListener(listener)
  }

  fun addEventListener(listener: InlineCompletionEventListener, parentDisposable: Disposable) {
    addEventListener(listener)
    Disposer.register(parentDisposable) { removeEventListener(listener) }
  }

  fun removeEventListener(listener: InlineCompletionEventListener) {
    eventListeners.removeListener(listener)
  }

  fun invoke(event: InlineCompletionEvent.DocumentChange) = invokeEvent(event)
  fun invoke(event: InlineCompletionEvent.CaretMove) = invokeEvent(event)
  fun invoke(event: InlineCompletionEvent.LookupChange) = invokeEvent(event)
  fun invoke(event: InlineCompletionEvent.DirectCall) = invokeEvent(event)

  private fun invokeEvent(event: InlineCompletionEvent) {
    if (!application.isDispatchThread) {
      LOG.error("Cannot run inline completion handler outside of EDT.")
      return
    }

    LOG.trace("Start processing inline event $event")
    if (isMuted.get()) {
      LOG.trace("Muted")
      return
    }

    val provider = getProvider(event)

    val request = event.toRequest() ?: return
    if (updateContextOrInvalidate(request, provider) || provider == null) {
      return
    }

    executor.switchJobSafely {
      invokeRequest(request, provider)
    }
  }

  private suspend fun invokeRequest(request: InlineCompletionRequest, provider: InlineCompletionProvider) {
    val editor = request.editor
    val offset = request.endOffset

    lastInvocationTime = System.currentTimeMillis()
    val modificationStamp = request.document.modificationStamp
    val resultFlow = request(provider, request) // .flowOn(Dispatchers.IO)

    val context = InlineCompletionSession.getOrInit(editor, provider).context

    // If you write a test and observe an infinite hang here, set [UsefulTestCase.runInDispatchThread] to false.
    withContext(Dispatchers.EDT) {
      resultFlow
        .onStart { isShowing.set(true) }
        .onEmpty {
          trace(InlineCompletionEventType.Empty)
          InlineCompletionSession.remove(editor)
        }
        .onCompletion {
          complete(currentCoroutineContext().isActive, editor, it, context)
        }
        .collectIndexed { index, it ->
          ensureActive()

          if (!isShowing.get() || modificationStamp != request.document.modificationStamp) {
            cancel()
            return@collectIndexed
          }

          showInlineElement(it, index, offset, context)
        }
    }
  }

  suspend fun request(provider: InlineCompletionProvider, request: InlineCompletionRequest): Flow<InlineCompletionElement> {
    trace(InlineCompletionEventType.Request(lastInvocationTime, request, provider::class.java))
    return provider.getProposals(request)
  }

  private fun showInlineElement(
    element: InlineCompletionElement,
    index: Int,
    offset: Int,
    context: InlineCompletionContext
  ) {
    trace(InlineCompletionEventType.Show(element, index))
    context.renderElement(element, offset)
  }

  private fun InlineCompletionContext.renderElement(element: InlineCompletionElement, startOffset: Int) {
    withSafeMute {
      element.render(editor, lastOffset ?: startOffset)
      state.addElement(element)
    }
  }

  fun insert(editor: Editor) {
    val context = InlineCompletionContext.getOrNull(editor) ?: return
    trace(InlineCompletionEventType.Insert)

    withSafeMute {
      val offset = context.lastOffset ?: return@withSafeMute
      val currentCompletion = context.lineToInsert

      editor.document.insertString(offset, currentCompletion)
      editor.caretModel.moveToOffset(offset + currentCompletion.length)
    }

    LookupManager.getActiveLookup(editor)?.hideLookup(false) //TODO: remove this
    hide(editor, false, context)
  }

  fun hide(editor: Editor, explicit: Boolean, context: InlineCompletionContext) {
    if (context.isCurrentlyDisplayingInlays) {
      trace(InlineCompletionEventType.Hide(explicit))
    }

    withSafeMute {
      isShowing.set(false)
      InlineCompletionSession.remove(editor)
    }
  }

  fun cancel(editor: Editor) {
    executor.cancel()
    application.invokeAndWait {
      InlineCompletionContext.getOrNull(editor)?.let { context ->
        hide(editor, false, context)
      }
    }
  }

  fun complete(isActive: Boolean, editor: Editor, cause: Throwable?, context: InlineCompletionContext) {
    trace(InlineCompletionEventType.Completion(cause, isActive))
    isShowing.set(false)
    if (cause != null) {
      hide(editor, false, context)
    }
  }

  private fun getContextUpdater(): DefinedInlineCompletionContextUpdater {
    return CompositeContextUpdater(AppendPrefixContextUpdater(), LookupChangeContextUpdater()).invalidateOnUndefined()
  }

  /**
   * @return `true` if update was successful. Otherwise, [hide] is invoked to invalidate the current context.
   */
  @RequiresBlockingContext
  private fun updateContextOrInvalidate(
    request: InlineCompletionRequest,
    provider: InlineCompletionProvider?
  ): Boolean {
    val session = InlineCompletionSession.getOrNull(request.editor) ?: return false
    if (provider != null && session.provider != provider || session.provider.requiresInvalidation(request.event)) {
      application.invokeAndWait { session.invalidate() }
      return false
    }

    val context = session.context
    val result = getContextUpdater().onEvent(context, request.event)
    application.invokeAndWait {
      when (result) {
        is InlineCompletionContextUpdater.Result.Updated.Changed -> {
          context.editor.inlayModel.execute(true) {
            context.clear()
            trace(InlineCompletionEventType.Change(result.truncateTyping))
            result.newElements.forEach { context.renderElement(it, request.endOffset) }
          }
        }
        is InlineCompletionContextUpdater.Result.Updated.Same -> Unit
        is InlineCompletionContextUpdater.Result.Invalidated -> session.invalidate()
      }
    }
    return result is InlineCompletionContextUpdater.Result.Updated
  }

  @RequiresEdt
  private fun InlineCompletionSession.invalidate() {
    if (context.isCurrentlyDisplayingInlays) {
      hide(context.editor, false, context)
    }
  }

  private fun trace(event: InlineCompletionEventType) {
    eventListeners.getMulticaster().on(event)
  }

  @Deprecated(
    "replaced with direct event call type",
    ReplaceWith("invoke(InlineCompletionEvent.DocumentChange(event, editor))"),
    DeprecationLevel.ERROR
  )
  fun invoke(event: DocumentEvent, editor: Editor) {
    return invoke(InlineCompletionEvent.DocumentChange(event, editor))
  }

  @Deprecated(
    "replaced with direct event call type",
    ReplaceWith("invoke(InlineCompletionEvent.CaretMove(event))"),
    DeprecationLevel.ERROR
  )
  fun invoke(event: EditorMouseEvent) {
    return invoke(InlineCompletionEvent.CaretMove(event))
  }

  @Deprecated(
    "replaced with direct event call type",
    ReplaceWith("invoke(InlineCompletionEvent.LookupChange(event))"),
    DeprecationLevel.ERROR
  )
  fun invoke(event: LookupEvent) {
    return invoke(InlineCompletionEvent.LookupChange(event))
  }

  @Deprecated(
    "replaced with direct event call type",
    ReplaceWith("invoke(InlineCompletionEvent.DirectCall(editor, file, caret, context))"),
    DeprecationLevel.ERROR
  )
  fun invoke(editor: Editor, file: PsiFile, caret: Caret, context: DataContext?) {
    return invoke(InlineCompletionEvent.DirectCall(editor, file, caret, context))
  }

  @TestOnly
  suspend fun awaitExecution() {
    executor.awaitAll()
  }

  companion object {
    private val LOG = thisLogger()
    val KEY = Key.create<InlineCompletionHandler>("inline.completion.handler")

    fun getOrNull(editor: Editor) = editor.getUserData(KEY)

    val isMuted: AtomicBoolean = AtomicBoolean(false)
    val isShowing: AtomicBoolean = AtomicBoolean(false)

    inline fun withSafeMute(block: () -> Unit) {
      mute()
      try {
        block()
      }
      finally {
        unmute()
      }
    }

    fun mute(): Unit = isMuted.set(true)
    fun unmute(): Unit = isMuted.set(false)

    private var testProvider: InlineCompletionProvider? = null

    @TestOnly
    fun registerTestHandler(provider: InlineCompletionProvider) {
      testProvider = provider
    }

    @TestOnly
    fun unRegisterTestHandler() {
      testProvider = null
    }
  }
}
