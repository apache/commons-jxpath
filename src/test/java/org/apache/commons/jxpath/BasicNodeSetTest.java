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

package org.apache.commons.jxpath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

/**
 * Test BasicNodeSet
 */
class BasicNodeSetTest extends AbstractJXPathTest {

    /** JXPathContext */
    protected JXPathContext context;
    /** BasicNodeSet */
    protected BasicNodeSet nodeSet;

    /**
     * Add the pointers for the specified path to {@code nodeSet}.
     *
     * @param xpath
     */
    protected void addPointers(final String xpath) {
        for (final Iterator<Pointer> iter = context.iteratePointers(xpath); iter.hasNext();) {
            nodeSet.add(iter.next());
        }
        nudge();
    }

    /**
     * Do assertions on DOM element names.
     *
     * @param names    List of expected names
     * @param elements List of DOM elements
     */
    protected void assertElementNames(final List names, final List elements) {
        assertEquals(names.size(), elements.size());
        final Iterator nameIter = names.iterator();
        final Iterator elementIter = elements.iterator();
        while (elementIter.hasNext()) {
            assertEquals(nameIter.next(), ((Element) elementIter.next()).getTagName());
        }
    }

    /**
     * Do assertions on DOM element values.
     *
     * @param values   List of expected values
     * @param elements List of DOM elements
     */
    protected void assertElementValues(final List values, final List elements) {
        assertEquals(values.size(), elements.size());
        final Iterator valueIter = values.iterator();
        final Iterator elementIter = elements.iterator();
        while (elementIter.hasNext()) {
            assertEquals(valueIter.next(), ((Element) elementIter.next()).getFirstChild().getNodeValue());
        }
    }

    /**
     * "Nudge" the nodeSet.
     */
    protected void nudge() {
        nodeSet.getPointers();
        nodeSet.getValues();
        nodeSet.getNodes();
    }

    /**
     * Remove the pointers for the specified path from {@code nodeSet}.
     *
     * @param xpath
     */
    protected void removePointers(final String xpath) {
        for (final Iterator<Pointer> iter = context.iteratePointers(xpath); iter.hasNext();) {
            nodeSet.remove(iter.next());
        }
        nudge();
    }

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        context = JXPathContext.newContext(new TestMixedModelBean());
        nodeSet = new BasicNodeSet();
    }

    /**
     * Test adding pointers.
     */
    @Test
    void testAdd() {
        addPointers("/bean/integers");
        assertEquals(list("/bean/integers[1]", "/bean/integers[2]", "/bean/integers[3]", "/bean/integers[4]").toString(), nodeSet.getPointers().toString());
        assertEquals(list(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)), nodeSet.getValues());
        assertEquals(list(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)), nodeSet.getNodes());
    }

    /**
     * Demonstrate when nodes != values: in XML models.
     */
    @Test
    void testNodes() {
        addPointers("/document/vendor/contact");
        assertEquals(
                list("/document/vendor[1]/contact[1]", "/document/vendor[1]/contact[2]", "/document/vendor[1]/contact[3]", "/document/vendor[1]/contact[4]")
                        .toString(),
                nodeSet.getPointers().toString());
        assertEquals(list("John", "Jack", "Jim", "Jack Black"), nodeSet.getValues());
        assertElementNames(list("contact", "contact", "contact", "contact"), nodeSet.getNodes());
        assertElementValues(list("John", "Jack", "Jim", "Jack Black"), nodeSet.getNodes());
    }

    /**
     * Test removing a pointer.
     */
    @Test
    void testRemove() {
        addPointers("/bean/integers");
        removePointers("/bean/integers[4]");
        assertEquals(list("/bean/integers[1]", "/bean/integers[2]", "/bean/integers[3]").toString(), nodeSet.getPointers().toString());
        assertEquals(list(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)), nodeSet.getValues());
        assertEquals(list(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)), nodeSet.getNodes());
    }
}
