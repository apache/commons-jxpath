/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/beans/BeanPropertyPointer.java,v 1.11 2002/11/28 01:02:04 dmitri Exp $
 * $Revision: 1.11 $
 * $Date: 2002/11/28 01:02:04 $
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
package org.apache.commons.jxpath.ri.model.beans;

import java.beans.PropertyDescriptor;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathBeanInfo;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Pointer pointing to a property of a JavaBean.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.11 $ $Date: 2002/11/28 01:02:04 $
 */
public class BeanPropertyPointer extends PropertyPointer {
    private String propertyName;
    private JXPathBeanInfo beanInfo;
    private PropertyDescriptor propertyDescriptors[];
    private PropertyDescriptor propertyDescriptor;
    private String[] names;
    private static final Object UNINITIALIZED = new Object();
    private Object baseValue = UNINITIALIZED;
    private Object value = UNINITIALIZED;

    public BeanPropertyPointer(NodePointer parent, JXPathBeanInfo beanInfo){
        super(parent);
        this.beanInfo = beanInfo;
    }

    /**
     * This type of node is auxiliary.
     */
    public boolean isContainer(){
        return true;
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
        setPropertyIndex(UNSPECIFIED_PROPERTY);
        this.propertyName = propertyName;
    }

    /**
     * Selects a property by its offset in the alphabetically sorted list.
     */
    public void setPropertyIndex(int index){
        if (propertyIndex != index){
            super.setPropertyIndex(index);
            propertyName = null;
            propertyDescriptor = null;
            baseValue = UNINITIALIZED;
            value = UNINITIALIZED;
        }
    }

    /**
     * The value of the currently selected property.
     */
    public Object getBaseValue(){
        if (baseValue == UNINITIALIZED){
            PropertyDescriptor pd = getPropertyDescriptor();
            if (pd == null){
                return null;
            }
            baseValue = ValueUtils.getValue(getBean(), pd);
        }
        return baseValue;
    }

    public void setIndex(int index){
        if (this.index != index){
            // When dealing with a scalar, index == 0 is equivalent to
            // WHOLE_COLLECTION, so do not change it.
            if (this.index != WHOLE_COLLECTION || index != 0 || isCollection()){
                super.setIndex(index);
                value = UNINITIALIZED;
            }
        }
    }

    /**
     * If index == WHOLE_COLLECTION, the value of the property, otherwise
     * the value of the index'th element of the collection represented by the
     * property. If the property is not a collection, index should be zero
     * and the value will be the property itself.
     */
    public Object getImmediateNode(){
        if (value == UNINITIALIZED){
            Object baseValue = getBaseValue();
            if (index == WHOLE_COLLECTION){
                value = baseValue;
            }
            else if (value != null && index >= 0 && index < getLength()){
                value = ValueUtils.getValue(baseValue, index);
            }
            else {
                value = null;
            }
        }
        return value;
    }

    protected boolean isActualProperty(){
        return getPropertyDescriptor() != null;
    }

    public boolean isCollection(){
        PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null){
            return false;
        }
        
        int hint = ValueUtils.getCollectionHint(pd.getPropertyType());
        if (hint == -1){
            return false;
        }
        if (hint == 1){
            return true;
        }
        
        Object value = getBaseValue();
        return value != null && ValueUtils.isCollection(value);
    }
    
    /**
     * If the property contains a collection, then the length of that
     * collection, otherwise - 1.
     */
    public int getLength(){
        PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null){
            return 1;
        }
        
        int hint = ValueUtils.getCollectionHint(pd.getPropertyType());
        if (hint == -1){
            return 1;
        }
        return ValueUtils.getLength(getBaseValue());
    }
    
    /**
     * If index == WHOLE_COLLECTION, change the value of the property, otherwise
     * change the value of the index'th element of the collection
     * represented by the property.
     */
    public void setValue(Object value){
        PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null){
            throw new JXPathException("Cannot set property: " + asPath() +
                    " - no such property");
        }

        if (index == WHOLE_COLLECTION){
            ValueUtils.setValue(getBean(), pd, value);
        }
        else {
            ValueUtils.setValue(getBean(), pd, index, value);
        }
        this.value = value;
    }

    public NodePointer createPath(JXPathContext context){
        if (getNode() == null){
            AbstractFactory factory = getAbstractFactory(context);
            int inx = (index == WHOLE_COLLECTION ? 0 : index);
            if (!factory.createObject(context, this, getBean(),
                    getPropertyName(), inx)){
                throw new JXPathException(
                    "Factory could not create an object for path: " + asPath());
            }
            baseValue = UNINITIALIZED;
            value = UNINITIALIZED;
        }
        return this;
    }

    public NodePointer createChild(JXPathContext context,
            QName name, int index){
        return createPath(context).getValuePointer().
                createChild(context, name, index);
    }

    public NodePointer createChild(JXPathContext context,
            QName name, int index, Object value){
        return createPath(context).getValuePointer().
                createChild(context, name, index, value);
    }

    private BeanPropertyPointer setIndexExpandingCollection(
            JXPathContext context, QName name, int index){
        // Ignore the name passed to us, use our own information
        PropertyDescriptor pd = getPropertyDescriptor();
        if (pd == null){
            throw new JXPathException("Cannot create path: " + asPath() +
                    " - property '" + getPropertyName() + "' does not exist");
        }

        if (index < 0){
            throw new JXPathException("Index is less than 1: " + asPath());
        }

        if (index >= getLength()){
            AbstractFactory factory = getAbstractFactory(context);
            if (!factory.createObject(context, this, getBean(),
                    getPropertyName(), index)){
                throw new JXPathException("Factory could not create path " +
                        asPath());
            }
        }
        BeanPropertyPointer clone = (BeanPropertyPointer)this.clone();
        clone.baseValue = UNINITIALIZED;
        clone.value = UNINITIALIZED;
        clone.setIndex(index);
        return clone;
    }

    public void remove(){
        if (index == WHOLE_COLLECTION){
            setValue(null);
        }
        else if (isCollection()){
            Object collection = ValueUtils.remove(getBaseValue(), index);
            ValueUtils.setValue(getBean(), getPropertyDescriptor(), collection);
        }
        else if (index == 0){
            index = WHOLE_COLLECTION;
            setValue(null);
        }
    }

    /**
     * Name of the currently selected property.
     */
    public String getPropertyName(){
        if (propertyName == null){
            PropertyDescriptor pd = getPropertyDescriptor();
            if (pd != null){
                propertyName = pd.getName();
            }
        }
        return propertyName != null ? propertyName : "*";
    }

    /**
     * Finds the property descriptor corresponding to the current property
     * index.
     */
    private PropertyDescriptor getPropertyDescriptor(){
        if (propertyDescriptor == null){
            int inx = getPropertyIndex();
            if (inx == UNSPECIFIED_PROPERTY){
                propertyDescriptor = beanInfo.
                        getPropertyDescriptor(propertyName);
            }
            else {
                PropertyDescriptor propertyDescriptors[] =
                        getPropertyDescriptors();
                if (inx >=0 && inx < propertyDescriptors.length){
                    propertyDescriptor = propertyDescriptors[inx];
                }
                else {
                    propertyDescriptor = null;
                }
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

    private AbstractFactory getAbstractFactory(JXPathContext context){
        AbstractFactory factory = context.getFactory();
        if (factory == null){
            throw new JXPathException("Factory is not set on the " +
                "JXPathContext - cannot create path: " + asPath());
        }
        return factory;
    }
}