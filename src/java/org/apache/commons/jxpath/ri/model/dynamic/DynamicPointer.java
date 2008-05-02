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
 * A Pointer that points to an object with Dynamic Properties. It is used for
 * the first element of a path; following elements will by of type
 * {@link PropertyPointer}.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class DynamicPointer extends PropertyOwnerPointer {
    private QName name;
    private Object bean;
    private DynamicPropertyHandler handler;

    /**
     * Create a new DynamicPointer.
     * @param name property name
     * @param bean owning bean
     * @param handler DynamicPropertyHandler
     * @param locale Locale
     */
    public DynamicPointer(QName name, Object bean,
            DynamicPropertyHandler handler, Locale locale) {
        super(null, locale);
        this.name = name;
        this.bean = bean;
        this.handler = handler;
    }

    /**
     * Create a new DynamicPointer.
     * @param parent parent pointer
     * @param name property name
     * @param bean owning bean
     * @param handler DynamicPropertyHandler
     */
    public DynamicPointer(NodePointer parent, QName name,
            Object bean, DynamicPropertyHandler handler) {
        super(parent);
        this.name = name;
        this.bean = bean;
        this.handler = handler;
    }

    public PropertyPointer getPropertyPointer() {
        return new DynamicPropertyPointer(this, handler);
    }

    public NodeIterator createNodeIterator(
                String property, boolean reverse, NodePointer startWith) {
        return new PropertyIterator(this, property, reverse, startWith);
    }

    public NodeIterator attributeIterator(QName name) {
        return new DynamicAttributeIterator(this, name);
    }

    public QName getName() {
        return name;
    }

    public boolean isDynamicPropertyDeclarationSupported() {
        return true;
    }

    /**
     * Returns the DP object iself.
     * @return Object
     */
    public Object getBaseValue() {
        return bean;
    }

    public boolean isLeaf() {
        Object value = getNode();
        return value == null || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }

    public boolean isCollection() {
        return false;
    }

    /**
     * Returns 1.
     * @return int
     */
    public int getLength() {
        return 1;
    }

    public String asPath() {
        return parent == null ? "/" : super.asPath();
    }

    public int hashCode() {
        return System.identityHashCode(bean) + name.hashCode();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof DynamicPointer)) {
            return false;
        }

        DynamicPointer other = (DynamicPointer) object;
        return bean == other.bean && name.equals(other.name);
    }
}
