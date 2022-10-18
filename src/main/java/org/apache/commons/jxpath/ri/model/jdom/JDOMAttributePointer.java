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
    private final Attribute attr;

    private static final long serialVersionUID = 8896050354479644028L;

    /**
     * Create a JDOMAttributePointer.
     * @param parent NodePointer parent
     * @param attr JDOM Attribute
     */
    public JDOMAttributePointer(final NodePointer parent, final Attribute attr) {
        super(parent);
        this.attr = attr;
    }

    @Override
    public QName getName() {
        return new QName(
            JDOMNodePointer.getPrefix(attr),
            JDOMNodePointer.getLocalName(attr));
    }

    @Override
    public String getNamespaceURI() {
        String uri = attr.getNamespaceURI();
        if (uri != null && uri.equals("")) {
            uri = null;
        }
        return uri;
    }

    @Override
    public Object getValue() {
        return attr.getValue();
    }

    @Override
    public Object getBaseValue() {
        return attr;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public Object getImmediateNode() {
        return attr;
    }

    @Override
    public boolean isActual() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public void setValue(final Object value) {
        attr.setValue((String) TypeUtils.convert(value, String.class));
    }

    @Override
    public void remove() {
        attr.getParent().removeAttribute(attr);
    }

    @Override
    public String asPath() {
        final StringBuffer buffer = new StringBuffer();
        if (parent != null) {
            buffer.append(parent.asPath());
            if (buffer.length() == 0
                || buffer.charAt(buffer.length() - 1) != '/') {
                buffer.append('/');
            }
        }
        buffer.append('@');
        buffer.append(getName());
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(attr);
    }

    @Override
    public boolean equals(final Object object) {
        return object == this || object instanceof JDOMAttributePointer
                && ((JDOMAttributePointer) object).attr == attr;
    }

    @Override
    public int compareChildNodePointers(
            final NodePointer pointer1,
            final NodePointer pointer2) {
        // Won't happen - attributes don't have children
        return 0;
    }
}
