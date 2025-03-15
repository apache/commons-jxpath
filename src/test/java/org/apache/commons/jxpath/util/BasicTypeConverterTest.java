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
package org.apache.commons.jxpath.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.Pointer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests BasicTypeConverter
 */
public class BasicTypeConverterTest {

    @Test
    public void testPrimitiveToString() {
        assertConversion(Integer.valueOf(1), String.class, "1");
    }

    @Test
    public void testArrayToList() {
        assertConversion(
            new int[] { 1, 2 },
            List.class,
            Arrays.asList(new Object[] { Integer.valueOf(1), Integer.valueOf(2)}));
    }

    @Test
    public void testArrayToArray() {
        assertConversion(
            new int[] { 1, 2 },
            String[].class,
            Arrays.asList(new String[] { "1", "2" }));
    }

    @Test
    public void testListToArray() {
        assertConversion(
            Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(2)}),
            String[].class,
            Arrays.asList(new String[] { "1", "2" }));

        assertConversion(
            Arrays.asList(new String[] { "1", "2" }),
            int[].class,
            Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(2)}));
    }

    @Test
    public void testInvalidConversion() {
        assertThrows(Exception.class, () -> TypeUtils.convert("'foo'", Date.class),
            "Type conversion exception");
    }

    public void assertConversion(final Object from, final Class toType, final Object expected) {
        final boolean can = TypeUtils.canConvert(from, toType);
        assertTrue(can, "Can convert: " + from.getClass() + " to " + toType);
        Object result = TypeUtils.convert(from, toType);
        if (result.getClass().isArray()) {
            final ArrayList list = new ArrayList();
            for (int j = 0; j < Array.getLength(result); j++) {
                list.add(Array.get(result, j));
            }
            result = list;
        }
        assertEquals(
            expected,
            result,
            "Convert: " + from.getClass() + " to " + toType);
    }

    @Test
    public void testSingletonCollectionToString() {
        assertConversion(Collections.singleton("Earth"), String.class, "Earth");
    }

    @Test
    public void testSingletonArrayToString() {
        assertConversion(new String[] { "Earth" }, String.class, "Earth");
    }

    @Test
    public void testPointerToString() {
        assertConversion(new Pointer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Object getValue() {
                return "value";
            }
            @Override
            public Object getNode() {
                return null;
            }
            @Override
            public void setValue(final Object value) {
            }
            @Override
            public Object getRootNode() {
                return null;
            }
            @Override
            public String asPath() {
                return null;
            }
            @Override
            public Object clone() {
                return null;
            }
            @Override
            public int compareTo(final Object o) {
                return 0;
            }
        }, String.class, "value");
    }

    @Test
    public void testNodeSetToString() {
        assertConversion(new NodeSet() {
            @Override
            public List getNodes() {
                return null;
            }
            @Override
            public List getPointers() {
                return null;
            }
            @Override
            public List getValues() {
                final List list = new ArrayList();
                list.add("hello");
                list.add("goodbye");
                return Collections.singletonList(list);
            }
        }, String.class, "hello");
    }

    // succeeds in current version
    @Test
    public void testNodeSetToInteger() {
        assertConversion(new NodeSet() {
            @Override
            public List getNodes() {
                return null;
            }
            @Override
            public List getPointers() {
                return null;
            }
            @Override
            public List getValues() {
                return Collections.singletonList("9");
            }
        }, Integer.class, Integer.valueOf(9));
    }

    @Test
    public void testBeanUtilsConverter() {
        assertConversion("12", BigDecimal.class, new BigDecimal(12));
    }
}