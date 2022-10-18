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

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.NestedTestBean;
import org.apache.commons.jxpath.Pointer;

/**
 * Test AbstractFactory.
 */
public class TestDynaBeanFactory extends AbstractFactory {

    /**
     */
    @Override
    public boolean createObject(
        final JXPathContext context,
        final Pointer pointer,
        final Object parent,
        final String name,
        final int index)
    {
        if (name.equals("nestedBean")) {
            ((DynaBean) parent).set(
                "nestedBean",
                new NestedTestBean("newName"));
            return true;
        }
        else if (name.equals("beans")) {
            final DynaBean bean = (DynaBean) parent;
            Object beans[] = (Object[]) bean.get("beans");
            if (beans == null || index >= beans.length) {
                beans = new NestedTestBean[index + 1];
                bean.set("beans", beans);
            }
            beans[index] = new NestedTestBean("newName");
            return true;
        }
        else if (name.equals("integers")) {
            final DynaBean bean = (DynaBean) parent;
            bean.set("integers", index, Integer.valueOf(0));
            return true;
        }
        return false;
    }

    /**
     */
    @Override
    public boolean declareVariable(final JXPathContext context, final String name) {
        context.getVariables().declareVariable(name, null);
        return true;
    }
}
