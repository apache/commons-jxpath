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
package org.apache.commons.jxpath.ri.model.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.TestBean;

/**
 * TODO more iterator testing with maps
 */

public class DynamicPropertiesModelTest extends JXPathTestCase {
    private JXPathContext context;

    @Override
    public void setUp() {
        if (context == null) {
            context = JXPathContext.newContext(new TestBean());
            context.setFactory(new TestDynamicPropertyFactory());
        }
    }

    public void testAxisChild() {
        assertXPathValue(context, "map/Key1", "Value 1");

        assertXPathPointer(context, "map/Key1", "/map[@name='Key1']");

        assertXPathValue(context, "map/Key2/name", "Name 6");

        assertXPathPointer(context, "map/Key2/name", "/map[@name='Key2']/name");
    }

    public void testAxisDescendant() {
        assertXPathValue(context, "//Key1", "Value 1");
    }

    /**
     * Testing the pseudo-attribute "name" that dynamic property
     * objects appear to have.
     */
    public void testAttributeName() {
        assertXPathValue(context, "map[@name = 'Key1']", "Value 1");

        assertXPathPointer(
            context,
            "map[@name = 'Key1']",
            "/map[@name='Key1']");

        assertXPathPointerLenient(
            context,
            "map[@name = 'Key&quot;&apos;&quot;&apos;1']",
            "/map[@name='Key&quot;&apos;&quot;&apos;1']");

        assertXPathValue(context, "/.[@name='map']/Key2/name", "Name 6");

        assertXPathPointer(
            context,
            "/.[@name='map']/Key2/name",
            "/map[@name='Key2']/name");

        // Bean in a map
        assertXPathValue(context, "/map[@name='Key2'][@name='name']", "Name 6");

        assertXPathPointer(
            context,
            "/map[@name='Key2'][@name='name']",
            "/map[@name='Key2']/name");

        // Map in a bean in a map
        assertXPathValue(
            context,
            "/.[@name='map'][@name='Key2'][@name='name']",
            "Name 6");

        assertXPathPointer(
            context,
            "/.[@name='map'][@name='Key2'][@name='name']",
            "/map[@name='Key2']/name");

        ((Map)context.getValue("map")).put("Key:3", "value3");

        assertXPathValueAndPointer(
            context,
            "/map[@name='Key:3']",
            "value3",
            "/map[@name='Key:3']");

        assertXPathValueAndPointer(
            context,
            "/map[@name='Key:4:5']",
            null,
            "/map[@name='Key:4:5']");
    }

    public void testSetPrimitiveValue() {
        assertXPathSetValue(context, "map/Key1", Integer.valueOf(6));
    }

    public void testSetCollection() {
        // See if we can assign a whole collection
        context.setValue(
            "map/Key1",
            new Integer[] { Integer.valueOf(7), Integer.valueOf(8)});

        // And then an element in that collection
        assertXPathSetValue(context, "map/Key1[1]", Integer.valueOf(9));
    }

    /**
     * The key does not exist, but the assignment should succeed anyway,
     * because you should always be able to store anything in a Map.
     */
    public void testSetNewKey() {
        // Using a "simple" path
        assertXPathSetValue(context, "map/Key4", Integer.valueOf(7));

        // Using a "non-simple" path
        assertXPathPointerLenient(context, "//map/Key5", "/map/Key5");

        assertXPathSetValue(context, "//map/Key5", Integer.valueOf(8));
    }

    public void testCreatePath() {
        final TestBean bean = (TestBean) context.getContextBean();
        bean.setMap(null);

        // Calls factory.createObject(..., testBean, "map"), then
        // sets the value
        assertXPathCreatePath(
            context,
            "/map[@name='TestKey1']",
            "",
            "/map[@name='TestKey1']");
    }

    public void testCreatePathAndSetValue() {
        final TestBean bean = (TestBean) context.getContextBean();
        bean.setMap(null);

        // Calls factory.createObject(..., testBean, "map"), then
        // sets the value
        assertXPathCreatePathAndSetValue(
            context,
            "/map[@name='TestKey1']",
            "Test",
            "/map[@name='TestKey1']");
    }

    public void testCreatePathCreateBean() {
        final TestBean bean = (TestBean) context.getContextBean();
        bean.setMap(null);

        // Calls factory.createObject(..., testBean, "map"), then
        // then factory.createObject(..., map, "TestKey2"), then
        // sets the value
        assertXPathCreatePath(
            context,
            "/map[@name='TestKey2']/int",
            Integer.valueOf(1),
            "/map[@name='TestKey2']/int");
    }

    public void testCreatePathAndSetValueCreateBean() {
        final TestBean bean = (TestBean) context.getContextBean();
        bean.setMap(null);

        // Calls factory.createObject(..., testBean, "map"), then
        // then factory.createObject(..., map, "TestKey2"), then
        // sets the value
        assertXPathCreatePathAndSetValue(
            context,
            "/map[@name='TestKey2']/int",
            Integer.valueOf(4),
            "/map[@name='TestKey2']/int");
    }

    public void testCreatePathCollectionElement() {
        final TestBean bean = (TestBean) context.getContextBean();
        bean.setMap(null);

        assertXPathCreatePath(
            context,
            "/map/TestKey3[2]",
            null,
            "/map[@name='TestKey3'][2]");

        // Should be the same as the one before
        assertXPathCreatePath(
            context,
            "/map[@name='TestKey3'][3]",
            null,
            "/map[@name='TestKey3'][3]");
    }

    public void testCreatePathAndSetValueCollectionElement() {
        final TestBean bean = (TestBean) context.getContextBean();
        bean.setMap(null);

        assertXPathCreatePathAndSetValue(
            context,
            "/map/TestKey3[2]",
            "Test1",
            "/map[@name='TestKey3'][2]");

        // Should be the same as the one before
        assertXPathCreatePathAndSetValue(
            context,
            "/map[@name='TestKey3'][3]",
            "Test2",
            "/map[@name='TestKey3'][3]");
    }

    public void testCreatePathNewCollectionElement() {
        final TestBean bean = (TestBean) context.getContextBean();
        bean.setMap(null);

        // Create an element of a dynamic map element, which is a collection
        assertXPathCreatePath(
            context,
            "/map/TestKey4[1]/int",
            Integer.valueOf(1),
            "/map[@name='TestKey4'][1]/int");

        bean.getMap().remove("TestKey4");

        // Should be the same as the one before
        assertXPathCreatePath(
            context,
            "/map/TestKey4[1]/int",
            Integer.valueOf(1),
            "/map[@name='TestKey4'][1]/int");
    }

    public void testCreatePathAndSetValueNewCollectionElement() {
        final TestBean bean = (TestBean) context.getContextBean();
        bean.setMap(null);

        // Create an element of a dynamic map element, which is a collection
        assertXPathCreatePathAndSetValue(
            context,
            "/map/TestKey4[1]/int",
            Integer.valueOf(2),
            "/map[@name='TestKey4'][1]/int");

        bean.getMap().remove("TestKey4");

        // Should be the same as the one before
        assertXPathCreatePathAndSetValue(
            context,
            "/map/TestKey4[1]/int",
            Integer.valueOf(3),
            "/map[@name='TestKey4'][1]/int");
    }

    public void testRemovePath() {
        final TestBean bean = (TestBean) context.getContextBean();
        bean.getMap().put("TestKey1", "test");

        // Remove dynamic property
        context.removePath("map[@name = 'TestKey1']");
        assertEquals(
            "Remove dynamic property value",
            null,
            context.getValue("map[@name = 'TestKey1']"));
    }

    public void testRemovePathArrayElement() {
        final TestBean bean = (TestBean) context.getContextBean();

        bean.getMap().put("TestKey2", new String[] { "temp1", "temp2" });
        context.removePath("map[@name = 'TestKey2'][1]");
        assertEquals(
            "Remove dynamic property collection element",
            "temp2",
            context.getValue("map[@name = 'TestKey2'][1]"));
    }

    public void testCollectionOfMaps() {
        final TestBean bean = (TestBean) context.getContextBean();
        final List list = new ArrayList();

        bean.getMap().put("stuff", list);

        Map m = new HashMap();
        m.put("fruit", "apple");
        list.add(m);

        m = new HashMap();
        m.put("berry", "watermelon");
        list.add(m);

        m = new HashMap();
        m.put("fruit", "banana");
        list.add(m);

        assertXPathValueIterator(
            context,
            "/map/stuff/fruit",
            list("apple", "banana"));

        assertXPathValueIterator(
            context,
            "/map/stuff[@name='fruit']",
            list("apple", "banana"));
    }

    public void testMapOfMaps() {
        final TestBean bean = (TestBean) context.getContextBean();

        final Map fruit = new HashMap();
        fruit.put("apple", "green");
        fruit.put("orange", "red");

        final Map meat = new HashMap();
        meat.put("pork", "pig");
        meat.put("beef", "cow");

        bean.getMap().put("fruit", fruit);
        bean.getMap().put("meat", meat);

        assertXPathPointer(
            context,
            "//beef",
            "/map[@name='meat'][@name='beef']");

        assertXPathPointer(
            context,
            "map//apple",
            "/map[@name='fruit'][@name='apple']");

        // Ambiguous search - will return nothing
        assertXPathPointerLenient(context, "map//banana", "null()");

        // Unambiguous, even though the particular key is missing
        assertXPathPointerLenient(
            context,
            "//fruit/pear",
            "/map[@name='fruit']/pear");
    }
}
