/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/beans/PropertyOwnerPointer.java,v 1.14 2003/03/11 00:59:25 dmitri Exp $
 * $Revision: 1.14 $
 * $Date: 2003/03/11 00:59:25 $
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
package org.apache.commons.jxpath.ri.model.beans;

import java.util.Locale;

import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * A pointer describing a node that has properties, each of which could be
 * a collection.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.14 $ $Date: 2003/03/11 00:59:25 $
 */
public abstract class PropertyOwnerPointer extends NodePointer {

    public NodeIterator childIterator(
        NodeTest test,
        boolean reverse,
        NodePointer startWith) 
    {
        if (test == null) {
            return createNodeIterator(null, reverse, startWith);
        }
        else if (test instanceof NodeNameTest) {
            QName testName = ((NodeNameTest) test).getNodeName();
            String property;
            if (!isDefaultNamespace(testName.getPrefix())) {
                return null;
            }
            else if (testName.getName().equals("*")) {
                property = null;
            }
            else {
                property = testName.getName();
            }
            return createNodeIterator(property, reverse, startWith);
        }
        else if (test instanceof NodeTypeTest) {
            if (((NodeTypeTest) test).getNodeType()
                == Compiler.NODE_TYPE_NODE) {
                return createNodeIterator(null, reverse, startWith);
            }
        }
        return null;
    }

    public NodeIterator createNodeIterator(
                String property,
                boolean reverse,
                NodePointer startWith) 
    {
        return new PropertyIterator(this, property, reverse, startWith);
    }

    public NodeIterator attributeIterator(QName name) {
        return new BeanAttributeIterator(this, name);
    }

    protected PropertyOwnerPointer(NodePointer parent, Locale locale) {
        super(parent, locale);
    }

    protected PropertyOwnerPointer(NodePointer parent) {
        super(parent);
    }

    public void setIndex(int index) {
        if (this.index != index) {
            super.setIndex(index);
            value = UNINITIALIZED;
        }
    }

    private static final Object UNINITIALIZED = new Object();

    private Object value = UNINITIALIZED;
    public Object getImmediateNode() {
        if (value == UNINITIALIZED) {
            if (index == WHOLE_COLLECTION) {
                value = getBaseValue();
            }
            else {
                value = ValueUtils.getValue(getBaseValue(), index);
            }
        }
        return value;
    }

    public abstract QName getName();

    /**
     * Throws an exception if you try to change the root element, otherwise
     * forwards the call to the parent pointer.
     */
    public void setValue(Object value) {
        this.value = value;
        if (parent.isContainer()) {
            parent.setValue(value);
        }
        else if (parent != null) {
            if (index == WHOLE_COLLECTION) {
                throw new UnsupportedOperationException(
                    "Cannot setValue of an object that is not "
                        + "some other object's property");
            }
            else {
                throw new JXPathException(
                    "The specified collection element does not exist: " + this);
            }
        }
        else {
            throw new UnsupportedOperationException(
                "Cannot replace the root object");
        }
    }

    /**
     * If this is a root node pointer, throws an exception; otherwise
     * forwards the call to the parent node.
     */
    public void remove() {
        this.value = null;
        if (parent != null) {
            parent.remove();
        }
        else {
            throw new UnsupportedOperationException(
                "Cannot remove an object that is not "
                    + "some other object's property or a collection element");
        }
    }

    public abstract PropertyPointer getPropertyPointer();

    public int compareChildNodePointers(
        NodePointer pointer1,
        NodePointer pointer2) 
    {
        int r =
            pointer1.getName().toString().compareTo(
                pointer2.getName().toString());
        if (r != 0) {
            return r;
        }
        return pointer1.getIndex() - pointer2.getIndex();
    }
}