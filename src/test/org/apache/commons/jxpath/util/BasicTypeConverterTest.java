/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/util/BasicTypeConverterTest.java,v 1.5 2003/10/09 21:31:44 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/10/09 21:31:44 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

package org.apache.commons.jxpath.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.Pointer;

/**
 * Tests BasicTypeConverter
 * 
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2003/10/09 21:31:44 $
 */

public class BasicTypeConverterTest extends TestCase {
    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BasicTypeConverterTest(String name) {
        super(name);
    }

    public void testPrimitiveToString() {
        assertConversion(new Integer(1), String.class, "1");
    }

    public void testArrayToList() {
        assertConversion(
            new int[] { 1, 2 },
            List.class,
            Arrays.asList(new Object[] { new Integer(1), new Integer(2)}));
    }

    public void testArrayToArray() {
        assertConversion(
            new int[] { 1, 2 },
            String[].class,
            Arrays.asList(new String[] { "1", "2" }));
    }

    public void testListToArray() {
        assertConversion(
            Arrays.asList(new Integer[] { new Integer(1), new Integer(2)}),
            String[].class,
            Arrays.asList(new String[] { "1", "2" }));

        assertConversion(
            Arrays.asList(new String[] { "1", "2" }),
            int[].class,
            Arrays.asList(new Integer[] { new Integer(1), new Integer(2)}));
    }

    public void testInvalidConversion() {
        boolean exception = false;
        try {
            TypeUtils.convert("'foo'", Date.class);
        }
        catch (Throwable ex) {
            exception = true;
        }
        assertTrue("Type conversion exception", exception);
    }

    public void assertConversion(Object from, Class toType, Object expected) {
        boolean can = TypeUtils.canConvert(from, toType);
        assertTrue("Can convert: " + from.getClass() + " to " + toType, can);
        Object result = TypeUtils.convert(from, toType);
        if (result.getClass().isArray()) {
            ArrayList list = new ArrayList();
            for (int j = 0; j < Array.getLength(result); j++) {
                list.add(Array.get(result, j));
            }
            result = list;
        }
        assertEquals(
            "Convert: " + from.getClass() + " to " + toType,
            expected,
            result);
    }
    
    public void testSingletonCollectionToString() {
        assertConversion(Collections.singleton("Earth"), String.class, "Earth");
    }

    public void testSingletonArrayToString() {
        assertConversion(new String[] { "Earth" }, String.class, "Earth");
    }

    public void testPointerToString() {
        assertConversion(new Pointer() {
            public Object getValue() {
                return "value";
            }
            public Object getNode() {
                return null;
            }
            public void setValue(Object value) {
            }
            public Object getRootNode() {
                return null;
            }
            public String asPath() {
                return null;
            }
            public Object clone() {
                return null;
            }
            public int compareTo(Object o) {
                return 0;
            }
        }, String.class, "value");
    }

    public void testNodeSetToString() {
        assertConversion(new NodeSet() {
            public List getNodes() {
                return null;
            }
            public List getPointers() {
                return null;
            }
            public List getValues() {
                List list = new ArrayList();
                list.add("hello");
                list.add("goodbye");
                return Collections.singletonList(list);
            }
        }, String.class, "hello");
    }

    // succeeds in current version
    public void testNodeSetToInteger() {
        assertConversion(new NodeSet() {
            public List getNodes() {
                return null;
            }
            public List getPointers() {
                return null;
            }
            public List getValues() {
                return Collections.singletonList("9");
            }
        }, Integer.class, new Integer(9));
    }    
}