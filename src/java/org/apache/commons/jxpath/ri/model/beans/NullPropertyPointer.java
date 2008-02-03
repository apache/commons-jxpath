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
package org.apache.commons.jxpath.ri.model.beans;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathAbstractFactoryException;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class NullPropertyPointer extends PropertyPointer {

    private String propertyName = "*";
    private boolean byNameAttribute = false;

    /**
     * Create a new NullPropertyPointer.
     * @param parent pointer
     */
    public NullPropertyPointer(NodePointer parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    public QName getName() {
        return new QName(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyIndex(int index) {
    }

    /**
     * {@inheritDoc}
     */
    public int getLength() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Object getBaseValue() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object getImmediateNode() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeaf() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public NodePointer getValuePointer() {
        return new NullPointer(this,  new QName(getPropertyName()));
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isActualProperty() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActual() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isContainer() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(Object value) {
        if (parent == null || parent.isContainer()) {
            throw new JXPathInvalidAccessException(
                "Cannot set property "
                    + asPath()
                    + ", the target object is null");
        }
        if (parent instanceof PropertyOwnerPointer
                && ((PropertyOwnerPointer) parent)
                        .isDynamicPropertyDeclarationSupported()) {
            // If the parent property owner can create
            // a property automatically - let it do so
            PropertyPointer propertyPointer =
                ((PropertyOwnerPointer) parent).getPropertyPointer();
            propertyPointer.setPropertyName(propertyName);
            propertyPointer.setValue(value);
        }
        else {
            throw new JXPathInvalidAccessException(
                "Cannot set property "
                    + asPath()
                    + ", path does not match a changeable location");
        }
    }

    /**
     * {@inheritDoc}
     */
    public NodePointer createPath(JXPathContext context) {
        NodePointer newParent = parent.createPath(context);
        if (isAttribute()) {
            return newParent.createAttribute(context, getName());
        }
        if (parent instanceof NullPointer && parent.equals(newParent)) {
            throw createBadFactoryException(context.getFactory());
        }
        // Consider these two use cases:
        // 1. The parent pointer of NullPropertyPointer is
        //    a PropertyOwnerPointer other than NullPointer. When we call
        //    createPath on it, it most likely returns itself. We then
        //    take a PropertyPointer from it and get the PropertyPointer
        //    to expand the collection for the corresponding property.
        //
        // 2. The parent pointer of NullPropertyPointer is a NullPointer.
        //    When we call createPath, it may return a PropertyOwnerPointer
        //    or it may return anything else, like a DOMNodePointer.
        //    In the former case we need to do exactly what we did in use
        //    case 1.  In the latter case, we simply request that the
        //    non-property pointer expand the collection by itself.
        if (newParent instanceof PropertyOwnerPointer) {
            PropertyOwnerPointer pop = (PropertyOwnerPointer) newParent;
            newParent = pop.getPropertyPointer();
        }
        return newParent.createChild(context, getName(), getIndex());
    }

    /**
     * {@inheritDoc}
     */
    public NodePointer createPath(JXPathContext context, Object value) {
        NodePointer newParent = parent.createPath(context);
        if (isAttribute()) {
            NodePointer pointer = newParent.createAttribute(context, getName());
            pointer.setValue(value);
            return pointer;
        }
        if (parent instanceof NullPointer && parent.equals(newParent)) {
            throw createBadFactoryException(context.getFactory());
        }
        if (newParent instanceof PropertyOwnerPointer) {
            PropertyOwnerPointer pop = (PropertyOwnerPointer) newParent;
            newParent = pop.getPropertyPointer();
        }
        return newParent.createChild(context, getName(), index, value);
    }

    /**
     * {@inheritDoc}
     */
    public NodePointer createChild(JXPathContext context, QName name, int index) {
        return createPath(context).createChild(context, name, index);
    }

    /**
     * {@inheritDoc}
     */
    public NodePointer createChild(JXPathContext context, QName name,
            int index, Object value) {
        return createPath(context).createChild(context, name, index, value);
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Set the name attribute.
     * @param attributeValue value to set
     */
    public void setNameAttributeValue(String attributeValue) {
        this.propertyName = attributeValue;
        byNameAttribute = true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCollection() {
        return getIndex() != WHOLE_COLLECTION;
    }

    /**
     * {@inheritDoc}
     */
    public int getPropertyCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getPropertyNames() {
        return new String[0];
    }

    /**
     * {@inheritDoc}
     */
    public String asPath() {
        if (!byNameAttribute) {
            return super.asPath();
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(getImmediateParentPointer().asPath());
        buffer.append("[@name='");
        buffer.append(escape(getPropertyName()));
        buffer.append("']");
        if (index != WHOLE_COLLECTION) {
            buffer.append('[').append(index + 1).append(']');
        }
        return buffer.toString();
    }

    /**
     * Return a string escaping single and double quotes.
     * @param string string to treat
     * @return string with any necessary changes made.
     */
    private String escape(String string) {
        final char[] c = new char[] { '\'', '"' };
        final String[] esc = new String[] { "&apos;", "&quot;" };
        StringBuffer sb = null;
        for (int i = 0; sb == null && i < c.length; i++) {
            if (string.indexOf(c[i]) >= 0) {
                sb = new StringBuffer(string);
            }
        }
        if (sb == null) {
            return string;
        }
        for (int i = 0; i < c.length; i++) {
            if (string.indexOf(c[i]) < 0) {
                continue;
            }
            int pos = 0;
            while (pos < sb.length()) {
                if (sb.charAt(pos) == c[i]) {
                    sb.replace(pos, pos + 1, esc[i]);
                    pos += esc[i].length();
                }
                else {
                    pos++;
                }
            }
        }
        return sb.toString();
    }

    /**
     * Create a "bad factory" JXPathAbstractFactoryException for the specified AbstractFactory.
     * @param factory AbstractFactory
     * @return JXPathAbstractFactoryException
     */
    private JXPathAbstractFactoryException createBadFactoryException(AbstractFactory factory) {
        return new JXPathAbstractFactoryException("Factory " + factory
                + " reported success creating object for path: " + asPath()
                + " but object was null.  Terminating to avoid stack recursion.");
    }
}