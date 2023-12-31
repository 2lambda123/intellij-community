/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.completion.ml.util

import com.intellij.codeInsight.completion.BaseCompletionLookupArranger
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementDecorator
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.template.impl.LiveTemplateLookupElement
import com.intellij.openapi.util.Key

private val CACHED_ITEM_ID: Key<String> = Key.create("CACHED_ITEM_ID")

fun LookupElement.idString(): String {
    var itemId = getUserData(CACHED_ITEM_ID)
    if (itemId == null) {
        itemId = LookupElementIdProvider.tryGetIdString(this) ?: run {
          val p = BaseCompletionLookupArranger.getDefaultPresentation(this) ?: LookupElementPresentation.renderElement(this)
          "${p.itemText} ${p.tailText} ${p.typeText}"
        }
        putUserData(CACHED_ITEM_ID, itemId)
    }
    return itemId
}

fun LookupElement.isLiveTemplate(): Boolean {
    if (this is LiveTemplateLookupElement) return true

    var item: LookupElement = this
    while (item is LookupElementDecorator<*>) {
      item = item.delegate
      if (item is LiveTemplateLookupElement) return true
    }

    return false
}