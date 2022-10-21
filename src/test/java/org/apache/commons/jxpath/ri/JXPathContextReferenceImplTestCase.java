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

package org.apache.commons.jxpath.ri;

import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.ri.model.container.ContainerPointerFactory;
import org.apache.commons.jxpath.JXPathContext;


public class JXPathContextReferenceImplTestCase extends JXPathTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("jxpath.class.allow", "org.apache.commons.jxpath.ri.JXPathContextFactoryReferenceImpl");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        System.clearProperty("jxpath.class.allow");
    }

    /**
     * https://issues.apache.org/jira/browse/JXPATH-166
     */
    public void testInit() {
        final ContainerPointerFactory factory = new ContainerPointerFactory();
        try {
            JXPathContextReferenceImpl.addNodePointerFactory(factory);
        } finally {
            while (JXPathContextReferenceImpl.removeNodePointerFactory(factory)) {

            }
        }
    }

    public void testDangerousClass() {
        try {
            JXPathContext context = JXPathContext.newContext(new Object() {});
            String jxPath = "run(newInstance(loadClass(getClassLoader(getClass(/)), \"org.apache.commons.jxpath.ri.TestDangerousClass\")), \"blabla\")";
            context.getValue(jxPath);
            fail("failed to block org.apache.commons.jxpath.ri.TestDangerousClass.run()");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Calling method is not allowed: class java.lang.Object.getClass()"));
        }
    }
}
