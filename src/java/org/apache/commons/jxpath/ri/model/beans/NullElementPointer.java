/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Used when there is a need to construct a Pointer for a collection element
 * that does not exist.  For example, if the path is "foo[3]", but the
 * collection "foo" only has one element or is empty or is null, the
 * NullElementPointer can be used to capture this situation without putting a
 * regular NodePointer into an invalid state.  Just create a NullElementPointer
 * with index 2 (= 3 - 1) and a "foo" pointer as the parent.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.16 $ $Date: 2004/02/29 14:17:41 $
 */
public class NullElementPointer extends CollectionPointer {

    public NullElementPointer(NodePointer parent, int index) {
        super(parent, (Object) null);
        this.index = index;
    }

    public QName getName() {
        return null;
    }

    public Object getBaseValue() {
        return null;
    }

    public Object getImmediateNode() {
        return null;
    }
    
    public boolean isLeaf() {
        return true;
    }    
    
    public boolean isCollection() {
        return false;
    }

    public PropertyPointer getPropertyPointer() {
        return new NullPropertyPointer(this);
    }

    public NodePointer getValuePointer() {
        return new NullPointer(this, getName());
    }

    public void setValue(Object value) {
        throw new UnsupportedOperationException(
            "Collection element does not exist: " + this);
    }

    public boolean isActual() {
        return false;
    }

    public boolean isContainer() {
        return true;
    }

    public NodePointer createPath(JXPathContext context) {
        return parent.createChild(context, null, index);
    }
    
    public NodePointer createPath(JXPathContext context, Object value) {
        return parent.createChild(context, null, index, value);
    }

    public int hashCode() {
        return getParent().hashCode() + index;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof NullElementPointer)) {
            return false;
        }

        NullElementPointer other = (NullElementPointer) object;
        return getParent() == other.getParent() && index == other.index;
    }

    public int getLength() {
        return 0;
    }
    
    public String asPath() {
        StringBuffer buffer = new StringBuffer();
        NodePointer parent = getParent();
        if (parent != null) {
            buffer.append(parent.asPath());
        }
        if (index != WHOLE_COLLECTION) {
            // Address the list[1][2] case
            if (parent != null && parent.getIndex() != WHOLE_COLLECTION) {
                buffer.append("/.");
            }
            else if (
                parent != null
                    && parent.getParent() != null
                    && parent.getParent().getIndex() != WHOLE_COLLECTION) {
                buffer.append("/.");
            }
            buffer.append("[").append(index + 1).append(']');
        }

        return buffer.toString();
    }    
}