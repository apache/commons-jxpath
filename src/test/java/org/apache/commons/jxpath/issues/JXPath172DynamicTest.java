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
package org.apache.commons.jxpath.issues;

import java.util.HashMap;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.model.dynamic.DynamicPropertyPointer;

import junit.framework.TestSuite;

public class JXPath172DynamicTest extends JXPathTestCase
{

    /**
     * Return the tests included in this test suite.
     */
    public static TestSuite suite()
    {
        return new TestSuite(JXPath172DynamicTest.class);
    }

    public void testIssue172_propertyExistAndIsNotNull()
    {
        final JXPathContext context = getContext("ciao", false);
        final Object bRet = context.selectSingleNode("value");
        assertNotNull("null!!", bRet);
        assertEquals("Is " + bRet.getClass(), "ciao", bRet);

        final Pointer pointer = context.getPointer("value");
        assertNotNull(pointer);
        assertEquals(DynamicPropertyPointer.class, pointer.getClass());
        assertEquals("ciao", pointer.getValue());
    }

    public void testIssue172_propertyExistAndIsNull()
    {
        final JXPathContext context = getContext(null, false);
        final Object bRet = context.selectSingleNode("value");
        assertNull("not null!!", bRet);

        final Pointer pointer = context.getPointer("value");
        assertNotNull(pointer);
        assertEquals(DynamicPropertyPointer.class, pointer.getClass());
        assertNull(pointer.getValue());
    }

    public void testIssue172_propertyDoesNotExist()
    {
        final JXPathContext context = getContext(null, false);
        final Object bRet = context.selectSingleNode("unexisting");
        assertNull(bRet);

        final Pointer pointer = context.getPointer("unexisting");
        assertEquals(DynamicPropertyPointer.class, pointer.getClass());
        assertNull(pointer.getValue());

    }

    public void testIssue172_propertyDoesNotExist_Lenient()
    {
        final JXPathContext context = getContext(null, true);
        final Object bRet = context.selectSingleNode("unexisting");
        assertNull(bRet);

        final Pointer pointer = context.getPointer("unexisting");
        assertEquals(DynamicPropertyPointer.class, pointer.getClass());
        assertNull(pointer.getValue());

    }

    public void testIssue172_nestedpropertyDoesNotExist_Lenient()
    {
        final JXPathContext context = getContext(null, true);
        final Object bRet = context.selectSingleNode("value.unexisting");
        assertNull(bRet);

        final Pointer pointer = context.getPointer("value.unexisting");
        assertEquals(DynamicPropertyPointer.class, pointer.getClass());
        assertNull(pointer.getValue());

    }

    public void testIssue172_nestedpropertyDoesNotExist_NotLenient()
    {
        final JXPathContext context = getContext(null, false);
        final Object bRet = context.selectSingleNode("value.unexisting");
        assertNull(bRet);

        final Pointer pointer = context.getPointer("value.unexisting");
        assertEquals(DynamicPropertyPointer.class, pointer.getClass());
        assertNull(pointer.getValue());

    }

    /**
     * Helper, returns a {@link JXPathContext} filled with a Map whose "value"
     * key is associated to the passed <code>val</code> value.
     *
     * @param val
     * @return A {@link JXPathContext}, never <code>null</code>.
     */
    private JXPathContext getContext(final String val, final boolean lenient)
    {
        final HashMap map = new HashMap();
        // if (val!=null) // no diffs
        map.put("value", val);
        final Object target = map;
        final JXPathContext context = JXPathContext.newContext(null, target);
        context.setLenient(lenient);
        return context;
    }

}
