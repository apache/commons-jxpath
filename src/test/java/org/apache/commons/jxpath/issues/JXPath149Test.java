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
package org.apache.commons.jxpath.issues;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;

public class JXPath149Test extends JXPathTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("jxpath.class.allow", "*");
    }

    @Override
    public void tearDown() throws Exception {
        System.clearProperty("jxpath.class.allow");
        super.tearDown();
    }

    public void testComplexOperationWithVariables() {
        final JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("a", Integer.valueOf(0));
        context.getVariables().declareVariable("b", Integer.valueOf(0));
        context.getVariables().declareVariable("c", Integer.valueOf(1));
        assertXPathValue(context, "$a + $b <= $c", Boolean.TRUE);
    }
}
