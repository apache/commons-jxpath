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
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jxpath.BasicNodeSet;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.NestedTestBean;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.NodeSet;

/**
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class TestFunctions {

    private int foo;
    private String bar;

    public TestFunctions() {
    }

    public TestFunctions(int foo, String bar) {
        this.foo = foo;
        this.bar = bar;
    }

    public TestFunctions(ExpressionContext context, String bar) {
        this.foo =
            ((Number) context.getContextNodePointer().getValue()).intValue();
        this.bar = bar;
    }
    
    public TestFunctions(int foo, Object object, boolean another) {
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

    public TestFunctions setFooAndBar(int foo, String bar) {
        this.foo = foo;
        this.bar = bar;
        return this;
    }

    public static TestFunctions build(int foo, String bar) {
        return new TestFunctions(foo, bar);
    }

    public String toString() {
        return "foo=" + foo + "; bar=" + bar;
    }

    public static String path(ExpressionContext context) {
        return context.getContextNodePointer().asPath();
    }

    public String instancePath(ExpressionContext context) {
        return context.getContextNodePointer().asPath();
    }

    public String pathWithSuffix(ExpressionContext context, String suffix) {
        return context.getContextNodePointer().asPath() + suffix;
    }

    public String className(
        ExpressionContext context,
        ExpressionContext child) 
    {
        return context.getContextNodePointer().asPath();
    }

    /**
     * Returns true if the current node in the current context is a map
     */
    public static boolean isMap(ExpressionContext context) {
        Pointer ptr = context.getContextNodePointer();
        return ptr == null ? false : (ptr.getValue() instanceof Map);
    }

    /**
     * Returns the number of nodes in the context that is passed as
     * the first argument.
     */
    public static int count(ExpressionContext context, Collection col) {
        for (Iterator iter = col.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (!(element instanceof String)) {
                throw new RuntimeException("Invalid argument");
            }
        };
        return col.size();
    }
    
    public static int countPointers(NodeSet nodeSet) {
        return nodeSet.getPointers().size();
    }
    
    public static String string(String string) {
        return string;
    }
    
    public static Collection collection() {
        ArrayList list = new ArrayList();
        list.add(new NestedTestBean("foo"));
        list.add(new NestedTestBean("bar"));
        return list;
    }
    
    public static NodeSet nodeSet(ExpressionContext context) {
        JXPathContext jxpathCtx = context.getJXPathContext();
        BasicNodeSet set = new BasicNodeSet();
        set.add(jxpathCtx.getPointer("/beans[1]"));
        set.add(jxpathCtx.getPointer("/beans[2]"));
        
        return set;
    }
    
    public static Collection items(Collection arg) {
        return arg;
    }

    public static Boolean isInstance(Object o, Class c) {
        return c.isInstance(o) ? Boolean.TRUE : Boolean.FALSE;
    }

}