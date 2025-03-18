/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jxpath.ri.model.jdom;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.TypeUtils;
import org.jdom.Attribute;

/**
 * A Pointer that points to a DOM node.
 */
public class JDOMAttributePointer extends NodePointer {

    private static final long serialVersionUID = 8896050354479644028L;

    /** JDOM Attribute. */
    private final Attribute attribute;

    /**
     * Create a JDOMAttributePointer.
     *
     * @param parent NodePointer parent.
     * @param attribute   JDOM Attribute.
     */
    public JDOMAttributePointer(final NodePointer parent, final Attribute attribute) {
        super(parent);
        this.attribute = attribute;
    }

    @Override
    public String asPath() {
        final StringBuilder buffer = new StringBuilder();
        if (parent != null) {
            buffer.append(parent.asPath());
            if (buffer.length() == 0 || buffer.charAt(buffer.length() - 1) != '/') {
                buffer.append('/');
            }
        }
        buffer.append('@');
        buffer.append(getName());
        return buffer.toString();
    }

    @Override
    public int compareChildNodePointers(final NodePointer pointer1, final NodePointer pointer2) {
        // Won't happen - attributes don't have children
        return 0;
    }

    @Override
    public boolean equals(final Object object) {
        return object == this || object instanceof JDOMAttributePointer && ((JDOMAttributePointer) object).attribute == attribute;
    }

    @Override
    public Object getBaseValue() {
        return attribute;
    }

    @Override
    public Object getImmediateNode() {
        return attribute;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public QName getName() {
        return new QName(JDOMNodePointer.getPrefix(attribute), JDOMNodePointer.getLocalName(attribute));
    }

    @Override
    public String getNamespaceURI() {
        String uri = attribute.getNamespaceURI();
        if (uri != null && uri.isEmpty()) {
            uri = null;
        }
        return uri;
    }

    @Override
    public Object getValue() {
        return attribute.getValue();
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(attribute);
    }

    @Override
    public boolean isActual() {
        return true;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public void remove() {
        attribute.getParent().removeAttribute(attribute);
    }

    @Override
    public void setValue(final Object value) {
        attribute.setValue((String) TypeUtils.convert(value, String.class));
    }
}
