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
 * {@code NodePointerFactory} that returns a special type of
 * {@code NodePointer} for {@code LazyDynaBean}s. The
 * {@code PropertyPointer}s returned by these will respect
 * {@link LazyDynaClass#isDynaProperty(String)} when determining
 * {@link PropertyPointer#isActual()}.
 */
public class StrictLazyDynaBeanPointerFactory implements NodePointerFactory {
    /**
     * Pointer implementation.
     */
    private static final class StrictLazyDynaBeanPointer extends DynaBeanPointer {
        private static final long serialVersionUID = 1L;

        private final LazyDynaBean lazyDynaBean;

        /**
         * Create a new StrictLazyDynaBeanPointer instance.
         *
         * @param parent pointer
         * @param name is the name given to the first node
         * @param lazyDynaBean pointed
         */
        public StrictLazyDynaBeanPointer(final NodePointer parent, final QName name, final LazyDynaBean lazyDynaBean) {
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
        public StrictLazyDynaBeanPointer(final QName name, final LazyDynaBean lazyDynaBean, final Locale locale) {
            super(name, lazyDynaBean, locale);
            this.lazyDynaBean = lazyDynaBean;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PropertyPointer getPropertyPointer() {
            return new DynaBeanPropertyPointer(this, lazyDynaBean) {
                private static final long serialVersionUID = 1L;

                @Override
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
    @Override
    public int getOrder() {
        return DynaBeanPointerFactory.DYNA_BEAN_POINTER_FACTORY_ORDER - 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodePointer createNodePointer(final QName name, final Object object, final Locale locale) {
        return object instanceof LazyDynaBean ? new StrictLazyDynaBeanPointer(name,
                (LazyDynaBean) object, locale) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodePointer createNodePointer(final NodePointer parent, final QName name, final Object object) {
        return object instanceof LazyDynaBean ? new StrictLazyDynaBeanPointer(parent, name,
                (LazyDynaBean) object) : null;
    }

}
