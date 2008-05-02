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
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class JDOMAttributePointer extends NodePointer {
    private Attribute attr;

    private static final long serialVersionUID = 8896050354479644028L;

    /**
     * Create a JDOMAttributePointer.
     * @param parent NodePointer parent
     * @param attr JDOM Attribute
     */
    public JDOMAttributePointer(NodePointer parent, Attribute attr) {
        super(parent);
        this.attr = attr;
    }

    public QName getName() {
        return new QName(
            JDOMNodePointer.getPrefix(attr),
            JDOMNodePointer.getLocalName(attr));
    }

    public String getNamespaceURI() {
        String uri = attr.getNamespaceURI();
        if (uri != null && uri.equals("")) {
            uri = null;
        }
        return uri;
    }

    public Object getValue() {
        return attr.getValue();
    }

    public Object getBaseValue() {
        return attr;
    }

    public boolean isCollection() {
        return false;
    }

    public int getLength() {
        return 1;
    }

    public Object getImmediateNode() {
        return attr;
    }

    public boolean isActual() {
        return true;
    }

    public boolean isLeaf() {
        return true;
    }

    public void setValue(Object value) {
        attr.setValue((String) TypeUtils.convert(value, String.class));
    }

    public void remove() {
        attr.getParent().removeAttribute(attr);
    }

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

    public int hashCode() {
        return System.identityHashCode(attr);
    }

    public boolean equals(Object object) {
        return object == this || object instanceof JDOMAttributePointer
                && ((JDOMAttributePointer) object).attr == attr;
    }

    public int compareChildNodePointers(
            NodePointer pointer1,
            NodePointer pointer2) {
        // Won't happen - attributes don't have children
        return 0;
    }
}
