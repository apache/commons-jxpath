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

/**
 * Represents a namespace node.
 */
public class JDOMNamespacePointer extends NodePointer {
    private final String prefix;
    private String namespaceURI;

    private static final long serialVersionUID = 7935311686545862379L;

    /**
     * Create a new JDOMNamespacePointer.
     * @param parent parent pointer
     * @param prefix ns prefix
     */
    public JDOMNamespacePointer(final NodePointer parent, final String prefix) {
        super(parent);
        this.prefix = prefix;
    }

    /**
     * Create a new JDOMNamespacePointer.
     * @param parent parent pointer
     * @param prefix ns prefix
     * @param namespaceURI ns URI
     */
    public JDOMNamespacePointer(
            final NodePointer parent,
            final String prefix,
            final String namespaceURI) {
        super(parent);
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
    }

    @Override
    public QName getName() {
        return new QName(prefix);
    }

    @Override
    public Object getBaseValue() {
        return null;
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
        return getNamespaceURI();
    }

    @Override
    public String getNamespaceURI() {
        if (namespaceURI == null) {
            namespaceURI = parent.getNamespaceURI(prefix);
        }
        return namespaceURI;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    /**
     * Throws UnsupportedOperationException.
     * @param value Object value to set
     */
    @Override
    public void setValue(final Object value) {
        throw new UnsupportedOperationException("Cannot modify a namespace");
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
        buffer.append("namespace::");
        buffer.append(prefix);
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return prefix.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return object == this || object instanceof JDOMNamespacePointer && prefix.equals(((JDOMNamespacePointer) object).prefix);
    }

    @Override
    public int compareChildNodePointers(
        final NodePointer pointer1,
        final NodePointer pointer2) {
        // Won't happen - namespaces don't have children
        return 0;
    }
}
