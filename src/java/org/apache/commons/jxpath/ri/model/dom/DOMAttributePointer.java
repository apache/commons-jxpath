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
package org.apache.commons.jxpath.ri.model.dom;

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.TypeUtils;
import org.w3c.dom.Attr;

/**
 * A Pointer that points to a DOM node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class DOMAttributePointer extends NodePointer {
    private Attr attr;

    /**
     * Create a new DOMAttributePointer.
     * @param parent pointer
     * @param attr pointed
     */
    public DOMAttributePointer(NodePointer parent, Attr attr) {
        super(parent);
        this.attr = attr;
    }

    /**
     * {@inheritDoc}
     */
    public QName getName() {
        return new QName(
            DOMNodePointer.getPrefix(attr),
            DOMNodePointer.getLocalName(attr));
    }

    /**
     * {@inheritDoc}
     */
    public String getNamespaceURI() {
        String prefix = DOMNodePointer.getPrefix(attr);
        return prefix == null ? null : parent.getNamespaceURI(prefix);
    }

    /**
     * {@inheritDoc}
     */
    public Object getValue() {
        String value = attr.getValue();
        if (value == null || (value.equals("") && !attr.getSpecified())) {
            return null;
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public Object getBaseValue() {
        return attr;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCollection() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int getLength() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    public Object getImmediateNode() {
        return attr;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActual() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeaf() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean testNode(NodeTest nodeTest) {
        return nodeTest == null
            || ((nodeTest instanceof NodeTypeTest)
                && ((NodeTypeTest) nodeTest).getNodeType() == Compiler.NODE_TYPE_NODE);
    }

    /**
     * Sets the value of this attribute.
     * @param value to set
     */
    public void setValue(Object value) {
        attr.setValue((String) TypeUtils.convert(value, String.class));
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        attr.getOwnerElement().removeAttributeNode(attr);
    }

    /**
     * {@inheritDoc}
     */
    public String asPath() {
        StringBuffer buffer = new StringBuffer();
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

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return System.identityHashCode(attr);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object object) {
        return object == this || object instanceof DOMAttributePointer
                && attr == ((DOMAttributePointer) object).attr;
    }

    /**
     * {@inheritDoc}
     */
    public int compareChildNodePointers(NodePointer pointer1,
            NodePointer pointer2) {
        // Won't happen - attributes don't have children
        return 0;
    }
}