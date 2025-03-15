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

import java.util.Collections;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.AbstractJXPathTest;
import org.junit.jupiter.api.Test;

/**
 * Be sure empty lists/sets/arrays work.
 */
public class EmptyCollectionTest extends AbstractJXPathTest {
    public static class HasChild {
        private final Object child;

        /**
         * Constructs a new EmptyCollectionTest.HasChild instance.
         */
        public HasChild(final Object child) {
            this.child = child;
        }

        public Object getChild() {
            return child;
        }
    }

    @Test
    public void testEmptyList() {
        assertXPathPointerIterator(JXPathContext.newContext(Collections.EMPTY_LIST), "/*",
                Collections.EMPTY_LIST);
    }

    @Test
    public void testEmptyArray() {
        assertXPathPointerIterator(JXPathContext.newContext(new Object[0]), "/*", list());
    }

    @Test
    public void testEmptySet() {
        assertXPathPointerIterator(JXPathContext.newContext(Collections.EMPTY_SET), "/*",
                Collections.EMPTY_SET);
    }

    @Test
    public void testEmptyChildList() {
        assertXPathPointerIterator(JXPathContext.newContext(new HasChild(Collections.EMPTY_LIST)),
                "/child/*", Collections.EMPTY_LIST);
    }

    @Test
    public void testEmptyChildArray() {
        assertXPathPointerIterator(JXPathContext.newContext(new HasChild(new Object[0])),
                "/child/*", list());
    }

    @Test
    public void testEmptyChildSet() {
        assertXPathPointerIterator(JXPathContext.newContext(new HasChild(Collections.EMPTY_SET)),
                "/child/*", Collections.EMPTY_SET);
    }
}
