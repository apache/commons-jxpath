/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/JXPathTestCase.java,v 1.4 2001/09/08 20:59:58 dmitri Exp $
 * $Revision: 1.4 $
 * $Date: 2001/09/08 20:59:58 $
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

package org.apache.commons.jxpath;

import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.*;
import java.util.*;

import org.apache.commons.jxpath.ri.*;
import org.apache.commons.jxpath.ri.parser.*;
import org.apache.commons.jxpath.ri.pointers.*;
import org.apache.commons.jxpath.ri.axes.*;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.tree.DOMWrapper;
import java.beans.*;


/**
 * <p>
 *  Test Case for the JXPath class.  The majority of these tests use
 *  instances of the TestBean class, so be sure to update the tests if you
 *  change the characteristics of that class.
 * </p>
 *
 * <p>
 *   Note that the tests are dependant upon the static aspects
 *   (such as array sizes...) of the TestBean.java class, so ensure
 *   than all changes to TestBean are reflected here.
 * </p>
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.4 $ $Date: 2001/09/08 20:59:58 $
 */

public class JXPathTestCase extends TestCase
{
    private boolean enabled = true;

    /**
     * Exercises this test case only
     */
    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    // ---------------------------------------------------- Instance Variables

    /**
     * The test bean for each test.
     */
    protected TestBean bean = null;


    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public JXPathTestCase(String name)
    {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp()
    {
        bean = new TestBean();
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite()
    {
        return (new TestSuite(JXPathTestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown()
    {
        bean = null;
    }


    // ------------------------------------------------ Individual Test Methods

    /**
     * Test property iterators, the core of the graph traversal engine
     */
    public void testIndividualIterators(){
        if (enabled){
//        testIndividual(0, 0, true, false, 3);
            testIndividual(+1, 0, true, false, 0);
            testIndividual(-1, 0, true, false, 4);

            testIndividual(0, -1, true, true, 4);
            testIndividual(+1, -1, true, true, 4);
            testIndividual(-1, -1, true, true, 0);

            testIndividual(0, 1, true, false, 2);
            testIndividual(0, 1, true, true, 1);

            testIndividual(0, 0, false, false, 4);
            testIndividual(0, 0, false, true, 4);
        }
    }

    private void testIndividual(int relativePropertyIndex, int offset, boolean useStartLocation, boolean reverse, int expected){
        PropertyOwnerPointer root = (PropertyOwnerPointer)NodePointer.createNodePointer(new QName(null, "root"), bean);
        NodeIterator it;

        if (useStartLocation){
            PropertyPointer holder = root.getPropertyPointer();
            holder.setPropertyIndex(relativeProperty(holder, relativePropertyIndex));
            holder.setIndex(offset);
            it = holder.siblingIterator(new QName(null, "integers"), reverse);
        }
        else {
//            it = PropertyIterator.iterator(root, "integers", reverse);
            it = root.childIterator(new QName(null, "integers"), reverse);
        }

        int size = 0;
        while(it.setPosition(it.getPosition() + 1)){
            size++;
        }
        assertEquals("ITERATIONS: Individual, relativePropertyIndex=" + relativePropertyIndex +
            ", offset=" + offset + ", useStartLocation=" + useStartLocation +
            ", reverse=" + reverse, expected, size);
    }

    public void testMultipleIterators(){
        if (enabled){
            testMultiple(0, 0, true, false, 20);

            testMultiple(3, 0, true, false, 16);
            testMultiple(3, -1, true, true, 8);
            testMultiple(3, 0, true, true, 4);
            testMultiple(0, 0, false, false, 21);
            testMultiple(0, 0, false, true, 21);

            testMultiple(3, 1, true, false, 15);
            testMultiple(3, 3, true, false, 13);
        }
    }

    private void testMultiple(int propertyIndex, int offset, boolean useStartLocation, boolean reverse, int expected){
        PropertyOwnerPointer root = (PropertyOwnerPointer)NodePointer.createNodePointer(new QName(null, "root"), bean);
        NodeIterator it;

        if (useStartLocation){
            PropertyPointer holder = root.getPropertyPointer();
            holder.setPropertyIndex(propertyIndex);
            holder.setIndex(offset);
            it = holder.siblingIterator(null, reverse);
        }
        else {
            it = root.childIterator(null, reverse);
        }

        int size = 0;
        while(it.setPosition(it.getPosition() + 1)){
//            System.err.println("LOC: " + it.getCurrentNodePointer());
            size++;
        }
        assertEquals("ITERATIONS: Multiple, propertyIndex=" + propertyIndex +
            ", offset=" + offset + ", useStartLocation=" + useStartLocation +
            ", reverse=" + reverse, expected, size);
    }

    private int relativeProperty(PropertyPointer holder, int offset){
        String[] names = holder.getPropertyNames();
        for (int i = 0; i < names.length; i++){
            if (names[i].equals("integers")){
                return i + offset;
            }
        }
        return -1;
    }

    /**
     * Test JXPath.getValue() with various arguments
     */
    public void testGetValue(){
        if (enabled){
            JXPathContext context = JXPathContext.newContext(bean);
            testGetValue(context, "2+2",                     new Double(4.0));
            testGetValue(context, "boolean",                 Boolean.FALSE);
            testGetValue(context, "substring(boolean, 1,2)", "fa"); // 'fa'lse
            testGetValue(context, "int*2",                   new Double(2.0));
            testGetValue(context, "integers[1]",             new Integer(1));
            testGetValue(context, "nestedBean",              bean.getNestedBean());
            testGetValue(context, "nestedBean/boolean",      Boolean.FALSE);
            testGetValue(context, "object/name",             "Name 5");
            testGetValue(context, "objects[1]",              new Integer(1));
            testGetValue(context, "map/Key1",                "Value 1");
            testGetValue(context, "beans[name = 'Name 1']",  bean.getBeans()[0]);
            testGetValue(context, ".[1]/int",                new Integer(1));
//        testGetValue(context, "id('foo')",               new Integer(1));
//        testGetValue(context, "key('foo', 'bar')",               new Integer(1));
            testGetValue(context, "integers[1]",            new Double(1), Double.class);
            testGetValue(context, "2 + 3",                  "5.0", String.class);
            testGetValue(context, "2 + 3",                  Boolean.TRUE, boolean.class);
            boolean exception = false;
            try {
                testGetValue(context, "'foo'",                  null, Date.class);
            }
            catch(Exception ex){
                exception = true;
            }
            assertTrue("Type conversion exception", exception);
        }
    }

    /**
     * Test JXPath.getValue() with variables
     */
    public void testVariables(){
        if (enabled){
            JXPathContext context = JXPathContext.newContext(bean.getBeans());
            context.getVariables().declareVariable("x", new Double(7.0));
            context.getVariables().declareVariable("y", null);
            context.getVariables().declareVariable("z", bean);
            context.getVariables().declareVariable("t", new String[]{"a", "b"});
            context.getVariables().declareVariable("m", bean.getMap());

            testGetValue(context, "$x + 3",  new Double(10.0));
            testGetValue(context, "$y",  null);
            testGetValue(context, "$y + 1",  new Double(1.0));
            boolean exception = false;
            try {
                testGetValue(context, "$none",  null);
            }
            catch (Exception ex){
                exception = true;
            }
            assertTrue("Evaluating '$none', expected exception - did not get it", exception);

            testGetValue(context, "$z/int",  new Integer(1));
            testGetValue(context, "$z/integers[$x - 5]",  new Integer(2));

            testGetValue(context, ".[2]/name",  "Name 2");
            testGetValue(context, "$t[2]",  "b");
            testGetValue(context, "$m/Key1",  "Value 1");
//          testGetValue(context, "[1]",  new Integer(2));
        }
    }

    private void testGetValue(JXPathContext context, String xpath, Object expected) {
        Object actual = context.getValue(xpath);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);
    }

    private void testGetValue(JXPathContext context, String xpath, Object expected, Class requiredType) {
        Object actual = context.getValue(xpath, requiredType);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);
    }

    /**
     * Test JXPath.eval() with various arguments
     */
    public void testEval(){
        if (enabled){
            JXPathContext context = JXPathContext.newContext(bean);
            testEval(context, "integers[position()<3]",
                    Arrays.asList(new Integer[]{new Integer(1), new Integer(2)}));
        }
    }

    private void testEval(JXPathContext context, String xpath, Object expected) {
        Object actual = context.eval(xpath);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);
    }

    public void testContextDependency(){
        if (enabled){
            testContextDependency("1", false);
            testContextDependency("$x", false);
            testContextDependency("/foo", false);
            testContextDependency("foo", true);
            testContextDependency("/foo[3]", false);
            testContextDependency("/foo[$x]", false);
            testContextDependency("/foo[bar]", true);
            testContextDependency("3 + 5", false);
            testContextDependency("test:func(3, 5)", false);
            testContextDependency("test:func(3, foo)", true);
        }
    }

    public void testContextDependency(String xpath, boolean expected){
        Expression expr = (Expression)Parser.parseExpression(xpath, new TreeCompiler());
        assertEquals("Evaluating <" + xpath + ">", expected, expr.isContextDependent());
    }

    /**
     * Test JXPath.setValue() with various arguments
     */
    public void testSetValue(){
        if (enabled){
            TestBean tBean = new TestBean();
            JXPathContext context = JXPathContext.newContext(tBean);
            context.getVariables().declareVariable("x", null);

            context.setValue("$x", new Integer(1));
            assertEquals("Modified <" + "$x" + ">", new Integer(1), context.getValue("$x"));

            boolean exception = false;
            try {
                context.setValue("$y", new Integer(1));
            }
            catch (Exception ex){
                exception = true;
            }
            assertTrue("Setting '$y = 1', expected exception - did not get it", exception);

            context.setValue("int", new Integer(3));
            assertEquals("Modified <" + "int" + ">", new Integer(3), context.getValue("int"));

            context.setValue("integers[2]", new Integer(5));
            assertEquals("Modified <" + "integers[2]" + ">", new Integer(5), context.getValue("integers[2]"));

            NestedTestBean nBean = new NestedTestBean("Name 9");
            tBean.getBeans()[1] = null;
            context.setValue("beans[2]", nBean);
            assertEquals("Modified <" + "beans[2]" + ">", nBean, context.getValue("beans[2]"));

            context.setValue("map/Key1", new Integer(6));
            assertEquals("Modified <" + "map/Key1" + ">", new Integer(6), context.getValue("map/Key1"));

            context.setValue("map/Key1", new Integer[]{new Integer(7), new Integer(8)});
            context.setValue("map/Key1[1]", new Integer(9));
            assertEquals("Modified <" + "map/Key1[1]" + ">", new Integer(9), context.getValue("map/Key1[1]"));

            context.setValue("map/Key4", new Integer(7));
            assertEquals("Modified <" + "map/Key4" + ">", new Integer(7), context.getValue("map/Key4"));
        }
    }

    public void testNull(){
        if (enabled){
            JXPathContext context = JXPathContext.newContext(new TestNull());
            testGetValue(context, "nothing", null);
            testGetValue(context, "child/nothing", null);
            testGetValue(context, "nothing/something", null);
            testGetValue(context, "array[2]", null);
            testGetValue(context, "array[2]/something", null);
        }
    }

    /**
     * Test JXPath.getValue() with nested contexts
     */
    public void testNestedContext(){
        if (enabled){
            JXPathContext pcontext = JXPathContext.newContext(null);
            pcontext.getVariables().declareVariable("x", bean);

            JXPathContext context = JXPathContext.newContext(pcontext, bean);

            testGetValue(context, "integers[$x/int]",  new Integer(1));
        }
    }

    public void testFunctions(){
        if (enabled){
            Object[] args;
            Function func;

            TestFunctions test = new TestFunctions();
            Functions funcs = new ClassFunctions(TestFunctions.class, "test");

            args = new Object[]{new Integer(1), "x"};
            func = funcs.getFunction("test", "new", args);
            assertEquals("test:new(1, x)", func.invoke(args).toString(), "foo=1; bar=x");

            args = new Object[]{new Integer(1), "x"};
            func = funcs.getFunction("test", "build", args);
            assertEquals("test:build(1, x)", func.invoke(args).toString(), "foo=1; bar=x");

            args = new Object[]{"7", new Integer(1)};
            func = funcs.getFunction("test", "build", args);
            assertEquals("test:build('7', 1)", func.invoke(args).toString(), "foo=7; bar=1");

            args = new Object[]{test};
            func = funcs.getFunction("test", "getFoo", args);
            assertEquals("test:getFoo($test, 1, x)", func.invoke(args).toString(), "0");
        }
    }

    /*
     * Remove the underscore from the method name if you want to see the output
     */
    public void _testXSLT(){
        Node node = DOMWrapper.createNode(bean, "test");
        printXML(node, System.err);
    }

    private static void printXML(Node node, java.io.OutputStream outputStream){
        try {
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(node);
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(outputStream);
            javax.xml.transform.Transformer trans = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            trans.transform(source, result);
        }
        catch (Exception ex){
            // We don't care about this. The method is only for debugging
            ex.printStackTrace();
        }
   }

   // Fails most tests. Remove "_" and run to see them.
    public void _testParserXalan(){
        System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,
                "org.apache.commons.jxpath.xalan.JXPathContextFactoryXalanImpl");
        testParser(JXPathContextFactory.newInstance().newContext(null, bean), true);
    }

    public void testParserReferenceImpl(){
        System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,
                "org.apache.commons.jxpath.ri.JXPathContextFactoryReferenceImpl");
        testParser(JXPathContextFactory.newInstance().newContext(null, bean), false);
    }

    public void testParser(JXPathContext ctx, boolean ignorePath){
        ctx.getVariables().declareVariable("a", new Double(1));
        ctx.getVariables().declareVariable("b", new Double(1));
        ctx.getVariables().declareVariable("nan", new Double(Double.NaN));
        ctx.getVariables().declareVariable("test", new TestFunctions(4, "test"));
        ctx.getVariables().declareVariable("testnull", new TestNull());
        FunctionLibrary lib = new FunctionLibrary();
        lib.addFunctions(new ClassFunctions(TestFunctions.class, "test"));
        lib.addFunctions(new ClassFunctions(TestFunctions2.class, "test"));
        lib.addFunctions(new PackageFunctions("", "call"));
        lib.addFunctions(new PackageFunctions("org.apache.commons.jxpath.", "jxpathtest"));
        ctx.setFunctions(lib);
        testXPaths(ctx, xpath_tests, ignorePath);
    }

    private void testXPaths(JXPathContext ctx, XP xpath_tests[], boolean ignorePath){
        for  (int i=0; i < xpath_tests.length; i++) {
            try {
                Object actual;
//                System.err.println("XPATH: " + xpath_tests[i].xpath);
                if (xpath_tests[i].path){
                    if (ignorePath){
                        actual = xpath_tests[i].expected;
                    }
                    else {
                        if (xpath_tests[i].eval){
                            List list = ctx.locate(xpath_tests[i].xpath);
                            List paths = new ArrayList();
                            for (Iterator it = list.iterator(); it.hasNext();){
                                paths.add(((Pointer)it.next()).asPath());
                            }
                            actual = paths;
                        }
                        else {
                            actual = ctx.locateValue(xpath_tests[i].xpath).asPath();
                        }
                    }
                }
                else {
                    if (xpath_tests[i].eval){
                        actual = ctx.eval(xpath_tests[i].xpath);
                    }
                    else {
                        actual = ctx.getValue(xpath_tests[i].xpath);
                    }
                }
                assertEquals("Evaluating <" + xpath_tests[i].xpath + ">", xpath_tests[i].expected, actual);
            }
            catch (Exception ex){
                System.err.println("Exception during <" + xpath_tests[i].xpath + ">");
                ex.printStackTrace();
            }
        }
    }

    private static class XP {
        public String xpath;
        public Object expected;
        public boolean eval;
        public boolean path;

        public XP(String xpath,  Object expected, boolean eval, boolean path){
            this.xpath = xpath;
            this.expected = expected;
            this.eval = eval;
            this.path = path;
        }
    }

    private static XP test(String xpath, Object expected){
        return new XP(xpath, expected, false, false);
    }

    private static XP testEval(String xpath, Object expected){
        return new XP(xpath, expected, true, false);
    }

    private static XP testPath(String xpath, Object expected){
        return new XP(xpath, expected, false, true);
    }

    private static XP testEvalPath(String xpath, Object expected){
        return new XP(xpath, expected, true, true);
    }

    static final XP[] xpath_tests = new XP[]{
        // Numbers
        test("1", new Double(1.0)),
        testEval("1", list(new Double(1.0))),
        test("-1", new Double(-1.0)),
        test("2 + 2", new Double(4.0)),
        test("3 - 2", new Double(1.0)),
        test("1 + 2 + 3 - 4 + 5", new Double(7.0)),
        test("3 * 2", new Double(3.0*2.0)),
        test("3 div 2", new Double(3.0/2.0)),
        test("5 mod 2", new Double(1.0)),
        test("5.9 mod 2.1", new Double(1.0)),     // Error in Xalan?
        test("5 mod -2", new Double(1.0)),
        test("-5 mod 2", new Double(-1.0)),
        test("-5 mod -2", new Double(-1.0)),
        test("1 < 2", Boolean.TRUE),
        test("1 > 2", Boolean.FALSE),
        test("1 <= 1", Boolean.TRUE),
        test("1 >= 2", Boolean.FALSE),
        test("3 > 2 > 1", Boolean.FALSE),
        test("3 > 2 and 2 > 1", Boolean.TRUE),
        test("3 > 2 and 2 < 1", Boolean.FALSE),
        test("3 < 2 or 2 > 1", Boolean.TRUE),
        test("3 < 2 or 2 < 1", Boolean.FALSE),
        test("1 = 1", Boolean.TRUE),
        test("1 = '1'", Boolean.TRUE),
        test("1 > 2 = 2 > 3", Boolean.TRUE),
        test("1 > 2 = 0", Boolean.TRUE),
        test("1 = 2", Boolean.FALSE),

        // Variables
        test("$a", new Double(1)),
        test("$a = $b", Boolean.TRUE),
        test("$a = $test", Boolean.FALSE),

        // Traversal
        // ancestor::
        test("int/ancestor::root = /", Boolean.TRUE),
//        testEval("beans/name/ancestor-or-self::node()", new Double(5)),
        test("count(beans/name/ancestor-or-self::node())", new Double(5)),
        test("beans/name/ancestor-or-self::node()[3] = /", Boolean.TRUE),

        // child::
        test("count(set)", new Double(3)),
        test("boolean", Boolean.FALSE),
        testPath("boolean", "/boolean"),
        testEvalPath("boolean", list("/boolean")),
        test("nestedBean/name", "Name 0"),
        testPath("nestedBean/name", "/nestedBean/name"),
        testEvalPath("nestedBean/name", list("/nestedBean/name")),
        testEval("integers", list(new Integer(1), new Integer(2), new Integer(3), new Integer(4))),
        testPath("integers", "/integers"),
        testEvalPath("integers", list("/integers[1]", "/integers[2]", "/integers[3]", "/integers[4]")),
        test("integers[2]", new Integer(2)),
        testPath("integers[2]", "/integers[2]"),
        testEvalPath("integers[2]", list("/integers[2]")),
        test("beans[1]/name", "Name 1"),
        testPath("beans[1]/name", "/beans[1]/name"),
        testEval("beans[1]/strings", list("String 1", "String 2", "String 3")),
        testEval("beans/strings[2]", list("String 2", "String 2")),
        test("beans/strings[2]", "String 2"),
        test("(beans/strings[2])[1]", "String 2"),
        test("count(*)", new Double(21)),
        test("count(child::node())", new Double(21)),

        // descendant::
        test("count(descendant::node())", new Double(65)),
        test("count(descendant::root)", new Double(0)),
        test("count(descendant::name)", new Double(7)),

        // descendant-or-self::
        testEval("//name", list("Name 1", "Name 2", "Name 3", "Name 6", "Name 0", "Name 5", "Name 4")),
        test("//Key1", "Value 1"),
        testEval("//self::node()[name = 'Name 0']/name", list("Name 0")),
        testEval("//self::node()[name(.) = concat('n', 'a', 'm', 'e')]",
                list("Name 1", "Name 2", "Name 3", "Name 6", "Name 0", "Name 5", "Name 4")),
        test("count(//self::beans)", new Double(2)),
        test("count(nestedBean//.)", new Double(7)),
        testEval("descendant-or-self::name", list("Name 1", "Name 2", "Name 3", "Name 6", "Name 0", "Name 5", "Name 4")),
        test("count(descendant-or-self::root)", new Double(1)),
        test("count(descendant-or-self::node())", new Double(66)),

        // following::
        test("count(nestedBean/strings[2]/following::node())", new Double(21)),
        test("count(nestedBean/strings[2]/following::strings)", new Double(7)),

        // following-sibling::
        test("count(/nestedBean/following-sibling::node())", new Double(8)),
        test("count(/nestedBean/following-sibling::object)", new Double(1)),
        test("count(/nestedBean/boolean/../following-sibling::node())", new Double(8)),
        test("count(/nestedBean/boolean/../following-sibling::object)", new Double(1)),
        test("count(/descendant::boolean/following-sibling::node())", new Double(53)),
        test("count(/descendant::boolean/following-sibling::name)", new Double(7)),


        // parent::
        test("count(/beans/..)", new Double(1)),
        test("count(//..)", new Double(9)),
        test("count(//../..)", new Double(2)),
        testEval("//parent::beans/name", list("Name 1", "Name 2")),

        // preceding::
        test("count(beans[2]/int/preceding::node())", new Double(8)),
        test("count(beans[2]/int/preceding::boolean)", new Double(2)),

        // preceding-sibling::
        test("count(/boolean/preceding-sibling::node())", new Double(2)),
        test("count(/nestedBean/int/../preceding-sibling::node())", new Double(12)),
        test("count(/descendant::int/preceding-sibling::node())", new Double(10)),

        // self::
        test("self::node() = /", Boolean.TRUE),
        test("self::root = /", Boolean.TRUE),

        // Union
        testEval("integers | beans[1]/strings",
            list(new Integer(1), new Integer(2), new Integer(3), new Integer(4), "String 1", "String 2", "String 3")),

        test("count((integers | beans[1]/strings)[contains(., '1')])", new Double(2)),
        test("count((integers | beans[1]/strings)[name(.) = 'strings'])", new Double(3)),

        // Note that the following is different from "integer[2]" - it is a filter expression
        test("(integers)[2]", new Integer(2)),

        // Core functions
        test("integers[last()]", new Integer(4)),
        test("integers[position() = last() - 1]", new Integer(3)),
        testEval("integers[position() < 3]", list(new Integer(1), new Integer(2))),
        test("count(beans/strings)", new Double(6)),
//        test("integers[string() = '2.0']", new Integer(2)),  // Incorrect -- TBD

        test("name(integers)", "integers"),
        testEval("*[name(.) = 'integers']", list(new Integer(1), new Integer(2), new Integer(3), new Integer(4))),

        // Dynamic properties
        test("nestedBean[@name = 'int']", new Integer(1)),    // Not implemented in Xalan
        testPath("nestedBean[@name = 'int']", "/nestedBean/int"),
        test("map[@name = 'Key1']", "Value 1"),               // Not implemented in Xalan
        testPath("map[@name = 'Key1']", "/map[@name='Key1']"),
        test("map/Key1", "Value 1"),
        testPath("map/Key1", "/map[@name='Key1']"),
        testPath("map[@name = 'Key&quot;&apos;&quot;&apos;1']", "/map[@name='Key&quot;&apos;&quot;&apos;1']"),

        // Standard functions
        test("string(2)", "2.0"),
        test("string($nan)", "NaN"),
        test("string(-$nan)", "NaN"),
        test("string(-2 div 0)", "-Infinity"),
        test("string(2 div 0)", "Infinity"),
        test("concat('a', 'b', 'c')", "abc"),
        test("starts-with('abc', 'ab')", Boolean.TRUE),
        test("starts-with('xabc', 'ab')", Boolean.FALSE),
        test("contains('xabc', 'ab')", Boolean.TRUE),
        test("contains('xabc', 'ba')", Boolean.FALSE),
        test("substring-before('1999/04/01', '/')", "1999"),
        test("substring-after('1999/04/01', '/')", "04/01"),
        test("substring('12345', 2, 3)", "234"),
        test("substring('12345', 2)", "2345"),
        test("substring('12345', 1.5, 2.6)", "234"),
        test("substring('12345', 0, 3)", "12"),
        test("substring('12345', 0 div 0, 3)", ""),
        test("substring('12345', 1, 0 div 0)", ""),
        test("substring('12345', -42, 1 div 0)", "12345"),
        test("substring('12345', -1 div 0, 1 div 0)", ""),
        test("string-length('12345')", new Double(5)),
        testEval("beans[1]/strings[string-length() = 8]", list("String 1", "String 2", "String 3")),
        test("normalize-space(' abc  def  ')", "abc def"),
        test("normalize-space('abc def')", "abc def"),
        test("normalize-space('   ')", ""),
        test("translate('--aaa--', 'abc-', 'ABC')", "AAA"),
        test("boolean(1)", Boolean.TRUE),
        test("boolean(0)", Boolean.FALSE),
        test("boolean('x')", Boolean.TRUE),
        test("boolean('')", Boolean.FALSE),
        test("boolean(boolean)", Boolean.FALSE),
        test("boolean(integers[position() < 3])", Boolean.TRUE),
        test("boolean(integers[position() > 4])", Boolean.FALSE),
        test("true()", Boolean.TRUE),
        test("false()", Boolean.FALSE),
        test("not(false())", Boolean.TRUE),
        test("not(true())", Boolean.FALSE),
        test("number('1')", new Double(1)),
        test("sum(integers)", new Double(10)),
        test("floor(1.5)", new Double(1)),
        test("floor(-1.5)", new Double(-2)),
        test("ceiling(1.5)", new Double(2)),
        test("ceiling(-1.5)", new Double(-1)),
        test("round(1.5)", new Double(2)),
        test("round(-1.5)", new Double(-1)),

        // Extension functions
        test("string(test:new())", "foo=0; bar=null"),
        test("string(jxpathtest:TestFunctions.new())", "foo=0; bar=null"),
        test("string(" + TestFunctions.class.getName() + ".new())", "foo=0; bar=null"),
        test("string(test:new(3, 'baz'))", "foo=3; bar=baz"),
        test("string(test:new('3', 4))", "foo=3; bar=4.0"),
        test("string(test:getFoo($test))", "4.0"),
        test("string(call:getFoo($test))", "4.0"),
        test("string(getFoo($test))", "4.0"),
        test("string(test:setFooAndBar($test, 7, 'biz'))", "foo=7; bar=biz"),
        test("string(test:build(8, 'goober'))", "foo=8; bar=goober"),
        test("string(jxpathtest:TestFunctions.build(8, 'goober'))", "foo=8; bar=goober"),
        test("string(" + TestFunctions.class.getName() + ".build(8, 'goober'))", "foo=8; bar=goober"),
        test("string(test:increment(8))", "9.0"),
        test("length('foo')", new Integer(3)),
        test("call:substring('foo', 1, 2)", "o"),


        // null
        testPath("$testnull/nothing", "$testnull/nothing"),
        testEval("$testnull/nothing[1]", Collections.EMPTY_LIST),
    };

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

    private static List list(Object o1, Object o2, Object o3){
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        return list;
    }

    private static List list(Object o1, Object o2, Object o3, Object o4){
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        return list;
    }

    private static List list(Object o1, Object o2, Object o3, Object o4, Object o5){
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        return list;
    }

    private static List list(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6){
        List list = new ArrayList();
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        list.add(o6);
        return list;
    }

    private static List list(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7){
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

    public void testDOM(){
        System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,
                "org.apache.commons.jxpath.ri.JXPathContextFactoryReferenceImpl");
        try {
            XMLDocumentContainer docCtr = new XMLDocumentContainer(getClass().getResource("Test.properties"));
            Document doc = (Document)docCtr.getValue();
            JXPathContext ctx = JXPathContextFactory.newInstance().newContext(null, doc);
            ctx.getVariables().declareVariable("dom", doc);
            ctx.getVariables().declareVariable("object", docCtr);
            TestBeanWithDOM tbwdom = new TestBeanWithDOM();
            tbwdom.setVendor(doc.getDocumentElement());
            tbwdom.setObject(docCtr);
            ctx.getVariables().declareVariable("test", tbwdom);
            testXPaths(ctx, dom_tests, false);
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException("Test failed");
        }
    }

    static final XP[] dom_tests = new XP[]{
        // Numbers
        test("vendor/location/address/street", "Some street"),
        test("vendor/location[2]/address/street", "Other street"),
        test("//street", "Some street"),
        test("name(//street/..)", "address"),
        test("number(vendor/location/employeeCount)", new Double(10)),
        test("vendor/location/employeeCount + 1", new Double(11)),
        test("vendor/location/employeeCount and true()", Boolean.TRUE),
        test("vendor/location[.//employeeCount = 10]/following-sibling::location//street", "Other street"),
        testPath("vendor/location[.//employeeCount = 10]/following-sibling::location//street",
                "/vendor[1]/location[2]/address[1]/street[1]"),
        testPath("//location[2]/preceding-sibling::location//street",
                "/vendor[1]/location[1]/address[1]/street[1]"),
        test("vendor/location/@id", "100"),
        testPath("vendor/location/@id", "/vendor[1]/location[1]/@id"),
        testEval("vendor/location/@id", list("100", "101")),
        test("vendor/location/@blank", ""),
        test("vendor/location/@missing", null),
        test("count(vendor/location[1]/@*)", new Double(3)),
        test("vendor/location[@id='101']//street", "Other street"),
        test("$test/int", new Integer(1)),
        test("$test/vendor/location[1]//street", "Some street"),
        test("$dom/vendor//street", "Some street"),
        test("$test/object/vendor/location[1]//street", "Some street"),
        testPath("$test/object/vendor/location[1]//street", "$test/object/vendor[1]/location[1]/address[1]/street[1]"),
        test("$object//street", "Some street"),
        testPath("$object//street", "$object/vendor[1]/location[1]/address[1]/street[1]"),
    };
}

