/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/pointers/Attic/BeanPropertyPointer.java,v 1.4 2002/04/10 03:40:20 dmitri Exp $
 * $Revision: 1.4 $
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

import java.lang.reflect.*;
import java.util.*;
import java.beans.*;

/**
 * Pointer pointing to a property of a JavaBean.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.4 $ $Date: 2002/04/10 03:40:20 $
 */
public class BeanPropertyPointer extends PropertyPointer {
    private String propertyName;
    private JXPathBeanInfo beanInfo;
    private PropertyDescriptor propertyDescriptors[];
    private PropertyDescriptor propertyDescriptor;
    private String[] names;

    public BeanPropertyPointer(NodePointer parent, JXPathBeanInfo beanInfo){
        super(parent);
        this.beanInfo = beanInfo;
    }

    /**
     * Number of the bean's properties.
     */
    public int getPropertyCount(){
        return getPropertyDescriptors().length;
    }

    /**
     * Names of all properties, sorted alphabetically
     */
    public String[] getPropertyNames(){
        if (names == null){
            PropertyDescriptor pds[] = getPropertyDescriptors();
            names = new String[pds.length];
            for (int i = 0; i < names.length; i++){
                names[i] = pds[i].getName();
            }
        }
        return names;
    }

    /**
     * Select a property by name
     */
    public void setPropertyName(String propertyName){
        this.propertyName = propertyName;
        setPropertyIndex(UNSPECIFIED_PROPERTY);
        String[] names = getPropertyNames();
        for (int i = 0; i < names.length; i++){
            if (names[i].equals(propertyName)){
                propertyIndex = i;
                break;
            }
        }
    }

    /**
     * Selects a property by its offset in the alphabetically sorted list.
     */
    public void setPropertyIndex(int index){
        if (propertyIndex != index){
            super.setPropertyIndex(index);
            propertyDescriptor = null;
        }
    }

    /**
     * If the property contains a collection, then the length of that
     * collection, otherwise - 1.
     */
    public int getLength(){
        PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null){
            return 0;
        }
        return PropertyAccessHelper.getLength(getBean(), pd);
    }

    /**
     * The value of the currently selected property.
     */
    public Object getBaseValue(){
        PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null){
            return null;
        }
        return PropertyAccessHelper.getValue(getBean(), pd);
    }

    /**
     * If index == WHOLE_COLLECTION, the value of the property, otherwise
     * the value of the index'th element of the collection represented by the
     * property. If the property is not a collection, index should be zero
     * and the value will be the property itself.
     */
    public Object getValue(){
        Object value;
        PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null){
            value = null;
        }
        else {
            if (index == WHOLE_COLLECTION){
                value = PropertyAccessHelper.getValue(getBean(), pd);
            }
            else {
                value = PropertyAccessHelper.getValue(getBean(), pd, index);
            }
        }
        return value;
    }

    protected boolean isActualProperty(){
        return getPropertyDescriptor() != null;
    }

    /**
     * If index == WHOLE_COLLECTION, change the value of the property, otherwise
     * change the value of the index'th element of the collection
     * represented by the property.
     */
    public void setValue(Object value){
        PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null){
            throw new RuntimeException("Cannot set property: " + asPath() + " - no such property");
        }

        if (index == WHOLE_COLLECTION){
            PropertyAccessHelper.setValue(getBean(), pd, value);
        }
        else {
            PropertyAccessHelper.setValue(getBean(), pd, index, value);
        }
    }

    public NodePointer createPath(JXPathContext context){
        if (getValue() == null){
            AbstractFactory factory = getAbstractFactory(context);
            int inx = (index == WHOLE_COLLECTION ? 0 : index);
            if (!factory.createObject(context, this, getBean(), getPropertyName(), inx)){
                throw new RuntimeException("Factory could not create an object for path: " + asPath());
            }
        }
        return this;
    }

    public NodePointer createPath(JXPathContext context, int index){
        setIndexExpandingCollection(context, index);
        return createPath(context);
    }

    public void createPath(JXPathContext context, int index, Object value){
        setIndexExpandingCollection(context, index);
        setValue(value);
    }

    private void setIndexExpandingCollection(JXPathContext context, int index){
        PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null){
            throw new RuntimeException("Cannot create path: " + asPath() +
                    " - property '" + getPropertyName() + "' does not exist");
        }

        if (index < 0){
            throw new RuntimeException("Index is less than 1: " + asPath());
        }

        if (index >= getLength()){
            AbstractFactory factory = getAbstractFactory(context);
            if (!factory.expandCollection(context, this, getBean(), getPropertyName(), index + 1)){
                throw new RuntimeException("Factory could not expand collection for path " + asPath() +
                    " to size " + (index + 1));
            }
        }
        setIndex(index);
    }

    /**
     * Name of the currently selected property.
     */
    public String getPropertyName(){
        PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null){
            return propertyName != null ? propertyName : "*";
        }
        return pd.getName();
    }

    /**
     * Finds the property descriptor corresponding to the current property index.
     */
    private PropertyDescriptor getPropertyDescriptor(){
        if (propertyDescriptor == null){
            int inx = getPropertyIndex();
            PropertyDescriptor propertyDescriptors[] = getPropertyDescriptors();
            if (inx >=0 && inx < propertyDescriptors.length){
                propertyDescriptor = propertyDescriptors[inx];
            }
            else {
                propertyDescriptor = null;
            }
        }
        return propertyDescriptor;
    }

    protected PropertyDescriptor[] getPropertyDescriptors(){
        if (propertyDescriptors == null){
            propertyDescriptors = beanInfo.getPropertyDescriptors();
        }
        return propertyDescriptors;
    }
}