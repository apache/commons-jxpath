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

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.DynamicPropertyHandler;
import org.apache.commons.jxpath.JXPathAbstractFactoryException;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Pointer pointing to a property of an object with dynamic properties.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class DynamicPropertyPointer extends PropertyPointer {

    private static final long serialVersionUID = -5720585681149150822L;

    private DynamicPropertyHandler handler;
    private String name;
    private String[] names;
    private String requiredPropertyName;

    /**
     * Create a new DynamicPropertyPointer.
     * @param parent pointer
     * @param handler DynamicPropertyHandler
     */
    public DynamicPropertyPointer(NodePointer parent,
            DynamicPropertyHandler handler) {
        super(parent);
        this.handler = handler;
    }

    /**
     * This type of node is auxiliary.
     * @return true
     */
    public boolean isContainer() {
        return true;
    }

    /**
     * Number of the DP object's properties.
     * @return int
     */
    public int getPropertyCount() {
        return getPropertyNames().length;
    }

    /**
     * Names of all properties, sorted alphabetically.
     * @return String[]
     */
    public String[] getPropertyNames() {
        if (names == null) {
            String[] allNames = handler.getPropertyNames(getBean());
            names = new String[allNames.length];
            for (int i = 0; i < names.length; i++) {
                names[i] = allNames[i];
            }
            Arrays.sort(names);
            if (requiredPropertyName != null) {
                int inx = Arrays.binarySearch(names, requiredPropertyName);
                if (inx < 0) {
                    allNames = names;
                    names = new String[allNames.length + 1];
                    names[0] = requiredPropertyName;
                    System.arraycopy(allNames, 0, names, 1, allNames.length);
                    Arrays.sort(names);
                }
            }
        }
        return names;
    }

    /**
     * Returns the name of the currently selected property or "*"
     * if none has been selected.
     * @return String
     */
    public String getPropertyName() {
        if (name == null) {
            String[] names = getPropertyNames();
            name = propertyIndex >= 0 && propertyIndex < names.length ? names[propertyIndex] : "*";
        }
        return name;
    }

    /**
     * Select a property by name.  If the supplied name is
     * not one of the object's existing properties, it implicitly
     * adds this name to the object's property name list. It does not
     * set the property value though. In order to set the property
     * value, call setValue().
     * @param propertyName to set
     */
    public void setPropertyName(String propertyName) {
        setPropertyIndex(UNSPECIFIED_PROPERTY);
        this.name = propertyName;
        requiredPropertyName = propertyName;
        if (names != null && Arrays.binarySearch(names, propertyName) < 0) {
            names = null;
        }
    }

    /**
     * Index of the currently selected property in the list of all
     * properties sorted alphabetically.
     * @return int
     */
    public int getPropertyIndex() {
        if (propertyIndex == UNSPECIFIED_PROPERTY) {
            String[] names = getPropertyNames();
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(name)) {
                    setPropertyIndex(i);
                    break;
                }
            }
        }
        return super.getPropertyIndex();
    }

    /**
     * Index a property by its index in the list of all
     * properties sorted alphabetically.
     * @param index to set
     */
    public void setPropertyIndex(int index) {
        if (propertyIndex != index) {
            super.setPropertyIndex(index);
            name = null;
        }
    }

    /**
     * Returns the value of the property, not an element of the collection
     * represented by the property, if any.
     * @return Object
     */
    public Object getBaseValue() {
        return handler.getProperty(getBean(), getPropertyName());
    }

    /**
     * If index == WHOLE_COLLECTION, the value of the property, otherwise
     * the value of the index'th element of the collection represented by the
     * property. If the property is not a collection, index should be zero
     * and the value will be the property itself.
     * @return Object
     */
    public Object getImmediateNode() {
        Object value;
        if (index == WHOLE_COLLECTION) {
            value = ValueUtils.getValue(handler.getProperty(
                    getBean(),
                    getPropertyName()));
        }
        else {
            value = ValueUtils.getValue(handler.getProperty(
                    getBean(),
                    getPropertyName()), index);
        }
        return value;
    }

    /**
     * A dynamic property is always considered actual - all keys are apparently
     * existing with possibly the value of null.
     * @return boolean
     */
    protected boolean isActualProperty() {
        return true;
    }

    /**
     * If index == WHOLE_COLLECTION, change the value of the property, otherwise
     * change the value of the index'th element of the collection
     * represented by the property.
     * @param value to set
     */
    public void setValue(Object value) {
        if (index == WHOLE_COLLECTION) {
            handler.setProperty(getBean(), getPropertyName(), value);
        }
        else {
            ValueUtils.setValue(
                handler.getProperty(getBean(), getPropertyName()),
                index,
                value);
        }
    }

    public NodePointer createPath(JXPathContext context) {
        // Ignore the name passed to us, use our own data
        Object collection = getBaseValue();
        if (collection == null) {
            AbstractFactory factory = getAbstractFactory(context);
            boolean success =
                factory.createObject(
                    context,
                    this,
                    getBean(),
                    getPropertyName(),
                    0);
            if (!success) {
                throw new JXPathAbstractFactoryException(
                    "Factory could not create an object for path: " + asPath());
            }
            collection = getBaseValue();
        }

        if (index != WHOLE_COLLECTION) {
            if (index < 0) {
                throw new JXPathInvalidAccessException("Index is less than 1: "
                        + asPath());
            }

            if (index >= getLength()) {
                collection = ValueUtils.expandCollection(collection, index + 1);
                handler.setProperty(getBean(), getPropertyName(), collection);
            }
        }

        return this;
    }

    public NodePointer createPath(JXPathContext context, Object value) {
        if (index == WHOLE_COLLECTION) {
            handler.setProperty(getBean(), getPropertyName(), value);
        }
        else {
            createPath(context);
            ValueUtils.setValue(getBaseValue(), index, value);
        }
        return this;
    }

    public void remove() {
        if (index == WHOLE_COLLECTION) {
            removeKey();
        }
        else if (isCollection()) {
            Object collection = ValueUtils.remove(getBaseValue(), index);
            handler.setProperty(getBean(), getPropertyName(), collection);
        }
        else if (index == 0) {
            removeKey();
        }
    }

    /**
     * Remove the current property.
     */
    private void removeKey() {
        Object bean = getBean();
        if (bean instanceof Map) {
            ((Map) bean).remove(getPropertyName());
        }
        else {
            handler.setProperty(bean, getPropertyName(), null);
        }
    }

    public String asPath() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getImmediateParentPointer().asPath());
        if (buffer.length() == 0) {
            buffer.append("/.");
        }
        else if (buffer.charAt(buffer.length() - 1) == '/') {
            buffer.append('.');
        }
        buffer.append("[@name='");
        buffer.append(escape(getPropertyName()));
        buffer.append("']");
        if (index != WHOLE_COLLECTION && isCollection()) {
            buffer.append('[').append(index + 1).append(']');
        }
        return buffer.toString();
    }

}
