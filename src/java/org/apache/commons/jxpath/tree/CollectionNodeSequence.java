/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/CollectionNodeSequence.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
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
import java.lang.reflect.*;
import org.w3c.dom.*;
import java.beans.PropertyDescriptor;

/**
 * A NodeSequence that is represents a java.util.Collection (like a Set or a List).
 *
 * @see NodeSequence
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public class CollectionNodeSequence extends NodeSequence
{
    private Iterator iterator;
    private int currentIteratorIndex;

    public CollectionNodeSequence(ElementList elementList, int seqID, Object bean, PropertyDescriptor propertyDescriptor){
         super(elementList, seqID, bean, propertyDescriptor);
    }

    public CollectionNodeSequence(ElementList elementList, int seqID, Object value, String name){
         super(elementList, seqID, value, name);
    }

    /**
     * Returns the size of the collection
     */
    protected int computeLength(){
        return ((Collection)getValue()).size();
    }

    /**
     * Returns the index'th element of the collection. Attempts to
     * use a cached iterator if the collection is not a list.
     * Invokes BeanNodeFactory to allocate a node for the value.
     */
    protected Node computeItem(int index){
        Object value = null;
        if (iterator != null && currentIteratorIndex == index - 1){
            value = iterator.next();
            currentIteratorIndex++;
        }
        else {
            Object col = getValue();
            if (col instanceof List){
                value = ((List)getValue()).get(index);
            }
            else {
                iterator = ((Collection)col).iterator();
                for (int i = 0; i <= index; i++){
                    value = iterator.next();
                }
                currentIteratorIndex = index;
            }
        }
        String name = getValueName();
        return BeanNodeFactory.allocateNode(parent, this, index, value, name);
    }
}