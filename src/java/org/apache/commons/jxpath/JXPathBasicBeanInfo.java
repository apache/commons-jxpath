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
package org.apache.commons.jxpath;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of JXPathBeanInfo based on JavaBeans' BeanInfo. Properties
 * advertised by JXPathBasicBeanInfo are the same as those advertised by
 * BeanInfo for the corresponding class.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 * @see java.beans.BeanInfo
 * @see java.beans.Introspector
 */
@SuppressWarnings({"unchecked", "WeakerAccess"})
public class JXPathBasicBeanInfo implements JXPathBeanInfo {

  private static final long serialVersionUID = -3863803443111484155L;

  private static final Comparator PROPERTY_DESCRIPTOR_COMPARATOR =
      Comparator.comparing(left -> ((PropertyDescriptor) left).getName());

  private boolean atomic = false;
  private Class clazz;
  private Class dynamicPropertyHandlerClass;
  private transient volatile PropertyDescriptor[] propertyDescriptors;
  private transient volatile Map propertyDescriptorMap;

  /**
   * Create a new JXPathBasicBeanInfo.
   *
   * @param clazz bean class
   */
  public JXPathBasicBeanInfo(Class clazz) {
    this.clazz = clazz;
    this.propertyDescriptors = descriptors();
  }

  /**
   * Create a new JXPathBasicBeanInfo.
   *
   * @param clazz  bean class
   * @param atomic whether objects of this class are treated as atomic
   *               objects which have no properties of their own.
   */
  public JXPathBasicBeanInfo(Class clazz, boolean atomic) {
    this.clazz = clazz;
    this.atomic = atomic;
    this.propertyDescriptors = descriptors();
  }

  /**
   * Create a new JXPathBasicBeanInfo.
   *
   * @param clazz                       bean class
   * @param dynamicPropertyHandlerClass dynamic property handler class
   */
  public JXPathBasicBeanInfo(Class clazz, Class dynamicPropertyHandlerClass) {
    this.clazz = clazz;
    this.atomic = false;
    this.dynamicPropertyHandlerClass = dynamicPropertyHandlerClass;
    this.propertyDescriptors = descriptors();
  }

  /**
   * Returns true if objects of this class are treated as atomic
   * objects which have no properties of their own.
   *
   * @return boolean
   */
  public boolean isAtomic() {
    return atomic;
  }

  /**
   * Return true if the corresponding objects have dynamic properties.
   *
   * @return boolean
   */
  public boolean isDynamic() {
    return dynamicPropertyHandlerClass != null;
  }

  public PropertyDescriptor[] getPropertyDescriptors() {
    if (this.propertyDescriptors == null) {
      this.propertyDescriptors = descriptors();
    }
    return this.propertyDescriptors;
  }

  public PropertyDescriptor getPropertyDescriptor(String propertyName) {
    if (this.propertyDescriptorMap == null) {
      this.propertyDescriptorMap = descriptorsMap();
    }
    return (PropertyDescriptor) propertyDescriptorMap.get(propertyName);
  }

  /**
   * For a dynamic class, returns the corresponding DynamicPropertyHandler
   * class.
   *
   * @return Class
   */
  public Class getDynamicPropertyHandlerClass() {
    return dynamicPropertyHandlerClass;
  }

  private PropertyDescriptor[] descriptors() {
    if (clazz == Object.class) {
      return new PropertyDescriptor[0];
    } else {
      try {
        BeanInfo bi;
        if (clazz.isInterface()) {
          bi = Introspector.getBeanInfo(clazz);
        } else {
          bi = Introspector.getBeanInfo(clazz, Object.class);
        }
        PropertyDescriptor[] pds = bi.getPropertyDescriptors();
        PropertyDescriptor[] descriptors = new PropertyDescriptor[pds.length];
        System.arraycopy(pds, 0, descriptors, 0, pds.length);
        Arrays.sort(descriptors, PROPERTY_DESCRIPTOR_COMPARATOR);
        return descriptors;
      } catch (IntrospectionException ex) {
        ex.printStackTrace();
      }
    }
    return new PropertyDescriptor[0];
  }

  private Map descriptorsMap() {
    Map propertyDescriptorMap = new HashMap();
    PropertyDescriptor[] pds = getPropertyDescriptors();
    for (PropertyDescriptor pd : pds) {
      propertyDescriptorMap.put(pd.getName(), pd);
    }
    return propertyDescriptorMap;
  }


  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("BeanInfo [class = ");
    buffer.append(clazz.getName());
    if (isDynamic()) {
      buffer.append(", dynamic");
    }
    if (isAtomic()) {
      buffer.append(", atomic");
    }
    buffer.append(", properties = ");
    PropertyDescriptor[] jpds = getPropertyDescriptors();
    for (PropertyDescriptor jpd : jpds) {
      buffer.append("\n    ");
      buffer.append(jpd.getPropertyType());
      buffer.append(": ");
      buffer.append(jpd.getName());
    }
    buffer.append("]");
    return buffer.toString();
  }


}
