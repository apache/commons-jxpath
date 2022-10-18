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
package org.apache.commons.jxpath.issues;

import java.util.Iterator;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Testcase proving JXPATH-118 issue with asPath() returning wrong names.
 */
public class JXPath118Test extends TestCase
{

    public void testJXPATH118IssueWithAsPath() throws Exception
    {
        final Object contextBean = new SomeChildClass();
        final JXPathContext context = JXPathContext.newContext(contextBean);
        final Iterator iteratePointers = context.iteratePointers("//*");
        Assert.assertEquals("/bar", ((Pointer) iteratePointers.next()).asPath());
        Assert.assertEquals("/baz", ((Pointer) iteratePointers.next()).asPath());
        Assert.assertEquals("/foo", ((Pointer) iteratePointers.next()).asPath());
    }

    public static class SomeChildClass
    {

        private int foo = 1;
        private int bar = 2;
        private int baz = 3;

        public int getFoo()
        {
            return foo;
        }

        public void setFoo(final int foo)
        {
            this.foo = foo;
        }

        public int getBar()
        {
            return bar;
        }

        public void setBar(final int bar)
        {
            this.bar = bar;
        }

        public int getBaz()
        {
            return baz;
        }

        public void setBaz(final int baz)
        {
            this.baz = baz;
        }

    }

}
