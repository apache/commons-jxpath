/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/ValueHandle.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/08/23 00:47:01 $
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
package org.apache.commons.jxpath.tree;

import org.w3c.dom.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * A ValueHandle maintains a named value.  There are two ways the name-value
 * pair can be specified:
 * <ul>
 * <li>Object and its name.
 * <li>A JavaBean and a PropertyDescriptor from that bean's BeanInfo.  In this
 *     case the value logically associated with the node is the value of the
 *     corresponding property of the specified bean.
 * </ul>
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public abstract class ValueHandle
{
    private Object bean;
    private String name;
    private PropertyDescriptor propertyDescriptor;
    private boolean valueRetrieved = false;
    private Object value;

    /**
     * Associates the ValueHandle with a property of a JavaBean.
     */
    public ValueHandle(Object bean, PropertyDescriptor propertyDescriptor){
        this.bean = bean;
        this.propertyDescriptor = propertyDescriptor;
    }

    /**
     * Associates the ValueHandle with an named bean.
     */
    public ValueHandle(Object bean, String name){
        this(bean);
        this.name = name;
    }

    /**
     * Associates the ValueHandle with an unnamed bean.
     */
    public ValueHandle(Object bean){
        this.bean = bean;
        value = bean;
        valueRetrieved = true;
    }

    /**
     * Returns the bean associated with the ValueHandle. If the handle
     * is configured with a PropertyDescriptor, this method returns
     * the parent JavaBean, not a property thereof.
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Returns the PropertyDescriptor this handle is associated with.
     */
    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    /**
     * Returns the name of the value. If the handle was configured
     * with a PropertyDescriptor, the method returns the name of the
     * property.
     */
    public String getValueName() {
        if (propertyDescriptor != null){
            return propertyDescriptor.getName();
        }
        else {
            return name;
        }
    }

    /**
     * Returns the bean this handle is associated with or a property
     * of the bean if a PropertyDescriptor was specified.
     * <p>
     * @throws RuntimeException if the property cannot be accessed
     */
    public Object getValue() {
        if (!valueRetrieved){
            valueRetrieved = true;
            Method method = propertyDescriptor.getReadMethod();
            try {
                if (method == null){
                    throw new RuntimeException("No read method");
                }
                value = method.invoke(bean, null);
            }
            catch (Exception e){
                throw new RuntimeException("Cannot access property " +
                    bean.getClass().getName() + '.' + propertyDescriptor.getName() + "\n" +
                    e.getMessage());
            }
        }
        return value;
    }

    /**
     * Equivalent to getValue(), except that any cached value is ignored.
     */
    public Object getFreshValue() {
        if (propertyDescriptor != null){
            valueRetrieved = false;
        }
        return getValue();
    }

    /**
     * Modifies the property described by the PropertyDescriptor associated with
     * this handle.  Throws a RuntimeException if the handle is not associated
     * with a property descriptor or if the property is not writable.
     */
    public void setValue(Object value){
//        System.err.println("SETTING VALUE: " + bean + "." + propertyDescriptor + " = " + value);
        try {
            if (propertyDescriptor == null){
                throw new RuntimeException("Not a property");
            }

            Method method = propertyDescriptor.getWriteMethod();
            if (method == null){
                throw new RuntimeException("No write method");
            }
            method.invoke(bean, new Object[]{value});
        }
        catch (Exception e){
            throw new RuntimeException("Cannot modify property " +
                bean.getClass().getName() + '.' + propertyDescriptor.getName() + "\n" +
                e.getMessage());
        }
        valueRetrieved = false;
    }
}