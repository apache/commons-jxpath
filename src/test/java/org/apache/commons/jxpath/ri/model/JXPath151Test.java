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
import java.util.Locale;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.TestBean;

public class JXPath151Test extends JXPathTestCase {

    private JXPathContext context;

    @Override
    public void setUp() {
        final TestBean testBean = new TestBean();
        final HashMap m = new HashMap();
        m.put("a", Integer.valueOf(1));
        m.put("b", null);
        m.put("c", Integer.valueOf(1));
        m.put("d", Integer.valueOf(0));
        testBean.setMap(m);
        context = JXPathContext.newContext(testBean);
        context.setLocale(Locale.US);
    }

    public void testMapValueEquality() {
        assertXPathValue(context, "map/b != map/a", Boolean.TRUE);
        assertXPathValue(context, "map/a != map/b", Boolean.TRUE);
        assertXPathValue(context, "map/a != map/c", Boolean.FALSE);
        assertXPathValue(context, "map/a = map/b", Boolean.FALSE);
        assertXPathValue(context, "map/a = map/c", Boolean.TRUE);
        assertXPathValue(context, "not(map/a = map/b)", Boolean.TRUE);
        assertXPathValue(context, "not(map/a = map/c)", Boolean.FALSE);
    }

    public void testMapValueEqualityUsingNameAttribute() {
        assertXPathValue(context, "map[@name = 'b'] != map[@name = 'c']", Boolean.TRUE);
        assertXPathValue(context, "map[@name = 'a'] != map[@name = 'b']", Boolean.TRUE);
        assertXPathValue(context, "map[@name = 'a'] != map[@name = 'c']", Boolean.FALSE);
        assertXPathValue(context, "map[@name = 'a'] = map[@name = 'b']", Boolean.FALSE);
        assertXPathValue(context, "map[@name = 'a'] = map[@name = 'c']", Boolean.TRUE);
        assertXPathValue(context, "map[@name = 'd'] = map[@name = 'b']", Boolean.TRUE);
        assertXPathValue(context, "map[@name = 'd'] = map[@name = 'b']", Boolean.TRUE);
        assertXPathValue(context, "not(map[@name = 'a'] = map[@name = 'b'])", Boolean.TRUE);
        assertXPathValue(context, "not(map[@name = 'a'] = map[@name = 'c'])", Boolean.FALSE);
    }
}
