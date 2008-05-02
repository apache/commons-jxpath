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

/**
 * Represents a namespace node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class NamespacePointer extends NodePointer {
    private String prefix;
    private String namespaceURI;

    private static final long serialVersionUID = -7622456151550131709L;

    /**
     * Create a new NamespacePointer.
     * @param parent parent pointer
     * @param prefix associated ns prefix.
     */
    public NamespacePointer(NodePointer parent, String prefix) {
        super(parent);
        this.prefix = prefix;
    }

    /**
     * Create a new NamespacePointer.
     * @param parent parent pointer
     * @param prefix associated ns prefix.
     * @param namespaceURI associated ns URI.
     */
    public NamespacePointer(
        NodePointer parent,
        String prefix,
        String namespaceURI) {
        super(parent);
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
    }

    public QName getName() {
        return new QName(prefix);
    }

    public Object getBaseValue() {
        return null;
    }

    public boolean isCollection() {
        return false;
    }

    public int getLength() {
        return 1;
    }

    public Object getImmediateNode() {
        return getNamespaceURI();
    }

    public String getNamespaceURI() {
        if (namespaceURI == null) {
            namespaceURI = parent.getNamespaceURI(prefix);
        }
        return namespaceURI;
    }

    public boolean isLeaf() {
        return true;
    }

    /**
     * Throws UnsupportedOperationException.
     * @param value Object
     */
    public void setValue(Object value) {
        throw new UnsupportedOperationException("Cannot modify DOM trees");
    }

    public boolean testNode(NodeTest nodeTest) {
        return nodeTest == null
            || ((nodeTest instanceof NodeTypeTest)
                && ((NodeTypeTest) nodeTest).getNodeType()
                    == Compiler.NODE_TYPE_NODE);
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
        buffer.append("namespace::");
        buffer.append(prefix);
        return buffer.toString();
    }

    public int hashCode() {
        return prefix.hashCode();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof NamespacePointer)) {
            return false;
        }

        NamespacePointer other = (NamespacePointer) object;
        return prefix.equals(other.prefix);
    }

    public int compareChildNodePointers(
        NodePointer pointer1,
        NodePointer pointer2) {
        // Won't happen - namespaces don't have children
        return 0;
    }
}
