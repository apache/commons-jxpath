/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/ri/compiler/ExtensionFunctionTest.java,v 1.4 2003/01/20 00:00:27 dmitri Exp $
 * $Revision: 1.4 $
 * $Date: 2003/01/20 00:00:27 $
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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

import java.util.List;
import java.util.Locale;

import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.FunctionLibrary;
import org.apache.commons.jxpath.Functions;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.PackageFunctions;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.TestBean;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Test extension functions.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.4 $ $Date: 2003/01/20 00:00:27 $
 */

public class ExtensionFunctionTest extends JXPathTestCase {
    private Functions functions;
    private JXPathContext context;

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public ExtensionFunctionTest(String name) {
        super(name);
    }

    public void setUp() {
        if (context == null) {
            context = JXPathContext.newContext(new TestBean());
            Variables vars = context.getVariables();
            vars.declareVariable("test", new TestFunctions(4, "test"));

            FunctionLibrary lib = new FunctionLibrary();
            lib.addFunctions(new ClassFunctions(TestFunctions.class, "test"));
            lib.addFunctions(new ClassFunctions(TestFunctions2.class, "test"));
            lib.addFunctions(new PackageFunctions("", "call"));
            lib.addFunctions(
                new PackageFunctions(
                    "org.apache.commons.jxpath.ri.compiler.",
                    "jxpathtest"));
            context.setFunctions(lib);
        }
        functions = new ClassFunctions(TestFunctions.class, "test");
    }

    public void testConstructorLookup() {
        Object[] args = new Object[] { new Integer(1), "x" };
        Function func = functions.getFunction("test", "new", args);

        assertEquals(
            "test:new(1, x)",
            func.invoke(new Context(null), args).toString(),
            "foo=1; bar=x");
    }

    public void testConstructorLookupWithExpressionContext() {
        Object[] args = new Object[] { "baz" };
        Function func = functions.getFunction("test", "new", args);
        assertEquals(
            "test:new('baz')",
            func.invoke(new Context(new Integer(1)), args).toString(),
            "foo=1; bar=baz");
    }

    public void testStaticMethodLookup() {
        Object[] args = new Object[] { new Integer(1), "x" };
        Function func = functions.getFunction("test", "build", args);
        assertEquals(
            "test:build(1, x)",
            func.invoke(new Context(null), args).toString(),
            "foo=1; bar=x");
    }

    public void testStaticMethodLookupWithConversion() {
        Object[] args = new Object[] { "7", new Integer(1)};
        Function func = functions.getFunction("test", "build", args);
        assertEquals(
            "test:build('7', 1)",
            func.invoke(new Context(null), args).toString(),
            "foo=7; bar=1");
    }

    public void testMethodLookup() {
        Object[] args = new Object[] { new TestFunctions()};
        Function func = functions.getFunction("test", "getFoo", args);
        assertEquals(
            "test:getFoo($test, 1, x)",
            func.invoke(new Context(null), args).toString(),
            "0");
    }

    public void testStaticMethodLookupWithExpressionContext() {
        Object[] args = new Object[0];
        Function func = functions.getFunction("test", "path", args);
        assertEquals(
            "test:path()",
            func.invoke(new Context(new Integer(1)), args),
            "1");
    }

    public void testMethodLookupWithExpressionContext() {
        Object[] args = new Object[] { new TestFunctions()};
        Function func = functions.getFunction("test", "instancePath", args);
        assertEquals(
            "test:instancePath()",
            func.invoke(new Context(new Integer(1)), args),
            "1");
    }

    public void testMethodLookupWithExpressionContextAndArgument() {
        Object[] args = new Object[] { new TestFunctions(), "*" };
        Function func = functions.getFunction("test", "pathWithSuffix", args);
        assertEquals(
            "test:pathWithSuffix('*')",
            func.invoke(new Context(new Integer(1)), args),
            "1*");
    }

    public void testAllocation() {

        // Allocate new object using the default constructor
        assertXPathValue(context, "string(test:new())", "foo=0; bar=null");

        // Allocate new object using PackageFunctions and class name
        assertXPathValue(
            context,
            "string(jxpathtest:TestFunctions.new())",
            "foo=0; bar=null");

        // Allocate new object using a fully qualified class name
        assertXPathValue(
            context,
            "string(" + TestFunctions.class.getName() + ".new())",
            "foo=0; bar=null");

        // Allocate new object using a custom constructor
        assertXPathValue(
            context,
            "string(test:new(3, 'baz'))",
            "foo=3; bar=baz");

        // Allocate new object using a custom constructor - type conversion
        assertXPathValue(context, "string(test:new('3', 4))", "foo=3; bar=4.0");
    }

    public void testMethodCall() {
        assertXPathValue(context, "length('foo')", new Integer(3));

        // We are just calling a method - prefix is ignored
        assertXPathValue(context, "call:substring('foo', 1, 2)", "o");

        // Invoke a function implemented as a regular method
        assertXPathValue(context, "string(test:getFoo($test))", "4");

        // Note that the prefix is ignored anyway, we are just calling a method
        assertXPathValue(context, "string(call:getFoo($test))", "4");

        // We don't really need to supply a prefix in this case
        assertXPathValue(context, "string(getFoo($test))", "4");

        // Method with two arguments
        assertXPathValue(
            context,
            "string(test:setFooAndBar($test, 7, 'biz'))",
            "foo=7; bar=biz");
    }

    public void testStaticMethodCall() {

        assertXPathValue(
            context,
            "string(test:build(8, 'goober'))",
            "foo=8; bar=goober");

        // Call a static method using PackageFunctions and class name
        assertXPathValue(
            context,
            "string(jxpathtest:TestFunctions.build(8, 'goober'))",
            "foo=8; bar=goober");

        // Call a static method with a fully qualified class name
        assertXPathValue(
            context,
            "string(" + TestFunctions.class.getName() + ".build(8, 'goober'))",
            "foo=8; bar=goober");

        // Two ClassFunctions are sharing the same prefix.
        // This is TestFunctions2
        assertXPathValue(context, "string(test:increment(8))", "9");

    }

    public void testExpressionContext() {
        // Execute an extension function for each node while searching
        // The function uses ExpressionContext to get to the current
        // node.
        assertXPathValue(context, "//.[test:isMap()]/Key1", "Value 1");

        // The function uses ExpressionContext to get to all
        // nodes in the context that is passed to it.
        assertXPathValue(
            context,
            "count(//.[test:count(strings) = 3])",
            new Double(7));

        // The function uses ExpressionContext to get to the current
        // pointer and returns its path.
        assertXPathValue(
            context,
            "/beans[contains(test:path(), '[2]')]/name",
            "Name 2");
    }

    private static class Context implements ExpressionContext {
        private Object object;

        public Context(Object object) {
            this.object = object;
        }

        public Pointer getContextNodePointer() {
            return NodePointer.newNodePointer(
                null,
                object,
                Locale.getDefault());
        }

        public List getContextNodeList() {
            return null;
        }

        public JXPathContext getJXPathContext() {
            return null;
        }

        public int getPosition() {
            return 0;
        }
    }
}