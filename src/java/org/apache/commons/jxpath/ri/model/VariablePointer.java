/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/VariablePointer.java,v 1.11 2003/01/12 01:52:56 dmitri Exp $
 * $Revision: 1.11 $
 * $Date: 2003/01/12 01:52:56 $
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
package org.apache.commons.jxpath.ri.model;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Pointer to a context variable.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.11 $ $Date: 2003/01/12 01:52:56 $
 */
public class VariablePointer extends NodePointer {
    private Variables variables;
    private QName name;
    private NodePointer valuePointer;
    private boolean actual;

    public VariablePointer(Variables variables, QName name) {
        super(null);
        this.variables = variables;
        this.name = name;
        actual = true;
    }

    public VariablePointer(QName name) {
        super(null);
        this.name = name;
        actual = false;
    }

    public boolean isContainer() {
        return true;
    }

    public QName getName() {
        return name;
    }

    public Object getBaseValue() {
        if (!actual) {
            throw new JXPathException("Undefined variable: " + name);
        }
        return variables.getVariable(name.getName());
    }
    
    public boolean isLeaf() {
        Object value = getNode();
        return value == null
            || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }
    
    public boolean isCollection() {
        Object value = getBaseValue();
        return value != null && ValueUtils.isCollection(value);
    }

    public Object getImmediateNode() {
        Object value = getBaseValue();
        if (index != WHOLE_COLLECTION) {
            return ValueUtils.getValue(value, index);
        }
        return value;
    }
    
    public void setValue(Object value) {
        if (!actual) {
            throw new JXPathException("Cannot set undefined variable: " + name);
        }
        valuePointer = null;
        if (index != WHOLE_COLLECTION) {
            Object collection = getBaseValue();
            ValueUtils.setValue(collection, index, value);
        }
        else {
            variables.declareVariable(name.getName(), value);
        }
    }

    public boolean isActual() {
        return actual;
    }

    public NodePointer getImmediateValuePointer() {
        if (valuePointer == null) {
            Object value = null;
            if (actual) {
                value = getImmediateNode();
                valuePointer =
                    NodePointer.newChildNodePointer(this, null, value);
            }
            else {
                return new NullPointer(this, getName()) {
                    public Object getImmediateNode() {
                        throw new JXPathException(
                            "Undefined variable: " + name);
                    }
                };
            }
        }
        return valuePointer;
    }

    public int getLength() {
        if (actual) {
            Object value = getBaseValue();
            if (value == null) {
                return 1;
            }
            return ValueUtils.getLength(value);
        }
        return 0;
    }

    public NodePointer createPath(JXPathContext context, Object value) {
        if (actual) {
            setValue(value);
            return this;
        }
        NodePointer ptr = createPath(context);
        ptr.setValue(value);
        return ptr;
    }

    public NodePointer createPath(JXPathContext context) {
        if (!actual) {
            AbstractFactory factory = getAbstractFactory(context);
            if (!factory.declareVariable(context, name.toString())) {
                throw new JXPathException(
                    "Factory cannot define variable '"
                        + name
                        + "' for path: "
                        + asPath());
            }
            findVariables(context);
            // Assert: actual == true
        }
        return this;
    }

    public NodePointer createChild(
        JXPathContext context,
        QName name,
        int index) 
    {
        Object collection = createCollection(context, index);
        if (!isActual() || (index != 0 && index != WHOLE_COLLECTION)) {
            AbstractFactory factory = getAbstractFactory(context);
            boolean success =
                factory.createObject(
                    context,
                    this,
                    collection,
                    getName().toString(),
                    index);
            if (!success) {
                throw new JXPathException(
                    "Factory could not create object path: " + asPath());
            }
            setIndex(index);
        }
        return this;
    }

    public NodePointer createChild(
            JXPathContext context,
            QName name, 
            int index,
            Object value) 
    {
        Object collection = createCollection(context, index);
        ValueUtils.setValue(collection, index, value);
        NodePointer cl = (NodePointer) clone();
        cl.setIndex(index);
        return cl;
    }

    private Object createCollection(JXPathContext context, int index) {
        createPath(context);

        Object collection = getBaseValue();
        if (collection == null) {
            throw new JXPathException(
                "Factory did not assign a collection to variable '"
                    + name
                    + "' for path: "
                    + asPath());
        }

        if (index == WHOLE_COLLECTION) {
            index = 0;
        }
        else if (index < 0) {
            throw new JXPathException("Index is less than 1: " + asPath());
        }

        if (index >= getLength()) {
            collection = ValueUtils.expandCollection(collection, index + 1);
            variables.declareVariable(name.toString(), collection);
        }

        return collection;
    }

    public void remove() {
        if (actual) {
            if (index == WHOLE_COLLECTION) {
                variables.undeclareVariable(name.toString());
            }
            else {
                if (index < 0) {
                    throw new JXPathException(
                        "Index is less than 1: " + asPath());
                }

                Object collection = getBaseValue();
                if (collection != null && index < getLength()) {
                    collection = ValueUtils.remove(collection, index);
                    variables.declareVariable(name.toString(), collection);
                }
            }
        }
    }

    protected void findVariables(JXPathContext context) {
        valuePointer = null;
        JXPathContext varCtx = context;
        while (varCtx != null) {
            variables = varCtx.getVariables();
            if (variables.isDeclaredVariable(name.toString())) {
                actual = true;
                break;
            }
            varCtx = varCtx.getParentContext();
            variables = null;
        }
    }

    public int hashCode() {
        return (actual ? System.identityHashCode(variables) : 0)
            + name.hashCode()
            + index;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof VariablePointer)) {
            return false;
        }

        VariablePointer other = (VariablePointer) object;
        return variables == other.variables
            && name.equals(other.name)
            && index == other.index;
    }

    public String asPath() {
        StringBuffer buffer = new StringBuffer();
        buffer.append('$');
        buffer.append(name);
        if (!actual) {
            if (index != WHOLE_COLLECTION) {
                buffer.append('[').append(index + 1).append(']');
            }
        }
        else if (
            index != WHOLE_COLLECTION
                && (getNode() == null || isCollection())) {
            buffer.append('[').append(index + 1).append(']');
        }
        return buffer.toString();
    }

    public NodeIterator childIterator(
        NodeTest test,
        boolean reverse,
        NodePointer startWith) 
    {
        return getValuePointer().childIterator(test, reverse, startWith);
    }

    public NodeIterator attributeIterator(QName name) {
        return getValuePointer().attributeIterator(name);
    }

    public NodeIterator namespaceIterator() {
        return getValuePointer().namespaceIterator();
    }

    public NodePointer namespacePointer(String name) {
        return getValuePointer().namespacePointer(name);
    }

    public boolean testNode(NodeTest nodeTest) {
        return getValuePointer().testNode(nodeTest);
    }

    private AbstractFactory getAbstractFactory(JXPathContext context) {
        AbstractFactory factory = context.getFactory();
        if (factory == null) {
            throw new JXPathException(
              "Factory is not set on the JXPathContext - cannot create path: "
              + asPath());
        }
        return factory;
    }

    public int compareChildNodePointers(
        NodePointer pointer1,
        NodePointer pointer2) 
    {
        return pointer1.getIndex() - pointer2.getIndex();
    }
}