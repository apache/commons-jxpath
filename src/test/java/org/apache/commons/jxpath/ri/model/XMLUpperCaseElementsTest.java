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

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.AbstractJXPathTest;
import org.apache.commons.jxpath.xml.DocumentContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for uppercase element matching, etc. showing JXPATH-136 is not reproducible.
 */
public class XMLUpperCaseElementsTest extends AbstractJXPathTest {
    protected JXPathContext context;

    protected DocumentContainer createDocumentContainer(final String model) {
        return new DocumentContainer(AbstractJXPathTest.class.getResource("VendorUpper.xml"), model);
    }

    protected JXPathContext createContext(final String model) {
        final JXPathContext context = JXPathContext.newContext(createDocumentContainer(model));
        return context;
    }

    protected void doTest(final String id, final String model, final String expectedValue) {
        final JXPathContext context = JXPathContext.newContext(createDocumentContainer(model));
        assertEquals(expectedValue, context.getValue("test/text[@id='" + id + "']"));
    }

    @Test
    public void testBasicGetDOM() {
        assertXPathValue(createContext(DocumentContainer.MODEL_DOM), "/Vendor[1]/Contact[1]",
                "John");
    }

    @Test
    public void testBasicGetJDOM() {
        assertXPathValue(createContext(DocumentContainer.MODEL_JDOM), "/Vendor[1]/Contact[1]",
                "John");
    }

    @Test
    public void testBasicIterateDOM() {
        assertXPathValueIterator(createContext(DocumentContainer.MODEL_DOM), "/Vendor/Contact",
                list("John", "Jack", "Jim", "Jack Black"));
    }

    @Test
    public void testBasicIterateJDOM() {
        assertXPathValueIterator(createContext(DocumentContainer.MODEL_JDOM), "/Vendor/Contact",
                list("John", "Jack", "Jim", "Jack Black"));
    }
}