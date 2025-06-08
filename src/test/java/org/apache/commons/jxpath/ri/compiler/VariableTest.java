/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jxpath.ri.compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.jxpath.AbstractJXPathTest;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.TestMixedModelBean;
import org.apache.commons.jxpath.Variables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test basic functionality of JXPath - infoset types, operations.
 */
class VariableTest extends AbstractJXPathTest {

    private JXPathContext context;

    @Override
    @BeforeEach
    public void setUp() {
        if (context == null) {
            context = JXPathContext.newContext(new TestMixedModelBean());
            context.setFactory(new VariableFactory());
            final Variables vars = context.getVariables();
            vars.declareVariable("a", Double.valueOf(1));
            vars.declareVariable("b", Double.valueOf(1));
            vars.declareVariable("c", null);
            vars.declareVariable("d", new String[] { "a", "b" });
            vars.declareVariable("integer", Integer.valueOf(1));
            vars.declareVariable("nan", Double.valueOf(Double.NaN));
            vars.declareVariable("x", null);
        }
    }

    @Test
    void testCreateAndSetValuePathDeclareVariableSetCollectionElement() {
        // Calls factory.declareVariable("stringArray").
        // The factory needs to create a collection
        assertXPathCreatePathAndSetValue(context, "$stringArray[2]", "Value2", "$stringArray[2]");
        // See if the factory populated the first element as well
        assertEquals("Value1", context.getValue("$stringArray[1]"), "Created <" + "$stringArray[1]" + ">");
    }

    @Test
    void testCreatePathAndSetValueDeclareVariable() {
        // Calls factory.declareVariable("string")
        assertXPathCreatePathAndSetValue(context, "$string", "Value", "$string");
    }

    @Test
    void testCreatePathAndSetValueDeclareVariableSetProperty() {
        // Calls factory.declareVariable("test").
        // The factory should create a TestBean
        assertXPathCreatePathAndSetValue(context, "$test/boolean", Boolean.TRUE, "$test/boolean");
    }

    @Test
    void testCreatePathAndSetValueDeclVarSetCollectionElementProperty() {
        // Calls factory.declareVariable("testArray").
        // The factory should create a collection of TestBeans.
        // Then calls factory.createObject(..., collection, "testArray", 1).
        // That one should produce an instance of TestBean and
        // put it in the collection at index 1.
        assertXPathCreatePathAndSetValue(context, "$testArray[2]/boolean", Boolean.TRUE, "$testArray[2]/boolean");
    }

    @Test
    void testCreatePathAndSetValueExpandCollection() {
        context.getVariables().declareVariable("array", new String[] { "Value1" });
        // Does not involve factory at all - just expands the collection
        assertXPathCreatePathAndSetValue(context, "$array[2]", "Value2", "$array[2]");
        // Make sure it is still the same array
        assertEquals("Value1", context.getValue("$array[1]"), "Created <" + "$array[1]" + ">");
    }

    @Test
    void testCreatePathDeclareVariable() {
        // Calls factory.declareVariable("string")
        assertXPathCreatePath(context, "$string", null, "$string");
    }

    @Test
    void testCreatePathDeclareVariableSetCollectionElement() {
        // Calls factory.declareVariable("stringArray").
        // The factory needs to create a collection
        assertXPathCreatePath(context, "$stringArray[2]", "", "$stringArray[2]");
        // See if the factory populated the first element as well
        assertEquals("Value1", context.getValue("$stringArray[1]"), "Created <" + "$stringArray[1]" + ">");
    }

    @Test
    void testCreatePathDeclareVariableSetCollectionElementProperty() {
        // Calls factory.declareVariable("testArray").
        // The factory should create a collection of TestBeans.
        // Then calls factory.createObject(..., collection, "testArray", 1).
        // That one should produce an instance of TestBean and
        // put it in the collection at index 1.
        assertXPathCreatePath(context, "$testArray[2]/boolean", Boolean.FALSE, "$testArray[2]/boolean");
    }

    @Test
    void testCreatePathDeclareVariableSetProperty() {
        // Calls factory.declareVariable("test").
        // The factory should create a TestBean
        assertXPathCreatePath(context, "$test/boolean", Boolean.FALSE, "$test/boolean");
    }

    @Test
    void testCreatePathExpandCollection() {
        context.getVariables().declareVariable("array", new String[] { "Value1" });
        // Does not involve factory at all - just expands the collection
        assertXPathCreatePath(context, "$array[2]", "", "$array[2]");
        // Make sure it is still the same array
        assertEquals("Value1", context.getValue("$array[1]"), "Created <" + "$array[1]" + ">");
    }

    @Test
    void testInvalidVariableName() {
        assertThrows(Exception.class, () -> context.getValue("$none"), "Evaluating '$none', expected exception - did not get it");
        assertThrows(Exception.class, () -> context.setValue("$none", Integer.valueOf(1)), "Setting '$none = 1', expected exception - did not get it");
    }

    @Test
    void testIterateVariable() throws Exception {
        assertXPathValueIterator(context, "$d", list("a", "b"));
        assertXPathValue(context, "$d = 'a'", Boolean.TRUE);
        assertXPathValue(context, "$d = 'b'", Boolean.TRUE);
    }

    @Test
    void testNestedContext() {
        final JXPathContext nestedContext = JXPathContext.newContext(context, null);
        assertXPathValue(nestedContext, "$a", Double.valueOf(1));
    }

    @Test
    void testRemovePathArrayElement() {
        // Remove array element - reassigns the new array to the var
        context.getVariables().declareVariable("temp", new String[] { "temp1", "temp2" });
        context.removePath("$temp[1]");
        assertEquals("temp2", context.getValue("$temp[1]"), "Remove array element");
    }

    @Test
    void testRemovePathCollectionElement() {
        // Remove list element - does not create a new list
        context.getVariables().declareVariable("temp", list("temp1", "temp2"));
        context.removePath("$temp[1]");
        assertEquals("temp2", context.getValue("$temp[1]"), "Remove collection element");
    }

    @Test
    void testRemovePathUndeclareVariable() {
        // Undeclare variable
        context.getVariables().declareVariable("temp", "temp");
        context.removePath("$temp");
        assertFalse(context.getVariables().isDeclaredVariable("temp"), "Undeclare variable");
    }

    @Test
    void testSetValue() {
        assertXPathSetValue(context, "$x", Integer.valueOf(1));
    }

    @Test
    void testUnionOfVariableAndNode() throws Exception {
        assertXPathValue(context, "count($a | /document/vendor/location)", Double.valueOf(3));
        assertXPathValue(context, "count($a | /list)", Double.valueOf(7)); // $o + list which contains six discrete values (one is duped, wrapped in a
                                                                           // Container)
    }

    @Test
    void testVariables() {
        // Variables
        assertXPathValueAndPointer(context, "$a", Double.valueOf(1), "$a");
    }

    @Test
    void testVariablesInExpressions() {
        assertXPathValue(context, "$a = $b", Boolean.TRUE);
        assertXPathValue(context, "$a = $nan", Boolean.FALSE);
        assertXPathValue(context, "$a + 1", Double.valueOf(2));
        assertXPathValue(context, "$c", null);
        assertXPathValue(context, "$d[2]", "b");
    }
}