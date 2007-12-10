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

import java.util.Iterator;

/**
 * Test BasicNodeSet
 * 
 * @author Matt Benson
 * @version $Revision$ $Date$
 */
public class BasicNodeSetTest extends JXPathTestCase {
    /** JXPathContext */
    protected JXPathContext context;

    /** BasicNodeSet */
    protected BasicNodeSet nodeSet;

    /**
     * Create a new BasicNodeSetTest.
     * 
     * @param name testcase name
     */
    public BasicNodeSetTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        context = JXPathContext.newContext(new TestBean());
        nodeSet = new BasicNodeSet();
    }

    /**
     * Add the pointers for the specified path to <code>nodeSet</code>.
     * 
     * @param xpath
     */
    protected void addPointers(String xpath) {
        for (Iterator iter = context.iteratePointers(xpath); iter.hasNext();) {
            nodeSet.add((Pointer) iter.next());
        }
        nudge();
    }

    /**
     * Remove the pointers for the specified path from <code>nodeSet</code>.
     * 
     * @param xpath
     */
    protected void removePointers(String xpath) {
        for (Iterator iter = context.iteratePointers(xpath); iter.hasNext();) {
            nodeSet.remove((Pointer) iter.next());
        }
        nudge();
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
     * Test adding pointers.
     */
    public void testAdd() {
        addPointers("/integers");
        assertEquals(nodeSet.getPointers().toString(), list("/integers[1]",
                "/integers[2]", "/integers[3]", "/integers[4]").toString());
    }

    /**
     * Test removing a pointer.
     */
    public void testRemove() {
        addPointers("/integers");
        removePointers("/integers[4]");
        assertEquals(list("/integers[1]", "/integers[2]", "/integers[3]")
                .toString(), nodeSet.getPointers().toString());
        assertEquals(list(new Integer(1), new Integer(2), new Integer(3)),
                nodeSet.getValues());
    }
}
