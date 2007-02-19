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
package org.apache.commons.jxpath.util;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.JXPathTypeConversionException;
import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.Pointer;

/**
 * The default implementation of TypeConverter.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class BasicTypeConverter implements TypeConverter {

    /**
     * Returns true if it can convert the supplied
     * object to the specified class.
     */
    public boolean canConvert(Object object, Class toType) {
        if (object == null) {
            return true;
        }

        if (toType == Object.class) {
            return true;
        }

        Class fromType = object.getClass();
        if (fromType.equals(toType)) {
            return true;
        }

        if (toType.isAssignableFrom(fromType)) {
            return true;
        }

        if (toType == String.class) {
            return true;
        }

        if (object instanceof Boolean) {
            if (toType == boolean.class
                || Number.class.isAssignableFrom(toType)) {
                return true;
            }
        }
        if (object instanceof Number) {
            if (toType.isPrimitive()
                || Number.class.isAssignableFrom(toType)) {
                return true;
            }
        }
        if (object instanceof Character) {
            if (toType == char.class) {
                return true;
            }
        }
        if (object instanceof String) {
            if (toType.isPrimitive()) {
                return true;
            }
            if (toType == Boolean.class
                || toType == Character.class
                || toType == Byte.class
                || toType == Short.class
                || toType == Integer.class
                || toType == Long.class
                || toType == Float.class
                || toType == Double.class) {
                return true;
            }
        }
        if (fromType.isArray()) {
            // Collection -> array
            if (toType.isArray()) {
                Class cType = toType.getComponentType();
                int length = Array.getLength(object);
                for (int i = 0; i < length; i++) {
                    Object value = Array.get(object, i);
                    if (!canConvert(value, cType)) {
                        return false;
                    }
                }
                return true;
            }
            if (Collection.class.isAssignableFrom(toType)) {
                return canCreateCollection(toType);
            }
            if (Array.getLength(object) > 0) {
                Object value = Array.get(object, 0);
                return canConvert(value, toType);
            }
            return canConvert("", toType);
        }
        if (object instanceof Collection) {
            // Collection -> array
            if (toType.isArray()) {
                Class cType = toType.getComponentType();
                Iterator it = ((Collection) object).iterator();
                while (it.hasNext()) {
                    Object value = it.next();
                    if (!canConvert(value, cType)) {
                        return false;
                    }
                }
                return true;
            }
            if (Collection.class.isAssignableFrom(toType)) {
                return canCreateCollection(toType);
            }
            if (((Collection) object).size() > 0) {
                Object value;
                if (object instanceof List) {
                    value = ((List) object).get(0);
                }
                else {
                    Iterator it = ((Collection) object).iterator();
                    value = it.next();
                }
                return canConvert(value, toType);
            }
            return canConvert("", toType);
        }
        if (object instanceof NodeSet) {
            return canConvert(((NodeSet) object).getValues(), toType);
        }
        if (object instanceof Pointer) {
            return canConvert(((Pointer) object).getValue(), toType);
        }
        return ConvertUtils.lookup(toType) != null;
    }

    /**
     * Converts the supplied object to the specified
     * type. Throws a runtime exception if the conversion is
     * not possible.
     */
    public Object convert(Object object, Class toType) {
        if (object == null) {
            return toType.isPrimitive() ? convertNullToPrimitive(toType) : null;
        }

        if (toType == Object.class) {
            if (object instanceof NodeSet) {
                return convert(((NodeSet) object).getValues(), toType);
            }
            if (object instanceof Pointer) {
                return convert(((Pointer) object).getValue(), toType);
            }
            return object;
        }

        Class fromType = object.getClass();
        if (fromType.equals(toType) || toType.isAssignableFrom(fromType)) {
            return object;
        }

        if (fromType.isArray()) {
            int length = Array.getLength(object);
            if (toType.isArray()) {
                Class cType = toType.getComponentType();

                Object array = Array.newInstance(cType, length);
                for (int i = 0; i < length; i++) {
                    Object value = Array.get(object, i);
                    Array.set(array, i, convert(value, cType));
                }
                return array;
            }
            if (Collection.class.isAssignableFrom(toType)) {
                Collection collection = allocateCollection(toType);
                for (int i = 0; i < length; i++) {
                    collection.add(Array.get(object, i));
                }
                return unmodifiableCollection(collection);
            }
            if (length > 0) { 
                Object value = Array.get(object, 0);
                return convert(value, toType);
            }
            return convert("", toType);
        }
        if (object instanceof Collection) {
            int length = ((Collection) object).size();
            if (toType.isArray()) {
                Class cType = toType.getComponentType();
                Object array = Array.newInstance(cType, length);
                Iterator it = ((Collection) object).iterator();
                for (int i = 0; i < length; i++) {
                    Object value = it.next();
                    Array.set(array, i, convert(value, cType));
                }
                return array;
            }
            if (Collection.class.isAssignableFrom(toType)) {
                Collection collection = allocateCollection(toType);
                collection.addAll((Collection) object);
                return unmodifiableCollection(collection);
            }
            if (length > 0) {
                Object value;
                if (object instanceof List) {
                    value = ((List) object).get(0);
                }
                else {
                    Iterator it = ((Collection) object).iterator();
                    value = it.next();
                }
                return convert(value, toType);
            }
            return convert("", toType);
        }
        if (object instanceof NodeSet) {
            return convert(((NodeSet) object).getValues(), toType);
        }
        if (object instanceof Pointer) {
            return convert(((Pointer) object).getValue(), toType);
        }
        if (toType == String.class) {
            return object.toString();
        }
        if (object instanceof Boolean) {
            if (toType == boolean.class) {
                return object;
            }
            if (toType.isPrimitive() || Number.class.isAssignableFrom(toType)) {
                boolean value = ((Boolean) object).booleanValue();
                return allocateNumber(toType, value ? 1 : 0);
            }
        }
        if (object instanceof Number) {
            double value = ((Number) object).doubleValue();
            if (toType == boolean.class || toType == Boolean.class) {
                return value == 0.0 ? Boolean.FALSE : Boolean.TRUE;
            }
            if (toType.isPrimitive() || Number.class.isAssignableFrom(toType)) {
                return allocateNumber(toType, value);
            }
        }
        if (object instanceof Character) {
            if (toType == char.class) {
                return object;
            }
        }
        if (object instanceof String) {
            Object value = convertStringToPrimitive(object, toType);
            if (value != null) {
                return value;
            }
        }

        Converter converter = ConvertUtils.lookup(toType);
        if (converter != null) {
            return converter.convert(toType, object);
        }

        throw new JXPathTypeConversionException("Cannot convert "
                + object.getClass() + " to " + toType);
    }

    protected Object convertNullToPrimitive(Class toType) {
        if (toType == boolean.class) {
            return Boolean.FALSE;
        }
        if (toType == char.class) {
            return new Character('\0');
        }
        if (toType == byte.class) {
            return new Byte((byte) 0);
        }
        if (toType == short.class) {
            return new Short((short) 0);
        }
        if (toType == int.class) {
            return new Integer(0);
        }
        if (toType == long.class) {
            return new Long(0L);
        }
        if (toType == float.class) {
            return new Float(0.0f);
        }
        if (toType == double.class) {
            return new Double(0.0);
        }
        return null;
    }

    protected Object convertStringToPrimitive(Object object, Class toType) {
        if (toType == boolean.class || toType == Boolean.class) {
            return Boolean.valueOf((String) object);
        }
        if (toType == char.class || toType == Character.class) {
            return new Character(((String) object).charAt(0));
        }
        if (toType == byte.class || toType == Byte.class) {
            return new Byte((String) object);
        }
        if (toType == short.class || toType == Short.class) {
            return new Short((String) object);
        }
        if (toType == int.class || toType == Integer.class) {
            return new Integer((String) object);
        }
        if (toType == long.class || toType == Long.class) {
            return new Long((String) object);
        }
        if (toType == float.class || toType == Float.class) {
            return new Float((String) object);
        }
        if (toType == double.class || toType == Double.class) {
            return new Double((String) object);
        }
        return null;
    }
    
    protected Number allocateNumber(Class type, double value) {
        if (type == Byte.class || type == byte.class) {
            return new Byte((byte) value);
        }
        if (type == Short.class || type == short.class) {
            return new Short((short) value);
        }
        if (type == Integer.class || type == int.class) {
            return new Integer((int) value);
        }
        if (type == Long.class || type == long.class) {
            return new Long((long) value);
        }
        if (type == Float.class || type == float.class) {
            return new Float((float) value);
        }
        if (type == Double.class || type == double.class) {
            return new Double(value);
        }
        return null;
    }

    protected boolean canCreateCollection(Class type) {
        if (!type.isInterface()
            && ((type.getModifiers() & Modifier.ABSTRACT) == 0)) {
            return true;
        }

        if (type == List.class) {
            return true;
        }

        if (type == Set.class) {
            return true;
        }
        return false;
    }

    protected Collection allocateCollection(Class type) {
        if (!type.isInterface()
            && ((type.getModifiers() & Modifier.ABSTRACT) == 0)) {
            try {
                return (Collection) type.newInstance();
            }
            catch (Exception ex) {
                throw new JXPathInvalidAccessException(
                        "Cannot create collection of type: " + type, ex);
            }
        }

        if (type == List.class || type == Collection.class) {
            return new ArrayList();
        }
        if (type == Set.class) {
            return new HashSet();
        }
        throw new JXPathInvalidAccessException(
                "Cannot create collection of type: " + type);
    }

    protected Collection unmodifiableCollection(Collection collection) {
        if (collection instanceof List) {
            return Collections.unmodifiableList((List) collection);
        }
        if (collection instanceof SortedSet) {
            return Collections.unmodifiableSortedSet((SortedSet) collection);
        }
        if (collection instanceof Set) {
            return Collections.unmodifiableSet((Set) collection);
        }
        return Collections.unmodifiableCollection(collection);
    }

    static final class ValueNodeSet implements NodeSet {
        private List values;
        private List pointers;

        public ValueNodeSet(List values) {
           this.values = values;
        }
        
        public List getValues() {
            return Collections.unmodifiableList(values);
        }
        
        public List getNodes() {
            return Collections.unmodifiableList(values);
        }
        
        public List getPointers() {
            if (pointers == null) {
                pointers = new ArrayList();
                for (int i = 0; i < values.size(); i++) {
                    pointers.add(new ValuePointer(values.get(i)));
                }
                pointers = Collections.unmodifiableList(pointers);
            }
            return pointers;
        }
    }
    
    static final class ValuePointer implements Pointer {
        private Object bean;

        public ValuePointer(Object object) {
            this.bean = object;
        }
        
        public Object getValue() {
            return bean;
        }
        
        public Object getNode() {
            return bean;
        }
        
        public Object getRootNode() {
            return bean;
        }        
        
        public void setValue(Object value) {
            throw new UnsupportedOperationException();
        }
        
        public Object clone() {
            return this;
        }
        
        public int compareTo(Object object) {
            return 0;
        }
        
        public String asPath() {
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
            return "{object of type " + bean.getClass().getName() + "}";
        }
    }
}