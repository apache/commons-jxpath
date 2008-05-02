/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.ri.model;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathAbstractFactoryException;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Pointer to a context variable.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class VariablePointer extends NodePointer {
    private Variables variables;
    private QName name;
    private NodePointer valuePointer;
    private boolean actual;

    /**
     * Create a new VariablePointer.
     * @param variables Variables instance
     * @param name variable name
     */
    public VariablePointer(Variables variables, QName name) {
        super(null);
        this.variables = variables;
        this.name = name;
        actual = true;
    }

    /**
     * Create a new (non-actual) VariablePointer.
     * @param name variable name
     */
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
        return variables.getVariable(name.toString());
    }

    public boolean isLeaf() {
        Object value = getNode();
        return value == null || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }

    public boolean isCollection() {
        Object value = getBaseValue();
        return value != null && ValueUtils.isCollection(value);
    }

    public Object getImmediateNode() {
        Object value = getBaseValue();
        return index == WHOLE_COLLECTION ? ValueUtils.getValue(value)
                : ValueUtils.getValue(value, index);
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
            variables.declareVariable(name.toString(), value);
        }
    }

    public boolean isActual() {
        return actual;
    }

    public void setIndex(int index) {
        super.setIndex(index);
        valuePointer = null;
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
            return value == null ? 1 : ValueUtils.getLength(value);
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
                throw new JXPathAbstractFactoryException(
                        "Factory cannot define variable '" + name
                                + "' for path: " + asPath());
            }
            findVariables(context);
            // Assert: actual == true
        }
        return this;
    }

    public NodePointer createChild(
        JXPathContext context,
        QName name,
        int index) {
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
                throw new JXPathAbstractFactoryException(
                        "Factory could not create object path: " + asPath());
            }
            NodePointer cln = (NodePointer) clone();
            cln.setIndex(index);
            return cln;
        }
        return this;
    }

    public NodePointer createChild(
            JXPathContext context,
            QName name,
            int index,
            Object value) {
        Object collection = createCollection(context, index);
        ValueUtils.setValue(collection, index, value);
        NodePointer cl = (NodePointer) clone();
        cl.setIndex(index);
        return cl;
    }

    /**
     * Create a collection.
     * @param context JXPathContext
     * @param index collection index
     * @return Object
     */
    private Object createCollection(JXPathContext context, int index) {
        createPath(context);

        Object collection = getBaseValue();
        if (collection == null) {
            throw new JXPathAbstractFactoryException(
                "Factory did not assign a collection to variable '"
                    + name
                    + "' for path: "
                    + asPath());
        }

        if (index == WHOLE_COLLECTION) {
            index = 0;
        }
        else if (index < 0) {
            throw new JXPathInvalidAccessException("Index is less than 1: "
                    + asPath());
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
                    throw new JXPathInvalidAccessException(
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

    /**
     * Assimilate the Variables instance associated with the specified context.
     * @param context JXPathContext to search
     */
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
        NodePointer startWith) {
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

    public int compareChildNodePointers(
        NodePointer pointer1,
        NodePointer pointer2) {
        return pointer1.getIndex() - pointer2.getIndex();
    }
}
