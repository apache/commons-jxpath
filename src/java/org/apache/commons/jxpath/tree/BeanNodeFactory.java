/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/BeanNodeFactory.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
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
import org.w3c.dom.Element;

import java.beans.PropertyDescriptor;

/**
 * BeanNodeFactory is used whenever a Node or a NodeSequence needs to be allocated to
 * represent a stand-alone value or a JavaBean property. BeanNodeFactory takes into
 * consideration the type of the property.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public class BeanNodeFactory  {

    /**
     * Allocates a BeanElement.
     *
     * @see BeanElement
     */
    public static Node allocateNode(Node parent, NodeSequencer sequencer, int index, Object value, String name){
        return new BeanElement(parent, sequencer, index, value, name);
    }

    /**
     * Allocates a BeanElement.
     *
     * @see BeanElement
     */
    public static Node allocateNode(Node parent, NodeSequencer sequencer, int index, Object bean, PropertyDescriptor propertyDescriptor){
        return new BeanElement(parent, sequencer, index, bean, propertyDescriptor);
    }

    /**
     * Allocates a BeanElementList with a single NodeSequence representing the
     * specified collection (which can be either a collection or an array).
     *
     * @see BeanElement
     */
    public static NodeList allocateNodeList(Object collection, String tagName){
        return new BeanNodeList(collection, tagName);
    }

    /**
     * Returns true if the specified type requires a NodeSequence, false if it
     * needs a simple Node.
     *
     * Properties types requiring a NodeSequence are:
     * <ul>
     * <li>Array
     * <li>java.util.Collection
     * <li>Any interface, except java.util.Map
     * <li>java.lang.Object
     * </ul>
     *
     * @see BeanElement
     */
    public static boolean isMultiElement(Class type){
        if (type.isArray()){
            return true;
        }
        else if (Collection.class.isAssignableFrom(type)){
            return true;
        }
        else if (type.isInterface() && !Map.class.isAssignableFrom(type)){
            return true;
        }
        else if (type.getName().equals("java.lang.Object")){
            return true;
        }
        return false;
    }

    /**
     * Allocates a NodeSequence according to the type of the property.
     * It allocates an {@link ArrayNodeSequence ArrayNodeSequence} for any array,
     * a {@link CollectionNodeSequence CollectionNodeSequence} for java.util.Collection,
     * a {@link ValueNodeSequence ValueNodeSequence} for a scalar type.
     * The ValueNodeSequence alternative is used when the property is declared
     * as java.lang.Object or an interface. Since we don't know upfront if the actual
     * instance will be a scalar object or a collection, we assume that it might
     * be a collection and require a NodeSequence for it.  If later the instance
     * turns out to be scalar, we use ValueNodeSequence, which is a collection
     * of one Node.
     */
    public static NodeSequence allocateNodeSequence(ElementList elementList, int seqID, Object bean, PropertyDescriptor propertyDescriptor){
        Class type = propertyDescriptor.getPropertyType();
        if (type.isArray()){
            return new ArrayNodeSequence(elementList, seqID, bean, propertyDescriptor);
        }
        else if (Collection.class.isAssignableFrom(type)){
            return new CollectionNodeSequence(elementList, seqID, bean, propertyDescriptor);
        }
        else {
            // Unknown type: could be a collection, could be a singular object
            // We will first try to treat it as a singular object
            ValueNodeSequence seq = new ValueNodeSequence(elementList, seqID, bean, propertyDescriptor);
            Object value = seq.getValue();
            if (value == null || !isMultiElement(value.getClass())){
                return seq;
            }
            else {
                // If it turned out to be a collection - forward to the method
                // that allocates a multi-element sequence
                return allocateNodeSequence(elementList, seqID, value, seq.getValueName());
            }
        }
    }

    /**
     * Allocates a named NodeSequence to represent a stand-alone collection.
     * It is used in the cases where the collection is not a property value
     * (for example, if it an element of a Map).
     */
    public static NodeSequence allocateNodeSequence(ElementList elementList, int seqID, Object value, String name){
        Class type = value.getClass();
        if (type.isArray()){
            return new ArrayNodeSequence(elementList, seqID, value, name);
        }
        else if (Collection.class.isAssignableFrom(type)){
            return new CollectionNodeSequence(elementList, seqID, value, name);
        }
        else {
            return new ValueNodeSequence(elementList, seqID, value, name);
        }
    }
}