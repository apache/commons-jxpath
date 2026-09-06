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

import java.util.Locale;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Pointer whose value is {@code null}.
 */
public class NullPointer extends PropertyOwnerPointer {

    private static final long serialVersionUID = 2193425983220679887L;

    /**
     * The name of this node
     */
    private final QName qName;

    /**
     * Optional ID, may be null.
     */
    private final String id;

    /**
     * Constructs a new NullPointer.
     *
     * @param locale Locale.
     * @param id     ID.
     */
    public NullPointer(final Locale locale, final String id) {
        super(null, locale);
        this.id = id;
        this.qName = null;
    }

    /**
     * Used for the root node.
     *
     * @param parent parent pointer
     * @param qName  node name
     */
    public NullPointer(final NodePointer parent, final QName qName) {
        super(parent);
        this.qName = qName;
        this.id = null;
    }

    /**
     * Constructs a new NullPointer.
     *
     * @param qName  node name
     * @param locale Locale
     */
    public NullPointer(final QName qName, final Locale locale) {
        super(null, locale);
        this.qName = qName;
        this.id = null;
    }

    @Override
    public String asPath() {
        if (id != null) {
            return "id(" + id + ")";
        }
        return parent == null ? "null()" : super.asPath();
    }

    @Override
    public NodePointer createChild(final JXPathContext context, final QName qName, final int index) {
        return createPath(context).createChild(context, qName, index);
    }

    @Override
    public NodePointer createChild(final JXPathContext context, final QName qName, final int index, final Object value) {
        return createPath(context).createChild(context, qName, index, value);
    }

    @Override
    public NodePointer createPath(final JXPathContext context) {
        if (parent != null) {
            return parent.createPath(context).getValuePointer();
        }
        throw new UnsupportedOperationException("Cannot create the root object: " + asPath());
    }

    @Override
    public NodePointer createPath(final JXPathContext context, final Object value) {
        if (parent != null) {
            return parent.createPath(context, value).getValuePointer();
        }
        throw new UnsupportedOperationException("Cannot create the root object: " + asPath());
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof NullPointer)) {
            return false;
        }
        final NullPointer other = (NullPointer) object;
        return qName == other.qName || qName != null && qName.equals(other.qName);
    }

    @Override
    public Object getBaseValue() {
        return null;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public QName getName() {
        return qName;
    }

    @Override
    public PropertyPointer getPropertyPointer() {
        return new NullPropertyPointer(this);
    }

    @Override
    public int hashCode() {
        return qName == null ? 0 : qName.hashCode();
    }

    @Override
    public boolean isActual() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
