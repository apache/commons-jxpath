/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/ri/compiler/VariableTest.java,v 1.3 2003/03/11 00:59:36 dmitri Exp $
 * $Revision: 1.3 $
 * $Date: 2003/03/11 00:59:36 $
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Plotnix, Inc,
 * <http://www.plotnix.com/>.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.Variables;

/**
 * Test basic functionality of JXPath - infoset types,
 * operations.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.3 $ $Date: 2003/03/11 00:59:36 $
 */

public class VariableTest extends JXPathTestCase {
    private JXPathContext context;

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public VariableTest(String name) {
        super(name);
    }

    public void setUp() {
        if (context == null) {
            context = JXPathContext.newContext(null);
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
}