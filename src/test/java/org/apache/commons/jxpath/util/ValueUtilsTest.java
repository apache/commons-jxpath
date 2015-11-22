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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

public class ValueUtilsTest extends TestCase {

    
    
    public void testGetValueFromArrayTooSmall() {
        assertNull(ValueUtils.getValue(new Object[0], 2));
    }

    public void testGetValueFromListTooSmall() {
        assertNull(ValueUtils.getValue(Collections.EMPTY_LIST, 2));
    }

    /*
     * This test would break without the patch and an NoSuchElementException being
     * thrown instead.
     */
    public void testGetValueFromSetTooSmall() {
        assertNull(ValueUtils.getValue(Collections.EMPTY_SET, 2));
    }

    public void testGetValueFromArray() {
        final Object data = new Object();
        assertSame(data, ValueUtils.getValue(new Object[] {data}, 0));
    }

    public void testGetValueFromList() {
        final Object data = new Object();
        assertSame(data, ValueUtils.getValue(Arrays.asList(new Object[]{data}), 0));
    }

    public void testGetValueFromSet() {
        final Object data = new Object();
        final Set dataSet = new HashSet();
        dataSet.add(data);
        assertSame(data, ValueUtils.getValue(dataSet, 0));
    }
    
    public void testGetValueFromArrayNegativeIndex() {
        final Object data = new Object();
        assertNull(ValueUtils.getValue(new Object[] {data}, -1));
    }

    public void testGetValueFromListNegativeIndex() {
        final Object data = new Object();
        final Object res = ValueUtils.getValue(Arrays.asList(new Object[]{data}), -1);
        assertNull("Expected null, is " + res, res);
    }

    public void testGetValueFromSetNegativeIndex() {
        final Object data = new Object();
        final Set dataSet = new HashSet();
        dataSet.add(data);
        assertNull(ValueUtils.getValue(dataSet, -1));
    }
}
