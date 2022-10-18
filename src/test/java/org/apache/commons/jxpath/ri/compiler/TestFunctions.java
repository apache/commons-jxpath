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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.jxpath.BasicNodeSet;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.NestedTestBean;
import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.Pointer;

/**
 */
public class TestFunctions {

    private int foo;
    private String bar;

    public TestFunctions() {
    }

    public TestFunctions(final int foo, final String bar) {
        this.foo = foo;
        this.bar = bar;
    }

    public TestFunctions(final ExpressionContext context, final String bar) {
        this.foo =
            ((Number) context.getContextNodePointer().getValue()).intValue();
        this.bar = bar;
    }

    public TestFunctions(final int foo, final Object object, final boolean another) {
        this.foo = foo;
        bar = String.valueOf(object);
    }

    public int getFoo() {
        return foo;
    }

    public String getBar() {
        return bar;
    }

    public void doit() {
    }

    public TestFunctions setFooAndBar(final int foo, final String bar) {
        this.foo = foo;
        this.bar = bar;
        return this;
    }

    public static TestFunctions build(final int foo, final String bar) {
        return new TestFunctions(foo, bar);
    }

    @Override
    public String toString() {
        return "foo=" + foo + "; bar=" + bar;
    }

    public static String path(final ExpressionContext context) {
        return context.getContextNodePointer().asPath();
    }

    public String instancePath(final ExpressionContext context) {
        return context.getContextNodePointer().asPath();
    }

    public String pathWithSuffix(final ExpressionContext context, final String suffix) {
        return context.getContextNodePointer().asPath() + suffix;
    }

    public String className(
        final ExpressionContext context,
        final ExpressionContext child)
    {
        return context.getContextNodePointer().asPath();
    }

    /**
     * Returns true if the current node in the current context is a map
     */
    public static boolean isMap(final ExpressionContext context) {
        final Pointer ptr = context.getContextNodePointer();
        return ptr == null ? false : ptr.getValue() instanceof Map;
    }

    /**
     * Returns the number of nodes in the context that is passed as
     * the first argument.
     */
    public static int count(final ExpressionContext context, final Collection col) {
        for (final Object element : col) {
            if (!(element instanceof String)) {
                throw new RuntimeException("Invalid argument");
            }
        }
        return col.size();
    }

    public static int countPointers(final NodeSet nodeSet) {
        return nodeSet.getPointers().size();
    }

    public static String string(final String string) {
        return string;
    }

    public static Collection collection() {
        final ArrayList list = new ArrayList();
        list.add(new NestedTestBean("foo"));
        list.add(new NestedTestBean("bar"));
        return list;
    }

    public static NodeSet nodeSet(final ExpressionContext context) {
        final JXPathContext jxpathCtx = context.getJXPathContext();
        final BasicNodeSet set = new BasicNodeSet();
        set.add(jxpathCtx.getPointer("/beans[1]"));
        set.add(jxpathCtx.getPointer("/beans[2]"));

        return set;
    }

    public static Collection items(final Collection arg) {
        return arg;
    }

    public static Boolean isInstance(final Object o, final Class c) {
        return c.isInstance(o) ? Boolean.TRUE : Boolean.FALSE;
    }

}