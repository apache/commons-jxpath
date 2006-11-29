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

import junit.framework.TestCase;

import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Abstract superclass for various JXPath tests.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */

public abstract class JXPathTestCase extends TestCase {
    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public JXPathTestCase(String name) {
        super(name);
        Locale.setDefault(Locale.US);
    }
    
    protected void assertXPathValue(JXPathContext ctx,
                String xpath, Object expected)
    {
        ctx.setLenient(false);
        Object actual = ctx.getValue(xpath);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);
    }

    protected void assertXPathValue(JXPathContext ctx,
                String xpath, Object expected, Class resultType)
    {
        ctx.setLenient(false);
        Object actual = ctx.getValue(xpath, resultType);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);
    }

    protected void assertXPathValueLenient(JXPathContext ctx,
                String xpath, Object expected)
    {
        ctx.setLenient(true);
        Object actual = ctx.getValue(xpath);
        ctx.setLenient(false);
        assertEquals("Evaluating lenient <" + xpath + ">", expected, actual);
    }

    protected void assertXPathSetValue(JXPathContext ctx,
                String xpath, Object value)
    {
        assertXPathSetValue(ctx, xpath, value, value);
    }
    
    protected void assertXPathSetValue(JXPathContext ctx,
                String xpath, Object value, Object expected)
    {
        ctx.setValue(xpath, value);
        Object actual = ctx.getValue(xpath);
        assertEquals("Modifying <" + xpath + ">", expected, actual);
    }
    
    protected void assertXPathCreatePath(JXPathContext ctx,
                String xpath, 
                Object expectedValue, String expectedPath)
    {
        Pointer pointer = ctx.createPath(xpath);
        assertEquals("Creating path <" + xpath + ">", 
                expectedPath, pointer.asPath());
                
        assertEquals("Creating path (pointer value) <" + xpath + ">", 
                expectedValue, pointer.getValue());
                
        assertEquals("Creating path (context value) <" + xpath + ">", 
                expectedValue, ctx.getValue(pointer.asPath()));
    }
    
    protected void assertXPathCreatePathAndSetValue(JXPathContext ctx,
                String xpath, Object value,
                String expectedPath)
    {
        Pointer pointer = ctx.createPathAndSetValue(xpath, value);
        assertEquals("Creating path <" + xpath + ">", 
                expectedPath, pointer.asPath());
                
        assertEquals("Creating path (pointer value) <" + xpath + ">", 
                value, pointer.getValue());
                
        assertEquals("Creating path (context value) <" + xpath + ">", 
                value, ctx.getValue(pointer.asPath()));
    }    
    
    protected void assertXPathPointer(JXPathContext ctx,
                String xpath, String expected)
    {
        ctx.setLenient(false);
        Pointer pointer = ctx.getPointer(xpath);
        String actual = pointer.toString();
        assertEquals("Evaluating pointer <" + xpath + ">", expected, actual);
    }

    protected void assertXPathPointerLenient(JXPathContext ctx,
                String xpath, String expected)
    {
        ctx.setLenient(true);
        Pointer pointer = ctx.getPointer(xpath);
        String actual = pointer.toString();
        assertEquals("Evaluating pointer <" + xpath + ">", expected, actual);
    }

    protected void assertXPathValueAndPointer(JXPathContext ctx,
                String xpath, Object expectedValue, String expectedPointer)
    {
        assertXPathValue(ctx, xpath, expectedValue);
        assertXPathPointer(ctx, xpath, expectedPointer);
    }
    
    protected void assertXPathValueIterator(JXPathContext ctx,
                String xpath, Collection expected)
    {
        Collection actual;
        if (expected instanceof List) {
            actual = new ArrayList();
        }
        else {
            actual = new HashSet();
        }
        Iterator it = ctx.iterate(xpath);
        while (it.hasNext()) {
            actual.add(it.next());
        }
        assertEquals("Evaluating value iterator <" + xpath + ">",
                expected, actual);
    }

    protected void assertXPathPointerIterator(
        JXPathContext ctx,
        String xpath,
        Collection expected) 
    {
        Collection actual;
        if (expected instanceof List) {
            actual = new ArrayList();
        }
        else {
            actual = new HashSet();
        }
        Iterator it = ctx.iteratePointers(xpath);
        while (it.hasNext()) {
            Pointer pointer = (Pointer) it.next();
            actual.add(pointer.toString());
        }
        assertEquals(
            "Evaluating pointer iterator <" + xpath + ">",
            expected,
            actual);
    }

    protected void assertDocumentOrder(
        JXPathContext context,
        String path1,
        String path2,
        int expected) 
    {
        NodePointer np1 = (NodePointer) context.getPointer(path1);
        NodePointer np2 = (NodePointer) context.getPointer(path2);
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
            JXPathContext ctx,
            String xpath,
            Class clazz) 
    {
        ctx.setLenient(false);
        Object actual = ctx.getValue(xpath);
        assertTrue("Evaluating <" + xpath + "> = " + actual.getClass(), 
                clazz.isAssignableFrom(actual.getClass()));
    }
    
    protected void assertXPathNodeType(
            JXPathContext ctx,
            String xpath,
            Class clazz) 
    {
        ctx.setLenient(false);
        Pointer actual = ctx.getPointer(xpath);
        assertTrue("Evaluating <" + xpath + "> = " + actual.getNode().getClass(), 
                clazz.isAssignableFrom(actual.getNode().getClass()));
    }
    
    protected static List list() {
        return Collections.EMPTY_LIST;
    }

    protected static List list(Object o1) {
        List list = new ArrayList();
        list.add(o1);
        return list;
    }

    protected static List list(Object o1, Object o2) {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        return list;
    }

    protected static List list(Object o1, Object o2, Object o3) {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        return list;
    }

    protected static Set set(Object o1, Object o2, Object o3) {
        Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        return list;
    }

    protected static List list(Object o1, Object o2, Object o3, Object o4) {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        return list;
    }

    protected static Set set(Object o1, Object o2, Object o3, Object o4) {
        Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        return list;
    }

    protected static List list(Object o1, Object o2, Object o3,
                Object o4, Object o5)
    {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        return list;
    }

    protected static Set set(Object o1, Object o2, Object o3, 
                Object o4, Object o5) 
    {
        Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        return list;
    }

    protected static List list(Object o1, Object o2, Object o3,
                Object o4, Object o5, Object o6)
    {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        return list;
    }

    protected static Set set(Object o1, Object o2, Object o3,
                Object o4, Object o5, Object o6)
    {
        Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        return list;
    }
    
    protected static List list(Object o1, Object o2, Object o3,
                Object o4, Object o5, Object o6, Object o7)
    {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        list.add(o7);
        return list;
    }

    protected static Set set(Object o1, Object o2, Object o3,
                Object o4, Object o5, Object o6, Object o7)
    {
        Set list = new HashSet();
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