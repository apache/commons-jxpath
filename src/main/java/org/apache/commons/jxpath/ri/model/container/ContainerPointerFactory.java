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

package org.apache.commons.jxpath.ri.model.container;

import java.util.Locale;

import org.apache.commons.jxpath.Container;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;

/**
 * Implements NodePointerFactory for {@link Container} objects.
 */
public class ContainerPointerFactory implements NodePointerFactory {

    /** Factory order for this factory */
    public static final int CONTAINER_POINTER_FACTORY_ORDER = 200;

    /**
     * Constructs a new instance.
     */
    public ContainerPointerFactory() {
        // empty
    }

    @Override
    public NodePointer createNodePointer(final NodePointer parent, final QName qName, final Object bean) {
        return bean instanceof Container ? new ContainerPointer(parent, (Container) bean) : null;
    }

    @Override
    public NodePointer createNodePointer(final QName qName, final Object bean, final Locale locale) {
        return bean instanceof Container ? new ContainerPointer((Container) bean, locale) : null;
    }

    @Override
    public int getOrder() {
        return CONTAINER_POINTER_FACTORY_ORDER;
    }
}
