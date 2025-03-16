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
package org.apache.commons.jxpath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.jxpath.ri.model.NodePointer;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract superclass for various JXPath tests.
 */
public abstract class AbstractJXPathTest {

    protected static List list() {
        return Collections.EMPTY_LIST;
    }

    protected static List list(final Object o1) {
        final List list = new ArrayList();
        list.add(o1);
        return list;
    }

    protected static List list(final Object o1, final Object o2) {
        final List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        return list;
    }

    protected static List list(final Object o1, final Object o2, final Object o3) {
        final List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        return list;
    }

    protected static List list(final Object o1, final Object o2, final Object o3, final Object o4) {
        final List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        return list;
    }

    protected static List list(final Object o1, final Object o2, final Object o3,
                final Object o4, final Object o5)
    {
        final List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        return list;
    }

    protected static List list(final Object o1, final Object o2, final Object o3,
                final Object o4, final Object o5, final Object o6)
    {
        final List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        return list;
    }

    protected static List list(final Object o1, final Object o2, final Object o3,
                final Object o4, final Object o5, final Object o6, final Object o7)
    {
        final List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        list.add(o7);
        return list;
    }

    protected static Set set(final Object o1, final Object o2) {
        final Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        return list;
    }

    protected static Set set(final Object o1, final Object o2, final Object o3) {
        final Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        return list;
    }

    protected static Set set(final Object o1, final Object o2, final Object o3, final Object o4) {
        final Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        return list;
    }

    protected static Set set(final Object o1, final Object o2, final Object o3,
                final Object o4, final Object o5)
    {
        final Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        return list;
    }

    protected static Set set(final Object o1, final Object o2, final Object o3,
                final Object o4, final Object o5, final Object o6)
    {
        final Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        return list;
    }

    protected static Set set(final Object o1, final Object o2, final Object o3,
                final Object o4, final Object o5, final Object o6, final Object o7)
    {
        final Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        list.add(o7);
        return list;
    }

    protected void assertDocumentOrder(
        final JXPathContext context,
        final String path1,
        final String path2,
        final int expected)
    {
        final NodePointer np1 = (NodePointer) context.getPointer(path1);
        final NodePointer np2 = (NodePointer) context.getPointer(path2);
        int res = np1.compareTo(np2);
        if (res < 0) {
            res = -1;
        }
        else if (res > 0) {
            res = 1;
        }
        assertEquals(
            expected,
            res,
            "Comparing paths '" + path1 + "' and '" + path2 + "'");
    }

    protected void assertXPathCreatePath(final JXPathContext ctx,
                final String xpath,
                final Object expectedValue, final String expectedPath)
    {
        final Pointer pointer = ctx.createPath(xpath);
        assertEquals(expectedPath, pointer.asPath(),
                "Creating path <" + xpath + ">");

        assertEquals(expectedValue, pointer.getValue(),
                "Creating path (pointer value) <" + xpath + ">");

        assertEquals(expectedValue, ctx.getValue(pointer.asPath()),
                "Creating path (context value) <" + xpath + ">");
    }

    protected void assertXPathCreatePathAndSetValue(final JXPathContext ctx,
                final String xpath, final Object value,
                final String expectedPath)
    {
        final Pointer pointer = ctx.createPathAndSetValue(xpath, value);
        assertEquals(expectedPath, pointer.asPath(),
                "Creating path <" + xpath + ">");

        assertEquals(value, pointer.getValue(),
                "Creating path (pointer value) <" + xpath + ">");

        assertEquals(value, ctx.getValue(pointer.asPath()),
                "Creating path (context value) <" + xpath + ">");
    }

    protected void assertXPathNodeType(
            final JXPathContext ctx,
            final String xpath,
            final Class clazz)
    {
        ctx.setLenient(false);
        final Pointer actual = ctx.getPointer(xpath);
        assertTrue(clazz.isAssignableFrom(actual.getNode().getClass()),
                "Evaluating <" + xpath + "> = " + actual.getNode().getClass());
    }

    protected void assertXPathPointer(final JXPathContext ctx,
                final String xpath, final String expected)
    {
        ctx.setLenient(false);
        final Pointer pointer = ctx.getPointer(xpath);
        final String actual = pointer.toString();
        assertEquals(expected, actual, "Evaluating pointer <" + xpath + ">");
    }

    protected void assertXPathPointerIterator(
        final JXPathContext ctx,
        final String xpath,
        final Collection expected)
    {
        Collection actual;
        if (expected instanceof List) {
            actual = new ArrayList();
        }
        else {
            actual = new HashSet();
        }
        final Iterator<Pointer> it = ctx.iteratePointers(xpath);
        while (it.hasNext()) {
            final Pointer pointer = it.next();
            actual.add(pointer.toString());
        }
        assertEquals(
            expected,
            actual,
            "Evaluating pointer iterator <" + xpath + ">");
    }

    protected void assertXPathPointerLenient(final JXPathContext ctx,
                final String xpath, final String expected)
    {
        ctx.setLenient(true);
        final Pointer pointer = ctx.getPointer(xpath);
        final String actual = pointer.toString();
        assertEquals(expected, actual, "Evaluating pointer <" + xpath + ">");
    }

    protected void assertXPathSetValue(final JXPathContext ctx,
                final String xpath, final Object value)
    {
        assertXPathSetValue(ctx, xpath, value, value);
    }

    protected void assertXPathSetValue(final JXPathContext ctx,
                final String xpath, final Object value, final Object expected)
    {
        ctx.setValue(xpath, value);
        final Object actual = ctx.getValue(xpath);
        assertEquals(expected, actual, "Modifying <" + xpath + ">");
    }

    protected void assertXPathValue(final JXPathContext ctx,
                final String xpath, final Object expected)
    {
        ctx.setLenient(false);
        final Object actual = ctx.getValue(xpath);
        assertEquals(expected, actual, "Evaluating <" + xpath + ">");
    }

    protected void assertXPathValue(final JXPathContext ctx,
                final String xpath, final Object expected, final Class resultType)
    {
        ctx.setLenient(false);
        final Object actual = ctx.getValue(xpath, resultType);
        assertEquals(expected, actual, "Evaluating <" + xpath + ">");
    }

    protected void assertXPathValueAndPointer(final JXPathContext ctx,
                final String xpath, final Object expectedValue, final String expectedPointer)
    {
        assertXPathValue(ctx, xpath, expectedValue);
        assertXPathPointer(ctx, xpath, expectedPointer);
    }

    protected <E> void assertXPathValueIterator(final JXPathContext ctx,
                final String xpath, final Collection<E> expected)
    {
        Collection<E> actual;
        if (expected instanceof List) {
            actual = new ArrayList<>();
        } else {
            actual = new HashSet<>();
        }
        final Iterator<E> it = ctx.iterate(xpath);
        while (it.hasNext()) {
            actual.add(it.next());
        }
        assertEquals(expected.hashCode(), actual.hashCode(),
                String.format("[hashCode()] Evaluating value iterator <%s>, expected.class %s(%,d): %s, actual.class %s(%,d): %s", xpath,
                        expected.getClass(), expected.size(), expected, actual.getClass(), actual.size(), actual));
        assertEquals(expected, actual,
                String.format("[equals()] Evaluating value iterator <%s>, expected.class %s(%,d): %s, actual.class %s(%,d): %s", xpath,
                        expected.getClass(), expected.size(), expected, actual.getClass(), actual.size(), actual));
    }

    protected void assertXPathValueLenient(final JXPathContext ctx,
                final String xpath, final Object expected)
    {
        ctx.setLenient(true);
        final Object actual = ctx.getValue(xpath);
        ctx.setLenient(false);
        assertEquals(expected, actual, "Evaluating lenient <" + xpath + ">");
    }

    protected void assertXPathValueType(
            final JXPathContext ctx,
            final String xpath,
            final Class clazz)
    {
        ctx.setLenient(false);
        final Object actual = ctx.getValue(xpath);
        assertTrue(clazz.isAssignableFrom(actual.getClass()), "Evaluating <" + xpath + "> = " + actual.getClass());
    }

    /**
     * Constructs a new instance of this test case.
     *
     * @throws Exception In case of errors during setup
     */
    @BeforeEach
    protected void setUp() throws Exception
    {
        Locale.setDefault(Locale.US);
    }

}