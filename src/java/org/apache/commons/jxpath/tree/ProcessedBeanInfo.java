/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/ProcessedBeanInfo.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
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

import java.util.*;
import org.w3c.dom.*;
import org.apache.commons.jxpath.*;
import java.beans.PropertyDescriptor;

/**
 * An optimized version of JXPathBeanInfo. Properties are grouped into two
 * categories: those representing singular values and those representing
 * collections.
 * <p>
 * The idea of the optimization is that if we scan singular
 * properties first, and only then iterate through collection, we would
 * find property values quicker, statistically speaking.
 *
 * @see org.apache.commons.jxpath.JXPathBeanInfo
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public class ProcessedBeanInfo implements JXPathBeanInfo {
    private JXPathBeanInfo beanInfo;
    private PropertyDescriptor[] singularPropertyDescriptors;
    private PropertyDescriptor[] sequencePropertyDescriptors;
    private boolean processed = false;
    private DynamicPropertyHandler dynamicPropertyHandler;

    private static final PropertyDescriptor[] EMPTY_ARRAY = new PropertyDescriptor[0];

    public ProcessedBeanInfo(JXPathBeanInfo beanInfo){
        this.beanInfo = beanInfo;
    }

    /**
     * Returns the same value as the corresponding JXPathBeanInfo
     */
    public boolean isAtomic(){
        return beanInfo.isAtomic();
    }

    /**
     * Returns the same value as the corresponding JXPathBeanInfo
     */
    public boolean isDynamic(){
        return beanInfo.isDynamic();
    }

    /**
     * Returns the same value as the corresponding JXPathBeanInfo
     */
    public Class getDynamicPropertyHandlerClass() {
        return beanInfo.getDynamicPropertyHandlerClass();
    }

    /**
     * Returns a shared instance of the dynamic property handler class
     * returned by <code>getDynamicPropertyHandlerClass()</code>.
     */
    public DynamicPropertyHandler getDynamicPropertyHandler() {
        if (dynamicPropertyHandler == null){
            try {
                dynamicPropertyHandler =
                  (DynamicPropertyHandler)getDynamicPropertyHandlerClass().newInstance();
            }
            catch (Exception ex){
                throw new RuntimeException("Cannot allocate dynamic property handler " +
                    " of class " + getDynamicPropertyHandlerClass() + ".\n" + ex);
            }
        }
        return dynamicPropertyHandler;
    }

    /**
     * Returns the same value as the corresponding JXPathBeanInfo
     */
    public PropertyDescriptor[] getPropertyDescriptors(){
        return beanInfo.getPropertyDescriptors();
    }

    /**
     * Returns property descriptors that represent singular values.
     * Consults BeanNodeFactory to determine whether a property is a collection.
     *
     * @see BeanNodeFactory#isMultiElement
     */
    public PropertyDescriptor[] getSingularPropertyDescriptors(){
        if (!processed){
            process();
        }
        return singularPropertyDescriptors;
    }

    /**
     * Returns property descriptors that represent collections.
     * Consults BeanNodeFactory to determine whether a property is a collection.
     *
     * @see BeanNodeFactory#isMultiElement
     */
    public PropertyDescriptor[] getCollectionPropertyDescriptors(){
        if (!processed){
            process();
        }
        return sequencePropertyDescriptors;
    }

    private void process(){
        processed = true;

        PropertyDescriptor pds[] = getPropertyDescriptors();

        int sCount = 0;
        int cCount = 0;
        for (int i = 0; i < pds.length; i++){
            if (BeanNodeFactory.isMultiElement(pds[i].getPropertyType())){
                cCount ++;
            }
            else {
                sCount ++;
            }
        }

        if (sCount != 0){
            singularPropertyDescriptors = new PropertyDescriptor[sCount];
        }
        else {
            singularPropertyDescriptors = EMPTY_ARRAY;
        }

        if (cCount != 0){
            sequencePropertyDescriptors = new PropertyDescriptor[cCount];
        }
        else {
            sequencePropertyDescriptors = EMPTY_ARRAY;
        }

        int sIndex = 0;
        int cIndex = 0;
        for (int i = 0; i < pds.length; i++){
            Class type = pds[i].getPropertyType();
            if (BeanNodeFactory.isMultiElement(type)){
                sequencePropertyDescriptors[cIndex++] = pds[i];
            }
            else {
                singularPropertyDescriptors[sIndex++] = pds[i];
            }
        }
    }
}