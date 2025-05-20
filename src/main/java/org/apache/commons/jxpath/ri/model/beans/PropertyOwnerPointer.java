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

package org.apache.commons.jxpath.ri.model.beans;

import java.util.Locale;

import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * A pointer describing a node that has properties, each of which could be a collection.
 */
public abstract class PropertyOwnerPointer extends NodePointer {

    private static final long serialVersionUID = 1L;
    private static final Object UNINITIALIZED = new Object();

    /**
     * Supports {@link #getImmediateNode}.
     */
    private Object value = UNINITIALIZED;

    /**
     * Constructs a new PropertyOwnerPointer.
     *
     * @param parent pointer
     */
    protected PropertyOwnerPointer(final NodePointer parent) {
        super(parent);
    }

    /**
     * Constructs a new PropertyOwnerPointer.
     *
     * @param parent parent pointer
     * @param locale Locale
     */
    protected PropertyOwnerPointer(final NodePointer parent, final Locale locale) {
        super(parent, locale);
    }

    @Override
    public NodeIterator attributeIterator(final QName qName) {
        return new BeanAttributeIterator(this, qName);
    }

    @Override
    public NodeIterator childIterator(final NodeTest test, final boolean reverse, final NodePointer startWith) {
        if (test == null) {
            return createNodeIterator(null, reverse, startWith);
        }
        if (test instanceof NodeNameTest) {
            final NodeNameTest nodeNameTest = (NodeNameTest) test;
            final QName testName = nodeNameTest.getNodeName();
            if (isValidProperty(testName)) {
                return createNodeIterator(nodeNameTest.isWildcard() ? null : testName.toString(), reverse, startWith);
            }
            return null;
        }
        return test instanceof NodeTypeTest && ((NodeTypeTest) test).getNodeType() == Compiler.NODE_TYPE_NODE ? createNodeIterator(null, reverse, startWith)
                : null;
    }

    @Override
    public int compareChildNodePointers(final NodePointer pointer1, final NodePointer pointer2) {
        final int r = pointer1.getName().toString().compareTo(pointer2.getName().toString());
        return r == 0 ? pointer1.getIndex() - pointer2.getIndex() : r;
    }

    /**
     * Create a NodeIterator.
     *
     * @param property  property name
     * @param reverse   whether to iterate in reverse
     * @param startWith first pointer to return
     * @return NodeIterator
     */
    public NodeIterator createNodeIterator(final String property, final boolean reverse, final NodePointer startWith) {
        return new PropertyIterator(this, property, reverse, startWith);
    }

    @Override
    public Object getImmediateNode() {
        if (value == UNINITIALIZED) {
            value = index == WHOLE_COLLECTION ? ValueUtils.getValue(getBaseValue()) : ValueUtils.getValue(getBaseValue(), index);
        }
        return value;
    }

    @Override
    public abstract QName getName();

    /**
     * Gets a PropertyPointer for this PropertyOwnerPointer.
     *
     * @return PropertyPointer
     */
    public abstract PropertyPointer getPropertyPointer();

    /**
     * Tests whether dynamic property declaration is supported.
     *
     * @return true if the property owner can set a property "does not exist". A good example is a Map. You can always assign a value to any key even if it has
     *         never been "declared".
     */
    public boolean isDynamicPropertyDeclarationSupported() {
        return false;
    }

    /**
     * Tests whether {@code name} is a valid child name for this PropertyOwnerPointer.
     *
     * @param qName the QName to test
     * @return {@code true} if {@code QName} is a valid property name.
     * @since JXPath 1.3
     */
    public boolean isValidProperty(final QName qName) {
        return isDefaultNamespace(qName.getPrefix());
    }

    /**
     * If this is a root node pointer, throws an exception; otherwise forwards the call to the parent node.
     */
    @Override
    public void remove() {
        this.value = null;
        if (parent == null) {
            throw new UnsupportedOperationException("Cannot remove an object that is not " + "some other object's property or a collection element");
        }
        parent.remove();
    }

    @Override
    public void setIndex(final int index) {
        if (this.index != index) {
            super.setIndex(index);
            value = UNINITIALIZED;
        }
    }

    /**
     * Throws an exception if you try to change the root element, otherwise forwards the call to the parent pointer.
     *
     * @param value to set
     */
    @Override
    public void setValue(final Object value) {
        this.value = value;
        if (parent == null) {
            throw new UnsupportedOperationException("Cannot replace the root object");
        }
        if (!parent.isContainer()) {
            if (index == WHOLE_COLLECTION) {
                throw new UnsupportedOperationException("Cannot setValue of an object that is not " + "some other object's property");
            }
            throw new JXPathInvalidAccessException("The specified collection element does not exist: " + this);
        }
        parent.setValue(value);
    }
}
