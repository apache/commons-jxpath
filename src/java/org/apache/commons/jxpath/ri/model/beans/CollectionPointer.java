/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/beans/CollectionPointer.java,v 1.6 2002/08/10 16:13:04 dmitri Exp $
 * $Revision: 1.6 $
 * $Date: 2002/08/10 16:13:04 $
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

import java.util.Locale;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Transparent pointer to a collection (array or Collection).
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2002/08/10 16:13:04 $
 */
public class CollectionPointer extends NodePointer {
    private Object collection;
    private NodePointer valuePointer;

    public CollectionPointer(Object collection, Locale locale){
        super(null, locale);
        this.collection = collection;
    }

    public CollectionPointer(NodePointer parent, Object collection){
        super(parent);
        this.collection = collection;
    }

    public QName getName(){
        return null;
    }

    public Object getBaseValue(){
        return collection;
    }

    public boolean isNode(){
        return index == WHOLE_COLLECTION;
    }

    public Object getNode(){
        if (index != WHOLE_COLLECTION){
            return ValueUtils.getValue(collection, index);
        }
        return collection;
    }

    public void setValue(Object value){
        if (index == WHOLE_COLLECTION){
            parent.setValue(value);
        }
        else {
            ValueUtils.setValue(collection, index, value);
        }
    }

    public void setIndex(int index){
        super.setIndex(index);
        valuePointer = null;
    }

    public NodePointer getValuePointer(){
        if (valuePointer == null){
            if (index == WHOLE_COLLECTION){
                valuePointer = this;
            }
            else {
                Object value = getNode();
                valuePointer = NodePointer.newChildNodePointer(this, getName(), value);
            }
        }
        return valuePointer;
    }

    public NodePointer createChild(JXPathContext context, QName name, int index, Object value){
        if (parent instanceof PropertyPointer){
            return parent.createChild(context, name, index, value);
        }
        else {
            Object collection = getBaseValue();
            if (ValueUtils.getLength(collection) <= index){
                ValueUtils.expandCollection(getNode(), index + 1);
            }
            ValueUtils.setValue(collection, index, value);
            NodePointer ptr = (NodePointer)clone();
            ptr.setIndex(index);
            return ptr;
        }
    }

    public NodePointer createPath(JXPathContext context){
        if (parent instanceof PropertyPointer){
            return parent.createPath(context);
        }
        else {
            Object collection = getBaseValue();
            if (ValueUtils.getLength(collection) <= index){
                ValueUtils.expandCollection(getNode(), index + 1);
            }
            return this;
        }
    }

    public NodePointer createChild(JXPathContext context, QName name, int index){
        if (parent instanceof PropertyPointer){
            return parent.createChild(context, name, index);
        }
        else {
            Object collection = getBaseValue();
            if (ValueUtils.getLength(collection) <= index){
                ValueUtils.expandCollection(getNode(), index + 1);
            }
            return this;
        }
    }

    public int hashCode(){
        return System.identityHashCode(collection) + index;
    }

    public boolean equals(Object object){
        if (object == this){
            return true;
        }

        if (!(object instanceof CollectionPointer)){
            return false;
        }

        CollectionPointer other = (CollectionPointer)object;
        return collection == other.collection &&
                index == other.index;
    }

    public NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith){
        if (index == WHOLE_COLLECTION){
            return null;
        }
        return getValuePointer().childIterator(test, reverse, startWith);
    }

    public NodeIterator attributeIterator(QName name){
        if (index == WHOLE_COLLECTION){
            return null;
        }
        return getValuePointer().attributeIterator(name);
    }

    public NodeIterator namespaceIterator(){
        if (index == WHOLE_COLLECTION){
            return null;
        }
        return getValuePointer().namespaceIterator();
    }

    public NodePointer namespacePointer(String namespace){
        if (index == WHOLE_COLLECTION){
            return null;
        }
        return getValuePointer().namespacePointer(namespace);
    }

    public boolean testNode(NodeTest nodeTest){
        return getValuePointer().testNode(nodeTest);
    }

    public int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2){
        return pointer1.getIndex() - pointer2.getIndex();
    }
}