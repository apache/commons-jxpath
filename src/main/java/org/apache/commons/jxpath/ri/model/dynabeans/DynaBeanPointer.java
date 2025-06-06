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

package org.apache.commons.jxpath.ri.model.dynabeans;

import java.util.Locale;
import java.util.Objects;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyOwnerPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;

/**
 * A Pointer that points to a {@link DynaBean}. If the target DynaBean is Serializable, so should this instance be.
 */
public class DynaBeanPointer extends PropertyOwnerPointer {

    private static final long serialVersionUID = -9135052498044877965L;

    /**
     * Qualified name.
     */
    private final QName qName;

    /**
     * DynaBean.
     */
    private final DynaBean dynaBean;

    /**
     * Constructs a new DynaBeanPointer.
     *
     * @param parent   pointer
     * @param qName     is the name given to the first node
     * @param dynaBean pointed
     */
    public DynaBeanPointer(final NodePointer parent, final QName qName, final DynaBean dynaBean) {
        super(parent);
        this.qName = qName;
        this.dynaBean = dynaBean;
    }

    /**
     * Constructs a new DynaBeanPointer.
     *
     * @param qName     is the name given to the first node
     * @param dynaBean pointed
     * @param locale   Locale
     */
    public DynaBeanPointer(final QName qName, final DynaBean dynaBean, final Locale locale) {
        super(null, locale);
        this.qName = qName;
        this.dynaBean = dynaBean;
    }

    @Override
    public String asPath() {
        return parent == null ? "/" : super.asPath();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof DynaBeanPointer)) {
            return false;
        }
        final DynaBeanPointer other = (DynaBeanPointer) object;
        if (!(Objects.equals(parent, other.parent) && Objects.equals(qName, other.qName))) {
            return false;
        }
        final int iThis = index == WHOLE_COLLECTION ? 0 : index;
        final int iOther = other.index == WHOLE_COLLECTION ? 0 : other.index;
        return iThis == iOther && dynaBean == other.dynaBean;
    }

    @Override
    public Object getBaseValue() {
        return dynaBean;
    }

    @Override
    public Object getImmediateNode() {
        return dynaBean;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public QName getName() {
        return qName;
    }

    @Override
    public PropertyPointer getPropertyPointer() {
        return new DynaBeanPropertyPointer(this, dynaBean);
    }

    @Override
    public int hashCode() {
        return qName == null ? 0 : qName.hashCode();
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
