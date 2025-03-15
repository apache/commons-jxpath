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
package org.apache.commons.jxpath.ri.axes;

import org.apache.commons.jxpath.AbstractJXPathTest;
import org.apache.commons.jxpath.TestBean;
import org.apache.commons.jxpath.xml.DocumentContainer;
import org.w3c.dom.Document;

/**
 * Test bean for mixed model JUnit tests.
 */
public class TestBeanWithNode extends TestBean {
    private Object node;
    private Object object;

    public Object getVendor() {
        return node;
    }

    public Object[] getVendors() {
        return new Object[] { node };
    }

    public void setVendor(final Object node) {
        this.node = node;
    }

    @Override
    public Object getObject() {
        return object;
    }

    public void setObject(final Object object) {
        this.object = object;
    }

    public static TestBeanWithNode createTestBeanWithDOM() {
        final DocumentContainer docCtr =
            new DocumentContainer(
                AbstractJXPathTest.class.getResource("Vendor.xml"));
        final Document doc = (Document) docCtr.getValue();
        final TestBeanWithNode tbwdom = new TestBeanWithNode();
        tbwdom.setVendor(doc.getDocumentElement());
        tbwdom.setObject(docCtr);
        return tbwdom;
    }

}
