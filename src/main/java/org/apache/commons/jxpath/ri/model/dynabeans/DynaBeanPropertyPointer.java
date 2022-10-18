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

import java.util.ArrayList;
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
 * Pointer pointing to a property of a {@link DynaBean}. If the target DynaBean is
 * Serializable, so should this instance be.
 */
public class DynaBeanPropertyPointer extends PropertyPointer {
    private static final String CLASS = "class";

    private final DynaBean dynaBean;
    private String name;
    private String[] names;

    private static final long serialVersionUID = 2094421509141267239L;

    /**
     * Create a new DynaBeanPropertyPointer.
     * @param parent pointer
     * @param dynaBean pointed
     */
    public DynaBeanPropertyPointer(final NodePointer parent, final DynaBean dynaBean) {
        super(parent);
        this.dynaBean = dynaBean;
    }

    @Override
    public Object getBaseValue() {
        return dynaBean.get(getPropertyName());
    }

    /**
     * This type of node is auxiliary.
     * @return true
     */
    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public int getPropertyCount() {
        return getPropertyNames().length;
    }

    @Override
    public String[] getPropertyNames() {
        /* @todo do something about the sorting - LIKE WHAT? - MJB */
        if (names == null) {
            final DynaClass dynaClass = dynaBean.getDynaClass();
            final DynaProperty[] dynaProperties = dynaClass.getDynaProperties();
            final ArrayList properties = new ArrayList(dynaProperties.length);
            for (final DynaProperty element : dynaProperties) {
                final String name = element.getName();
                if (!CLASS.equals(name)) {
                    properties.add(name);
                }
            }
            names = (String[]) properties.toArray(new String[properties.size()]);
            Arrays.sort(names);
        }
        return names;
    }

    /**
     * Returns the name of the currently selected property or "*"
     * if none has been selected.
     * @return String
     */
    @Override
    public String getPropertyName() {
        if (name == null) {
            final String[] names = getPropertyNames();
            name = propertyIndex >= 0 && propertyIndex < names.length ? names[propertyIndex] : "*";
        }
        return name;
    }

    /**
     * Select a property by name.
     * @param propertyName to select
     */
    @Override
    public void setPropertyName(final String propertyName) {
        setPropertyIndex(UNSPECIFIED_PROPERTY);
        this.name = propertyName;
    }

    /**
     * Index of the currently selected property in the list of all
     * properties sorted alphabetically.
     * @return int
     */
    @Override
    public int getPropertyIndex() {
        if (propertyIndex == UNSPECIFIED_PROPERTY) {
            final String[] names = getPropertyNames();
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
     * @param index to set
     */
    @Override
    public void setPropertyIndex(final int index) {
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
     * @return Object
     */
    @Override
    public Object getImmediateNode() {
        final String name = getPropertyName();
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
            catch (final ArrayIndexOutOfBoundsException ex) {
                value = null;
            }
            catch (final IllegalArgumentException ex) {
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
     * Returns true if the bean has the currently selected property.
     * @return boolean
     */
    @Override
    protected boolean isActualProperty() {
        final DynaClass dynaClass = dynaBean.getDynaClass();
        return dynaClass.getDynaProperty(getPropertyName()) != null;
    }

    /**
     * Learn whether the property referenced is an indexed property.
     * @return boolean
     */
    protected boolean isIndexedProperty() {
        final DynaClass dynaClass = dynaBean.getDynaClass();
        final DynaProperty property = dynaClass.getDynaProperty(name);
        return property.isIndexed();
    }

    /**
     * If index == WHOLE_COLLECTION, change the value of the property, otherwise
     * change the value of the index'th element of the collection
     * represented by the property.
     * @param value to set
     */
    @Override
    public void setValue(final Object value) {
        setValue(index, value);
    }

    @Override
    public void remove() {
        if (index == WHOLE_COLLECTION) {
            dynaBean.set(getPropertyName(), null);
        }
        else if (isIndexedProperty()) {
            dynaBean.set(getPropertyName(), index, null);
        }
        else if (isCollection()) {
            final Object collection = ValueUtils.remove(getBaseValue(), index);
            dynaBean.set(getPropertyName(), collection);
        }
        else if (index == 0) {
            dynaBean.set(getPropertyName(), null);
        }
    }

    /**
     * Set an indexed value.
     * @param index to change
     * @param value to set
     */
    private void setValue(final int index, final Object value) {
        if (index == WHOLE_COLLECTION) {
            dynaBean.set(getPropertyName(), convert(value, false));
        }
        else if (isIndexedProperty()) {
            dynaBean.set(getPropertyName(), index, convert(value, true));
        }
        else {
            final Object baseValue = dynaBean.get(getPropertyName());
            ValueUtils.setValue(baseValue, index, value);
        }
    }


    /**
     * Convert a value to the appropriate property type.
     * @param value to convert
     * @param element whether this should be a collection element.
     * @return conversion result
     */
    private Object convert(final Object value, final boolean element) {
        final DynaClass dynaClass = dynaBean.getDynaClass();
        final DynaProperty property = dynaClass.getDynaProperty(getPropertyName());
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
        catch (final Exception ex) {
            final String string = value == null ? "null" : value.getClass().getName();
            throw new JXPathTypeConversionException(
                    "Cannot convert value of class " + string + " to type "
                            + type, ex);
        }
    }
}
