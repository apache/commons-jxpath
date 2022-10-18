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
package org.apache.commons.jxpath.ri.model.beans;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.TestBean;
import org.apache.commons.jxpath.ri.model.BeanModelTestCase;

/**
 * Tests JXPath with JavaBeans
*
 */
public class BeanModelTest extends BeanModelTestCase {

    @Override
    protected Object createContextBean() {
        return new TestBean();
    }

    @Override
    protected AbstractFactory getAbstractFactory() {
        return new TestBeanFactory();
    }

    public void testIndexedProperty() {
        final JXPathContext context =
            JXPathContext.newContext(null, new TestIndexedPropertyBean());

        assertXPathValueAndPointer(
            context,
            "indexed[1]",
            Integer.valueOf(0),
            "/indexed[1]");
    }


}