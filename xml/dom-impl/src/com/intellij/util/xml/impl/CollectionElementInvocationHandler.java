// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.util.xml.impl;

import com.intellij.openapi.util.Factory;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.EvaluatedXmlName;
import com.intellij.util.xml.events.DomEvent;
import com.intellij.util.xml.stubs.ElementStub;
import com.intellij.util.xml.stubs.StubParentStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

public class CollectionElementInvocationHandler extends DomInvocationHandler {

  public CollectionElementInvocationHandler(final Type type, final @NotNull XmlTag tag,
                                            final AbstractCollectionChildDescription description,
                                            final DomInvocationHandler parent,
                                            @Nullable ElementStub stub) {
    super(type, new PhysicalDomParentStrategy(tag, parent.getManager()), description.createEvaluatedXmlName(parent, tag),
          (AbstractDomChildDescriptionImpl)description, parent.getManager(), true, stub);
  }

  public CollectionElementInvocationHandler(@NotNull EvaluatedXmlName tagName,
                                            AbstractDomChildDescriptionImpl childDescription,
                                            DomManagerImpl manager,
                                            ElementStub stub) {
    super(childDescription.getType(), new StubParentStrategy(stub), tagName, childDescription, manager, true, stub);
  }

  @Override
  protected @Nullable String getValue() {
    return myStub == null ? super.getValue() : ((ElementStub)myStub).getValue();
  }

  @Override
  protected Type narrowType(final @NotNull Type nominalType) {
    return getStub() == null ? getManager().getTypeChooserManager().getTypeChooser(nominalType).chooseType(getXmlTag()) : nominalType;
  }

  @Override
  protected final XmlTag setEmptyXmlTag() {
    throw new UnsupportedOperationException("CollectionElementInvocationHandler.setXmlTag() shouldn't be called;" +
                                            "\nparent=" + getParent() + ";\n" +
                                            "xmlElementName=" + getXmlElementName());
  }

  @Override
  protected String checkValidity() {
    final String s = super.checkValidity();
    if (s != null) {
      return s;
    }

    if (getXmlTag() == null) {
      return "no XmlTag for collection element: " + getDomElementType();
    }

    return null;
  }

  @Override
  public final void undefineInternal() {
    final DomElement parent = getParent();
    final XmlTag tag = getXmlTag();
    if (tag == null) return;

    setXmlElement(null);
    deleteTag(tag);
    getManager().fireEvent(new DomEvent(parent, false));
  }

  @Override
  public DomElement createPathStableCopy() {
    AbstractDomChildDescriptionImpl description = getChildDescription();
    final DomElement parent = getParent();
    assert parent != null;
    final DomElement parentCopy = parent.createStableCopy();
    final int index = description.getValues(parent).indexOf(getProxy());
    return getManager().createStableValue((Factory<DomElement>)() -> {
      if (parentCopy.isValid()) {
        final List<? extends DomElement> list = description.getValues(parentCopy);
        if (list.size() > index) {
          return list.get(index);
        }
      }
      return null;
    });
  }

  @Override
  public int hashCode() {
    ElementStub stub = (ElementStub)getStub();
    if (stub != null) {
      return stub.getName().hashCode() + stub.getStubId();
    }
    final XmlElement element = getXmlElement();
    return element == null ? super.hashCode() : element.hashCode();
  }
}
