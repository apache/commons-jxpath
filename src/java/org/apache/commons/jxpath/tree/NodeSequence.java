/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/NodeSequence.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
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
 * An abstract subclass for classes that represent collections of Nodes
 * wrapping collections of objects (arrays, java.util.Collections, etc).
 * NodeSequences are used by ElementLists to represent properties of JavaBeans
 * whose values are collections.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public abstract class NodeSequence extends ValueHandle
        implements NodeSequencer
{
    protected ElementList elementList;
    protected Node parent;
    protected int seqID;
    private int length = -1;
    private Node nodes[];

    /**
     * @param elementList is the NodeList that contains this sequence
     * @param seqID is an integer that is assigned to the sequence by the parent
     *          element list and is used by it to identify the sequence
     * @param bean is the JavaBean whose property is associated with this sequence
     * @param propertyDescriptor identifies a property of that bean
     */
    public NodeSequence(ElementList elementList, int seqID, Object bean, PropertyDescriptor propertyDescriptor){
        super(bean, propertyDescriptor);
        this.elementList = elementList;
        this.parent = elementList.getParentNode();
        this.seqID = seqID;
    }

    /**
     * @param elementList is the NodeList that contains this sequence
     * @param seqID is an integer that is assigned to the sequence by the parent
     *          element list and is used by it to identify the sequence
     * @param value is the collection associated with this sequence
     * @param name is the name of the collection
     */
    public NodeSequence(ElementList elementList, int seqID, Object value, String name){
        super(value, name);
        this.elementList = elementList;
        this.parent = elementList.getParentNode();
        this.seqID = seqID;
    }

    /**
     * Returns the sequence ID passed to the object in the constructor - it is the identifier
     * of the sequence within the scope of the parent element list.
     */
    public int getSequenceID(){
        return seqID;
    }

    /**
     * Should be implemented by the subclasses to compute the collection length. NodeSequence
     * will cache the result.
     */
    protected abstract int computeLength();

    /**
     * Should be implemented by the subclasses to extract the index'th item of the sequence.
     * NodeSequence will cache the result.
     */
    protected abstract Node computeItem(int index);

    /**
     * Returns the length of the sequence.  It is computed only once and then cached.
     */
    public int getLength(){
        if (length == -1){
            length = computeLength();
        }
        return length;
    }

    /**
     * Returns the index'th item of the sequence. Each item is computed no
     * more than once and then cached.
     */
    public Node item(int index){
        if (nodes == null){
            nodes = new Node[getLength()];
        }

        Node node = nodes[index];
        if (node == null){
            node = computeItem(index);
            nodes[index] = node;
        }
        return node;
    }

    /**
     * Returns the first node of the sequence
     */
    public Node getFirstNode(){
        if (getLength() > 0){
            return item(0);
        }
        return null;
    }

    /**
     * Returns the node following the supplied one or null.
     */
    public Node getNextNode(Node node){
        int index = ((AbstractBeanNode)node).getIndex();
        int length = getLength();

        if (index + 1 < length){
            return item(index + 1);
        }

        return elementList.getNextNode(this);
    }

    /**
     * Returns the last node in the sequence.
     */
    public Node getLastNode(){
        int length = getLength();
        if (length > 0){
            return item(length - 1);
        }
        return null;
    }

    /**
     * Returns the node preceeding the supplied one in the sequence or null.
     */
    public Node getPreviousNode(Node node){
        int index = ((AbstractBeanNode)node).getIndex();
        int length = getLength();

        if (index > 0){
            return item(index - 1);
        }

        return elementList.getPreviousNode(this);
   }
}