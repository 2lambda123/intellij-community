/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
package com.intellij.codeInsight.editorActions.smartEnter;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.impl.source.BasicJavaAstTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.text.CharArrayUtil;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.impl.source.BasicJavaElementType.BASIC_ENUM_CONSTANT;

public class EnumFieldFixer implements Fixer {
  @Override
  public void apply(Editor editor, AbstractBasicJavaSmartEnterProcessor processor, @NotNull ASTNode astNode) throws IncorrectOperationException {
    if (BasicJavaAstTreeUtil.is(astNode, BASIC_ENUM_CONSTANT)) {
      int insertionOffset = astNode.getTextRange().getEndOffset();
      Document doc = editor.getDocument();
      final CharSequence text = doc.getCharsSequence();
      final int probableCommaOffset = CharArrayUtil.shiftForward(text, insertionOffset, " \t");
      if (probableCommaOffset >= text.length() || text.charAt(probableCommaOffset) != ',') {
        doc.insertString(insertionOffset, ",");
      }
    }
  }
}
