/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/JXPathBasicBeanInfo.java,v 1.5 2003/01/11 05:41:22 dmitri Exp $
 * $Revision: 1.5 $
 * $Date: 2003/01/11 05:41:22 $
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
package org.apache.commons.jxpath;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Comparator;

/**
 * An implementation of JXPathBeanInfo based on JavaBeans' BeanInfo. Properties
 * advertised by JXPathBasicBeanInfo are the same as those advertised by
 * BeanInfo for the corresponding class.
 *
 * See java.beans.BeanInfo, java.beans.Introspector
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2003/01/11 05:41:22 $
 */
public class JXPathBasicBeanInfo implements JXPathBeanInfo {
    private boolean atomic = false;
    private Class clazz;
    private PropertyDescriptor propertyDescriptors[];
    private String[] propertyNames;
    private Class dynamicPropertyHandlerClass;

    public JXPathBasicBeanInfo(Class clazz) {
        this.clazz = clazz;
    }

    public JXPathBasicBeanInfo(Class clazz, boolean atomic) {
        this.clazz = clazz;
        this.atomic = atomic;
    }

    public JXPathBasicBeanInfo(Class clazz, Class dynamicPropertyHandlerClass) {
        this.clazz = clazz;
        this.atomic = false;
        this.dynamicPropertyHandlerClass = dynamicPropertyHandlerClass;
    }

    /**
     * Returns true if objects of this class are treated as atomic
     * objects which have no properties of their own.
     */
    public boolean isAtomic() {
        return atomic;
    }

    /**
     * Return true if the corresponding objects have dynamic properties.
     */
    public boolean isDynamic() {
        return dynamicPropertyHandlerClass != null;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        if (propertyDescriptors == null) {
            try {
                BeanInfo bi = null;
                if (clazz.isInterface()) {
                    bi = Introspector.getBeanInfo(clazz);
                }
                else {
                    bi = Introspector.getBeanInfo(clazz, Object.class);
                }
                PropertyDescriptor[] pds = bi.getPropertyDescriptors();
                propertyDescriptors = new PropertyDescriptor[pds.length];
                System.arraycopy(pds, 0, propertyDescriptors, 0, pds.length);
                Arrays.sort(propertyDescriptors, new Comparator() {
                    public int compare(Object left, Object right) {
                        return ((PropertyDescriptor) left).getName().compareTo(
                            ((PropertyDescriptor) right).getName());
                    }
                });
            }
            catch (IntrospectionException ex) {
                ex.printStackTrace();
            }
        }
        return propertyDescriptors;
    }

    public PropertyDescriptor getPropertyDescriptor(String propertyName) {
        if (propertyNames == null) {
            PropertyDescriptor[] pds = getPropertyDescriptors();
            propertyNames = new String[pds.length];
            for (int i = 0; i < pds.length; i++) {
                propertyNames[i] = pds[i].getName();
            }
        }

        for (int i = 0; i < propertyNames.length; i++) {
            if (propertyNames[i] == propertyName) {
                return propertyDescriptors[i];
            }
        }

        for (int i = 0; i < propertyNames.length; i++) {
            if (propertyNames[i].equals(propertyName)) {
                return propertyDescriptors[i];
            }
        }
        return null;
    }

    /**
     * For  a dynamic class, returns the corresponding DynamicPropertyHandler
     * class.
     */
    public Class getDynamicPropertyHandlerClass() {
        return dynamicPropertyHandlerClass;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("BeanInfo [class = ");
        buffer.append(clazz.getName());
        buffer.append(", properties = ");
        PropertyDescriptor[] jpds = getPropertyDescriptors();
        for (int i = 0; i < jpds.length; i++) {
            buffer.append("\n    ");
            buffer.append(jpds[i].getPropertyType());
            buffer.append(": ");
            buffer.append(jpds[i].getName());
        }
        buffer.append("]");
        return buffer.toString();
    }
}