/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
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
 * A Pointer that points to the "lang" attribute of a JavaBean. The value of the attribute is based on the locale supplied to it in the constructor.
 */
public class LangAttributePointer extends NodePointer {

    private static final long serialVersionUID = -8665319197100034134L;

    /**
     * Constructs a new LangAttributePointer.
     *
     * @param parent parent pointer.
     */
    public LangAttributePointer(final NodePointer parent) {
        super(parent);
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
        buffer.append("@xml:lang");
        return buffer.toString();
    }

    @Override
    public int compareChildNodePointers(final NodePointer pointer1, final NodePointer pointer2) {
        // Won't happen - lang attributes don't have children
        return 0;
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof LangAttributePointer;
    }

    @Override
    public Object getBaseValue() {
        return parent.getLocale().toString().replace('_', '-');
    }

    @Override
    public Object getImmediateNode() {
        return getBaseValue();
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public QName getName() {
        return new QName("xml", "lang");
    }

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * Throws UnsupportedOperationException.
     *
     * @param value Object
     */
    @Override
    public void setValue(final Object value) {
        throw new UnsupportedOperationException("Cannot change locale using the 'lang' attribute");
    }

    @Override
    public boolean testNode(final NodeTest test) {
        return false;
    }
}
