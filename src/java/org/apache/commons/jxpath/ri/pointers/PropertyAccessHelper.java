/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/pointers/Attic/PropertyAccessHelper.java,v 1.3 2002/04/10 03:40:20 dmitri Exp $
 * $Revision: 1.3 $
 * $Date: 2002/04/10 03:40:20 $
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
package org.apache.commons.jxpath.ri.pointers;

import org.apache.commons.jxpath.*;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.functions.Types;

import java.lang.reflect.*;
import java.util.*;
import java.beans.*;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.3 $ $Date: 2002/04/10 03:40:20 $
 */
public class PropertyAccessHelper {
    private static Map dynamicPropertyHandlerMap = new HashMap();

    public static boolean isCollection(PropertyDescriptor propertyDescriptor){
        return false;
    }

    public static boolean isCollection(PropertyDescriptor propertyDescriptor, Object value){
        return isCollection(value);
    }

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

    public static int getLength(Object bean, PropertyDescriptor propertyDescriptor){
        Object obj = getValue(bean, propertyDescriptor);
        return getLength(obj);
    }

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

    public static Object expandCollection(Object collection, int size){
        if (collection == null){
            return null;
        }
        else if (collection.getClass().isArray()){
            Object bigger = Array.newInstance(collection.getClass().getComponentType(), size);
            System.arraycopy(collection, 0, bigger, 0, Array.getLength(collection));
            return bigger;
        }
        else if (collection instanceof Collection){
            while (((Collection)collection).size() < size){
                ((Collection)collection).add(null);
            }
            return collection;
        }
        else {
            throw new RuntimeException("Cannot turn " + collection.getClass().getName() +
                    " into a collection of size " + size);
        }
    }

    public static Object getValue(Object bean, PropertyDescriptor propertyDescriptor, int index){
        if (propertyDescriptor instanceof IndexedPropertyDescriptor){
            Object value;
            try {
                IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)propertyDescriptor;
                Method method = ipd.getIndexedReadMethod();
                if (method != null){
                    return method.invoke(bean, new Object[]{new Integer(index)});
                }
            }
            catch (Exception ex){
                throw new RuntimeException("Cannot access property: " + propertyDescriptor.getName() +
                    ", " + ex.getMessage());
            }
        }
        // We will fall through if there is no indexed read

        return getValue(getValue(bean, propertyDescriptor), index);
    }

    public static void setValue(Object bean, PropertyDescriptor propertyDescriptor, int index, Object value){
        if (propertyDescriptor instanceof IndexedPropertyDescriptor){
            try {
                IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)propertyDescriptor;
                Method method = ipd.getIndexedWriteMethod();
                if (method != null){
                    method.invoke(bean,
                        new Object[]{new Integer(index),
                                     convert(value, ipd.getIndexedPropertyType())});
                    return;
                }
            }
            catch (Exception ex){
                throw new RuntimeException("Cannot access property: " + propertyDescriptor.getName() +
                    ", " + ex.getMessage());
            }
        }
        // We will fall through if there is no indexed read
        setValue(getValue(bean, propertyDescriptor), index, value);
    }

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

    public static void setValue(Object collection, int index, Object value){
        if (collection != null){
            if (collection.getClass().isArray()){
                Array.set(collection, index, convert(value, collection.getClass().getComponentType()));
            }
            else if (collection instanceof List){
                ((List)collection).set(index, value);
            }
            else if (collection instanceof Collection){
                throw new UnsupportedOperationException("Cannot set value of an element of a " +
                        collection.getClass().getName());
            }
        }
    }

    public static Object getValue(Object bean, PropertyDescriptor propertyDescriptor){
        Object value;
        try {
            Method method = getAccessibleMethod(propertyDescriptor.getReadMethod());
            if (method == null){
                throw new RuntimeException("No read method");
            }
            value = method.invoke(bean, new Object[0]);
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException("Cannot access property: " + propertyDescriptor.getName() +
                ", " + ex.getMessage());
        }
        return value;
    }

    public static void setValue(Object bean, PropertyDescriptor propertyDescriptor, Object value){
        try {
            Method method = getAccessibleMethod(propertyDescriptor.getWriteMethod());
            if (method == null){
                throw new RuntimeException("No write method");
            }
            value = convert(value, propertyDescriptor.getPropertyType());
            value = method.invoke(bean, new Object[]{value});
        }
        catch (Exception ex){
            throw new RuntimeException("Cannot modify property: " + propertyDescriptor.getName() +
                ", " + ex);
        }
    }

    private static Object convert(Object value, Class type){
        if (!Types.canConvert(value, type)){
            throw new RuntimeException("Cannot convert value of class " +
                    (value == null ? "null" : value.getClass().getName()) +
                    " to type " + type);
        }
        return Types.convert(value, type);
    }

    /**
     * Returns a shared instance of the dynamic property handler class
     * returned by <code>getDynamicPropertyHandlerClass()</code>.
     */
    public static DynamicPropertyHandler getDynamicPropertyHandler(Class clazz) {
        DynamicPropertyHandler handler = (DynamicPropertyHandler)dynamicPropertyHandlerMap.get(clazz);
        if (handler == null){
            try {
                handler = (DynamicPropertyHandler)clazz.newInstance();
            }
            catch (Exception ex){
                throw new RuntimeException("Cannot allocate dynamic property handler " +
                    " of class " + clazz + ".\n" + ex);
            }
            dynamicPropertyHandlerMap.put(clazz, handler);
        }
        return handler;
    }

    // -------------------------------------------------------- Private Methods
    //
    //  The rest of the code in this file was copied FROM
    //  org.apache.commons.beanutils.PropertyUtil. We don't want to introduce a dependency
    //  on BeanUtils yet - DP.
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
        String methodName = method.getName();
        Class[] parameterTypes = method.getParameterTypes();
        method =
            getAccessibleMethodFromInterfaceNest(clazz,
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