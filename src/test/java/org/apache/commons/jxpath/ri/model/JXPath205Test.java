/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.ri.model;

import java.util.List;

import org.apache.commons.jxpath.AbstractJXPathTest;
import org.apache.commons.jxpath.JXPathContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests that a null collection or array property has no elements, while a null property of another type is a single node (JXPATH-151).
 */
class JXPath205Test extends AbstractJXPathTest {

    public static final class Bean {

        public String[] getLetters() {
            return null;
        }

        public Object getNothing() {
            return null;
        }

        public List<String> getWords() {
            return null;
        }
    }

    private JXPathContext context;

    @Override
    @BeforeEach
    public void setUp() {
        context = JXPathContext.newContext(new Bean());
    }

    @Test
    void testCountOfNullArray() {
        assertXPathValue(context, "count(letters)", Double.valueOf(0));
    }

    @Test
    void testCountOfNullCollection() {
        assertXPathValue(context, "count(words)", Double.valueOf(0));
    }

    @Test
    void testCountOfNullObject() {
        assertXPathValue(context, "count(nothing)", Double.valueOf(1));
    }

    @Test
    void testSizeOfNullCollection() {
        assertXPathValue(context, "size(words)", Integer.valueOf(0));
    }
}
