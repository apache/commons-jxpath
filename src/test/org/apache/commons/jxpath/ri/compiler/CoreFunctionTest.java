/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/ri/compiler/CoreFunctionTest.java,v 1.7 2004/01/18 01:43:30 dmitri Exp $
 * $Revision: 1.7 $
 * $Date: 2004/01/18 01:43:30 $
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

package org.apache.commons.jxpath.ri.compiler;

import java.text.DecimalFormatSymbols;

import org.apache.commons.jxpath.IdentityManager;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.KeyManager;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.TestMixedModelBean;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Test basic functionality of JXPath - core functions.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.7 $ $Date: 2004/01/18 01:43:30 $
 */

public class CoreFunctionTest extends JXPathTestCase {
    private JXPathContext context;

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public CoreFunctionTest(String name) {
        super(name);
    }

    public void setUp() {
        if (context == null) {
            context = JXPathContext.newContext(new TestMixedModelBean());
            Variables vars = context.getVariables();
            vars.declareVariable("nan", new Double(Double.NaN));
        }
    }

    public void testCoreFunctions() {
        assertXPathValue(context, "string(2)", "2");
        assertXPathValue(context, "string($nan)", "NaN");
        assertXPathValue(context, "string(-$nan)", "NaN");
        assertXPathValue(context, "string(-2 div 0)", "-Infinity");
        assertXPathValue(context, "string(2 div 0)", "Infinity");
        assertXPathValue(context, "concat('a', 'b', 'c')", "abc");
        assertXPathValue(context, "starts-with('abc', 'ab')", Boolean.TRUE);
        assertXPathValue(context, "starts-with('xabc', 'ab')", Boolean.FALSE);
        assertXPathValue(context, "contains('xabc', 'ab')", Boolean.TRUE);
        assertXPathValue(context, "contains('xabc', 'ba')", Boolean.FALSE);
        assertXPathValue(
            context,
            "substring-before('1999/04/01', '/')",
            "1999");
        assertXPathValue(
            context,
            "substring-after('1999/04/01', '/')",
            "04/01");
        assertXPathValue(context, "substring('12345', 2, 3)", "234");
        assertXPathValue(context, "substring('12345', 2)", "2345");
        assertXPathValue(context, "substring('12345', 1.5, 2.6)", "234");
        assertXPathValue(context, "substring('12345', 0, 3)", "12");
        assertXPathValue(context, "substring('12345', 0 div 0, 3)", "");
        assertXPathValue(context, "substring('12345', 1, 0 div 0)", "");
        assertXPathValue(context, "substring('12345', -42, 1 div 0)", "12345");
        assertXPathValue(context, "substring('12345', -1 div 0, 1 div 0)", "");
        assertXPathValue(context, "string-length('12345')", new Double(5));
        assertXPathValue(context, "normalize-space(' abc  def  ')", "abc def");
        assertXPathValue(context, "normalize-space('abc def')", "abc def");
        assertXPathValue(context, "normalize-space('   ')", "");
        assertXPathValue(context, "translate('--aaa--', 'abc-', 'ABC')", "AAA");
        assertXPathValue(context, "boolean(1)", Boolean.TRUE);
        assertXPathValue(context, "boolean(0)", Boolean.FALSE);
        assertXPathValue(context, "boolean('x')", Boolean.TRUE);
        assertXPathValue(context, "boolean('')", Boolean.FALSE);

        assertXPathValue(context, "true()", Boolean.TRUE);
        assertXPathValue(context, "false()", Boolean.FALSE);
        assertXPathValue(context, "not(false())", Boolean.TRUE);
        assertXPathValue(context, "not(true())", Boolean.FALSE);
        assertXPathValue(context, "number('1')", new Double(1));
        assertXPathValue(context, "floor(1.5)", new Double(1));
        assertXPathValue(context, "floor(-1.5)", new Double(-2));
        assertXPathValue(context, "ceiling(1.5)", new Double(2));
        assertXPathValue(context, "ceiling(-1.5)", new Double(-1));
        assertXPathValue(context, "round(1.5)", new Double(2));
        assertXPathValue(context, "round(-1.5)", new Double(-1));
        assertXPathValue(context, "null()", null);
    }

    public void testIDFunction() {
        context.setIdentityManager(new IdentityManager() {
            public Pointer getPointerByID(JXPathContext context, String id) {
                NodePointer ptr = (NodePointer) context.getPointer("/document");
                ptr = ptr.getValuePointer();
                return ptr.getPointerByID(context, id);
            }
        });

        assertXPathValueAndPointer(
            context,
            "id(101)//street",
            "Tangerine Drive",
            "id('101')/address[1]/street[1]");

        assertXPathPointerLenient(
            context,
            "id(105)/address/street",
            "id(105)/address/street");
    }

    public void testKeyFunction() {
        context.setKeyManager(new KeyManager() {
            public Pointer getPointerByKey(
                JXPathContext context,
                String key,
                String value) 
            {
                return NodePointer.newNodePointer(null, "42", null, null);
            }
        });

        assertEquals("Test key", "42", context.getValue("key('a', 'b')"));
    }
    
    public void testFormatNumberFunction() {
        
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDigit('D');
        
        context.setDecimalFormatSymbols("test", symbols);
        
        assertXPathValue(
            context,
            "format-number(123456789, '#.000000000')",
            "123456789.000000000");

        assertXPathValue(
            context,
            "format-number(123456789, '#.0')",
            "123456789.0");

        assertXPathValue(
            context, 
            "format-number(0.123456789, '##%')", 
            "12%");

        assertXPathValue(
            context,
            "format-number(123456789, '################')",
            "123456789");

        assertXPathValue(
            context,
            "format-number(123456789, 'D.0', 'test')",
            "123456789.0");

        assertXPathValue(
            context,
            "format-number(123456789, '$DDD,DDD,DDD.DD', 'test')",
            "$123,456,789");
    }
}