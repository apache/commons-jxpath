/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.ri.Parser;

/**
 * Tests the determination of whether an expression is context dependent.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2004/02/29 14:17:42 $
 */

public class ContextDependencyTest extends JXPathTestCase {
    public ContextDependencyTest(String name) {
        super(name);
    }

    public void testContextDependency() {
        testContextDependency("1", false);
        testContextDependency("$x", false);
        testContextDependency("/foo", false);
        testContextDependency("foo", true);
        testContextDependency("/foo[3]", false);
        testContextDependency("/foo[$x]", false);
        testContextDependency("/foo[bar]", true);
        testContextDependency("3 + 5", false);
        testContextDependency("test:func(3, 5)", true);
        testContextDependency("test:func(3, foo)", true);
    }

    public void testContextDependency(String xpath, boolean expected) {
        Expression expr =
            (Expression) Parser.parseExpression(xpath, new TreeCompiler());

        assertEquals(
            "Context dependency <" + xpath + ">",
            expected,
            expr.isContextDependent());
    }
}