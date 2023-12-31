// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.java.editor;

import com.intellij.editor.AbstractBasicJavaHighlighterTest;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.JavaDocTokenType;
import com.intellij.psi.JavaTokenType;

public class JavaHighlighterTest extends AbstractBasicJavaHighlighterTest {

  public void testJavaDocCreation() {
    final String text1 = "{/*";
    String text = text1 + "\n";
    initDocument(text);

    WriteCommandAction.runWriteCommandAction(null, () -> myDocument.insertString(text1.length(), "*"));
  }

  public void testJavaDoc1() {
    final String text1 = "/**/";
    final String text = text1 + "public class B { }";
    initDocument(text);

    HighlighterIterator iterator = myHighlighter.createIterator(text1.length());
    assertEquals(JavaTokenType.PUBLIC_KEYWORD, iterator.getTokenType());
  }


  public void testJavaDocEditing1() {
    WriteCommandAction.runWriteCommandAction(null, () -> {
        final String text1 = "c/** * co *";
        String text = text1 + "class";
        initDocument(text);

        myDocument.insertString(text1.length(), "/");

        final HighlighterIterator iterator = myHighlighter.createIterator(text1.length() + 1);
        assertEquals(JavaTokenType.CLASS_KEYWORD, iterator.getTokenType());

        myDocument.deleteString(text1.length(), text1.length() + 1);
      });
  }

  public void testJavaDocEditing2() {
    WriteCommandAction.runWriteCommandAction(null, () -> {
        final String text1 = "/** ";
        final String text = text1 + "*/public class B { }";
        initDocument(text);

        HighlighterIterator iterator = myHighlighter.createIterator(text1.length() + 2);
        assertEquals(JavaTokenType.PUBLIC_KEYWORD, iterator.getTokenType());

        myDocument.deleteString(0, text1.length() + 1);

        iterator = myHighlighter.createIterator(1);
        assertEquals(JavaTokenType.PUBLIC_KEYWORD, iterator.getTokenType());
      });
  }

  public void testJavaDocHtmlEditing() {
    WriteCommandAction.runWriteCommandAction(null, () -> {
        final String text1 = "/**\n  * <co";
        final String text2 = text1 + " \n  * @param someParam\n  */";
        final String text3 = text2 + "class";
        initDocument(text3);

        HighlighterIterator iterator = myHighlighter.createIterator(text2.length());
        assertEquals(JavaTokenType.CLASS_KEYWORD, iterator.getTokenType());

        myDocument.insertString(text1.length(), "d");

        iterator = myHighlighter.createIterator(text2.length() + 1);
        assertEquals(JavaTokenType.CLASS_KEYWORD, iterator.getTokenType());

        myDocument.deleteString(text1.length(), text1.length() + 1);

        iterator = myHighlighter.createIterator(text2.length() + 1);
        assertEquals(JavaTokenType.CLASS_KEYWORD, iterator.getTokenType());
      });
  }

  public void testJavaDocTag1() {
    final String text1 = "/** ";
    String text = text1 + "@param */";
    initDocument(text);

    final HighlighterIterator iterator = myHighlighter.createIterator(text1.length());

    assertEquals(JavaDocTokenType.DOC_TAG_NAME, iterator.getTokenType());
  }

  public void testJavaDocTag3() {
    ApplicationManager.getApplication().runWriteAction(() -> {
      final String text1 = "/** @param <code";
      String text = text1 + " */";
      initDocument(text);

      myHighlighter.createIterator(text1.length());

      myDocument.insertString(text1.length(), ">");
    });

//    assertEquals(JavaHighlightingLexer.DOC_TOKEN_SHIFT + JavaDocTokenType.DOC_TAG_NAME, iterator.getTokenType());
  }

  public void testJavaDocTag2() {
    final String text1 = "package some; /**\n  *  ";
    String text = text1 + "@param */";
    initDocument(text);

    final HighlighterIterator iterator = myHighlighter.createIterator(text1.length());

    assertEquals(JavaDocTokenType.DOC_TAG_NAME, iterator.getTokenType());
  }
}
