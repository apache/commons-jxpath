/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/pointers/Attic/BeanPointer.java,v 1.1 2001/08/23 00:47:00 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/08/23 00:47:00 $
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

import java.lang.reflect.*;
import java.util.*;
import java.beans.*;

/**
 * A Pointer that points to a JavaBean or a collection. It is the first element of
 * a path, following elements will by of type PropertyPointer.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:00 $
 */
public class BeanPointer extends NodePointer {
    private QName name;
    private Object bean;
    private JXPathBeanInfo beanInfo;
    private PropertyDescriptor propertyDescriptors[];
    private String[] names;

    /**
     * @param name is the name given to the first node
     */
    public BeanPointer(QName name, Object bean, JXPathBeanInfo beanInfo){
        super(null);
        this.name = name;
        this.bean = bean;
        this.beanInfo = beanInfo;
    }

    public QName getName(){
        return name;
    }

    /**
     * Returns the bean itself
     */
    public Object getPropertyValue(){
        return bean;
    }

    /**
     * Throws an exception - you cannot change the root element.
     */
    public void setValue(Object value){
        throw new UnsupportedOperationException("Cannot setValue of an object that is not some other object's property");
    }

    /**
     * If the bean is a collection, returns the length of that collection,
     * otherwise returns 1.
     */
    public int getLength(){
        return PropertyAccessHelper.getLength(getPropertyValue());
    }

    public int hashCode(){
        return System.identityHashCode(bean) + name.hashCode();
    }

    public boolean equals(Object object){
        if (object == this){
            return true;
        }

        if (!(object instanceof BeanPointer)){
            return false;
        }

        BeanPointer other = (BeanPointer)object;
        return bean == other.bean &&
                name.equals(other.name);
    }

    /**
     * Empty string
     */
    public String asPath(){
        return "";
    }

    public String toString(){
        return bean.getClass().getName() + "@" + System.identityHashCode(bean) +
            "(" + name + ")";
    }

    public Object clone(){
        BeanPointer pointer = new BeanPointer(name, bean, beanInfo);
        pointer.index = index;
        pointer.value = value;
        pointer.propertyDescriptors = propertyDescriptors;
        pointer.names = names;
        return pointer;
    }
}