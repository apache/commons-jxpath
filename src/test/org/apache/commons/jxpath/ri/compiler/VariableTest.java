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

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.TestMixedModelBean;
import org.apache.commons.jxpath.Variables;

/**
 * Test basic functionality of JXPath - infoset types,
 * operations.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class VariableTest extends JXPathTestCase {
    private JXPathContext context;

    public void setUp() {
        if (context == null) {
            context = JXPathContext.newContext(new TestMixedModelBean());
            context.setFactory(new VariableFactory());

            Variables vars = context.getVariables();
            vars.declareVariable("a", new Double(1));
            vars.declareVariable("b", new Double(1));
            vars.declareVariable("c", null);
            vars.declareVariable("d", new String[] { "a", "b" });
            vars.declareVariable("integer", new Integer(1));
            vars.declareVariable("nan", new Double(Double.NaN));
            vars.declareVariable("x", null);
        }
    }

    public void testVariables() {
        // Variables
        assertXPathValueAndPointer(context, "$a", new Double(1), "$a");
    }

    public void testVariablesInExpressions() {
        assertXPathValue(context, "$a = $b", Boolean.TRUE);

        assertXPathValue(context, "$a = $nan", Boolean.FALSE);

        assertXPathValue(context, "$a + 1", new Double(2));

        assertXPathValue(context, "$c", null);

        assertXPathValue(context, "$d[2]", "b");
    }

    public void testInvalidVariableName() {
        boolean exception = false;
        try {
            context.getValue("$none");
        }
        catch (Exception ex) {
            exception = true;
        }
        assertTrue(
            "Evaluating '$none', expected exception - did not get it",
            exception);
        
        exception = false;
        try {
            context.setValue("$none", new Integer(1));
        }
        catch (Exception ex) {
            exception = true;
        }
        assertTrue(
            "Setting '$none = 1', expected exception - did not get it",
            exception);
    }

    public void testNestedContext() {
        JXPathContext nestedContext = JXPathContext.newContext(context, null);

        assertXPathValue(nestedContext, "$a", new Double(1));
    }

    public void testSetValue() {
        assertXPathSetValue(context, "$x", new Integer(1));
    }

    public void testCreatePathDeclareVariable() {
        // Calls factory.declareVariable("string")
        assertXPathCreatePath(context, "$string", null, "$string");
    }

    public void testCreatePathAndSetValueDeclareVariable() {
        // Calls factory.declareVariable("string")
        assertXPathCreatePathAndSetValue(
            context,
            "$string",
            "Value",
            "$string");
    }

    public void testCreatePathDeclareVariableSetCollectionElement() {
        // Calls factory.declareVariable("stringArray"). 
        // The factory needs to create a collection
        assertXPathCreatePath(
            context,
            "$stringArray[2]",
            "",
            "$stringArray[2]");

        // See if the factory populated the first element as well
        assertEquals(
            "Created <" + "$stringArray[1]" + ">",
            "Value1",
            context.getValue("$stringArray[1]"));
    }

    public void testCreateAndSetValuePathDeclareVariableSetCollectionElement() {
        // Calls factory.declareVariable("stringArray"). 
        // The factory needs to create a collection
        assertXPathCreatePathAndSetValue(
            context,
            "$stringArray[2]",
            "Value2",
            "$stringArray[2]");

        // See if the factory populated the first element as well
        assertEquals(
            "Created <" + "$stringArray[1]" + ">",
            "Value1",
            context.getValue("$stringArray[1]"));
    }

    public void testCreatePathExpandCollection() {
        context.getVariables().declareVariable(
            "array",
            new String[] { "Value1" });

        // Does not involve factory at all - just expands the collection
        assertXPathCreatePath(context, "$array[2]", "", "$array[2]");

        // Make sure it is still the same array
        assertEquals(
            "Created <" + "$array[1]" + ">",
            "Value1",
            context.getValue("$array[1]"));
    }

    public void testCreatePathAndSetValueExpandCollection() {
        context.getVariables().declareVariable(
            "array",
            new String[] { "Value1" });

        // Does not involve factory at all - just expands the collection
        assertXPathCreatePathAndSetValue(
            context,
            "$array[2]",
            "Value2",
            "$array[2]");

        // Make sure it is still the same array
        assertEquals(
            "Created <" + "$array[1]" + ">",
            "Value1",
            context.getValue("$array[1]"));
    }

    public void testCreatePathDeclareVariableSetProperty() {
        // Calls factory.declareVariable("test"). 
        // The factory should create a TestBean
        assertXPathCreatePath(
            context,
            "$test/boolean",
            Boolean.FALSE,
            "$test/boolean");

    }

    public void testCreatePathAndSetValueDeclareVariableSetProperty() {
        // Calls factory.declareVariable("test"). 
        // The factory should create a TestBean
        assertXPathCreatePathAndSetValue(
            context,
            "$test/boolean",
            Boolean.TRUE,
            "$test/boolean");

    }

    public void testCreatePathDeclareVariableSetCollectionElementProperty() {
        // Calls factory.declareVariable("testArray").
        // The factory should create a collection of TestBeans.
        // Then calls factory.createObject(..., collection, "testArray", 1).
        // That one should produce an instance of TestBean and 
        // put it in the collection at index 1.
        assertXPathCreatePath(
            context,
            "$testArray[2]/boolean",
            Boolean.FALSE,
            "$testArray[2]/boolean");
    }

    public void testCreatePathAndSetValueDeclVarSetCollectionElementProperty() {
        // Calls factory.declareVariable("testArray").
        // The factory should create a collection of TestBeans.
        // Then calls factory.createObject(..., collection, "testArray", 1).
        // That one should produce an instance of TestBean and 
        // put it in the collection at index 1.
        assertXPathCreatePathAndSetValue(
            context,
            "$testArray[2]/boolean",
            Boolean.TRUE,
            "$testArray[2]/boolean");
    }

    public void testRemovePathUndeclareVariable() {
        // Undeclare variable
        context.getVariables().declareVariable("temp", "temp");
        context.removePath("$temp");
        assertTrue(
            "Undeclare variable",
            !context.getVariables().isDeclaredVariable("temp"));

    }

    public void testRemovePathArrayElement() {
        // Remove array element - reassigns the new array to the var
        context.getVariables().declareVariable(
            "temp",
            new String[] { "temp1", "temp2" });
        context.removePath("$temp[1]");
        assertEquals(
            "Remove array element",
            "temp2",
            context.getValue("$temp[1]"));
    }

    public void testRemovePathCollectionElement() {
        // Remove list element - does not create a new list
        context.getVariables().declareVariable("temp", list("temp1", "temp2"));
        context.removePath("$temp[1]");
        assertEquals(
            "Remove collection element",
            "temp2",
            context.getValue("$temp[1]"));
    }
    
    public void testUnionOfVariableAndNode() throws Exception {
        assertXPathValue(context, "count($a | /document/vendor/location)", new Double(3));
        assertXPathValue(context, "count($a | /list)", new Double(7)); //$o + list which contains six discrete values (one is duped, wrapped in a Container)
    }

    public void testIterateVariable() throws Exception {
        assertXPathValueIterator(context, "$d", list("a", "b"));
        assertXPathValue(context, "$d = 'a'", Boolean.TRUE);
        assertXPathValue(context, "$d = 'b'", Boolean.TRUE);
    }
}