/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/beans/CollectionNodeIterator.java,v 1.1 2003/03/25 02:41:34 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/25 02:41:34 $
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
 */package org.apache.commons.jxpath.ri.model.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Combines node iterators of all elements of a collection into one
 * aggregate node iterator.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2003/03/25 02:41:34 $
 */
public abstract class CollectionNodeIterator implements NodeIterator {
    private CollectionPointer pointer;
    private boolean reverse;
    private NodePointer startWith; 
    private int position;
    private List collection;

    protected CollectionNodeIterator(
        CollectionPointer pointer,
        boolean reverse,
        NodePointer startWith) 
    {
        this.pointer = pointer;
        this.reverse = reverse;
        this.startWith = startWith;
    }
    
    /**
     * Implemened by subclasses to produce child/attribute node iterators.
     */
    protected abstract NodeIterator 
            getElementNodeIterator(NodePointer elementPointer);

    public int getPosition() {
        return position;
    }

    public boolean setPosition(int position) {
        if (collection == null) {
            prepare();
        }
        
        if (position < 1 || position > collection.size()) {
            return false;
        }
        this.position = position;
        return true;
    }

    public NodePointer getNodePointer() {
        if (position == 0) {
            return null;
        }
        return (NodePointer) collection.get(position - 1);
    }
    
    private void prepare() {
        collection = new ArrayList();
        NodePointer ptr = (NodePointer) pointer.clone();
        int length = ptr.getLength();
        for (int i = 0; i < length; i++) {
            ptr.setIndex(i);
            NodePointer elementPointer = ptr.getValuePointer();
            NodeIterator iter = getElementNodeIterator(elementPointer);

            for (int j = 1; iter.setPosition(j); j++) {
                NodePointer childPointer = iter.getNodePointer();
                if (reverse) {
                    collection.add(0, childPointer);
                }
                else {
                    collection.add(childPointer);
                }
            }
        }
        if (startWith != null) {
            int index = collection.indexOf(startWith);
            if (index == -1) {
                throw new JXPathException(
                    "Invalid starting pointer for iterator: " + startWith);
            }
            while (collection.size() > index) {
                if (!reverse) {
                    collection.remove(collection.size() - 1);
                }
                else {
                    collection.remove(0);
                }
            }
        }
    }
}
