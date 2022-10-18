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
package org.apache.commons.jxpath.ri.model.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.Container;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;

/**
 * Tests JXPath with containers as root or value of a variable, property, etc.
 */

public class ContainerModelTest extends JXPathTestCase {

    private class ArrayContainer implements Container
    {
        private static final long serialVersionUID = 1L;
        private final String[] array = {"foo", "bar"};
        @Override
        public Object getValue() {
            return array;
        }

        @Override
        public void setValue(final Object value) {
            throw new UnsupportedOperationException();
        }
    }

    public class ListContainer implements Container
    {
        private static final long serialVersionUID = 1L;
        private final List list;

        public ListContainer() {
            list = new ArrayList();
            list.add("foo");
            list.add("bar");
        }

        @Override
        public Object getValue() {
            return list;
        }

        @Override
        public void setValue(final Object value) {
            throw new UnsupportedOperationException();
        }
    }

    public class Bean
    {
        private final ListContainer container = new ListContainer();

        public ListContainer getContainer() {
            return container;
        }
    }

    public void testContainerVariableWithCollection() {
        final ArrayContainer container = new ArrayContainer();
        final String[] array = (String[]) container.getValue();

        final JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("list", container);

        assertXPathValueAndPointer(context, "$list", array, "$list");
        assertXPathValueAndPointer(context, "$list[1]", "foo", "$list[1]");
        assertXPathValueAndPointer(context, "$list[2]", "bar", "$list[2]");

        assertXPathSetValue(context, "$list[1]", "baz");
        assertEquals("Checking setValue(index)", "baz", array[0]);
    }

    public void testContainerPropertyWithCollection() {
        final Bean bean = new Bean();
        final List list = (List) bean.getContainer().getValue();

        final JXPathContext context = JXPathContext.newContext(bean);

        assertXPathValueAndPointer(context, "/container",
                list, "/container");
        assertXPathValueAndPointer(context, "/container[1]",
                list.get(0), "/container[1]");
        assertXPathValueAndPointer(context, "/container[2]",
                list.get(1), "/container[2]");

        assertXPathSetValue(context, "/container[1]", "baz");
        assertEquals("Checking setValue(index)", "baz", list.get(0));
    }

    public void testContainerMapWithCollection() {
        final ListContainer container = new ListContainer();
        final List list = (List) container.getValue();

        final Map map = new HashMap();
        map.put("container", container);

        final JXPathContext context = JXPathContext.newContext(map);

        assertXPathValueAndPointer(context, "/container",
                list, "/.[@name='container']");
        assertXPathValueAndPointer(context, "/container[1]",
                list.get(0), "/.[@name='container'][1]");
        assertXPathValueAndPointer(context, "/container[2]",
                list.get(1), "/.[@name='container'][2]");

        assertXPathSetValue(context, "/container[1]", "baz");
        assertEquals("Checking setValue(index)", "baz", list.get(0));
    }

    public void testContainerRootWithCollection() {
        final ArrayContainer container = new ArrayContainer();
        final String[] array = (String[]) container.getValue();

        final JXPathContext context = JXPathContext.newContext(container);
        context.getVariables().declareVariable("list", container);

        assertXPathValueAndPointer(context, "/", array, "/");
        assertXPathValueAndPointer(context, "/.[1]", "foo", "/.[1]");
        assertXPathValueAndPointer(context, "/.[2]", "bar", "/.[2]");

        assertXPathSetValue(context, "/.[1]", "baz");
        assertEquals("Checking setValue(index)", "baz", array[0]);    }

}