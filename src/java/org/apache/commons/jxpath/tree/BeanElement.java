/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/BeanElement.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
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
import org.apache.commons.jxpath.*;
import org.w3c.dom.*;
import org.w3c.dom.Element;
import java.beans.PropertyDescriptor;

/**
 * BeanElement is a DOM Element that is associated with a JavaBean. Its child
 * nodes represent properties of the JavaBean.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public class BeanElement extends AbstractBeanElement {
    private NodeList children;

    /**
     * @param parent is the parent Node
     * @param sequencer is the parent sequencer: BeanElements are linked together
     *        by NodeSequencers like BeanElementList and NodeSequence
     * @param index is the position of the element within it parent seqencer. It
     *        is not the position of the element within the parent's child node list.
     * @param value is the value this element represents
     * @param name is the name of that value
     */
    public BeanElement(Node parent, NodeSequencer sequencer, int index, Object value, String name){
        super(parent, sequencer, index, value, name);
    }

    /**
     * Used for nested JavaBeans. This BeanElement represents a property of the parent JavaBean.
     * @param bean is the JavaBean whose property this bean represents
     * @param propertyDescriptor describes the property this bean represents
     */
    public BeanElement(Node parent, NodeSequencer sequencer, int index, Object bean, PropertyDescriptor propertyDescriptor){
        super(parent, sequencer, index, bean, propertyDescriptor);
    }

    /**
     * A <code>NodeList</code> that contains all children of this node. Depending on
     * the type of the value associated with this element, the class of the NodeList
     * will be ValueNodeList (for an atomic value), BeanElementList (for a JavaBean)
     * or MapNodeList (for a map).
     */
    public NodeList getChildNodes() {
        if (children == null){
            PropertyDescriptor pd = getPropertyDescriptor();
            if (pd != null){
                Class type = pd.getPropertyType();
                ProcessedBeanInfo beanInfo = ProcessingIntrospector.getProcessedBeanInfo(type);
                if (beanInfo.isAtomic()){
                    children = new ValueNodeList(this, getBean(), pd);
                }
                else if (beanInfo.isDynamic()){
                    children = new DynamicPropertyElementList(this, getValue(), beanInfo.getDynamicPropertyHandler());
                }
            }
            if (children == null){
                Object value = getValue();
                if (value == null){
                    children = new ValueNodeList(this, null, getValueName());
                }
                else {
                    Class type = value.getClass();
                    ProcessedBeanInfo beanInfo = ProcessingIntrospector.getProcessedBeanInfo(type);
                    if (beanInfo.isAtomic()){
                        if (pd != null){
                            children = new ValueNodeList(this, getBean(), pd);
                        }
                        else {
                            children = new ValueNodeList(this, value, getValueName());
                        }
                    }
                    else if (beanInfo.isDynamic()){
                        children = new DynamicPropertyElementList(this, value, beanInfo.getDynamicPropertyHandler());
                    }
                    else {
                        children = new BeanElementList(this, value, beanInfo);
                    }
                }
            }
        }
        return children;
    }

    /**
     * Attributes are not supported.
     */
    public NamedNodeMap getAttributes() {
        return new BeanNamedNodeMap(null, null);
    }
}