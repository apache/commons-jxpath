/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/ElementList.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
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
import java.beans.PropertyDescriptor;
import org.apache.commons.jxpath.*;

/**
 * Common superclass for NodeLists containing singular elements as well as
 * sequences of elements representing collections.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public abstract class ElementList
            implements NodeList, NodeSequencer {
    protected Node parent;
    private Node singularChildren[] = null;
    private int singularChildCount;
    private NodeSequence childSequences[] = null;
    private int childSequenceCount;
    private int length = -1;

    public ElementList(Node parent){
        this.parent = parent;
    }

    /**
     * Returns the parent node of this element list.
     */
    public Node getParentNode(){
        return parent;
    }

    /**
     * Called by a subclass to establish the number of singular Nodes in the list.
     */
    protected void setSingularChildCount(int count){
        this.singularChildCount = count;
    }

    /**
     * Called by a subclass to establish the number of Nodes sequences in the list.
     */
    protected void setChildSequenceCount(int count){
        this.childSequenceCount = count;
    }

    /**
     * Implemented by a subclass to produce a singular node.
     */
    protected abstract Node allocateSingularNode(int index);

    /**
     * Implemented by a subclass to produce a node sequence.
     */
    protected abstract NodeSequence allocateNodeSequence(int index);

    /**
     * Returns the <code>index</code>'th item in the NodeList.
     */
    public Node item(int index){
        if (index < singularChildCount){
            if (singularChildren == null){
                singularChildren = new Node[singularChildCount];
            }
            Node node = singularChildren[index];
            if (node == null){
                node = allocateSingularNode(index);
                singularChildren[index] = node;
            }
            return node;
        }
        else {
            int offset = index - singularChildCount;
            for (int j = 0; j < childSequenceCount; j++){
                NodeSequence list = getNodeSequence(j);
                int l = list.getLength();
                if (offset < l){
                    return list.item(offset);
                }
                offset -= l;
            }
        }
        return null;
    }

    /**
     * The number of nodes in the list. The range of valid child node indices
     * is 0 to <code>length-1</code> inclusive.
     */
    public int getLength(){
        if (length == -1){
            length = singularChildCount;
            for (int i = 0; i < childSequenceCount; i++){
                length += getNodeSequence(i).getLength();
            }
        }

        return length;
    }

    /**
     * Returns a NodeSequence for the specified index, optionally
     * allocating the sequence.
     */
    private NodeSequence getNodeSequence(int index){
        if (childSequences == null){
            childSequences = new NodeSequence[childSequenceCount];
        }

        NodeSequence list = childSequences[index];
        if (list == null){
            list = allocateNodeSequence(index);
            childSequences[index] = list;
        }
        return list;
    }

    /**
     * Returns the first node of the list or null if the list is empty.
     */
    public Node getFirstNode(){
        if (singularChildCount > 0){
            return item(0);
        }
        if (childSequenceCount > 0){
            return getNodeSequence(0).getFirstNode();
        }
        return null;
    }

    /**
     * Returns the node following the supplied node or null the supplied node
     * is the last one.
     */
    public Node getNextNode(Node node){
        BeanElement element = (BeanElement)node;
        int index = element.getIndex();
        if (index == singularChildCount){
            if (childSequenceCount > 0){
                return getNodeSequence(0).getFirstNode();
            }
        }
        else {
            return item(index + 1);
        }

        return null;
    }

    /**
     * Returns the Node that logically follows the last Node of the
     * supplied NodeSequence.
     */
    public Node getNextNode(NodeSequence list){
        int seqID = list.getSequenceID();
        if (seqID + 1 < childSequenceCount){
            return getNodeSequence(seqID + 1).getFirstNode();
        }

        return null;
    }

    /**
     * Returns the last Node in the list, or null if the list is empty.
     */
    public Node getLastNode(){
        if (childSequenceCount > 0){
            return getNodeSequence(childSequenceCount - 1).getLastNode();
        }
        if (singularChildCount > 0){
            return item(singularChildCount - 1);
        }
        return null;
    }

    /**
     * Returns the Node immediately preceeding the supplied one or null
     * if the supplied node is the first one in the list.
     */
    public Node getPreviousNode(Node node){
        BeanElement element = (BeanElement)node;
        int index = element.getIndex();
        if (index > 0){
            return item(index - 1);
        }

        return null;
    }

    /**
     * Returns the Node logically preceeding the first node of the
     * supplied NodeSequence.
     */
    public Node getPreviousNode(NodeSequence list){
        int seqID = list.getSequenceID();
        if (seqID > 0){
            return getNodeSequence(seqID - 1).getLastNode();
        }
        else if (singularChildCount > 0){
            return item(singularChildCount - 1);
        }

        return null;
    }
}