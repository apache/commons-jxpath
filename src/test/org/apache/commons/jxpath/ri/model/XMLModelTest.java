/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/ri/model/Attic/XMLModelTest.java,v 1.1 2002/08/26 22:33:09 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2002/08/26 22:33:09 $
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

package org.apache.commons.jxpath.ri.model;

import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.*;
import java.util.*;
import java.lang.reflect.*;
import org.apache.commons.jxpath.*;
import org.apache.commons.jxpath.util.*;
import org.apache.commons.jxpath.ri.*;
import org.apache.commons.jxpath.ri.parser.*;
import org.apache.commons.jxpath.ri.model.*;
import org.apache.commons.jxpath.ri.model.beans.*;
import org.apache.commons.jxpath.ri.axes.*;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.ri.compiler.Expression;
import org.apache.commons.jxpath.xml.*;
import java.beans.*;

/**
 * Abstract superclass for pure XPath 1.0.  Subclasses
 * apply the same XPaths to contexts using different models:
 * DOM, JDOM etc.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2002/08/26 22:33:09 $
 */

public abstract class XMLModelTest extends TestCase
{
    private boolean enabled = true;

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public XMLModelTest(String name)
    {
        super(name);
    }


    protected abstract String getModel();

    protected DocumentContainer createDocumentContainer(){
        return new DocumentContainer(
                getClass().getClassLoader().
                        getResource("org/apache/commons/jxpath/Vendor.xml"),
                getModel());
    }

    protected JXPathContext createContext(){
        JXPathContext context =
                JXPathContext.newContext(createDocumentContainer());
        context.setFactory(new TestFactory());
        return context;
    }

    // ------------------------------------------------ Individual Test Methods

    public void testDocumentOrder(){
        if (!enabled){
            return;
        }

        JXPathContext context = createContext();
        testDocumentOrder(context, "vendor/location", "vendor/location/address/street", -1);
        testDocumentOrder(context, "vendor/location[@id = '100']", "vendor/location[@id = '101']", -1);
        testDocumentOrder(context, "vendor//price:amount", "vendor/location", 1);
    }

    private void testDocumentOrder(JXPathContext context, String path1, String path2, int expected){
        NodePointer np1 = (NodePointer)context.getPointer(path1);
        NodePointer np2 = (NodePointer)context.getPointer(path2);
        try {
            int res = np1.compareTo(np2);
            if (res < 0){
                res = -1;
            }
            else if (res > 0){
                res = 1;
            }
            assertEquals("Comparing paths '" + path1 + "' and '" + path2 + "'", expected, res);
        }
        catch (Exception ex){
            System.err.println("Comparing paths '" + path1 + "' and '" + path2 + "'");
            ex.printStackTrace();
        }
    }

    /**
     * Test JXPathContext.createPath() with various arguments
     */
    public void testCreatePath(){
        if (!enabled){
            return;
        }

        JXPathContext context = createContext();

        // Create a DOM element
        testCreatePath(context, "/vendor[1]/location[3]", "");

        // Create a DOM element with contents
        testCreatePath(context, "/vendor[1]/location[3]/address/street", "",
                "/vendor[1]/location[3]/address[1]/street[1]");

    }

    private void testCreatePath(JXPathContext context, String path, Object value){
        testCreatePath(context, path, value, path);
    }

    private void testCreatePath(JXPathContext context, String path,
                Object value, String expectedPath){
        Pointer ptr = null;
        try {
            ptr = context.createPath(path);
        }
        catch(JXPathException ex){
            ex.getException().printStackTrace();
        }

        assertEquals("Pointer <" + path + ">", expectedPath, ptr.asPath());
        assertEquals("Created <" + path + ">", value, ptr.getValue());
    }


    /**
     * Test JXPath.createPathAndSetValue() with various arguments
     */
    public void testCreatePathAndSetValue(){
        if (!enabled){
            return;
        }
        JXPathContext context = createContext();

        // Create a XML element
        testCreatePathAndSetValue(context, "vendor/location[3]", "");

        // Create a DOM element with contents
        testCreatePathAndSetValue(context, "vendor/location[3]/address/street", "Lemon Circle");
    }

    private void testCreatePathAndSetValue(JXPathContext context, String path, Object value){
        Pointer ptr = context.createPathAndSetValue(path, value);
        assertTrue("Pointer <" + path + ">", ptr != null);
        assertEquals("Created <" + path + ">", value, context.getValue(path));
        assertEquals("Pointer value <" + path + ">", value, ptr.getValue());
    }

    /**
     * Test JXPathContext.removePath() with various arguments
     */
    public void testRemovePath(){
        if (!enabled){
            return;
        }
        JXPathContext context = createContext();

        // Remove XML nodes
        context.removePath("vendor/location[@id = '101']//street/text()");
        assertEquals("Remove DOM text", "",
                    context.getValue("vendor/location[@id = '101']//street"));

        context.removePath("vendor/location[@id = '101']//street");
        assertEquals("Remove DOM element", new Double(0),
                    context.getValue("count(vendor/location[@id = '101']//street)"));

        context.removePath("vendor/location[@id = '100']/@name");
        assertEquals("Remove DOM attribute", new Double(0),
                    context.getValue("count(vendor/location[@id = '100']/@name)"));
    }

    public void testID(){
        if (!enabled){
            return;
        }
        JXPathContext context = createContext();
        context.setIdentityManager(new IdentityManager(){
            public Pointer getPointerByID(JXPathContext context, String id){
                NodePointer ptr = (NodePointer)context.getPointer("/");
                ptr = ptr.getValuePointer();        // Unwrap the container
                return ptr.getPointerByID(context, id);
            }
        });
        context.setKeyManager(new KeyManager(){
            public Pointer getPointerByKey(JXPathContext context,
                                            String key, String value){
                return NodePointer.newNodePointer(null, "42", null);
            }
        });
        assertEquals("Test ID", "Tangerine Drive",
            context.getValue("id(101)//street"));
        assertEquals("Test ID Path", "id('101')/address[1]/street[1]",
            context.getPointer("id(101)//street").asPath());

        context.setLenient(true);
        assertEquals("Test ID Path Null", "id(105)/address/street",
            context.getPointer("id(105)/address/street").asPath());
    }

    public void testModel() throws Exception {
        if (!enabled){
            return;
        }

        DocumentContainer docCtr = createDocumentContainer();
        JXPathContext context = createContext();
        context.getVariables().declareVariable("document", docCtr.getValue());
        context.getVariables().declareVariable("container", docCtr);
        testXPaths(context, dom_tests, false);
    }

    private void testXPaths(JXPathContext ctx, XP xpath_tests[], boolean ignorePath) throws Exception{
        Exception exception = null;
        for  (int i=0; i < xpath_tests.length; i++) {
            try {
                Object actual;
                // System.err.println("XPATH: " + xpath_tests[i].xpath);
                if (xpath_tests[i].path){
                    if (ignorePath){
                        actual = xpath_tests[i].expected;
                    }
                    else {
                        if (xpath_tests[i].eval){
                            Iterator it = ctx.iteratePointers(xpath_tests[i].xpath);
                            List paths = new ArrayList();
                            while (it.hasNext()){
                                paths.add(((Pointer)it.next()).asPath());
                            }
                            actual = paths;
                        }
                        else {
                            ctx.setLenient(xpath_tests[i].lenient);
                            actual = ctx.getPointer(xpath_tests[i].xpath).asPath();
                        }
                    }
                }
                else {
                    if (xpath_tests[i].eval){
                        ArrayList list = new ArrayList();
                        Iterator it = ctx.iterate(xpath_tests[i].xpath);
                        while (it.hasNext()){
                            list.add(it.next());
                        }
                        actual = list;
                    }
                    else {
                        ctx.setLenient(xpath_tests[i].lenient);
                        actual = ctx.getValue(xpath_tests[i].xpath);
                        ctx.setLenient(false);
                    }
                }
                assertEquals("Evaluating <" + xpath_tests[i].xpath + ">", xpath_tests[i].expected, actual);
            }
            catch (Exception ex){
                System.err.println("Exception during <" + xpath_tests[i].xpath + ">");
                ex.printStackTrace();
                exception = ex;
            }
            if (exception != null){
                throw exception;
            }
        }

        // Make sure that location paths are properly constructed
        for (int i=0; i < xpath_tests.length; i++) {
            try {
                if (!xpath_tests[i].path && !xpath_tests[i].eval){
                    Pointer ptr = ctx.getPointer(xpath_tests[i].xpath);
                    Pointer test = ctx.getPointer(ptr.asPath());
                    assertEquals("Testing pointer for <" + xpath_tests[i].xpath + ">", ptr.asPath(), test.asPath());
                }
            }
            catch (Exception ex){
                System.err.println("Exception during pointer test <" + xpath_tests[i].xpath + ">");
                ex.printStackTrace();
            }
        }
    }

    private static class XP {
        public String xpath;
        public Object expected;
        public boolean eval;
        public boolean path;
        public boolean lenient;

        public XP(String xpath,  Object expected, boolean eval, boolean path, boolean lenient){
            this.xpath = xpath;
            this.expected = expected;
            this.eval = eval;
            this.path = path;
            this.lenient = lenient;
        }
    }

    private static XP test(String xpath, Object expected){
        return new XP(xpath, expected, false, false, false);
    }

    private static XP testLenient(String xpath, Object expected){
        return new XP(xpath, expected, false, false, true);
    }

    private static XP testEval(String xpath, Object expected){
        return new XP(xpath, expected, true, false, false);
    }

    private static XP testPath(String xpath, Object expected){
        return new XP(xpath, expected, false, true, true);
    }

    private static XP testEvalPath(String xpath, Object expected){
        return new XP(xpath, expected, true, true, false);
    }

    private static List list(Object o1){
        List list = new ArrayList();
        list.add(o1);
        return list;
    }

    private static List list(Object o1, Object o2){
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        return list;
    }


    static final XP[] dom_tests = new XP[]{
        test("vendor/location/address/street", "Orchard Road"),
        test("vendor/location[2]/address/street", "Tangerine Drive"),
        test("vendor/location/address/city", "Fruit Market"),
        test("//street", "Orchard Road"),
        test("local-name(//street/..)", "address"),
        test("number(vendor/location/employeeCount)", new Double(10)),
        test("vendor/location/employeeCount + 1", new Double(11)),
        test("vendor/location/employeeCount and true()", Boolean.TRUE),
        test("vendor/location[.//employeeCount = 10]/following-sibling::location//street", "Tangerine Drive"),
        testPath("vendor/location[.//employeeCount = 10]/following-sibling::location//street",
                "/vendor[1]/location[2]/address[1]/street[1]"),
        testPath("//location[2]/preceding-sibling::location//street",
                "/vendor[1]/location[1]/address[1]/street[1]"),
        test("vendor/location/@id", "100"),
        testPath("vendor/location/@id", "/vendor[1]/location[1]/@id"),
        testEval("vendor/location/@id", list("100", "101")),
        test("vendor/product/price:amount", "45.95"),
        test("namespace-uri(vendor/product/price:amount)", "priceNS"),
        test("local-name(vendor/product/price:amount)", "amount"),
        test("name(vendor/product/price:amount)", "priceNS:amount"),
        test("vendor/product/prix", "934.99"),
        test("vendor/product/prix/namespace::price", "priceNS"),
        testPath("vendor/product/prix/namespace::price", "/vendor[1]/product[1]/prix[1]/namespace::price"),
        test("count(vendor/product/namespace::*)", new Double(3)),
        test("name(vendor/product/prix/namespace::price)", "priceNS:price"),
        test("local-name(vendor/product/prix/namespace::price)", "price"),
        test("vendor/product/price:amount/@price:discount", "10%"),
        test("vendor/product/value:amount/@value:discount", "10%"),
        test("namespace-uri(vendor/product/price:amount/@price:discount)", "priceNS"),
        test("local-name(vendor/product/price:amount/@price:discount)", "discount"),
        test("name(vendor/product/price:amount/@price:discount)", "priceNS:discount"),
        test("vendor/product/price:amount/@discount", "20%"),
        test("namespace-uri(vendor/product/price:amount/@discount)", ""),
        test("local-name(vendor/product/price:amount/@discount)", "discount"),
        test("name(vendor/product/price:amount/@discount)", "discount"),
        test("vendor/product/price:sale/saleEnds/ancestor::price:sale/saleEnds", "never"),
        test("vendor/product/price:sale/ancestor-or-self::price:sale/saleEnds", "never"),
        test("vendor/product/price:sale/saleEnds/ancestor::price:*" + "/saleEnds", "never"),
        test("count(vendor/product/price:*)", new Double(2)),
        test("count(vendor/product/value:*)", new Double(2)),
        test("count(vendor/product/*)", new Double(2)),
        testEval("vendor/product/price:amount/@price:*", list("10%")),
        testEval("vendor/product/price:amount/@*", list("20%")),
        test("count(//price:*)", new Double(2)),
        test("vendor/product/price:sale/saleEnds/parent::price:*" + "/saleEnds", "never"),
        test("//location/following::price:sale/saleEnds", "never"),
        test("//price:sale/self::price:sale/saleEnds", "never"),
        testLenient("//price:sale/self::x/saleEnds", null),

        test("//product/comment()", "We are not buying this product, ever"),
        test("//product/text()[. != '']", "We love this product."),
        testPath("//product/text()", "/vendor[1]/product[1]/text()[1]"),
        test("//product/processing-instruction()", "do not show anybody"),
        test("//product/processing-instruction('report')", "average only"),
        testPath("//product/processing-instruction('report')",
            "/vendor[1]/product[1]/processing-instruction('report')[1]"),
        test("name(//product/processing-instruction()[1])", "security"),

        test("//product/prix/@xml:lang", "fr"),
        test("//product/prix[lang('fr')]", "934.99"),
        test("//product/price:sale[lang('en')]/saleEnds", "never"),
        test("vendor/location/@manager", ""),
        testLenient("vendor/location/@missing", null),
        test("count(vendor/location[1]/@*)", new Double(3)),
        test("vendor/location[@id='101']//street", "Tangerine Drive"),

        test("$document/vendor/location[1]//street", "Orchard Road"),
        testPath("$document/vendor/location[1]//street",
            "$document/vendor[1]/location[1]/address[1]/street[1]"),
        test("$document/vendor//street", "Orchard Road"),
        test("$container/vendor//street", "Orchard Road"),
        test("$container//street", "Orchard Road"),
        testPath("$container//street", "$container/vendor[1]/location[1]/address[1]/street[1]"),

        testEval("vendor/contact/following::location//street",
            list("Orchard Road", "Tangerine Drive")),
   };
}