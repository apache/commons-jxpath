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
package org.apache.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.TestBean;

/**
 * Test AbstractFactory.
 */
public class VariableFactory extends AbstractFactory {

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
        switch (name) {
        case "testArray":
            ((TestBean[]) parent)[index] = new TestBean();
            return true;
        case "stringArray":
            ((String[]) parent)[index] = "";
            return true;
        case "array":
            ((String[]) parent)[index] = "";
            return true;
        default:
            break;
        }
        return false;
    }

    /**
     * Create a new object and set it on the specified variable
     */
    @Override
    public boolean declareVariable(final JXPathContext context, final String name) {
        switch (name) {
        case "test":
            context.getVariables().declareVariable(name, new TestBean());
            return true;
        case "testArray":
            context.getVariables().declareVariable(name, new TestBean[0]);
            return true;
        case "stringArray":
            context.getVariables().declareVariable(
                name,
                new String[] { "Value1" });
            return true;
        default:
            break;
        }
        context.getVariables().declareVariable(name, null);
        return true;
    }
}
