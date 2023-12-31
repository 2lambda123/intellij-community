// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.psi.impl.source.xml;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.meta.PsiMetaOwner;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlExtension;
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagNameReference implements PsiReference {
  private static final Logger LOG = Logger.getInstance(TagNameReference.class);

  protected final boolean myStartTagFlag;
  private final ASTNode myNameElement;

  public TagNameReference(ASTNode nameElement, boolean startTagFlag) {
    myStartTagFlag = startTagFlag;
    myNameElement = nameElement;
  }

  @Override
  public @NotNull PsiElement getElement() {
    PsiElement element = myNameElement.getPsi();
    final PsiElement parent = element.getParent();
    return parent instanceof XmlTag ? parent : element;
  }

  protected @Nullable XmlTag getTagElement() {
    final PsiElement element = getElement();
    if(element == myNameElement.getPsi()) return null;
    return (XmlTag)element;
  }

  @Override
  public @NotNull TextRange getRangeInElement() {
    final ASTNode nameElement = getNameElement();
    if (nameElement == null){
      return TextRange.EMPTY_RANGE;
    }

    int colon = getPrefixIndex(nameElement.getText()) + 1;
    if (myStartTagFlag) {
      final int parentOffset = nameElement.getStartOffsetInParent();
      return new TextRange(parentOffset + colon, parentOffset + nameElement.getTextLength());
    }
    else {
      final PsiElement element = getElement();
      if (element == myNameElement) return new TextRange(colon, myNameElement.getTextLength());

      final int elementLength = element.getTextLength();
      int diffFromEnd = 0;

      for(ASTNode node = element.getNode().getLastChildNode(); node != nameElement && node != null; node = node.getTreePrev()) {
        diffFromEnd += node.getTextLength();
      }

      final int nameEnd = elementLength - diffFromEnd;
      return new TextRange(nameEnd - nameElement.getTextLength() + colon, nameEnd);
    }
  }

  protected int getPrefixIndex(@NotNull String name) {
    return name.indexOf(":");
  }
  
  public ASTNode getNameElement() {
    return myNameElement;
  }

  @Override
  public PsiElement resolve() {
    final XmlTag tag = getTagElement();
    final XmlElementDescriptor descriptor = tag != null ? tag.getDescriptor():null;

    if (LOG.isDebugEnabled()) {
      LOG.debug("Descriptor for tag " +
                (tag != null ? tag.getName() : "NULL") +
                " is " +
                (descriptor != null ? (descriptor.toString() + ": " + descriptor.getClass().getCanonicalName()) : "NULL"));
    }

    if (descriptor != null){
      return descriptor instanceof AnyXmlElementDescriptor ? tag : descriptor.getDeclaration();
    }
    return null;
  }

  @Override
  public @NotNull String getCanonicalText() {
    return getNameElement().getText();
  }

  @Override
  public @Nullable PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
    final XmlTag element = getTagElement();
    if (element == null || !myStartTagFlag) return element;

    if (getPrefixIndex(newElementName) == -1) {
      final String namespacePrefix = element.getNamespacePrefix();
      final int index = newElementName.lastIndexOf('.');

      if (index != -1) {
        final PsiElement psiElement = resolve();
        
        if (psiElement instanceof PsiFile || (psiElement != null && psiElement.isEquivalentTo(psiElement.getContainingFile()))) {
          newElementName = newElementName.substring(0, index);
        }
      }
      newElementName = prependNamespacePrefix(newElementName, namespacePrefix);
    }
    element.setName(newElementName);
    return element;
  }

  protected String prependNamespacePrefix(String newElementName, String namespacePrefix) {
    newElementName = (!namespacePrefix.isEmpty() ? namespacePrefix + ":":namespacePrefix) + newElementName;
    return newElementName;
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    PsiMetaData metaData = null;

    if (element instanceof PsiMetaOwner owner){
      metaData = owner.getMetaData();

      if (metaData instanceof XmlElementDescriptor){
        return getTagElement().setName(metaData.getName(getElement())); // TODO: need to evaluate new ns prefix
      }
    } else if (element instanceof PsiFile) {
      final XmlTag tagElement = getTagElement();
      if (tagElement == null || !myStartTagFlag) return tagElement;
      String newElementName = ((PsiFile)element).getName();
      final int index = newElementName.lastIndexOf('.');

      // TODO: need to evaluate new ns prefix
      newElementName = prependNamespacePrefix(newElementName.substring(0, index), tagElement.getNamespacePrefix());

      return getTagElement().setName(newElementName);
    }

    final XmlTag tag = getTagElement();
    throw new IncorrectOperationException("Cant bind to not a xml element definition!"+element+","+metaData + "," + tag + "," + (tag != null ? tag.getDescriptor() : "unknown descriptor"));
  }

  @Override
  public boolean isReferenceTo(@NotNull PsiElement element) {
    return getElement().getManager().areElementsEquivalent(element, resolve());
  }

  @Override
  public boolean isSoft() {
    return false;
  }

  static @Nullable TagNameReference createTagNameReference(XmlElement element, @NotNull ASTNode nameElement, boolean startTagFlag) {
    final XmlExtension extension = XmlExtension.getExtensionByElement(element);
    return extension == null ? null : extension.createTagNameReference(nameElement, startTagFlag);
  }

  public boolean isStartTagFlag() {
    return myStartTagFlag;
  }
}
