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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;

import org.apache.commons.jxpath.JXPathBeanInfo;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Pointer to a property of a JavaBean.
 */
public class BeanPropertyPointer extends PropertyPointer {

    private static final long serialVersionUID = -6008991447676468786L;

    /**
     * Uninitialized object marker.
     */
    private static final Object UNINITIALIZED = new Object();

    /**
     * The name of the currently selected property.
     */
    private String propertyName;

    /**
     * JavaBean info.
     */
    private final JXPathBeanInfo beanInfo;

    /**
     * The value of the currently selected property..
     */
    private Object baseValue = UNINITIALIZED;

    /**
     * If index == WHOLE_COLLECTION, the value of the property, otherwise the value of the index'th element of the collection represented by the property. If
     * the property is not a collection, index should be zero and the value will be the property itself.
     */
    private Object value = UNINITIALIZED;

    /**
     * The names of all properties, sorted alphabetically.
     */
    private transient String[] names;

    /**
     * All PropertyDescriptors.
     */
    private transient PropertyDescriptor[] propertyDescriptors;

    /**
     * The property descriptor corresponding to the current property index.
     */
    private transient PropertyDescriptor propertyDescriptor;

    /**
     * Constructs a new BeanPropertyPointer.
     *
     * @param parent   parent pointer
     * @param beanInfo describes the target property/ies.
     */
    public BeanPropertyPointer(final NodePointer parent, final JXPathBeanInfo beanInfo) {
        super(parent);
        this.beanInfo = beanInfo;
    }

    @Override
    public NodePointer createPath(final JXPathContext context) {
        if (getImmediateNode() == null) {
            super.createPath(context);
            baseValue = UNINITIALIZED;
            value = UNINITIALIZED;
        }
        return this;
    }

    /**
     * Gets the value of the currently selected property.
     *
     * @return Object value
     */
    @Override
    public Object getBaseValue() {
        if (baseValue == UNINITIALIZED) {
            final PropertyDescriptor pd = getPropertyDescriptor();
            if (pd == null) {
                return null;
            }
            baseValue = ValueUtils.getValue(getBean(), pd);
        }
        return baseValue;
    }

    /**
     * If index == WHOLE_COLLECTION, the value of the property, otherwise the value of the index'th element of the collection represented by the property. If
     * the property is not a collection, index should be zero and the value will be the property itself.
     *
     * @return Object
     */
    @Override
    public Object getImmediateNode() {
        if (value == UNINITIALIZED) {
            if (index == WHOLE_COLLECTION) {
                value = ValueUtils.getValue(getBaseValue());
            } else {
                final PropertyDescriptor pd = getPropertyDescriptor();
                if (pd == null) {
                    value = null;
                } else {
                    value = ValueUtils.getValue(getBean(), pd, index);
                }
            }
        }
        return value;
    }

    /**
     * If the property contains a collection, then the length of that collection, otherwise - 1.
     *
     * @return int length
     */
    @Override
    public int getLength() {
        final PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null) {
            return 1;
        }
        if (pd instanceof IndexedPropertyDescriptor) {
            return ValueUtils.getIndexedPropertyLength(getBean(), (IndexedPropertyDescriptor) pd);
        }
        final int hint = ValueUtils.getCollectionHint(pd.getPropertyType());
        if (hint == -1) {
            return 1;
        }
        return super.getLength();
    }

    @Override
    public int getPropertyCount() {
        if (beanInfo.isAtomic()) {
            return 0;
        }
        return getPropertyDescriptors().length;
    }

    /**
     * Gets the property descriptor corresponding to the current property index.
     *
     * @return PropertyDescriptor
     */
    private PropertyDescriptor getPropertyDescriptor() {
        if (propertyDescriptor == null) {
            final int inx = getPropertyIndex();
            if (inx == UNSPECIFIED_PROPERTY) {
                propertyDescriptor = beanInfo.getPropertyDescriptor(propertyName);
            } else {
                final PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors();
                if (inx >= 0 && inx < propertyDescriptors.length) {
                    propertyDescriptor = propertyDescriptors[inx];
                } else {
                    propertyDescriptor = null;
                }
            }
        }
        return propertyDescriptor;
    }

    /**
     * Gets all PropertyDescriptors.
     *
     * @return PropertyDescriptor[]
     */
    protected synchronized PropertyDescriptor[] getPropertyDescriptors() {
        if (propertyDescriptors == null) {
            propertyDescriptors = beanInfo.getPropertyDescriptors();
        }
        return propertyDescriptors;
    }

    /**
     * Gets the name of the currently selected property.
     *
     * @return String property name
     */
    @Override
    public String getPropertyName() {
        if (propertyName == null) {
            final PropertyDescriptor pd = getPropertyDescriptor();
            if (pd != null) {
                propertyName = pd.getName();
            }
        }
        return propertyName != null ? propertyName : "*";
    }

    /**
     * Gets the names of all properties, sorted alphabetically.
     *
     * @return String[]
     */
    @Override
    public String[] getPropertyNames() {
        if (names == null) {
            final PropertyDescriptor[] pds = getPropertyDescriptors();
            names = new String[pds.length];
            for (int i = 0; i < names.length; i++) {
                names[i] = pds[i].getName();
            }
        }
        return names;
    }

    @Override
    protected boolean isActualProperty() {
        return getPropertyDescriptor() != null;
    }

    @Override
    public boolean isCollection() {
        final PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null) {
            return false;
        }
        if (pd instanceof IndexedPropertyDescriptor) {
            return true;
        }
        final int hint = ValueUtils.getCollectionHint(pd.getPropertyType());
        if (hint == -1) {
            return false;
        }
        if (hint == 1) {
            return true;
        }
        final Object value = getBaseValue();
        return value != null && ValueUtils.isCollection(value);
    }

    /**
     * This type of node is auxiliary.
     *
     * @return true
     */
    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public void remove() {
        if (index == WHOLE_COLLECTION) {
            setValue(null);
        } else if (isCollection()) {
            final Object o = getBaseValue();
            final Object collection = ValueUtils.remove(getBaseValue(), index);
            if (collection != o) {
                ValueUtils.setValue(getBean(), getPropertyDescriptor(), collection);
            }
        } else if (index == 0) {
            index = WHOLE_COLLECTION;
            setValue(null);
        }
    }

    @Override
    public void setIndex(final int index) {
        if (this.index == index) {
            return;
        }
        // When dealing with a scalar, index == 0 is equivalent to
        // WHOLE_COLLECTION, so do not change it.
        if (this.index != WHOLE_COLLECTION || index != 0 || isCollection()) {
            super.setIndex(index);
            value = UNINITIALIZED;
        }
    }

    /**
     * Selects a property by its offset in the alphabetically sorted list.
     *
     * @param index property index
     */
    @Override
    public void setPropertyIndex(final int index) {
        if (propertyIndex != index) {
            super.setPropertyIndex(index);
            propertyName = null;
            propertyDescriptor = null;
            baseValue = UNINITIALIZED;
            value = UNINITIALIZED;
        }
    }

    /**
     * Select a property by name.
     *
     * @param propertyName String name
     */
    @Override
    public void setPropertyName(final String propertyName) {
        setPropertyIndex(UNSPECIFIED_PROPERTY);
        this.propertyName = propertyName;
    }

    /**
     * If index == WHOLE_COLLECTION, change the value of the property, otherwise change the value of the index'th element of the collection represented by the
     * property.
     *
     * @param value value to set
     */
    @Override
    public void setValue(final Object value) {
        final PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null) {
            throw new JXPathInvalidAccessException("Cannot set property: " + asPath() + " - no such property");
        }
        if (index == WHOLE_COLLECTION) {
            ValueUtils.setValue(getBean(), pd, value);
        } else {
            ValueUtils.setValue(getBean(), pd, index, value);
        }
        this.value = value;
    }
}
