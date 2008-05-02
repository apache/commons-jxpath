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

import java.util.Locale;

import org.apache.commons.jxpath.JXPathBeanInfo;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * A Pointer that points to a JavaBean or a collection. It is either
 * the first element of a path or a pointer for a property value.
 * Typically there is a {@link BeanPropertyPointer} between two BeanPointers
 * in the chain.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class BeanPointer extends PropertyOwnerPointer {
    private QName name;
    private Object bean;
    private JXPathBeanInfo beanInfo;

    /**
     * Create a new BeanPointer.
     * @param name is the name given to the first node
     * @param bean pointed
     * @param beanInfo JXPathBeanInfo
     * @param locale Locale
     */
    public BeanPointer(QName name, Object bean, JXPathBeanInfo beanInfo,
            Locale locale) {
        super(null, locale);
        this.name = name;
        this.bean = bean;
        this.beanInfo = beanInfo;
    }

    /**
     * Create a new BeanPointer.
     * @param parent pointer
     * @param name is the name given to the first node
     * @param bean pointed
     * @param beanInfo JXPathBeanInfo
     */
    public BeanPointer(NodePointer parent, QName name, Object bean,
            JXPathBeanInfo beanInfo) {
        super(parent);
        this.name = name;
        this.bean = bean;
        this.beanInfo = beanInfo;
    }

    public PropertyPointer getPropertyPointer() {
        return new BeanPropertyPointer(this, beanInfo);
    }

    public QName getName() {
        return name;
    }

    public Object getBaseValue() {
        return bean;
    }

    /**
     * {@inheritDoc}
     * @return false
     */
    public boolean isCollection() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @return 1
     */
    public int getLength() {
        return 1;
    }

    public boolean isLeaf() {
        Object value = getNode();
        return value == null
            || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }

    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof BeanPointer)) {
            return false;
        }

        BeanPointer other = (BeanPointer) object;
        if (parent != other.parent) {
            if (parent == null || !parent.equals(other.parent)) {
                return false;
            }
        }

        if ((name == null && other.name != null)
            || (name != null && !name.equals(other.name))) {
            return false;
        }

        int iThis = (index == WHOLE_COLLECTION ? 0 : index);
        int iOther = (other.index == WHOLE_COLLECTION ? 0 : other.index);
        if (iThis != iOther) {
            return false;
        }

        if (bean instanceof Number
            || bean instanceof String
            || bean instanceof Boolean) {
            return bean.equals(other.bean);
        }
        return bean == other.bean;
    }

    /**
     * {@inheritDoc}
     * If the pointer has a parent, then parent's path.
     * If the bean is null, "null()".
     * If the bean is a primitive value, the value itself.
     * Otherwise - an empty string.
     */
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
}
