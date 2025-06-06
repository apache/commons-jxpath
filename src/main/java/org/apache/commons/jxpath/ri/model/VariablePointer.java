/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
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
 */
public class VariablePointer extends NodePointer {

    private static final long serialVersionUID = -454731297397189293L;

    /**
     * Variables.
     */
    private Variables variables;

    /**
     * Qualified name.
     */
    private final QName qName;

    /**
     * Value pointer.
     */
    private NodePointer valuePointer;

    /**
     * Implements {@link NodePointer#isActual()}.
     */
    private boolean actual;

    /**
     * Constructs a new (non-actual) VariablePointer.
     *
     * @param qName variable name
     */
    public VariablePointer(final QName qName) {
        super(null);
        this.qName = qName;
        actual = false;
    }

    /**
     * Constructs a new VariablePointer.
     *
     * @param variables Variables instance
     * @param qName      variable name
     */
    public VariablePointer(final Variables variables, final QName qName) {
        super(null);
        this.variables = variables;
        this.qName = qName;
        actual = true;
    }

    @Override
    public String asPath() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append('$');
        buffer.append(qName);
        if (!actual) {
            if (index != WHOLE_COLLECTION) {
                buffer.append('[').append(index + 1).append(']');
            }
        } else if (index != WHOLE_COLLECTION && (getNode() == null || isCollection())) {
            buffer.append('[').append(index + 1).append(']');
        }
        return buffer.toString();
    }

    @Override
    public NodeIterator attributeIterator(final QName qName) {
        return getValuePointer().attributeIterator(qName);
    }

    @Override
    public NodeIterator childIterator(final NodeTest test, final boolean reverse, final NodePointer startWith) {
        return getValuePointer().childIterator(test, reverse, startWith);
    }

    @Override
    public int compareChildNodePointers(final NodePointer pointer1, final NodePointer pointer2) {
        return pointer1.getIndex() - pointer2.getIndex();
    }

    @Override
    public NodePointer createChild(final JXPathContext context, final QName qName, final int index) {
        final Object collection = createCollection(context, index);
        if (!isActual() || index != 0 && index != WHOLE_COLLECTION) {
            final AbstractFactory factory = getAbstractFactory(context);
            final boolean success = factory.createObject(context, this, collection, getName().toString(), index);
            if (!success) {
                throw new JXPathAbstractFactoryException("Factory could not create object path: " + asPath());
            }
            final NodePointer cln = (NodePointer) clone();
            cln.setIndex(index);
            return cln;
        }
        return this;
    }

    @Override
    public NodePointer createChild(final JXPathContext context, final QName qName, final int index, final Object value) {
        final Object collection = createCollection(context, index);
        ValueUtils.setValue(collection, index, value);
        final NodePointer cl = (NodePointer) clone();
        cl.setIndex(index);
        return cl;
    }

    /**
     * Create a collection.
     *
     * @param context JXPathContext
     * @param index   collection index
     * @return Object
     */
    private Object createCollection(final JXPathContext context, int index) {
        createPath(context);
        Object collection = getBaseValue();
        if (collection == null) {
            throw new JXPathAbstractFactoryException("Factory did not assign a collection to variable '" + qName + "' for path: " + asPath());
        }
        if (index == WHOLE_COLLECTION) {
            index = 0;
        } else if (index < 0) {
            throw new JXPathInvalidAccessException("Index is less than 1: " + asPath());
        }
        if (index >= getLength()) {
            collection = ValueUtils.expandCollection(collection, index + 1);
            variables.declareVariable(qName.toString(), collection);
        }
        return collection;
    }

    @Override
    public NodePointer createPath(final JXPathContext context) {
        if (!actual) {
            final AbstractFactory factory = getAbstractFactory(context);
            if (!factory.declareVariable(context, qName.toString())) {
                throw new JXPathAbstractFactoryException("Factory cannot define variable '" + qName + "' for path: " + asPath());
            }
            findVariables(context);
            // Assert: actual == true
        }
        return this;
    }

    @Override
    public NodePointer createPath(final JXPathContext context, final Object value) {
        if (actual) {
            setValue(value);
            return this;
        }
        final NodePointer ptr = createPath(context);
        ptr.setValue(value);
        return ptr;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof VariablePointer)) {
            return false;
        }
        final VariablePointer other = (VariablePointer) object;
        return variables == other.variables && qName.equals(other.qName) && index == other.index;
    }

    /**
     * Assimilate the Variables instance associated with the specified context.
     *
     * @param context JXPathContext to search
     */
    protected void findVariables(final JXPathContext context) {
        valuePointer = null;
        JXPathContext varCtx = context;
        while (varCtx != null) {
            variables = varCtx.getVariables();
            if (variables.isDeclaredVariable(qName.toString())) {
                actual = true;
                break;
            }
            varCtx = varCtx.getParentContext();
            variables = null;
        }
    }

    @Override
    public Object getBaseValue() {
        if (!actual) {
            throw new JXPathException("Undefined variable: " + qName);
        }
        return variables.getVariable(qName.toString());
    }

    @Override
    public Object getImmediateNode() {
        final Object value = getBaseValue();
        return index == WHOLE_COLLECTION ? ValueUtils.getValue(value) : ValueUtils.getValue(value, index);
    }

    @Override
    public NodePointer getImmediateValuePointer() {
        if (valuePointer == null) {
            Object value;
            if (!actual) {
                return new NullPointer(this, getName()) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Object getImmediateNode() {
                        throw new JXPathException("Undefined variable: " + qName);
                    }
                };
            }
            value = getImmediateNode();
            valuePointer = newChildNodePointer(this, null, value);
        }
        return valuePointer;
    }

    @Override
    public int getLength() {
        if (actual) {
            final Object value = getBaseValue();
            return value == null ? 1 : ValueUtils.getLength(value);
        }
        return 0;
    }

    @Override
    public QName getName() {
        return qName;
    }

    @Override
    public int hashCode() {
        return (actual ? System.identityHashCode(variables) : 0) + qName.hashCode() + index;
    }

    @Override
    public boolean isActual() {
        return actual;
    }

    @Override
    public boolean isCollection() {
        final Object value = getBaseValue();
        return value != null && ValueUtils.isCollection(value);
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        final Object value = getNode();
        return value == null || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }

    @Override
    public NodeIterator namespaceIterator() {
        return getValuePointer().namespaceIterator();
    }

    @Override
    public NodePointer namespacePointer(final String name) {
        return getValuePointer().namespacePointer(name);
    }

    @Override
    public void remove() {
        if (actual) {
            if (index == WHOLE_COLLECTION) {
                variables.undeclareVariable(qName.toString());
            } else {
                if (index < 0) {
                    throw new JXPathInvalidAccessException("Index is less than 1: " + asPath());
                }
                Object collection = getBaseValue();
                if (collection != null && index < getLength()) {
                    collection = ValueUtils.remove(collection, index);
                    variables.declareVariable(qName.toString(), collection);
                }
            }
        }
    }

    @Override
    public void setIndex(final int index) {
        super.setIndex(index);
        valuePointer = null;
    }

    @Override
    public void setValue(final Object value) {
        if (!actual) {
            throw new JXPathException("Cannot set undefined variable: " + qName);
        }
        valuePointer = null;
        if (index != WHOLE_COLLECTION) {
            final Object collection = getBaseValue();
            ValueUtils.setValue(collection, index, value);
        } else {
            variables.declareVariable(qName.toString(), value);
        }
    }

    @Override
    public boolean testNode(final NodeTest nodeTest) {
        return getValuePointer().testNode(nodeTest);
    }
}
