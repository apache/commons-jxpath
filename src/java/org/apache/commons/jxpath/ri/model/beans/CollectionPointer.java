/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/beans/CollectionPointer.java,v 1.16 2003/10/09 21:31:40 rdonkin Exp $
 * $Revision: 1.16 $
 * $Date: 2003/10/09 21:31:40 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Transparent pointer to a collection (array or Collection).
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.16 $ $Date: 2003/10/09 21:31:40 $
 */
public class CollectionPointer extends NodePointer {
    private Object collection;
    private NodePointer valuePointer;

    public CollectionPointer(Object collection, Locale locale) {
        super(null, locale);
        this.collection = collection;
    }

    public CollectionPointer(NodePointer parent, Object collection) {
        super(parent);
        this.collection = collection;
    }

    public QName getName() {
        return null;
    }

    public Object getBaseValue() {
        return collection;
    }

    public boolean isCollection() {
        return true;
    }

    public int getLength() {
        return ValueUtils.getLength(getBaseValue());
    }

    public boolean isLeaf() {
        Object value = getNode();
        return value == null
            || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }

    public boolean isContainer() {
        return index != WHOLE_COLLECTION;
    }

    public Object getImmediateNode() {
        if (index != WHOLE_COLLECTION) {
            return ValueUtils.getValue(collection, index);
        }
        return collection;
    }

    public void setValue(Object value) {
        if (index == WHOLE_COLLECTION) {
            parent.setValue(value);
        }
        else {
            ValueUtils.setValue(collection, index, value);
        }
    }

    public void setIndex(int index) {
        super.setIndex(index);
        valuePointer = null;
    }

    public NodePointer getValuePointer() {
        if (valuePointer == null) {
            if (index == WHOLE_COLLECTION) {
                valuePointer = this;
            }
            else {
                Object value = getImmediateNode();
                valuePointer =
                    NodePointer.newChildNodePointer(this, getName(), value);
            }
        }
        return valuePointer;
    }

    public NodePointer createPath(JXPathContext context) {
        Object collection = getBaseValue();
        if (ValueUtils.getLength(collection) <= index) {
            collection = ValueUtils.expandCollection(getNode(), index + 1);
        }
        return this;
    }

    public NodePointer createPath(JXPathContext context, Object value) {
        NodePointer ptr = createPath(context);
        ptr.setValue(value);
        return ptr;
    }

    public NodePointer createChild(
        JXPathContext context,
        QName name,
        int index,
        Object value) 
    {
        NodePointer ptr = (NodePointer) clone();
        ptr.setIndex(index);
        return ptr.createPath(context, value);
    }

    public NodePointer createChild(
        JXPathContext context,
        QName name,
        int index) 
    {
        NodePointer ptr = (NodePointer) clone();
        ptr.setIndex(index);
        return ptr.createPath(context);
    }

    public int hashCode() {
        return System.identityHashCode(collection) + index;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof CollectionPointer)) {
            return false;
        }

        CollectionPointer other = (CollectionPointer) object;
        return collection == other.collection && index == other.index;
    }

    public NodeIterator childIterator(NodeTest test, 
                boolean reverse, NodePointer startWith)
    {
        if (index == WHOLE_COLLECTION) {
            return new CollectionChildNodeIterator(
                this,
                test,
                reverse,
                startWith);
        }
        else {
            return getValuePointer().childIterator(test, reverse, startWith);
        }
    }

    public NodeIterator attributeIterator(QName name) {
        if (index == WHOLE_COLLECTION) {
            return new CollectionAttributeNodeIterator(this, name);
        }
        return getValuePointer().attributeIterator(name);
    }

    public NodeIterator namespaceIterator() {
        if (index == WHOLE_COLLECTION) {
            return null;
        }
        return getValuePointer().namespaceIterator();
    }

    public NodePointer namespacePointer(String namespace) {
        if (index == WHOLE_COLLECTION) {
            return null;
        }
        return getValuePointer().namespacePointer(namespace);
    }

    public boolean testNode(NodeTest nodeTest) {
//        if (index
        /** @todo: infinite loop here */
        return getValuePointer().testNode(nodeTest);
    }

    public int compareChildNodePointers(
                NodePointer pointer1, NodePointer pointer2)
    {
        return pointer1.getIndex() - pointer2.getIndex();
    }

    /**
     * Returns an XPath that maps to this Pointer.
     */
    public String asPath() {
        StringBuffer buffer = new StringBuffer();
        NodePointer parent = getParent();
        if (parent != null) {
            buffer.append(parent.asPath());
            if (index != WHOLE_COLLECTION) {
                // Address the list[1][2] case
                if (parent.getIndex() != WHOLE_COLLECTION) {
                    buffer.append("/.");
                }
                buffer.append("[").append(index + 1).append(']');
            }
        }
        else {
            if (index != WHOLE_COLLECTION) {
                buffer.append("/.[").append(index + 1).append(']');
            }
            else {
                buffer.append("/");
            }
        }
        
        return buffer.toString();
    }
}