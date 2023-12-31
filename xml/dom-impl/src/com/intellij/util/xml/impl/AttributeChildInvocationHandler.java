// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.util.xml.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomElementVisitor;
import com.intellij.util.xml.EvaluatedXmlName;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.events.DomEvent;
import com.intellij.util.xml.stubs.AttributeStub;
import com.intellij.xml.util.XmlStringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttributeChildInvocationHandler extends DomInvocationHandler {
  private static final Logger LOG = Logger.getInstance(AttributeChildInvocationHandler.class);

  protected AttributeChildInvocationHandler(final EvaluatedXmlName attributeName,
                                            final AttributeChildDescriptionImpl description,
                                            final DomManagerImpl manager,
                                            final DomParentStrategy strategy,
                                            @Nullable AttributeStub stub) {
    super(description.getType(), strategy, attributeName, description, manager, false, stub);
  }

  @Override
  public void acceptChildren(DomElementVisitor visitor) {
  }

  @Override
  protected final XmlTag setEmptyXmlTag() {
    return ensureTagExists();
  }

  @Override
  protected boolean isAttribute() {
    return true;
  }

  @Override
  protected XmlElement recomputeXmlElement(final @NotNull DomInvocationHandler parent) {
    if (!parent.isValid()) return null;

    final XmlTag tag = parent.getXmlTag();
    if (tag == null) return null;

    return tag.getAttribute(getXmlElementName(), getXmlApiCompatibleNamespace(parent));
  }

  private @Nullable String getXmlApiCompatibleNamespace(DomInvocationHandler parent) {
    final XmlTag tag = parent.getXmlTag();
    if (tag == null) {
      return null;
    }

    String ns = getXmlName().getNamespace(tag, parent.getFile());
    // TODO: this seems ugly
    return tag.getNamespace().equals(ns) ? null : ns;
  }

  @Override
  public final XmlAttribute ensureXmlElementExists() {
    XmlAttribute attribute = (XmlAttribute)getXmlElement();
    if (attribute != null) return attribute;

    final DomManagerImpl manager = getManager();
    final boolean b = manager.setChanging(true);
    try {
      attribute = ensureTagExists().setAttribute(getXmlElementName(), getXmlApiCompatibleNamespace(getParentHandler()), "");
      setXmlElement(attribute);
      final DomElement element = getProxy();
      manager.fireEvent(new DomEvent(element, true));
      return attribute;
    }
    catch (IncorrectOperationException e) {
      LOG.error(e);
      return null;
    }
    finally {
      manager.setChanging(b);
    }
  }

  @Override
  public <T extends DomElement> T createStableCopy() {
    final DomElement parentCopy = getParent().createStableCopy();
    return getManager().createStableValue(() -> parentCopy.isValid() ? (T) getChildDescription().getValues(parentCopy).get(0) : null);
  }

  @Override
  public final void undefineInternal() {
    final XmlTag tag = getXmlTag();
    if (tag != null) {
      getManager().runChange(() -> {
        try {
          setXmlElement(null);
          tag.setAttribute(getXmlElementName(), getXmlApiCompatibleNamespace(getParentHandler()), null);
        }
        catch (IncorrectOperationException e) {
          LOG.error(e);
        }
      });
      fireUndefinedEvent();
    }
  }

  @Override
  public final @Nullable XmlTag getXmlTag() {
    final DomInvocationHandler handler = getParentHandler();
    return handler == null ? null : handler.getXmlTag();
  }

  @Override
  public final XmlTag ensureTagExists() {
    final DomInvocationHandler parent = getParentHandler();
    assert parent != null : "write operations should be performed on the DOM having a parent, your DOM may be not very fresh";
    return parent.ensureTagExists();
  }

  @Override
  protected @Nullable String getValue() {
    if (myStub != null) {
      return ((AttributeStub)myStub).getValue();
    }
    final XmlAttribute attribute = (XmlAttribute)getXmlElement();
    if (attribute != null) {
      final XmlAttributeValue value = attribute.getValueElement();
      if (value != null && value.getTextLength() >= 2) {
        return attribute.getDisplayValue();
      }
    }
    return null;
  }

  @Override
  protected void setValue(final @Nullable String value) {
    final XmlTag tag = ensureTagExists();
    final String attributeName = getXmlElementName();
    final String namespace = getXmlApiCompatibleNamespace(getParentHandler());
    String tagValue = tag.getAttributeValue(attributeName, namespace);
    final String oldValue = tagValue == null ? null : StringUtil.unescapeXmlEntities(tagValue);
    final String newValue = XmlStringUtil.escapeString(value);
    if (Comparing.equal(oldValue, newValue, true)) return;

    getManager().runChange(() -> {
      try {
        setXmlElement(tag.setAttribute(attributeName, namespace, newValue));
      }
      catch (IncorrectOperationException e) {
        LOG.error(e);
      }
    });
    final DomElement proxy = getProxy();
    getManager().fireEvent(oldValue != null ? new DomEvent(proxy, false) : new DomEvent(proxy, true));
  }

  @Override
  public void copyFrom(final DomElement other) {
    setValue(((GenericAttributeValue<?>) other).getStringValue());
  }

}
