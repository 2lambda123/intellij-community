// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.psi.impl.source.resolve.reference.impl.manipulators;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.XmlElementFactory;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.impl.CheckUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class XmlAttributeValueManipulator extends AbstractElementManipulator<XmlAttributeValue> {
  private static final Logger LOG = Logger.getInstance(XmlAttributeValueManipulator.class);

  @Override
  public XmlAttributeValue handleContentChange(@NotNull XmlAttributeValue element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
    CheckUtil.checkWritable(element);

    String text;
    final String oldText = element.getText();
    try {
      String textBeforeRange = oldText.substring(0, range.getStartOffset());
      String textAfterRange = oldText.substring(range.getEndOffset());
      newContent = oldText.startsWith("'") || oldText.endsWith("'") ?
                   newContent.replace("'", oldText.contains("&#39;") ? "&#39;" : "&apos;") :
                   newContent.replace("\"", oldText.contains("&#34;") ? "&#34;" : "&quot;");
      text = "<a value=" + textBeforeRange + newContent + textAfterRange;
    } catch(StringIndexOutOfBoundsException e) {
      LOG.error("Range: " + range + " in text: '" + oldText + "'", e);
      throw e;
    }
    final Project project = element.getProject();
    final XmlTag tag = element.getParent().getParent() instanceof HtmlTag ?
                       XmlElementFactory.getInstance(project).createHTMLTagFromText(text) :
                       XmlElementFactory.getInstance(project).createTagFromText(text);
    final XmlAttribute attribute = tag.getAttribute("value");
    assert attribute != null && attribute.getValueElement() != null;
    element.getNode().replaceAllChildrenToChildrenOf(attribute.getValueElement().getNode());
    return element;
  }

  @Override
  public @NotNull TextRange getRangeInElement(final @NotNull XmlAttributeValue xmlAttributeValue) {
    final PsiElement first = xmlAttributeValue.getFirstChild();
    if (first == null) {
      return TextRange.EMPTY_RANGE;
    }
    final ASTNode firstNode = first.getNode();
    assert firstNode != null;
    final PsiElement last = xmlAttributeValue.getLastChild();
    final ASTNode lastNode = last != null && last != first ? last.getNode() : null;

    final int textLength = xmlAttributeValue.getTextLength();
    final int start = firstNode.getElementType() == XmlTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER ? first.getTextLength() : 0;
    final int end = lastNode != null && lastNode.getElementType() == XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER ? last.getTextLength() : 0;
    return new TextRange(start, textLength - end);
  }
}
