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
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.xml.DocumentContainer;

/**
 * Test for uppercase element matching, etc. from JXPATH-136.
 * 
 * @author Matt Benson
 * @version $Revision$ $Date$
 */
public class XMLUpperCaseElementsTest extends JXPathTestCase {
    protected JXPathContext context;

    protected DocumentContainer createDocumentContainer(String model) {
        return new DocumentContainer(JXPathTestCase.class.getResource("VendorUpper.xml"), model);
    }

    protected JXPathContext createContext(String model) {
        JXPathContext context = JXPathContext.newContext(createDocumentContainer(model));
        return context;
    }

    protected void doTest(String id, String model, String expectedValue) {
        JXPathContext context = JXPathContext.newContext(createDocumentContainer(model));
        assertEquals(context.getValue("test/text[@id='" + id + "']"), expectedValue);
    }

    public void testBasicGetDOM() {
        assertXPathValue(createContext(DocumentContainer.MODEL_DOM), "/Vendor[1]/Contact[1]",
                "John");
    }

    public void testBasicGetJDOM() {
        assertXPathValue(createContext(DocumentContainer.MODEL_JDOM), "/Vendor[1]/Contact[1]",
                "John");
    }

    public void testBasicIterateDOM() {
        assertXPathValueIterator(createContext(DocumentContainer.MODEL_DOM), "/Vendor/Contact",
                list("John", "Jack", "Jim", "Jack Black"));
    }

    public void testBasicIterateJDOM() {
        assertXPathValueIterator(createContext(DocumentContainer.MODEL_JDOM), "/Vendor/Contact",
                list("John", "Jack", "Jim", "Jack Black"));
    }
}