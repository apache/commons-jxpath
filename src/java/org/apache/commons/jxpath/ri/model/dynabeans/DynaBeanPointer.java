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
package org.apache.commons.jxpath.ri.model.dynabeans;

import java.util.Locale;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyOwnerPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;


/**
 * A Pointer that points to a {@link DynaBean}.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class DynaBeanPointer extends PropertyOwnerPointer {
    private QName name;
    private DynaBean dynaBean;

    private static final long serialVersionUID = -9135052498044877965L;

    /**
     * Create a new DynaBeanPointer.
     * @param name is the name given to the first node
     * @param dynaBean pointed
     * @param locale Locale
     */
    public DynaBeanPointer(QName name, DynaBean dynaBean, Locale locale) {
        super(null, locale);
        this.name = name;
        this.dynaBean = dynaBean;
    }

    /**
     * Create a new DynaBeanPointer.
     * @param parent pointer
     * @param name is the name given to the first node
     * @param dynaBean pointed
     */
    public DynaBeanPointer(NodePointer parent, QName name, DynaBean dynaBean) {
        super(parent);
        this.name = name;
        this.dynaBean = dynaBean;
    }

    public PropertyPointer getPropertyPointer() {
        return new DynaBeanPropertyPointer(this, dynaBean);
    }

    public QName getName() {
        return name;
    }

    public Object getBaseValue() {
        return dynaBean;
    }

    public Object getImmediateNode() {
        return dynaBean;
    }

    public boolean isCollection() {
        return false;
    }

    public int getLength() {
        return 1;
    }

    public boolean isLeaf() {
        return false;
    }

    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof DynaBeanPointer)) {
            return false;
        }

        DynaBeanPointer other = (DynaBeanPointer) object;
        if (!(equalObjects(parent, other.parent) && equalObjects(name, other.name))) {
            return false;
        }

        int iThis = (index == WHOLE_COLLECTION ? 0 : index);
        int iOther = (other.index == WHOLE_COLLECTION ? 0 : other.index);
        return iThis == iOther && dynaBean == other.dynaBean;
    }

    public String asPath() {
        return parent == null ? "/" : super.asPath();
    }

    /**
     * Learn whether two objects are == || .equals().
     * @param o1 first object
     * @param o2 second object
     * @return boolean
     */
    private static boolean equalObjects(Object o1, Object o2) {
        return o1 == o2 || o1 != null && o1.equals(o2);
    }
}
