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
package org.apache.commons.jxpath.ri.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.TestBean;
import org.apache.commons.jxpath.TestMixedModelBean;
import org.apache.commons.jxpath.TestNull;
import org.apache.commons.jxpath.Variables;

/**
 * Tests JXPath with mixed model: beans, maps, DOM etc.
 */
public class MixedModelTest extends JXPathTestCase {
    private JXPathContext context;

    @Override
    public void setUp() {
        final TestMixedModelBean bean = new TestMixedModelBean();
        context = JXPathContext.newContext(bean);
        context.setFactory(new TestMixedModelFactory());
        context.setLocale(Locale.US);
        final Variables vars = context.getVariables();
        vars.declareVariable("string", bean.getString());
        vars.declareVariable("bean", bean.getBean());
        vars.declareVariable("map", bean.getMap());
        vars.declareVariable("list", bean.getList());
        vars.declareVariable("document", bean.getDocument());
        vars.declareVariable("element", bean.getElement());
        vars.declareVariable("container", bean.getContainer());
        vars.declareVariable("testnull", new TestNull());

        final int[][] matrix = new int[1][];
        matrix[0] = new int[1];
        matrix[0][0] = 3;
        vars.declareVariable("matrix", matrix);
    }

    public void testVar() {
        context.getVariables().declareVariable("foo:bar", "baz");

        assertXPathValueAndPointer(context,
            "$foo:bar",
            "baz",
            "$foo:bar");

    }

    public void testVarPrimitive() {
        assertXPathValueAndPointer(context, "$string", "string", "$string");
    }

    public void testVarBean() {
        assertXPathValueAndPointer(
            context,
            "$bean/int",
            Integer.valueOf(1),
            "$bean/int");
    }

    public void testVarMap() {
        assertXPathValueAndPointer(
            context,
            "$map/string",
            "string",
            "$map[@name='string']");
    }

    public void testVarList() {
        assertXPathValueAndPointer(context, "$list[1]", "string", "$list[1]");
    }

    public void testVarDocument() {
        assertXPathValueAndPointer(
            context,
            "$document/vendor/location/address/city",
            "Fruit Market",
            "$document/vendor[1]/location[2]/address[1]/city[1]");
    }

    public void testVarElement() {
        assertXPathValueAndPointer(
            context,
            "$element/location/address/city",
            "Fruit Market",
            "$element/location[2]/address[1]/city[1]");
    }

    public void testVarContainer() {
        assertXPathValueAndPointer(
            context,
            "$container/vendor/location/address/city",
            "Fruit Market",
            "$container/vendor[1]/location[2]/address[1]/city[1]");
    }

    // ----------------------------------------------------------------------

    public void testBeanPrimitive() {
        assertXPathValueAndPointer(context, "string", "string", "/string");
    }

    public void testBeanBean() {
        assertXPathValueAndPointer(
            context,
            "bean/int",
            Integer.valueOf(1),
            "/bean/int");
    }

    public void testBeanMap() {
        assertXPathValueAndPointer(
            context,
            "map/string",
            "string",
            "/map[@name='string']");
    }

    public void testBeanList() {
        assertXPathValueAndPointer(context, "list[1]", "string", "/list[1]");
    }

    public void testBeanDocument() {
        assertXPathValueAndPointer(
            context,
            "document/vendor/location/address/city",
            "Fruit Market",
            "/document/vendor[1]/location[2]/address[1]/city[1]");
    }

    public void testBeanElement() {
        assertXPathValueAndPointer(
            context,
            "element/location/address/city",
            "Fruit Market",
            "/element/location[2]/address[1]/city[1]");
    }

    public void testBeanContainer() {
        assertXPathValueAndPointer(
            context,
            "container/vendor/location/address/city",
            "Fruit Market",
            "/container/vendor[1]/location[2]/address[1]/city[1]");
    }

    // ----------------------------------------------------------------------

    public void testMapPrimitive() {
        assertXPathValueAndPointer(
            context,
            "map/string",
            "string",
            "/map[@name='string']");
    }

    public void testMapBean() {
        assertXPathValueAndPointer(
            context,
            "map/bean/int",
            Integer.valueOf(1),
            "/map[@name='bean']/int");
    }

    public void testMapMap() {
        assertXPathValueAndPointer(
            context,
            "map/map/string",
            "string",
            "/map[@name='map'][@name='string']");
    }

    public void testMapList() {
        assertXPathValueAndPointer(
            context,
            "map/list[1]",
            "string",
            "/map[@name='list'][1]");
    }

    public void testMapDocument() {
        assertXPathValueAndPointer(
            context,
            "map/document/vendor/location/address/city",
            "Fruit Market",
            "/map[@name='document']"
                + "/vendor[1]/location[2]/address[1]/city[1]");
    }

    public void testMapElement() {
        assertXPathValueAndPointer(
            context,
            "map/element/location/address/city",
            "Fruit Market",
            "/map[@name='element']/location[2]/address[1]/city[1]");
    }

    public void testMapContainer() {
        assertXPathValueAndPointer(
            context,
            "map/container/vendor/location/address/city",
            "Fruit Market",
            "/map[@name='container']"
                + "/vendor[1]/location[2]/address[1]/city[1]");
    }

    // ----------------------------------------------------------------------

    public void testListPrimitive() {
        assertXPathValueAndPointer(context, "list[1]", "string", "/list[1]");
    }

    public void testListBean() {
        assertXPathValueAndPointer(
            context,
            "list[2]/int",
            Integer.valueOf(1),
            "/list[2]/int");
    }

    public void testListMap() {
        assertXPathValueAndPointer(
            context,
            "list[3]/string",
            "string",
            "/list[3][@name='string']");
    }

    public void testListList() {
        /** @todo: what is this supposed to do? Should we stick to XPath,
         *  in which case [1] is simply ignored, or Java, in which case
         *  it is supposed to extract the first element from the list?
         */
//        assertXPathValueAndPointer(context,
//                "list[4][1]",
//                "string2",
//                "/list[4][1]");

        assertXPathValueAndPointer(
            context,
            "list[4]/.[1]",
            "string2",
            "/list[4]/.[1]");
    }

    public void testListDocument() {
        assertXPathValueAndPointer(
            context,
            "list[5]/vendor/location/address/city",
            "Fruit Market",
            "/list[5]/vendor[1]/location[2]/address[1]/city[1]");
    }

    public void testListElement() {
        assertXPathValueAndPointer(
            context,
            "list[6]/location/address/city",
            "Fruit Market",
            "/list[6]/location[2]/address[1]/city[1]");
    }

    public void testListContainer() {
        assertXPathValueAndPointer(
            context,
            "list[7]/vendor/location/address/city",
            "Fruit Market",
            "/list[7]/vendor[1]/location[2]/address[1]/city[1]");
    }

    public void testNull() {

        assertXPathPointerLenient(context, "$null", "$null");

        assertXPathPointerLenient(context, "$null[3]", "$null[3]");

        assertXPathPointerLenient(
            context,
            "$testnull/nothing",
            "$testnull/nothing");

        assertXPathPointerLenient(
            context,
            "$testnull/nothing[2]",
            "$testnull/nothing[2]");

        assertXPathPointerLenient(context, "beans[8]/int", "/beans[8]/int");

        assertXPathValueIterator(
            context,
            "$testnull/nothing[1]",
            list(null));

        final JXPathContext ctx = JXPathContext.newContext(new TestNull());
        assertXPathValue(ctx, "nothing", null);

        assertXPathValue(ctx, "child/nothing", null);

        assertXPathValue(ctx, "array[2]", null);

        assertXPathValueLenient(ctx, "nothing/something", null);

        assertXPathValueLenient(ctx, "array[2]/something", null);
    }

    public void testRootAsCollection() {
        assertXPathValue(context, ".[1]/string", "string");
    }

    public void testCreatePath() {
        context = JXPathContext.newContext(new TestBean());
        context.setFactory(new TestMixedModelFactory());

        final TestBean bean = (TestBean) context.getContextBean();
        bean.setMap(null);

        assertXPathCreatePath(
            context,
            "/map[@name='TestKey5']/nestedBean/int",
            Integer.valueOf(1),
            "/map[@name='TestKey5']/nestedBean/int");

        bean.setMap(null);
        assertXPathCreatePath(
            context,
            "/map[@name='TestKey5']/beans[2]/int",
            Integer.valueOf(1),
            "/map[@name='TestKey5']/beans[2]/int");
    }

    /**
     * Test JXPath.iterate() with map containing an array
     */
    public void testIterateArray() {
        final Map map = new HashMap();
        map.put("foo", new String[] { "a", "b", "c" });

        final JXPathContext context = JXPathContext.newContext(map);

        assertXPathValueIterator(context, "foo", list("a", "b", "c"));
    }

    public void testIteratePointersArray() {
        final Map map = new HashMap();
        map.put("foo", new String[] { "a", "b", "c" });

        final JXPathContext context = JXPathContext.newContext(map);

        final Iterator it = context.iteratePointers("foo");
        final List actual = new ArrayList();
        while (it.hasNext()) {
            final Pointer ptr = (Pointer) it.next();
            actual.add(context.getValue(ptr.asPath()));
        }
        assertEquals(
            "Iterating pointers <" + "foo" + ">",
            list("a", "b", "c"),
            actual);
    }

    public void testIteratePointersArrayElementWithVariable() {
        final Map map = new HashMap();
        map.put("foo", new String[] { "a", "b", "c" });

        final JXPathContext context = JXPathContext.newContext(map);
        context.getVariables().declareVariable("x", Integer.valueOf(2));
        final Iterator it = context.iteratePointers("foo[$x]");
        final List actual = new ArrayList();
        while (it.hasNext()) {
            final Pointer ptr = (Pointer) it.next();
            actual.add(context.getValue(ptr.asPath()));
        }
        assertEquals("Iterating pointers <" + "foo" + ">", list("b"), actual);
    }

    public void testIterateVector() {
        final Map map = new HashMap();
        final Vector vec = new Vector();
        vec.add(new HashMap());
        vec.add(new HashMap());

        map.put("vec", vec);
        final JXPathContext context = JXPathContext.newContext(map);
        assertXPathPointerIterator(
            context,
            "/vec",
            list("/.[@name='vec'][1]", "/.[@name='vec'][2]"));
    }

    public void testErrorProperty() {
        context.getVariables().declareVariable(
            "e",
            new ExceptionPropertyTestBean());

        boolean ex = false;
        try {
            assertXPathValue(context, "$e/errorString", null);
        }
        catch (final Throwable t) {
            ex = true;
        }
        assertTrue("Legitimate exception accessing property", ex);

        assertXPathPointer(context, "$e/errorString", "$e/errorString");

        assertXPathPointerLenient(
            context,
            "$e/errorStringArray[1]",
            "$e/errorStringArray[1]");

        assertXPathPointerIterator(
            context,
            "$e/errorString",
            list("$e/errorString"));

        assertXPathPointerIterator(
            context,
            "$e//error",
            Collections.EMPTY_LIST);
    }

    public void testMatrix() {
        assertXPathValueAndPointer(
            context,
            "$matrix[1]/.[1]",
            Integer.valueOf(3),
            "$matrix[1]/.[1]");

        context.setValue("$matrix[1]/.[1]", Integer.valueOf(2));

        assertXPathValueAndPointer(
            context,
            "matrix[1]/.[1]",
            Integer.valueOf(3),
            "/matrix[1]/.[1]");

        context.setValue("matrix[1]/.[1]", "2");

        assertXPathValue(context, "matrix[1]/.[1]", Integer.valueOf(2));

        context.getVariables().declareVariable(
            "wholebean",
            context.getContextBean());

        assertXPathValueAndPointer(
            context,
            "$wholebean/matrix[1]/.[1]",
            Integer.valueOf(2),
            "$wholebean/matrix[1]/.[1]");

        boolean ex = false;
        try {
            context.setValue("$wholebean/matrix[1]/.[2]", "4");
        }
        catch (final Exception e) {
            ex = true;
        }
        assertTrue("Exception setting value of non-existent element", ex);

        ex = false;
        try {
            context.setValue("$wholebean/matrix[2]/.[1]", "4");
        }
        catch (final Exception e) {
            ex = true;
        }
        assertTrue("Exception setting value of non-existent element", ex);
    }

    public void testCreatePathAndSetValueWithMatrix() {

        context.setValue("matrix", null);

        // Calls factory.createObject(..., TestMixedModelBean, "matrix")
        // Calls factory.createObject(..., nestedBean, "strings", 2)
        assertXPathCreatePathAndSetValue(
            context,
            "/matrix[1]/.[1]",
            Integer.valueOf(4),
            "/matrix[1]/.[1]");
    }

    /**
     * Scott Heaberlin's test - collection of collections
     */
    public void testCollectionPointer() {
        final List list = new ArrayList();
        final Map map = new HashMap();
        map.put("KeyOne", "SomeStringOne");
        map.put("KeyTwo", "SomeStringTwo");

        final Map map2 = new HashMap();
        map2.put("KeyA", "StringA");
        map2.put("KeyB", "StringB");

        map.put("KeyThree", map2);
        list.add(map);

        final List list2 = new ArrayList();
        list2.add("foo");
        list2.add(map);
        list2.add(map);
        list.add(list2);

        context = JXPathContext.newContext(list);

        assertEquals("SomeStringOne", context.getValue(".[1]/KeyOne"));
        assertEquals("StringA", context.getValue(".[1]/KeyThree/KeyA"));
        assertEquals(Integer.valueOf(3), context.getValue("size(.[1]/KeyThree)"));
        assertEquals(Double.valueOf(6.0), context.getValue("count(.[1]/KeyThree/*)"));
        assertEquals(Double.valueOf(3.0), context.getValue("count(.[1]/KeyThree/KeyA)"));
    }
}