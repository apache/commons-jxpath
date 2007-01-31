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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jxpath.Container;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;

/**
 * Tests JXPath with containers as root or value of a variable, property, etc.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */

public class ContainerModelTest extends JXPathTestCase {

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public ContainerModelTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    
    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(ContainerModelTest.class));
    }

    
    private class ArrayContainer implements Container
    {
        private String[] array = new String[]{"foo", "bar"};
        public Object getValue() {
            return array;
        }

        public void setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    };

    public class ListContainer implements Container
    {
        private List list;

        public ListContainer() {
            list = new ArrayList();
            list.add("foo");
            list.add("bar");
        }

        public Object getValue() {
            return list;
        }

        public void setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }

    public class Bean
    {
        private ListContainer container = new ListContainer();

        public ListContainer getContainer() {
            return container;
        }
    }
        
    public void testContainerVariableWithCollection() {
        ArrayContainer container = new ArrayContainer();
        String[] array = (String[]) container.getValue();
        
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("list", container);
        
        assertXPathValueAndPointer(context, "$list", array, "$list");
        assertXPathValueAndPointer(context, "$list[1]", "foo", "$list[1]");
        assertXPathValueAndPointer(context, "$list[2]", "bar", "$list[2]");
        
        assertXPathSetValue(context, "$list[1]", "baz");
        assertEquals("Checking setValue(index)", "baz", array[0]);
    }
    
    public void testContainerPropertyWithCollection() {
        Bean bean = new Bean();
        List list = (List) bean.getContainer().getValue();
        
        JXPathContext context = JXPathContext.newContext(bean);
        
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
        ListContainer container = new ListContainer();
        List list = (List) container.getValue();
                
        Map map = new HashMap();
        map.put("container", container);
        
        JXPathContext context = JXPathContext.newContext(map);
        
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
        ArrayContainer container = new ArrayContainer();
        String[] array = (String[]) container.getValue();
        
        JXPathContext context = JXPathContext.newContext(container);
        context.getVariables().declareVariable("list", container);
        
        assertXPathValueAndPointer(context, "/", array, "/");
        assertXPathValueAndPointer(context, "/.[1]", "foo", "/.[1]");
        assertXPathValueAndPointer(context, "/.[2]", "bar", "/.[2]");
        
        assertXPathSetValue(context, "/.[1]", "baz");
        assertEquals("Checking setValue(index)", "baz", array[0]);    }
    
}