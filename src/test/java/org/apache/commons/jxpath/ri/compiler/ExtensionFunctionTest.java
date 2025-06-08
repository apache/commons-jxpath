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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.jxpath.AbstractJXPathTest;
import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.FunctionLibrary;
import org.apache.commons.jxpath.Functions;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.PackageFunctions;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.TestBean;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.JXPath11CompatibleTypeConverter;
import org.apache.commons.jxpath.util.TypeConverter;
import org.apache.commons.jxpath.util.TypeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test extension functions.
 */
class ExtensionFunctionTest extends AbstractJXPathTest {

    private static final class Context implements ExpressionContext {

        private final Object object;

        public Context(final Object object) {
            this.object = object;
        }

        @Override
        public List<Pointer> getContextNodeList() {
            return null;
        }

        @Override
        public Pointer getContextNodePointer() {
            return NodePointer.newNodePointer(null, object, Locale.getDefault());
        }

        @Override
        public JXPathContext getJXPathContext() {
            return null;
        }

        @Override
        public int getPosition() {
            return 0;
        }
    }

    private Functions functions;
    private JXPathContext context;
    private TestBean testBean;
    private TypeConverter typeConverter;

    @Override
    @BeforeEach
    public void setUp() {
        if (context == null) {
            testBean = new TestBean();
            context = JXPathContext.newContext(testBean);
            final Variables vars = context.getVariables();
            vars.declareVariable("test", new TestFunctions(4, "test"));
            final FunctionLibrary lib = new FunctionLibrary();
            lib.addFunctions(new ClassFunctions(TestFunctions.class, "test"));
            lib.addFunctions(new ClassFunctions(TestFunctions2.class, "test"));
            lib.addFunctions(new PackageFunctions("", "call"));
            lib.addFunctions(new PackageFunctions("org.apache.commons.jxpath.ri.compiler.", "jxpathtest"));
            lib.addFunctions(new PackageFunctions("", null));
            context.setFunctions(lib);
            context.getVariables().declareVariable("List.class", List.class);
            context.getVariables().declareVariable("NodeSet.class", NodeSet.class);
        }
        functions = new ClassFunctions(TestFunctions.class, "test");
        typeConverter = TypeUtils.getTypeConverter();
    }

    @AfterEach
    public void tearDown() {
        TypeUtils.setTypeConverter(typeConverter);
    }

    @Test
    void testAllocation() {
        // Allocate new object using the default constructor
        assertXPathValue(context, "string(test:new())", "foo=0; bar=null");
        // Allocate new object using PackageFunctions and class name
        assertXPathValue(context, "string(jxpathtest:TestFunctions.new())", "foo=0; bar=null");
        // Allocate new object using a fully qualified class name
        assertXPathValue(context, "string(" + TestFunctions.class.getName() + ".new())", "foo=0; bar=null");
        // Allocate new object using a custom constructor
        assertXPathValue(context, "string(test:new(3, 'baz'))", "foo=3; bar=baz");
        // Allocate new object using a custom constructor - type conversion
        assertXPathValue(context, "string(test:new('3', 4))", "foo=3; bar=4.0");
        context.getVariables().declareVariable("A", "baz");
        assertXPathValue(context, "string(test:new(2, $A, false))", "foo=2; bar=baz");
    }

    @Test
    void testBCNodeSetHack() {
        TypeUtils.setTypeConverter(new JXPath11CompatibleTypeConverter());
        assertXPathValue(context, "test:isInstance(//strings, $List.class)", Boolean.FALSE);
        assertXPathValue(context, "test:isInstance(//strings, $NodeSet.class)", Boolean.TRUE);
    }

    @Test
    void testCollectionMethodCall() {
        final List list = new ArrayList();
        list.add("foo");
        context.getVariables().declareVariable("myList", list);
        assertXPathValue(context, "size($myList)", Integer.valueOf(1));
        assertXPathValue(context, "size(beans)", Integer.valueOf(2));
        context.getValue("add($myList, 'hello')");
        assertEquals(2, list.size(), "After adding an element");
        final JXPathContext context = JXPathContext.newContext(new ArrayList());
        assertEquals("0", String.valueOf(context.getValue("size(/)")), "Extension function on root collection");
    }

    @Test
    void testCollectionReturn() {
        assertXPathValueIterator(context, "test:collection()/name", list("foo", "bar"));
        assertXPathPointerIterator(context, "test:collection()/name", list("/.[1]/name", "/.[2]/name"));
        assertXPathValue(context, "test:collection()/name", "foo");
        assertXPathValue(context, "test:collection()/@name", "foo");
        final List list = new ArrayList();
        list.add("foo");
        list.add("bar");
        context.getVariables().declareVariable("list", list);
        final Object values = context.getValue("test:items($list)");
        assertInstanceOf(Collection.class, values, "Return type: ");
        assertEquals(list, new ArrayList((Collection) values), "Return values: ");
    }

    @Test
    void testConstructorLookup() {
        final Object[] args = { Integer.valueOf(1), "x" };
        final Function func = functions.getFunction("test", "new", args);
        assertEquals("foo=1; bar=x", func.invoke(new Context(null), args).toString(), "test:new(1, x)");
    }

    @Test
    void testConstructorLookupWithExpressionContext() {
        final Object[] args = { "baz" };
        final Function func = functions.getFunction("test", "new", args);
        assertEquals("foo=1; bar=baz", func.invoke(new Context(Integer.valueOf(1)), args).toString(), "test:new('baz')");
    }

    @Test
    void testEstablishNodeSetBaseline() {
        assertXPathValue(context, "test:isInstance(//strings, $List.class)", Boolean.TRUE);
        assertXPathValue(context, "test:isInstance(//strings, $NodeSet.class)", Boolean.FALSE);
    }

    @Test
    void testExpressionContext() {
        // Execute an extension function for each node while searching
        // The function uses ExpressionContext to get to the current
        // node.
        assertXPathValue(context, "//.[test:isMap()]/Key1", "Value 1");
        // The function gets all
        // nodes in the context that match the pattern.
        assertXPathValue(context, "count(//.[test:count(strings) = 3])", Double.valueOf(7));
        // The function receives a collection of strings
        // and checks their type for testing purposes
        assertXPathValue(context, "test:count(//strings)", Integer.valueOf(21));
        // The function receives a collection of pointers
        // and checks their type for testing purposes
        assertXPathValue(context, "test:countPointers(//strings)", Integer.valueOf(21));
        // The function uses ExpressionContext to get to the current
        // pointer and returns its path.
        assertXPathValue(context, "/beans[contains(test:path(), '[2]')]/name", "Name 2");
    }

    @Test
    void testMethodCall() {
        assertXPathValue(context, "length('foo')", Integer.valueOf(3));
        // We are just calling a method - prefix is ignored
        assertXPathValue(context, "call:substring('foo', 1, 2)", "o");
        // Invoke a function implemented as a regular method
        assertXPathValue(context, "string(test:getFoo($test))", "4");
        // Note that the prefix is ignored anyway, we are just calling a method
        assertXPathValue(context, "string(call:getFoo($test))", "4");
        // We don't really need to supply a prefix in this case
        assertXPathValue(context, "string(getFoo($test))", "4");
        // Method with two arguments
        assertXPathValue(context, "string(test:setFooAndBar($test, 7, 'biz'))", "foo=7; bar=biz");
    }

    @Test
    void testMethodLookup() {
        final Object[] args = { new TestFunctions() };
        final Function func = functions.getFunction("test", "getFoo", args);
        assertEquals("0", func.invoke(new Context(null), args).toString(), "test:getFoo($test, 1, x)");
    }

    @Test
    void testMethodLookupWithExpressionContext() {
        final Object[] args = { new TestFunctions() };
        final Function func = functions.getFunction("test", "instancePath", args);
        assertEquals("1", func.invoke(new Context(Integer.valueOf(1)), args), "test:instancePath()");
    }

    @Test
    void testMethodLookupWithExpressionContextAndArgument() {
        final Object[] args = { new TestFunctions(), "*" };
        final Function func = functions.getFunction("test", "pathWithSuffix", args);
        assertEquals("1*", func.invoke(new Context(Integer.valueOf(1)), args), "test:pathWithSuffix('*')");
    }

    @Test
    void testNodeSetReturn() {
        assertXPathValueIterator(context, "test:nodeSet()/name", list("Name 1", "Name 2"));
        assertXPathValueIterator(context, "test:nodeSet()", list(testBean.getBeans()[0], testBean.getBeans()[1]));
        assertXPathPointerIterator(context, "test:nodeSet()/name", list("/beans[1]/name", "/beans[2]/name"));
        assertXPathValueAndPointer(context, "test:nodeSet()/name", "Name 1", "/beans[1]/name");
        assertXPathValueAndPointer(context, "test:nodeSet()/@name", "Name 1", "/beans[1]/@name");
        assertEquals(2, ((Number) context.getValue("count(test:nodeSet())")).intValue());
        assertXPathValue(context, "test:nodeSet()", testBean.getBeans()[0]);
    }

    @Test
    void testStaticMethodCall() {
        assertXPathValue(context, "string(test:build(8, 'goober'))", "foo=8; bar=goober");
        // Call a static method using PackageFunctions and class name
        assertXPathValue(context, "string(jxpathtest:TestFunctions.build(8, 'goober'))", "foo=8; bar=goober");
        // Call a static method with a fully qualified class name
        assertXPathValue(context, "string(" + TestFunctions.class.getName() + ".build(8, 'goober'))", "foo=8; bar=goober");
        // Two ClassFunctions are sharing the same prefix.
        // This is TestFunctions2
        assertXPathValue(context, "string(test:increment(8))", "9");
        // See that a NodeSet gets properly converted to a string
        assertXPathValue(context, "test:string(/beans/name)", "Name 1");
    }

    @Test
    void testStaticMethodLookup() {
        final Object[] args = { Integer.valueOf(1), "x" };
        final Function func = functions.getFunction("test", "build", args);
        assertEquals("foo=1; bar=x", func.invoke(new Context(null), args).toString(), "test:build(1, x)");
    }

    @Test
    void testStaticMethodLookupWithConversion() {
        final Object[] args = { "7", Integer.valueOf(1) };
        final Function func = functions.getFunction("test", "build", args);
        assertEquals("foo=7; bar=1", func.invoke(new Context(null), args).toString(), "test:build('7', 1)");
    }

    @Test
    void testStaticMethodLookupWithExpressionContext() {
        final Object[] args = {};
        final Function func = functions.getFunction("test", "path", args);
        assertEquals("1", func.invoke(new Context(Integer.valueOf(1)), args), "test:path()");
    }
}