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

public class JXPath154Test extends JXPathTestCase {

    protected JXPathContext context;

    protected DocumentContainer createDocumentContainer(final String model) {
        return new DocumentContainer(JXPathTestCase.class.getResource("InnerEmptyNamespace.xml"), model);
    }

    protected void doTest(final String path, final String model, final String expectedValue) {
        final JXPathContext context = JXPathContext.newContext(createDocumentContainer(model));
        assertEquals(expectedValue, context.getPointer(path).asPath());
    }

    public void testInnerEmptyNamespaceDOM() {
        doTest("b:foo/test", DocumentContainer.MODEL_DOM, "/b:foo[1]/test[1]");
    }

    public void testInnerEmptyNamespaceJDOM() {
        doTest("b:foo/test", DocumentContainer.MODEL_JDOM, "/b:foo[1]/test[1]");
    }
}
