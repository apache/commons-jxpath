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
package org.apache.commons.jxpath.ri.model.beans;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * A Pointer that points to the "lang" attribute of a JavaBean. The value
 * of the attribute is based on the locale supplied to it in the constructor.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class LangAttributePointer extends NodePointer {
    /**
     * Create a new LangAttributePointer.
     * @param parent parent pointer.
     */
    public LangAttributePointer(NodePointer parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    public QName getName() {
        return new QName("xml", "lang");
    }

    /**
     * {@inheritDoc}
     */
    public String getNamespaceURI() {
        return null;
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
    public Object getBaseValue() {
        return parent.getLocale().toString().replace('_', '-');
    }

    /**
     * {@inheritDoc}
     */
    public Object getImmediateNode() {
        return getBaseValue();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeaf() {
        return true;
    }

    /**
     * Throws UnsupportedOperationException.
     * @param value Object
     */
    public void setValue(Object value) {
        throw new UnsupportedOperationException(
                "Cannot change locale using the 'lang' attribute");
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
        buffer.append("@xml:lang");
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object object) {
        return object instanceof LangAttributePointer;
    }

    /**
     * {@inheritDoc}
     */
    public boolean testNode(NodeTest test) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int compareChildNodePointers(
        NodePointer pointer1,
        NodePointer pointer2) {
        // Won't happen - lang attributes don't have children
        return 0;
    }
}