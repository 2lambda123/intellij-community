// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.psi.impl.source.html;

import com.intellij.lang.ASTNode;
import com.intellij.lang.html.HtmlCompatibleFile;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.ex.http.HttpFileSystem;
import com.intellij.openapi.vfs.impl.http.HttpVirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceResolver;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author maxim
 */
public class HtmlFileImpl extends XmlFileImpl implements FileReferenceResolver, HtmlCompatibleFile {
  public HtmlFileImpl(FileViewProvider provider) {
    this(provider, XmlElementType.HTML_FILE);
  }

  public HtmlFileImpl(FileViewProvider provider, IFileElementType type) {
    super(provider, type);
  }

  @Override
  public String toString() {

    return "HtmlFile:" + getName();
  }

  @Override
  public XmlDocument getDocument() {
    CompositeElement treeElement = calcTreeElement();

    ASTNode node = treeElement.findChildByType(XmlElementType.HTML_DOCUMENT);
    return node != null ? (XmlDocument)node.getPsi() : null;
  }

  @Override
  public @Nullable PsiFileSystemItem resolveFileReference(@NotNull FileReference reference, @NotNull String name) {
    VirtualFile file = getVirtualFile();
    if (!(file instanceof HttpVirtualFile)) {
      return null;
    }

    VirtualFile parent = file;
    if (!parent.isDirectory()) {
      parent = parent.getParent();
      if (parent == null) {
        parent = file;
      }
    }

    VirtualFile childFile = parent.findChild(name);
    HttpFileSystem fileSystem = (HttpFileSystem)parent.getFileSystem();
    if (childFile == null) {
      childFile = fileSystem.createChild(parent, name, !reference.isLast());
    }
    if (childFile.isDirectory()) {
      // pre create children
      VirtualFile childParent = childFile;
      FileReference[] references = reference.getFileReferenceSet().getAllReferences();
      for (int i = reference.getIndex() + 1, n = references.length; i < n; i++) {
        FileReference childReference = references[i];
        childParent = fileSystem.createChild(childParent, childReference.decode(childReference.getText()), i != (n - 1));
      }
      return getManager().findDirectory(childFile);
    }
    else {
      return getManager().findFile(childFile);
    }
  }

  @Override
  public Collection<Object> getVariants(@NotNull FileReference reference) {
    return Collections.emptyList();
  }
}
