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

package org.apache.commons.jxpath.ri.model.dynamic;

import java.util.Locale;

import org.apache.commons.jxpath.DynamicPropertyHandler;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyIterator;
import org.apache.commons.jxpath.ri.model.beans.PropertyOwnerPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;

/**
 * A Pointer that points to an object with Dynamic Properties. It is used for the first element of a path; following elements will by of type
 * {@link PropertyPointer}.
 */
public class DynamicPointer extends PropertyOwnerPointer {

    private static final long serialVersionUID = -1842347025295904256L;

    /**
     * Qualified name.
     */
    private final QName qName;

    /**
     * Java bean.
     */
    private final Object bean;

    /**
     * Dynamic property handler.
     */
    private final DynamicPropertyHandler handler;

    /**
     * Constructs a new DynamicPointer.
     *
     * @param parent  parent pointer
     * @param qName    property name
     * @param bean    owning bean
     * @param handler DynamicPropertyHandler
     */
    public DynamicPointer(final NodePointer parent, final QName qName, final Object bean, final DynamicPropertyHandler handler) {
        super(parent);
        this.qName = qName;
        this.bean = bean;
        this.handler = handler;
    }

    /**
     * Constructs a new DynamicPointer.
     *
     * @param qName    property name
     * @param bean    owning bean
     * @param handler DynamicPropertyHandler
     * @param locale  Locale
     */
    public DynamicPointer(final QName qName, final Object bean, final DynamicPropertyHandler handler, final Locale locale) {
        super(null, locale);
        this.qName = qName;
        this.bean = bean;
        this.handler = handler;
    }

    @Override
    public String asPath() {
        return parent == null ? "/" : super.asPath();
    }

    @Override
    public NodeIterator attributeIterator(final QName qName) {
        return new DynamicAttributeIterator(this, qName);
    }

    @Override
    public NodeIterator createNodeIterator(final String property, final boolean reverse, final NodePointer startWith) {
        return new PropertyIterator(this, property, reverse, startWith);
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof DynamicPointer)) {
            return false;
        }
        final DynamicPointer other = (DynamicPointer) object;
        if (bean != other.bean) {
            return false;
        }
        return qName == other.qName || qName != null && qName.equals(other.qName);
    }

    /**
     * Returns the DP object iself.
     *
     * @return Object
     */
    @Override
    public Object getBaseValue() {
        return bean;
    }

    /**
     * Returns 1.
     *
     * @return int
     */
    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public QName getName() {
        return qName;
    }

    @Override
    public PropertyPointer getPropertyPointer() {
        return new DynamicPropertyPointer(this, handler);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(bean) + (qName == null ? 0 : qName.hashCode());
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public boolean isDynamicPropertyDeclarationSupported() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        final Object value = getNode();
        return value == null || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }
}
