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

package org.apache.commons.jxpath;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * An implementation of JXPathBeanInfo based on JavaBeans' BeanInfo. Properties advertised by JXPathBasicBeanInfo are the same as those advertised by BeanInfo
 * for the corresponding class.
 *
 * @see java.beans.BeanInfo
 * @see java.beans.Introspector
 */
public class JXPathBasicBeanInfo implements JXPathBeanInfo {

    private static final long serialVersionUID = -3863803443111484155L;
    private static final Comparator<PropertyDescriptor> PROPERTY_DESCRIPTOR_COMPARATOR = Comparator.comparing(PropertyDescriptor::getName);
    /**
     * Whether objects of this class are treated as atomic objects which have no properties of their own.
     */
    private boolean atomic;

    /**
     * Bean class.
     */
    private final Class clazz;

    /**
     * The DynamicPropertyHandler class for a dynamic class.
     */
    private Class dynamicPropertyHandlerClass;

    /**
     * List of property descriptors for the beans described by this bean info object.
     */
    private transient PropertyDescriptor[] propertyDescriptors;

    /**
     * PropertyDescriptor for the specified name or null if there is no such property.
     */
    private transient HashMap<String, PropertyDescriptor> propertyDescriptorMap;

    /**
     * Constructs a new JXPathBasicBeanInfo.
     *
     * @param clazz bean class
     */
    public JXPathBasicBeanInfo(final Class clazz) {
        this.clazz = clazz;
    }

    /**
     * Constructs a new JXPathBasicBeanInfo.
     *
     * @param clazz  bean class
     * @param atomic whether objects of this class are treated as atomic objects which have no properties of their own.
     */
    public JXPathBasicBeanInfo(final Class clazz, final boolean atomic) {
        this.clazz = clazz;
        this.atomic = atomic;
    }

    /**
     * Constructs a new JXPathBasicBeanInfo.
     *
     * @param clazz                       bean class
     * @param dynamicPropertyHandlerClass dynamic property handler class
     */
    public JXPathBasicBeanInfo(final Class clazz, final Class dynamicPropertyHandlerClass) {
        this.clazz = clazz;
        this.atomic = false;
        this.dynamicPropertyHandlerClass = dynamicPropertyHandlerClass;
    }

    /**
     * Gets the DynamicPropertyHandler class for a dynamic class.
     *
     * @return the DynamicPropertyHandler class for a dynamic class.
     */
    @Override
    public Class getDynamicPropertyHandlerClass() {
        return dynamicPropertyHandlerClass;
    }

    @Override
    public synchronized PropertyDescriptor getPropertyDescriptor(final String propertyName) {
        if (propertyDescriptorMap == null) {
            propertyDescriptorMap = new HashMap<>();
            final PropertyDescriptor[] pds = getPropertyDescriptors();
            for (final PropertyDescriptor pd : pds) {
                propertyDescriptorMap.put(pd.getName(), pd);
            }
        }
        return propertyDescriptorMap.get(propertyName);
    }

    @Override
    public synchronized PropertyDescriptor[] getPropertyDescriptors() {
        if (propertyDescriptors == null) {
            if (clazz == Object.class) {
                propertyDescriptors = new PropertyDescriptor[0];
            } else {
                try {
                    BeanInfo bi;
                    if (clazz.isInterface()) {
                        bi = Introspector.getBeanInfo(clazz);
                    } else {
                        bi = Introspector.getBeanInfo(clazz, Object.class);
                    }
                    final PropertyDescriptor[] pds = bi.getPropertyDescriptors();
                    final PropertyDescriptor[] descriptors = new PropertyDescriptor[pds.length];
                    System.arraycopy(pds, 0, descriptors, 0, pds.length);
                    Arrays.sort(descriptors, PROPERTY_DESCRIPTOR_COMPARATOR);
                    propertyDescriptors = descriptors;
                } catch (final IntrospectionException ex) {
                    ex.printStackTrace();
                    return new PropertyDescriptor[0];
                }
            }
        }
        if (propertyDescriptors.length == 0) {
            return propertyDescriptors;
        }
        final PropertyDescriptor[] result = new PropertyDescriptor[propertyDescriptors.length];
        System.arraycopy(propertyDescriptors, 0, result, 0, propertyDescriptors.length);
        return result;
    }

    /**
     * Tests whether objects of this class are treated as atomic objects which have no properties of their own.
     *
     * @return whether objects of this class are treated as atomic objects which have no properties of their own.
     */
    @Override
    public boolean isAtomic() {
        return atomic;
    }

    /**
     * Return true if the corresponding objects have dynamic properties.
     *
     * @return boolean
     */
    @Override
    public boolean isDynamic() {
        return dynamicPropertyHandlerClass != null;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("BeanInfo [class = ");
        buffer.append(clazz.getName());
        if (isDynamic()) {
            buffer.append(", dynamic");
        }
        if (isAtomic()) {
            buffer.append(", atomic");
        }
        buffer.append(", properties = ");
        final PropertyDescriptor[] jpds = getPropertyDescriptors();
        for (final PropertyDescriptor jpd : jpds) {
            buffer.append("\n    ");
            buffer.append(jpd.getPropertyType());
            buffer.append(": ");
            buffer.append(jpd.getName());
        }
        buffer.append("]");
        return buffer.toString();
    }
}
