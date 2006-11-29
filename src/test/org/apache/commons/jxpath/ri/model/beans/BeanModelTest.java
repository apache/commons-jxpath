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

import junit.framework.TestSuite;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.TestBean;
import org.apache.commons.jxpath.ri.model.BeanModelTestCase;

/**
 * Tests JXPath with JavaBeans
*
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */

public class BeanModelTest extends BeanModelTestCase {
    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BeanModelTest(String name) {
        super(name);
    }

    /**
     * Return the tests included in this test suite.
     */
    public static TestSuite suite() {
        return (new TestSuite(BeanModelTest.class));
    }

    protected Object createContextBean() {
        return new TestBean();
    }

    protected AbstractFactory getAbstractFactory() {
        return new TestBeanFactory();
    }
    
    public void testIndexedProperty() {
        JXPathContext context =
            JXPathContext.newContext(null, new TestIndexedPropertyBean());
            
        assertXPathValueAndPointer(
            context,
            "indexed[1]",
            new Integer(0),
            "/indexed[1]");
    }


}