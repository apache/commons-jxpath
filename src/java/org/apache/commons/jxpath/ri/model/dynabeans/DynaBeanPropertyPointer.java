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
package org.apache.commons.jxpath.ri.model.dynabeans;

import java.util.Arrays;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.jxpath.JXPathTypeConversionException;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;
import org.apache.commons.jxpath.util.TypeUtils;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Pointer pointing to a property of a DynaBean.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class DynaBeanPropertyPointer extends PropertyPointer {
    private DynaBean dynaBean;
    private String name;
    private String[] names;

    public DynaBeanPropertyPointer(NodePointer parent, DynaBean dynaBean) {
        super(parent);
        this.dynaBean = dynaBean;
    }

    public Object getBaseValue() {
        return dynaBean.get(getPropertyName());
    }

    /**
     * This type of node is auxiliary.
     */
    public boolean isContainer() {
        return true;
    }

    /**
     * Number of the DP object's properties.
     */
    public int getPropertyCount() {
        return getPropertyNames().length;
    }

    /**
     * Names of all properties, sorted alphabetically
     *
     * @todo do something about the sorting
     */
    public String[] getPropertyNames() {
        if (names == null) {
            DynaClass dynaClass = dynaBean.getDynaClass();
            DynaProperty properties[] = dynaClass.getDynaProperties();
            int count = properties.length;
            boolean hasClass = dynaClass.getDynaProperty("class") != null;
            if (hasClass) {
                count--;       // Exclude "class" from properties
            }
            names = new String[count];
            for (int i = 0, j = 0; i < properties.length; i++) {
                String name = properties[i].getName();
                if (!hasClass || !name.equals("class")) {
                    names[j++] = name;
                }
            }
            Arrays.sort(names);
        }
        return names;
    }

    /**
     * Returns the name of the currently selected property or "*"
     * if none has been selected.
     */
    public String getPropertyName() {
        if (name == null) {
            String names[] = getPropertyNames();
            name = propertyIndex >= 0 && propertyIndex < names.length ? names[propertyIndex] : "*";
        }
        return name;
    }

    /**
     * Select a property by name.
     */
    public void setPropertyName(String propertyName) {
        setPropertyIndex(UNSPECIFIED_PROPERTY);
        this.name = propertyName;
    }

    /**
     * Index of the currently selected property in the list of all
     * properties sorted alphabetically.
     */
    public int getPropertyIndex() {
        if (propertyIndex == UNSPECIFIED_PROPERTY) {
            String names[] = getPropertyNames();
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(name)) {
                    propertyIndex = i;
                    name = null;
                    break;
                }
            }
        }
        return super.getPropertyIndex();
    }

    /**
     * Index a property by its index in the list of all
     * properties sorted alphabetically.
     */
    public void setPropertyIndex(int index) {
        if (propertyIndex != index) {
            super.setPropertyIndex(index);
            name = null;
        }
    }

    /**
     * If index == WHOLE_COLLECTION, the value of the property, otherwise
     * the value of the index'th element of the collection represented by the
     * property. If the property is not a collection, index should be zero
     * and the value will be the property itself.
     */
    public Object getImmediateNode() {
        String name = getPropertyName();
        if (name.equals("*")) {
            return null;
        }

        Object value;
        if (index == WHOLE_COLLECTION) {
            value = ValueUtils.getValue(dynaBean.get(name));
        }
        else if (isIndexedProperty()) {
            // DynaClass at this point is not based on whether
            // the property is indeed indexed, but rather on
            // whether it is an array or List. Therefore
            // the indexed set may fail.
            try {
                value = ValueUtils.getValue(dynaBean.get(name, index));
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                value = null;
            }
            catch (IllegalArgumentException ex) {
                value = dynaBean.get(name);
                value = ValueUtils.getValue(value, index);
            }
        }
        else {
            value = dynaBean.get(name);
            if (ValueUtils.isCollection(value)) {
                value = ValueUtils.getValue(value, index);
            }
            else if (index != 0) {
                value = null;
            }
        }
        return value;
    }

    /**
     * Returns true if the bean has the currently selected property
     */
    protected boolean isActualProperty() {
        DynaClass dynaClass = dynaBean.getDynaClass();
        return dynaClass.getDynaProperty(getPropertyName()) != null;
    }

    protected boolean isIndexedProperty() {
        DynaClass dynaClass = dynaBean.getDynaClass();
        DynaProperty property = dynaClass.getDynaProperty(name);
        return property.isIndexed();
    }

    /**
     * If index == WHOLE_COLLECTION, change the value of the property, otherwise
     * change the value of the index'th element of the collection
     * represented by the property.
     */
    public void setValue(Object value) {
        setValue(index, value);
    }

    public void remove() {
        if (index == WHOLE_COLLECTION) {
            dynaBean.set(getPropertyName(), null);
        }
        else if (isIndexedProperty()) {
            dynaBean.set(getPropertyName(), index, null);
        }
        else if (isCollection()) {
            Object collection = ValueUtils.remove(getBaseValue(), index);
            dynaBean.set(getPropertyName(), collection);
        }
        else if (index == 0) {
            dynaBean.set(getPropertyName(), null);
        }
    }

    private void setValue(int index, Object value) {
        if (index == WHOLE_COLLECTION) {
            dynaBean.set(getPropertyName(), convert(value, false));
        }
        else if (isIndexedProperty()) {
            dynaBean.set(getPropertyName(), index, convert(value, true));
        }
        else {
            Object baseValue = dynaBean.get(getPropertyName());
            ValueUtils.setValue(baseValue, index, value);
        }
   }


    private Object convert(Object value, boolean element) {
        DynaClass dynaClass = (DynaClass) dynaBean.getDynaClass();
        DynaProperty property = dynaClass.getDynaProperty(getPropertyName());
        Class type = property.getType();
        if (element) {
            if (type.isArray()) {
                type = type.getComponentType();
            }
            else {
                return value; // No need to convert
            }
        }

        try {
            return TypeUtils.convert(value, type);
        }
        catch (Exception ex) {
            String string = value == null ? "null" : value.getClass().getName();
            throw new JXPathTypeConversionException(
                    "Cannot convert value of class " + string + " to type "
                            + type, ex);
        }
    }
}