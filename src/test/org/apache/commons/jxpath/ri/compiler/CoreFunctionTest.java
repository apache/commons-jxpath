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

import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.jxpath.ExtendedKeyManager;
import org.apache.commons.jxpath.IdentityManager;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.KeyManager;
import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.TestMixedModelBean;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Test basic functionality of JXPath - core functions.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
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
            vars.declareVariable("bool_true", new Boolean("true"));
            vars.declareVariable("bool_false", new Boolean("false"));
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
        assertXPathValue(context, "substring('12345', 6, 6)", "");
        assertXPathValue(context, "substring('12345', 7, 8)", "");
        assertXPathValue(context, "substring('12345', 7)", "");
        assertXPathValue(context, "string-length('12345')", new Double(5));
        assertXPathValue(context, "normalize-space(' abc  def  ')", "abc def");
        assertXPathValue(context, "normalize-space('abc def')", "abc def");
        assertXPathValue(context, "normalize-space('   ')", "");
        assertXPathValue(context, "translate('--aaa--', 'abc-', 'ABC')", "AAA");
        assertXPathValue(context, "boolean(1)", Boolean.TRUE);
        assertXPathValue(context, "boolean(0)", Boolean.FALSE);
        assertXPathValue(context, "boolean('x')", Boolean.TRUE);
        assertXPathValue(context, "boolean('')", Boolean.FALSE);
        assertXPathValue(context, "boolean(/list)", Boolean.TRUE);
        assertXPathValue(context, "boolean(/list[position() < 1])", Boolean.FALSE);

        assertXPathValue(context, "true()", Boolean.TRUE);
        assertXPathValue(context, "false()", Boolean.FALSE);
        assertXPathValue(context, "not(false())", Boolean.TRUE);
        assertXPathValue(context, "not(true())", Boolean.FALSE);
        assertXPathValue(context, "null()", null);        
        assertXPathValue(context, "number('1')", new Double(1));
        assertXPathValue(context, "number($bool_true)", new Double(1));
        assertXPathValue(context, "number($bool_false)", new Double(0));
        assertXPathValue(context, "floor(1.5)", new Double(1));
        assertXPathValue(context, "floor(-1.5)", new Double(-2));
        assertXPathValue(context, "ceiling(1.5)", new Double(2));
        assertXPathValue(context, "ceiling(-1.5)", new Double(-1));
        assertXPathValue(context, "round(1.5)", new Double(2));
        assertXPathValue(context, "round(-1.5)", new Double(-1));
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
                return NodePointer.newNodePointer(null, "42", null);
            }
        });

        assertXPathValue(context, "key('a', 'b')", "42");
    }

    public void testExtendedKeyFunction() {
        context.setKeyManager(new ExtendedKeyManager() {
            public Pointer getPointerByKey(JXPathContext context, String key,
                    String value) {
                return NodePointer.newNodePointer(null, "incorrect", null);
            }

            public NodeSet getNodeSetByKey(JXPathContext context,
                    String keyName, Object keyValue) {
                return new NodeSet() {

                    public List getNodes() {
                        return Arrays.asList(new Object[] { "53", "64" });
                    }

                    public List getPointers() {
                        return Arrays.asList(new NodePointer[] {
                                NodePointer.newNodePointer(null, "53", null),
                                NodePointer.newNodePointer(null, "64", null) });
                    }

                    public List getValues() {
                        return Arrays.asList(new Object[] { "53", "64" });
                    }

                };
            }
        });
        assertXPathValue(context, "key('a', 'b')", "53");
        assertXPathValue(context, "key('a', 'b')[1]", "53");
        assertXPathValue(context, "key('a', 'b')[2]", "64");
        assertXPathValueIterator(context, "key('a', 'b')", list("53", "64"));
        assertXPathValueIterator(context, "'x' | 'y'", list("x", "y"));
        assertXPathValueIterator(context, "key('a', 'x' | 'y')", list("53", "64", "53", "64"));
        assertXPathValueIterator(context, "key('a', /list[position() < 4])", list("53", "64", "53", "64", "53", "64"));
        context.getVariables().declareVariable("ints", new int[] { 0, 0 });
        assertXPathValueIterator(context, "key('a', $ints)", list("53", "64", "53", "64"));
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