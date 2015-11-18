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

import java.util.HashMap;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;

/**
 * JXPATH-104 test.
 * 
 * @author Matt Benson
 * @version $Revision$ $Date$
 */
public class EmbeddedColonMapKeysTest extends JXPathTestCase {
    private JXPathContext context;

    protected void setUp() throws Exception {
        super.setUp();
        HashMap m = new HashMap();
        m.put("foo:key", "value");
        context = JXPathContext.newContext(m);
        context.setLenient(true);
    }

    public void testSelectNodes() throws Exception {
        assertXPathValueIterator(context, "/.[@name='foo:key']", list("value"));
        assertXPathValueIterator(context, "/foo:key", list());
    }

    public void testSelectSingleNode() throws Exception {
        assertXPathValue(context, "/.[@name='foo:key']", "value");
        assertXPathValueLenient(context, "/foo:key", null);
    }
}
