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

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.AbstractJXPathTest;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.model.beans.BeanPropertyPointer;
import org.apache.commons.jxpath.ri.model.beans.NullPropertyPointer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JXPath172Test extends AbstractJXPathTest
{

    @Test
    public void testIssue172_propertyExistAndIsNotNull()
    {
        final JXPathContext context = getContext("ciao", false);
        final Object bRet = context.selectSingleNode("value");
        assertNotNull(bRet, "null!!");
        assertEquals("ciao", bRet, "Is " + bRet.getClass());

        final Pointer pointer = context.getPointer("value");
        assertNotNull(pointer);
        assertEquals(BeanPropertyPointer.class, pointer.getClass());
        assertEquals("ciao", pointer.getValue());
    }

    @Test
    public void testIssue172_propertyExistAndIsNull()
    {
        final JXPathContext context = getContext(null, false);
        final Object bRet = context.selectSingleNode("value");
        assertNull(bRet, "not null!!");

        final Pointer pointer = context.getPointer("value");
        assertNotNull(pointer);
        assertEquals(BeanPropertyPointer.class, pointer.getClass());
        assertNull(pointer.getValue());
    }

    @Test
    public void testIssue172_PropertyUnexisting()
    {
        final JXPathContext context = getContext(null, true);
        final Object bRet = context.selectSingleNode("unexisting");
        assertNull(bRet, "not null!!");

        final Pointer pointer = context.getPointer("unexisting");
        assertNotNull(pointer);
        assertEquals(NullPropertyPointer.class, pointer.getClass());
        assertNull(pointer.getValue());
    }

    @Test
    public void testIssue172_NestedPropertyUnexisting()
    {
        final JXPathContext context = getContext(null, true);
        final Object bRet = context.selectSingleNode("value.child");
        assertNull(bRet, "not null!!");

        final Pointer pointer = context.getPointer("value.child");
        assertNotNull(pointer);
        assertEquals(NullPropertyPointer.class, pointer.getClass());
        assertNull(pointer.getValue());
    }

    @Test
    public void testIssue172_propertyDoesNotExist_NotLenient()
    {
        final JXPathContext context = getContext(null, false);

        assertThrows(JXPathNotFoundException.class, () -> context.selectSingleNode("unexisting"));
        assertThrows(JXPathNotFoundException.class, () -> context.getPointer("unexisting"));
        assertThrows(JXPathNotFoundException.class, () -> context.getPointer("value.unexisting"));
    }

    /**
     * Helper, returns a {@link JXPathContext} filled with {@link TestBean172}
     * whose {@link TestBean172#getValue()} method returns the passed
     * {@code val} value.
     *
     * @param val
     * @return A {@link JXPathContext}, never {@code null}.
     */
    private JXPathContext getContext(final String val, final boolean lenient)
    {
        final TestBean172 b = new TestBean172();
        b.setValue(val);
        final Object target = b;
        final JXPathContext context = JXPathContext.newContext(null, target);
        context.setLenient(lenient);
        return context;
    }

    public static class TestBean172
    {
        String value;

        public String getValue()
        {
            return value;
        }

        public void setValue(final String value)
        {
            this.value = value;
        }
    }

}
