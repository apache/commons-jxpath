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

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.LazyDynaClass;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;

/**
 * Implemented in response to [JXPATH-144]. Optionally pluggable
 * <code>NodePointerFactory</code> that returns a special type of
 * <code>NodePointer</code> for <code>LazyDynaBean</code>s. The
 * <code>PropertyPointer</code>s returned by these will respect
 * {@link LazyDynaClass#isDynaProperty(String)} when determining
 * {@link PropertyPointer#isActual()}.
 *
 * @version $Revision$ $Date$
 */
public class StrictLazyDynaBeanPointerFactory implements NodePointerFactory {
    /**
     * Pointer implementation.
     */
    private static class StrictLazyDynaBeanPointer extends DynaBeanPointer {
        private static final long serialVersionUID = 1L;

        private final LazyDynaBean lazyDynaBean;

        /**
         * Create a new StrictLazyDynaBeanPointer instance.
         *
         * @param parent pointer
         * @param name is the name given to the first node
         * @param lazyDynaBean pointed
         */
        public StrictLazyDynaBeanPointer(NodePointer parent, QName name, LazyDynaBean lazyDynaBean) {
            super(parent, name, lazyDynaBean);
            this.lazyDynaBean = lazyDynaBean;
        }

        /**
         * Create a new StrictLazyDynaBeanPointer instance.
         *
         * @param name is the name given to the first node
         * @param lazyDynaBean pointed
         * @param locale Locale
         */
        public StrictLazyDynaBeanPointer(QName name, LazyDynaBean lazyDynaBean, Locale locale) {
            super(name, lazyDynaBean, locale);
            this.lazyDynaBean = lazyDynaBean;
        }

        /**
         * {@inheritDoc}
         */
        public PropertyPointer getPropertyPointer() {
            return new DynaBeanPropertyPointer(this, lazyDynaBean) {
                private static final long serialVersionUID = 1L;

                protected boolean isActualProperty() {
                    return ((LazyDynaClass) lazyDynaBean.getDynaClass())
                            .isDynaProperty(getPropertyName());
                }
            };
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getOrder() {
        return DynaBeanPointerFactory.DYNA_BEAN_POINTER_FACTORY_ORDER - 1;
    }

    /**
     * {@inheritDoc}
     */
    public NodePointer createNodePointer(QName name, Object object, Locale locale) {
        return object instanceof LazyDynaBean ? new StrictLazyDynaBeanPointer(name,
                (LazyDynaBean) object, locale) : null;
    }

    /**
     * {@inheritDoc}
     */
    public NodePointer createNodePointer(NodePointer parent, QName name, Object object) {
        return object instanceof LazyDynaBean ? new StrictLazyDynaBeanPointer(parent, name,
                (LazyDynaBean) object) : null;
    }

}
