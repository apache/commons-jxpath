/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/dynabeans/DynaBeanPropertyPointer.java,v 1.7 2003/03/11 00:59:29 dmitri Exp $
 * $Revision: 1.7 $
 * $Date: 2003/03/11 00:59:29 $
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.jxpath.ri.model.dynabeans;

import java.util.Arrays;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;
import org.apache.commons.jxpath.util.TypeUtils;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Pointer pointing to a property of a DynaBean.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.7 $ $Date: 2003/03/11 00:59:29 $
 */
public class DynaBeanPropertyPointer extends PropertyPointer {
    private DynaBean dynaBean;
    private String name;
    private String[] names;

    public DynaBeanPropertyPointer(NodePointer parent, DynaBean dynaBean) {
        super(parent);
        this.dynaBean = dynaBean;
    }

    public Object getBaseValue() {
        return dynaBean.get(getPropertyName());
    }

    /**
     * This type of node is auxiliary.
     */
    public boolean isContainer() {
        return true;
    }

    /**
     * Number of the DP object's properties.
     */
    public int getPropertyCount() {
        return getPropertyNames().length;
    }

    /**
     * Names of all properties, sorted alphabetically
     *
     * @todo do something about the sorting
     */
    public String[] getPropertyNames() {
        if (names == null) {
            DynaClass dynaClass = dynaBean.getDynaClass();
            DynaProperty properties[] = dynaClass.getDynaProperties();
            int count = properties.length;
            boolean hasClass = dynaClass.getDynaProperty("class") != null;
            if (hasClass) {
                count--;       // Exclude "class" from properties
            }
            names = new String[count];
            for (int i = 0, j = 0; i < properties.length; i++) {
                String name = properties[i].getName();
                if (!hasClass || !name.equals("class")) {
                    names[j++] = name;
                }
            }
            Arrays.sort(names);
        }
        return names;
    }

    /**
     * Returns the name of the currently selected property or "*"
     * if none has been selected.
     */
    public String getPropertyName() {
        if (name == null) {
            String names[] = getPropertyNames();
            if (propertyIndex >= 0 && propertyIndex < names.length) {
                name = names[propertyIndex];
            }
            else {
                name = "*";
            }
        }
        return name;
    }

    /**
     * Select a property by name.
     */
    public void setPropertyName(String propertyName) {
        setPropertyIndex(UNSPECIFIED_PROPERTY);
        this.name = propertyName;
    }

    /**
     * Index of the currently selected property in the list of all
     * properties sorted alphabetically.
     */
    public int getPropertyIndex() {
        if (propertyIndex == UNSPECIFIED_PROPERTY) {
            String names[] = getPropertyNames();
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(name)) {
                    setPropertyIndex(i);
                    break;
                }
            }
        }
        return super.getPropertyIndex();
    }

    /**
     * Index a property by its index in the list of all
     * properties sorted alphabetically.
     */
    public void setPropertyIndex(int index) {
        if (propertyIndex != index) {
            super.setPropertyIndex(index);
            name = null;
        }
    }

    /**
     * If index == WHOLE_COLLECTION, the value of the property, otherwise
     * the value of the index'th element of the collection represented by the
     * property. If the property is not a collection, index should be zero
     * and the value will be the property itself.
     */
    public Object getImmediateNode() {
        String name = getPropertyName();
        if (name.equals("*")) {
            return null;
        }

        Object value;
        if (index == WHOLE_COLLECTION) {
            value = dynaBean.get(name);
        }
        else if (isIndexedProperty()) {
            // DynaClass at this point is not based on whether
            // the property is indeed indexed, but rather on
            // whether it is an array or List. Therefore
            // the indexed set may fail.
            try {
                value = dynaBean.get(name, index);
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                value = null;
            }
            catch (IllegalArgumentException ex) {
                value = dynaBean.get(name);
                value = ValueUtils.getValue(value, index);
            }
        }
        else {
            value = dynaBean.get(name);
            if (ValueUtils.isCollection(value)) {
                value = ValueUtils.getValue(value, index);
            }
            else if (index != 0) {
                value = null;
            }
        }
        return value;
    }

    /**
     * Returns true if the bean has the currently selected property
     */
    protected boolean isActualProperty() {
        DynaClass dynaClass = dynaBean.getDynaClass();
        return dynaClass.getDynaProperty(getPropertyName()) != null;
    }

    protected boolean isIndexedProperty() {
        DynaClass dynaClass = dynaBean.getDynaClass();
        DynaProperty property = dynaClass.getDynaProperty(name);
        return property.isIndexed();
    }

    /**
     * If index == WHOLE_COLLECTION, change the value of the property, otherwise
     * change the value of the index'th element of the collection
     * represented by the property.
     */
    public void setValue(Object value) {
        setValue(index, value);
    }

    public void remove() {
        if (index == WHOLE_COLLECTION) {
            dynaBean.set(getPropertyName(), null);
        }
        else if (isIndexedProperty()) {
            dynaBean.set(getPropertyName(), index, null);
        }
        else if (isCollection()) {
            Object collection = ValueUtils.remove(getBaseValue(), index);
            dynaBean.set(getPropertyName(), collection);
        }
        else if (index == 0) {
            dynaBean.set(getPropertyName(), null);
        }
    }

    private void setValue(int index, Object value) {
        if (index == WHOLE_COLLECTION) {
            dynaBean.set(getPropertyName(), convert(value, false));
        }
        else if (isIndexedProperty()) {
            dynaBean.set(getPropertyName(), index, convert(value, true));
        }
        else {
            Object baseValue = dynaBean.get(getPropertyName());
            ValueUtils.setValue(baseValue, index, value);
        }
   }


    private Object convert(Object value, boolean element) {
        DynaClass dynaClass = (DynaClass) dynaBean.getDynaClass();
        DynaProperty property = dynaClass.getDynaProperty(getPropertyName());
        Class type = property.getType();
        if (element) {
            if (type.isArray()) {
                type = type.getComponentType();
            }
            else {
                return value; // No need to convert
            }
        }

        try {
            return TypeUtils.convert(value, type);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new JXPathException(
                "Cannot convert value of class "
                    + (value == null ? "null" : value.getClass().getName())
                    + " to type "
                    + type,
                ex);
        }
    }
}