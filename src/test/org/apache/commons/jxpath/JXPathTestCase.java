/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/JXPathTestCase.java,v 1.32 2003/03/11 00:59:35 dmitri Exp $
 * $Revision: 1.32 $
 * $Date: 2003/03/11 00:59:35 $
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

package org.apache.commons.jxpath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Abstract superclass for various JXPath tests.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.32 $ $Date: 2003/03/11 00:59:35 $
 */

public abstract class JXPathTestCase extends TestCase {
    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public JXPathTestCase(String name) {
        super(name);
    }
    
    protected void assertXPathValue(JXPathContext ctx,
                String xpath, Object expected)
    {
        ctx.setLenient(false);
        Object actual = ctx.getValue(xpath);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);
    }

    protected void assertXPathValue(JXPathContext ctx,
                String xpath, Object expected, Class resultType)
    {
        ctx.setLenient(false);
        Object actual = ctx.getValue(xpath, resultType);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);
    }

    protected void assertXPathValueLenient(JXPathContext ctx,
                String xpath, Object expected)
    {
        ctx.setLenient(true);
        Object actual = ctx.getValue(xpath);
        ctx.setLenient(false);
        assertEquals("Evaluating lenient <" + xpath + ">", expected, actual);
    }

    protected void assertXPathSetValue(JXPathContext ctx,
                String xpath, Object value)
    {
        assertXPathSetValue(ctx, xpath, value, value);
    }
    
    protected void assertXPathSetValue(JXPathContext ctx,
                String xpath, Object value, Object expected)
    {
        ctx.setValue(xpath, value);
        Object actual = ctx.getValue(xpath);
        assertEquals("Modifying <" + xpath + ">", expected, actual);
    }
    
    protected void assertXPathCreatePath(JXPathContext ctx,
                String xpath, 
                Object expectedValue, String expectedPath)
    {
        Pointer pointer = ctx.createPath(xpath);
        assertEquals("Creating path <" + xpath + ">", 
                expectedPath, pointer.asPath());
                
        assertEquals("Creating path (pointer value) <" + xpath + ">", 
                expectedValue, pointer.getValue());
                
        assertEquals("Creating path (context value) <" + xpath + ">", 
                expectedValue, ctx.getValue(pointer.asPath()));
    }
    
    protected void assertXPathCreatePathAndSetValue(JXPathContext ctx,
                String xpath, Object value,
                String expectedPath)
    {
        Pointer pointer = ctx.createPathAndSetValue(xpath, value);
        assertEquals("Creating path <" + xpath + ">", 
                expectedPath, pointer.asPath());
                
        assertEquals("Creating path (pointer value) <" + xpath + ">", 
                value, pointer.getValue());
                
        assertEquals("Creating path (context value) <" + xpath + ">", 
                value, ctx.getValue(pointer.asPath()));
    }    
    
    protected void assertXPathPointer(JXPathContext ctx,
                String xpath, String expected)
    {
        ctx.setLenient(false);
        Pointer pointer = ctx.getPointer(xpath);
        String actual = pointer.toString();
        assertEquals("Evaluating pointer <" + xpath + ">", expected, actual);
    }

    protected void assertXPathPointerLenient(JXPathContext ctx,
                String xpath, String expected)
    {
        ctx.setLenient(true);
        Pointer pointer = ctx.getPointer(xpath);
        String actual = pointer.toString();
        assertEquals("Evaluating pointer <" + xpath + ">", expected, actual);
    }

    protected void assertXPathValueAndPointer(JXPathContext ctx,
                String xpath, Object expectedValue, String expectedPointer)
    {
        assertXPathValue(ctx, xpath, expectedValue);
        assertXPathPointer(ctx, xpath, expectedPointer);
    }
    
    protected void assertXPathValueIterator(JXPathContext ctx,
                String xpath, Collection expected)
    {
        Collection actual;
        if (expected instanceof List) {
            actual = new ArrayList();
        }
        else {
            actual = new HashSet();
        }
        Iterator it = ctx.iterate(xpath);
        while (it.hasNext()) {
            actual.add(it.next());
        }
        assertEquals("Evaluating value iterator <" + xpath + ">",
                expected, actual);
    }

    protected void assertXPathPointerIterator(
        JXPathContext ctx,
        String xpath,
        Collection expected) 
    {
        Collection actual;
        if (expected instanceof List) {
            actual = new ArrayList();
        }
        else {
            actual = new HashSet();
        }
        Iterator it = ctx.iteratePointers(xpath);
        while (it.hasNext()) {
            Pointer pointer = (Pointer) it.next();
            actual.add(pointer.toString());
        }
        assertEquals(
            "Evaluating pointer iterator <" + xpath + ">",
            expected,
            actual);
    }

    protected void assertDocumentOrder(
        JXPathContext context,
        String path1,
        String path2,
        int expected) 
    {
        NodePointer np1 = (NodePointer) context.getPointer(path1);
        NodePointer np2 = (NodePointer) context.getPointer(path2);
        int res = np1.compareTo(np2);
        if (res < 0) {
            res = -1;
        }
        else if (res > 0) {
            res = 1;
        }
        assertEquals(
            "Comparing paths '" + path1 + "' and '" + path2 + "'",
            expected,
            res);
    }

    protected static List list() {
        return Collections.EMPTY_LIST;
    }

    protected static List list(Object o1) {
        List list = new ArrayList();
        list.add(o1);
        return list;
    }

    protected static List list(Object o1, Object o2) {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        return list;
    }

    protected static List list(Object o1, Object o2, Object o3) {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        return list;
    }

    protected static Set set(Object o1, Object o2, Object o3) {
        Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        return list;
    }

    protected static List list(Object o1, Object o2, Object o3, Object o4) {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        return list;
    }

    protected static Set set(Object o1, Object o2, Object o3, Object o4) {
        Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        return list;
    }

    protected static List list(Object o1, Object o2, Object o3,
                Object o4, Object o5)
    {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        return list;
    }

    protected static Set set(Object o1, Object o2, Object o3, 
                Object o4, Object o5) 
    {
        Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        return list;
    }

    protected static List list(Object o1, Object o2, Object o3,
                Object o4, Object o5, Object o6)
    {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        return list;
    }

    protected static Set set(Object o1, Object o2, Object o3,
                Object o4, Object o5, Object o6)
    {
        Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        return list;
    }
    
    protected static List list(Object o1, Object o2, Object o3,
                Object o4, Object o5, Object o6, Object o7)
    {
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        list.add(o7);
        return list;
    }

    protected static Set set(Object o1, Object o2, Object o3,
                Object o4, Object o5, Object o6, Object o7)
    {
        Set list = new HashSet();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        list.add(o7);
        return list;
    }
    
}