/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/util/ValueUtils.java,v 1.10 2002/11/26 01:20:07 dmitri Exp $
 * $Revision: 1.10 $
 * $Date: 2002/11/26 01:20:07 $
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Plotnix, Inc,
 * <http://www.plotnix.com/>.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.jxpath.util;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import org.apache.commons.jxpath.DynamicPropertyHandler;
import org.apache.commons.jxpath.JXPathException;

/**
 * Collection and property access utilities.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.10 $ $Date: 2002/11/26 01:20:07 $
 */
public class ValueUtils {
    private static Map dynamicPropertyHandlerMap = new HashMap();

    /**
     * Returns true if the object is an array or a Collection
     */
    public static boolean isCollection(Object value){
        if (value == null){
            return false;
        }
        else if (value.getClass().isArray()){
            return true;
        }
        else if (value instanceof Collection){
            return true;
        }
        return false;
    }
    
    /**
     * Returns 1 if the type is a collection, 
     * -1 if it is definitely not
     * and 0 if it may be a collection in some cases.
     */
    public static int getCollectionHint(Class clazz){
        if (clazz.isArray()){
            return 1;
        }
        
        if (Collection.class.isAssignableFrom(clazz)){
            return 1;
        }
        
        if (clazz.isPrimitive()){
            return -1;
        }
        
        if (clazz.isInterface()){
            return 0;
        }
        
        if (Modifier.isFinal(clazz.getModifiers())){
            return -1;
        }
                
        return 0;
    }

    /**
     * Returns the length of the supplied collection. If the supplied object
     * is not a collection, returns 1. If collection is null, returns 0.
     */
    public static int getLength(Object collection){
        if (collection == null){
            return 0;
        }
        else if (collection.getClass().isArray()){
            return Array.getLength(collection);
        }
        else if (collection instanceof Collection){
            return ((Collection)collection).size();
        }
        else {
            return 1;
        }
    }

    /**
     * Returns an iterator for the supplied collection. If the argument
     * is null, returns an empty iterator. If the argument is not
     * a collection, returns an iterator that produces just that one object.
     */
    public static Iterator iterate(Object collection){
        if (collection == null){
            return Collections.EMPTY_LIST.iterator();
        }
        if (collection.getClass().isArray()){
            int length = Array.getLength(collection);
            if (length == 0){
                return Collections.EMPTY_LIST.iterator();
            }
            ArrayList list = new ArrayList();
            for (int i = 0; i < length; i++){
                list.add(Array.get(collection, i));
            }
            return list.iterator();
        }
        else if (collection instanceof Collection){
            return ((Collection)collection).iterator();
        }
        else {
            return Collections.singletonList(collection).iterator();
        }
    }

    /**
     * Grows the collection if necessary to the specified size. Returns
     * the new, expanded collection.
     */
    public static Object expandCollection(Object collection, int size){
        if (collection == null){
            return null;
        }
        else if (collection.getClass().isArray()){
            Object bigger = Array.newInstance(
                  collection.getClass().getComponentType(), size);
            System.arraycopy(
                  collection, 0, bigger, 0, Array.getLength(collection));
            return bigger;
        }
        else if (collection instanceof Collection){
            while (((Collection)collection).size() < size){
                ((Collection)collection).add(null);
            }
            return collection;
        }
        else {
            throw new JXPathException("Cannot turn " + collection.getClass().getName() +
                    " into a collection of size " + size);
        }
    }

    /**
     * Returns the index'th element from the supplied collection.
     */
    public static Object remove(Object collection, int index){
        if (collection == null){
            return null;
        }
        else if (collection.getClass().isArray()){
            int length = Array.getLength(collection);
            Object smaller = Array.newInstance(
                  collection.getClass().getComponentType(), length-1);
            if (index > 0){
                System.arraycopy(collection, 0, smaller, 0, index);
            }
            if (index < length - 1){
                System.arraycopy(collection, index + 1, smaller,
                        index, length - index - 1);
            }
            return smaller;
        }
        else if (collection instanceof List){
            int size = ((List)collection).size();
            if (index < size){
                ((List)collection).remove(index);
            }
            return collection;
        }
        else if (collection instanceof Collection){
            Iterator it = ((Collection)collection).iterator();
            for (int i = 0; i < index; i++){
                if (!it.hasNext()){
                    break;
                }
                it.next();
            }
            if (it.hasNext()){
                it.next();
                it.remove();
            }
            return collection;
        }
        else {
            throw new JXPathException("Cannot remove " +
                    collection.getClass().getName() + "[" + index + "]");
        }
    }

    /**
     * Returns the index'th element of the supplied collection.
     */
    public static Object getValue(Object collection, int index){
        Object value = collection;
        if (collection != null){
            if (collection.getClass().isArray()){
                if (index < 0 || index >= Array.getLength(collection)){
                    return null;
                }
                value = Array.get(collection, index);
            }
            else if (collection instanceof List){
                if (index < 0 || index >= ((List)collection).size()){
                    return null;
                }
                value = ((List)collection).get(index);
            }
            else if (collection instanceof Collection){
                int i = 0;
                Iterator it = ((Collection)collection).iterator();
                for (; i < index; i++){
                    it.next();
                }
                if (it.hasNext()){
                    value = it.next();
                }
                else {
                    value = null;
                }
            }
        }
        return value;
    }

    /**
     * Modifies the index'th element of the supplied collection.
     * Converts the value to the required type if necessary.
     */
    public static void setValue(Object collection, int index, Object value){
        if (collection != null){
            if (collection.getClass().isArray()){
                Array.set(collection, index,
                    convert(value, collection.getClass().getComponentType()));
            }
            else if (collection instanceof List){
                ((List)collection).set(index, value);
            }
            else if (collection instanceof Collection){
                throw new UnsupportedOperationException(
                        "Cannot set value of an element of a " +
                        collection.getClass().getName());
            }
        }
    }

    /**
     * Returns the value of the bean's property represented by
     * the supplied property descriptor.
     */
    public static Object getValue(Object bean,
                    PropertyDescriptor propertyDescriptor){
        Object value;
        try {
            Method method =
                getAccessibleMethod(propertyDescriptor.getReadMethod());
            if (method == null){
                throw new JXPathException("No read method");
            }
            value = method.invoke(bean, new Object[0]);
        }
        catch (Exception ex){
            throw new JXPathException(
                "Cannot access property: " +
                    (bean == null ? "null" : bean.getClass().getName()) +
                    "." + propertyDescriptor.getName(), ex);
        }
        return value;
    }

    /**
     * Modifies the value of the bean's property represented by
     * the supplied property descriptor.
     */
    public static void setValue(Object bean,
            PropertyDescriptor propertyDescriptor, Object value){
        try {
            Method method =
                getAccessibleMethod(propertyDescriptor.getWriteMethod());
            if (method == null){
                throw new JXPathException("No write method");
            }
            value = convert(value, propertyDescriptor.getPropertyType());
            value = method.invoke(bean, new Object[]{value});
        }
        catch (Exception ex){
            throw new JXPathException(
                "Cannot modify property: " +
                    (bean == null ? "null" : bean.getClass().getName()) +
                    "." + propertyDescriptor.getName(), ex);
        }
    }

    private static Object convert(Object value, Class type){
        try {
            return TypeUtils.convert(value, type);
        }
        catch (Exception ex){
            throw new JXPathException("Cannot convert value of class " +
                    (value == null ? "null" : value.getClass().getName()) +
                    " to type " + type, ex);
        }
    }

    /**
     * Returns the index'th element of the bean's property represented by
     * the supplied property descriptor.
     */
    public static Object getValue(Object bean,
            PropertyDescriptor propertyDescriptor, int index){
        if (propertyDescriptor instanceof IndexedPropertyDescriptor){
            try {
                IndexedPropertyDescriptor ipd =
                    (IndexedPropertyDescriptor)propertyDescriptor;
                Method method = ipd.getIndexedReadMethod();
                if (method != null){
                    return method.invoke(bean, new Object[]{new Integer(index)});
                }
            }
            catch (Exception ex){
                throw new JXPathException(
                    "Cannot access property: " + propertyDescriptor.getName(),
                    ex);
            }
        }

        // We will fall through if there is no indexed read

        return getValue(getValue(bean, propertyDescriptor), index);
    }

    /**
     * Modifies the index'th element of the bean's property represented by
     * the supplied property descriptor. Converts the value to the required
     * type if necessary.
     */
    public static void setValue(Object bean,
              PropertyDescriptor propertyDescriptor, int index, Object value){
        if (propertyDescriptor instanceof IndexedPropertyDescriptor){
            try {
                IndexedPropertyDescriptor ipd =
                      (IndexedPropertyDescriptor)propertyDescriptor;
                Method method = ipd.getIndexedWriteMethod();
                if (method != null){
                    method.invoke(bean,
                        new Object[]{new Integer(index),
                                     convert(value, ipd.getIndexedPropertyType())});
                    return;
                }
            }
            catch (Exception ex){
                throw new RuntimeException("Cannot access property: " +
                    propertyDescriptor.getName() + ", " + ex.getMessage());
            }
        }
        // We will fall through if there is no indexed read
        Object collection = getValue(bean, propertyDescriptor);
        if (isCollection(collection)){
            setValue(collection, index, value);
        }
        else if (index == 0){
            setValue(bean, propertyDescriptor, value);
        }
        else {
            throw new RuntimeException("Not a collection: " + propertyDescriptor.getName());
        }
    }

    /**
     * Returns a shared instance of the dynamic property handler class
     * returned by <code>getDynamicPropertyHandlerClass()</code>.
     */
    public static DynamicPropertyHandler getDynamicPropertyHandler(Class clazz){
        DynamicPropertyHandler handler =
            (DynamicPropertyHandler)dynamicPropertyHandlerMap.get(clazz);
        if (handler == null){
            try {
                handler = (DynamicPropertyHandler)clazz.newInstance();
            }
            catch (Exception ex){
                throw new JXPathException(
                    "Cannot allocate dynamic property handler of class "
                    + clazz.getName(), ex);
            }
            dynamicPropertyHandlerMap.put(clazz, handler);
        }
        return handler;
    }

    // -------------------------------------------------------- Private Methods
    //
    //  The rest of the code in this file was copied FROM
    //  org.apache.commons.beanutils.PropertyUtil. We don't want to introduce
    //  a dependency on BeanUtils yet - DP.
    //

    /**
     * Return an accessible method (that is, one that can be invoked via
     * reflection) that implements the specified Method.  If no such method
     * can be found, return <code>null</code>.
     *
     * @param method The method that we wish to call
     */
    private static Method getAccessibleMethod(Method method) {

        // Make sure we have a method to check
        if (method == null) {
            return (null);
        }

        // If the requested method is not public we cannot call it
        if (!Modifier.isPublic(method.getModifiers())) {
            return (null);
        }

        // If the declaring class is public, we are done
        Class clazz = method.getDeclaringClass();
        if (Modifier.isPublic(clazz.getModifiers())) {
            return (method);
        }

        // Check the implemented interfaces and subinterfaces
		method =
			getAccessibleMethodFromInterfaceNest(
				clazz,
				method.getName(),
				method.getParameterTypes());
        return (method);
    }


    /**
     * Return an accessible method (that is, one that can be invoked via
     * reflection) that implements the specified method, by scanning through
     * all implemented interfaces and subinterfaces.  If no such Method
     * can be found, return <code>null</code>.
     *
     * @param clazz Parent class for the interfaces to be checked
     * @param methodName Method name of the method we wish to call
     * @param parameterTypes The parameter type signatures
     */
    private static Method getAccessibleMethodFromInterfaceNest
        (Class clazz, String methodName, Class parameterTypes[]) {

        Method method = null;

        // Check the implemented interfaces of the parent class
        Class interfaces[] = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {

            // Is this interface public?
            if (!Modifier.isPublic(interfaces[i].getModifiers()))
                continue;

            // Does the method exist on this interface?
            try {
                method = interfaces[i].getDeclaredMethod(methodName,
                                                         parameterTypes);
            } catch (NoSuchMethodException e) {
                ;
            }
            if (method != null)
                break;

            // Recursively check our parent interfaces
            method =
                getAccessibleMethodFromInterfaceNest(interfaces[i],
                                                     methodName,
                                                     parameterTypes);
            if (method != null)
                break;

        }

        // Return whatever we have found
        return (method);
    }
}