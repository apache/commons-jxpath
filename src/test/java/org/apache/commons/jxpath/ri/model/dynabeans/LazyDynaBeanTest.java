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
package org.apache.commons.jxpath.ri.model.dynabeans;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;

/**
 *
 */
public class LazyDynaBeanTest extends JXPathTestCase {

    public void testLazyProperty() throws JXPathNotFoundException {
        final LazyDynaBean bean = new LazyDynaBean();
        final JXPathContext context = JXPathContext.newContext(bean);
        context.getValue("nosuch");
    }

    public void testStrictLazyDynaBeanPropertyFactory() {
        final StrictLazyDynaBeanPointerFactory factory = new StrictLazyDynaBeanPointerFactory();
        JXPathContextReferenceImpl.addNodePointerFactory(factory);
        try {
            testLazyProperty();
            fail();
        } catch (final JXPathNotFoundException e) {
            // okay
        } finally {
            while (JXPathContextReferenceImpl.removeNodePointerFactory(factory)) {

            }
        }
    }
}
