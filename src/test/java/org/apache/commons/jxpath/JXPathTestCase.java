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

import junit.framework.TestCase;

/**
 * Abstract superclass for various JXPath tests.
 */
public abstract class JXPathTestCase extends TestCase {

    /**
     * Construct a new instance of this test case.
     */
    public JXPathTestCase() {
        Locale.setDefault(Locale.US);
    }

    protected void assertXPathValue(final JXPathContext ctx,
                final String xpath, final Object expected)
    {
        ctx.setLenient(false);
        final Object actual = ctx.getValue(xpath);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);
    }

    protected void assertXPathValue(final JXPathContext ctx,
                final String xpath, final Object expected, final Class resultType)
    {
        ctx.setLenient(false);
        final Object actual = ctx.getValue(xpath, resultType);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);
    }

    protected void assertXPathValueLenient(final JXPathContext ctx,
                final String xpath, final Object expected)
    {
        ctx.setLenient(true);
        final Object actual = ctx.getValue(xpath);
        ctx.setLenient(false);
        assertEquals("Evaluating lenient <" + xpath + ">", expected, actual);
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
        assertEquals("Modifying <" + xpath + ">", expected, actual);
    }

    protected void assertXPathCreatePath(final JXPathContext ctx,
                final String xpath,
                final Object expectedValue, final String expectedPath)
    {
        final Pointer pointer = ctx.createPath(xpath);
        assertEquals("Creating path <" + xpath + ">",
                expectedPath, pointer.asPath());

        assertEquals("Creating path (pointer value) <" + xpath + ">",
                expectedValue, pointer.getValue());

        assertEquals("Creating path (context value) <" + xpath + ">",
                expectedValue, ctx.getValue(pointer.asPath()));
    }

    protected void assertXPathCreatePathAndSetValue(final JXPathContext ctx,
                final String xpath, final Object value,
                final String expectedPath)
    {
        final Pointer pointer = ctx.createPathAndSetValue(xpath, value);
        assertEquals("Creating path <" + xpath + ">",
                expectedPath, pointer.asPath());

        assertEquals("Creating path (pointer value) <" + xpath + ">",
                value, pointer.getValue());

        assertEquals("Creating path (context value) <" + xpath + ">",
                value, ctx.getValue(pointer.asPath()));
    }

    protected void assertXPathPointer(final JXPathContext ctx,
                final String xpath, final String expected)
    {
        ctx.setLenient(false);
        final Pointer pointer = ctx.getPointer(xpath);
        final String actual = pointer.toString();
        assertEquals("Evaluating pointer <" + xpath + ">", expected, actual);
    }

    protected void assertXPathPointerLenient(final JXPathContext ctx,
                final String xpath, final String expected)
    {
        ctx.setLenient(true);
        final Pointer pointer = ctx.getPointer(xpath);
        final String actual = pointer.toString();
        assertEquals("Evaluating pointer <" + xpath + ">", expected, actual);
    }

    protected void assertXPathValueAndPointer(final JXPathContext ctx,
                final String xpath, final Object expectedValue, final String expectedPointer)
    {
        assertXPathValue(ctx, xpath, expectedValue);
        assertXPathPointer(ctx, xpath, expectedPointer);
    }

    protected void assertXPathValueIterator(final JXPathContext ctx,
                final String xpath, final Collection expected)
    {
        Collection actual;
        if (expected instanceof List) {
            actual = new ArrayList();
        }
        else {
            actual = new HashSet();
        }
        final Iterator it = ctx.iterate(xpath);
        while (it.hasNext()) {
            actual.add(it.next());
        }
        assertEquals("Evaluating value iterator <" + xpath + ">",
                expected, actual);
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
        final Iterator it = ctx.iteratePointers(xpath);
        while (it.hasNext()) {
            final Pointer pointer = (Pointer) it.next();
            actual.add(pointer.toString());
        }
        assertEquals(
            "Evaluating pointer iterator <" + xpath + ">",
            expected,
            actual);
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
            "Comparing paths '" + path1 + "' and '" + path2 + "'",
            expected,
            res);
    }

    protected void assertXPathValueType(
            final JXPathContext ctx,
            final String xpath,
            final Class clazz)
    {
        ctx.setLenient(false);
        final Object actual = ctx.getValue(xpath);
        assertTrue("Evaluating <" + xpath + "> = " + actual.getClass(),
                clazz.isAssignableFrom(actual.getClass()));
    }

    protected void assertXPathNodeType(
            final JXPathContext ctx,
            final String xpath,
            final Class clazz)
    {
        ctx.setLenient(false);
        final Pointer actual = ctx.getPointer(xpath);
        assertTrue("Evaluating <" + xpath + "> = " + actual.getNode().getClass(),
                clazz.isAssignableFrom(actual.getNode().getClass()));
    }

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

    protected static List list(final Object o1, final Object o2, final Object o3, final Object o4) {
        final List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
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

}