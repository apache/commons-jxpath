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

import org.apache.commons.jxpath.JXPathBeanInfo;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * A Pointer that points to a JavaBean or a collection. It is either the first element of a path or a pointer for a property value. Typically there is a
 * {@link BeanPropertyPointer} between two BeanPointers in the chain.
 */
public class BeanPointer extends PropertyOwnerPointer {

    private static final long serialVersionUID = -8227317938284982440L;

    /**
     * Qualified name.
     */
    private final QName qName;

    /**
     * Bean.
     */
    private final Object bean;

    /**
     * Bean info.
     */
    private final JXPathBeanInfo beanInfo;

    /**
     * BeanPointer a new BeanPointer.
     *
     * @param parent   pointer
     * @param qName     is the name given to the first node
     * @param bean     pointed
     * @param beanInfo JXPathBeanInfo
     */
    public BeanPointer(final NodePointer parent, final QName qName, final Object bean, final JXPathBeanInfo beanInfo) {
        super(parent);
        this.qName = qName;
        this.bean = bean;
        this.beanInfo = beanInfo;
    }

    /**
     * Constructs a new BeanPointer.
     *
     * @param qName     is the name given to the first node
     * @param bean     pointed
     * @param beanInfo JXPathBeanInfo
     * @param locale   Locale
     */
    public BeanPointer(final QName qName, final Object bean, final JXPathBeanInfo beanInfo, final Locale locale) {
        super(null, locale);
        this.qName = qName;
        this.bean = bean;
        this.beanInfo = beanInfo;
    }

    /**
     * {@inheritDoc} If the pointer has a parent, then parent's path. If the bean is null, "null()". If the bean is a primitive value, the value itself.
     * Otherwise - an empty string.
     */
    @Override
    public String asPath() {
        if (parent != null) {
            return super.asPath();
        }
        if (bean == null) {
            return "null()";
        }
        if (bean instanceof Number) {
            String string = bean.toString();
            if (string.endsWith(".0")) {
                string = string.substring(0, string.length() - 2);
            }
            return string;
        }
        if (bean instanceof Boolean) {
            return ((Boolean) bean).booleanValue() ? "true()" : "false()";
        }
        if (bean instanceof String) {
            return "'" + bean + "'";
        }
        return "/";
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof BeanPointer)) {
            return false;
        }
        final BeanPointer other = (BeanPointer) object;
        if (parent != other.parent && (parent == null || !parent.equals(other.parent))) {
            return false;
        }
        if (qName == null && other.qName != null || qName != null && !qName.equals(other.qName)) {
            return false;
        }
        final int iThis = index == WHOLE_COLLECTION ? 0 : index;
        final int iOther = other.index == WHOLE_COLLECTION ? 0 : other.index;
        if (iThis != iOther) {
            return false;
        }
        if (bean instanceof Number || bean instanceof String || bean instanceof Boolean) {
            return bean.equals(other.bean);
        }
        return bean == other.bean;
    }

    @Override
    public Object getBaseValue() {
        return bean;
    }

    /**
     * {@inheritDoc}
     *
     * @return 1
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
        return new BeanPropertyPointer(this, beanInfo);
    }

    @Override
    public int hashCode() {
        return qName == null ? 0 : qName.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * @return false
     */
    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        final Object value = getNode();
        return value == null || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }
}
