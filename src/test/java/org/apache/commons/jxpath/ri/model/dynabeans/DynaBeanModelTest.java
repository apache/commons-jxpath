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

import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.TestBean;
import org.apache.commons.jxpath.ri.model.BeanModelTestCase;

/**
 * Test for support of DynaBeans (see BeanUtils)
 */

public class DynaBeanModelTest extends BeanModelTestCase {

    @Override
    protected Object createContextBean() {
        return new WrapDynaBean(new TestBean());
    }

    @Override
    protected AbstractFactory getAbstractFactory() {
        return new TestDynaBeanFactory();
    }
}
