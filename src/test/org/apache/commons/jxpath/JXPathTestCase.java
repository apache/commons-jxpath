/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/JXPathTestCase.java,v 1.20 2002/05/29 00:42:06 dmitri Exp $
 * $Revision: 1.20 $
 * $Date: 2002/05/29 00:42:06 $
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
import java.lang.reflect.*;
import org.apache.commons.jxpath.util.*;
import org.apache.commons.jxpath.ri.*;
import org.apache.commons.jxpath.ri.parser.*;
import org.apache.commons.jxpath.ri.model.*;
import org.apache.commons.jxpath.ri.model.beans.*;
import org.apache.commons.jxpath.ri.axes.*;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.ri.compiler.Expression;
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
 *   that all changes to TestBean are reflected here.
 * </p>
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.20 $ $Date: 2002/05/29 00:42:06 $
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
        if (true){
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
        PropertyOwnerPointer root = (PropertyOwnerPointer)NodePointer.newNodePointer(new QName(null, "root"), bean, Locale.getDefault());
        NodeIterator it;

        if (useStartLocation){
            PropertyPointer holder = root.getPropertyPointer();
            holder.setPropertyIndex(relativeProperty(holder, relativePropertyIndex));
            holder.setIndex(offset);
            it = root.childIterator(new NodeNameTest(new QName(null, "integers")), reverse, holder);
        }
        else {
            it = root.childIterator(new NodeNameTest(new QName(null, "integers")), reverse, null);
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
        if (true){
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
        PropertyOwnerPointer root = (PropertyOwnerPointer)NodePointer.newNodePointer(new QName(null, "root"), bean, Locale.getDefault());
        NodeIterator it;

        if (useStartLocation){
            PropertyPointer holder = root.getPropertyPointer();
            holder.setPropertyIndex(propertyIndex);
            holder.setIndex(offset);
            it = root.childIterator(null, reverse, holder);
        }
        else {
            it = root.childIterator(null, reverse, null);
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
        if (!enabled){
            return;
        }
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
        testGetValue(context, "'true'",                 Boolean.TRUE, Boolean.class);

        Map tm = new HashMap();
        tm.put("bar", "zz");
        bean.getMap().put("foo", new Map[]{tm, tm});
        testGetValue(context, "map/foo[2]/bar/../bar", "zz");

        boolean exception = false;
        try {
            testGetValue(context, "'foo'",              null, Date.class);
        }
        catch(Exception ex){
            exception = true;
        }
        assertTrue("Type conversion exception", exception);
    }

    /**
     * Test JXPath.iterate() with various arguments
     */
    public void testIterate(){
        if (!enabled){
            return;
        }
        Map map = new HashMap();
        map.put("foo", new String[]{"a", "b", "c"});
        JXPathContext context = JXPathContext.newContext(map);
        testIterate(context, "foo", list("a", "b", "c"));

//        context = JXPathContext.newContext(bean);
//        testIterate(context, "nestedBean/strings[2]/following::node()", null);
    }

    private void testIterate(JXPathContext context, String xpath, List expected) {
        Iterator it = context.iterate(xpath);
        List actual = new ArrayList();
        while (it.hasNext()){
            actual.add(it.next());
        }
        assertEquals("Iterating <" + xpath + ">", expected, actual);
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
            testGetValue(context, ".",  bean.getBeans());
//            testGetValue(context, ".[2]/name",  "Name 2");        // TBD: is this even legal?
            testGetValue(context, "$t[2]",  "b");
            testGetValue(context, "$m/Key1",  "Value 1");
        }
    }

    private void testGetValue(JXPathContext context, String xpath, Object expected) {
        Object actual = context.getValue(xpath);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);

        CompiledExpression expr = context.compile(xpath);
        actual = expr.getValue(context);
        assertEquals("Evaluating CE <" + xpath + ">", expected, actual);
    }

    private void testGetValue(JXPathContext context, String xpath, Object expected, Class requiredType) {
        Object actual = context.getValue(xpath, requiredType);
        assertEquals("Evaluating <" + xpath + ">", expected, actual);

        CompiledExpression expr = context.compile(xpath);
        actual = expr.getValue(context, requiredType);
        assertEquals("Evaluating CE <" + xpath + ">", expected, actual);
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
        Iterator it = context.iterate(xpath);
        ArrayList actual = new ArrayList();
        while (it.hasNext()){
            actual.add(it.next());
        }
        assertEquals("Evaluating <" + xpath + ">", expected, actual);

        CompiledExpression expr = context.compile(xpath);
        it = expr.iterate(context);
        actual = new ArrayList();
        while (it.hasNext()){
            actual.add(it.next());
        }
        assertEquals("Evaluating CE <" + xpath + ">", expected, actual);
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
            testContextDependency("test:func(3, 5)", true);
            testContextDependency("test:func(3, foo)", true);
        }
    }

    public void testContextDependency(String xpath, boolean expected){
        Expression expr = (Expression)Parser.parseExpression(xpath, new TreeCompiler());
        assertEquals("Evaluating <" + xpath + ">", expected, expr.isContextDependent());
    }

    public void testDocumentOrder(){
//        if (!enabled){
//            return;
//        }

        JXPathContext context = JXPathContext.newContext(createTestBeanWithDOM());

        testDocumentOrder(context, "boolean", "int", -1);
        testDocumentOrder(context, "integers[1]", "integers[2]", -1);
        testDocumentOrder(context, "integers[1]", "integers[1]", 0);
        testDocumentOrder(context, "nestedBean/int", "nestedBean", 1);
        testDocumentOrder(context, "nestedBean/int", "nestedBean/strings", -1);
        testDocumentOrder(context, "nestedBean/int", "object/int", -1);
        testDocumentOrder(context, "vendor/location", "vendor/location/address/street", -1);
        testDocumentOrder(context, "vendor/location[@id = '100']", "vendor/location[@id = '101']", -1);
        testDocumentOrder(context, "vendor//price:amount", "vendor/location", 1);
//        testDocumentOrder(context, "nonexistent//foo", "vendor/location", 1);     // Will throw an exception
    }

    private void testDocumentOrder(JXPathContext context, String path1, String path2, int expected){
        NodePointer np1 = (NodePointer)context.getPointer(path1);
        NodePointer np2 = (NodePointer)context.getPointer(path2);
        int res = np1.compareTo(np2);
        if (res < 0){
            res = -1;
        }
        else if (res > 0){
            res = 1;
        }
        assertEquals("Comparing paths '" + path1 + "' and '" + path2 + "'", expected, res);
    }

    /**
     * Test JXPath.setValue() with various arguments
     */
    public void testSetValue(){
        if (!enabled){
            return;
        }
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

        context.setValue("int", new int[]{4});
        assertEquals("Modified <" + "int" + ">", new Integer(4), context.getValue("int"));

        context.setValue("integers[2]", new Integer(5));
        assertEquals("Modified <" + "integers[2]" + ">", new Integer(5), context.getValue("integers[2]"));

        context.setValue("integers[2]", new int[]{6});
        assertEquals("Modified <" + "integers[2]" + ">", new Integer(6), context.getValue("integers[2]"));

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

        context.setValue("integers[. = 6]", new Integer(8));
        assertEquals("Modified <" + "integers[. = 6]" + ">", new Integer(8), context.getValue("integers[2]"));

        context.setValue("beans[name = 'Name 9']/int", new Integer(9));
        assertEquals("Modified <" + "beans[name = 'Name 9']/int" + ">", new Integer(9), context.getValue("beans[name = 'Name 9']/int"));
    }

    /**
     * Test JXPathContext.createPath() with various arguments
     */
    public void testCreatePath(){
        if (!enabled){
            return;
        }
        TestBeanWithDOM tBean = createTestBeanWithDOM();
        tBean.setNestedBean(null);
        tBean.setBeans(null);
        tBean.setMap(null);
        JXPathContext context = JXPathContext.newContext(tBean);
        context.setFactory(new TestFactory());

        // Calls factory.declareVariable("string")
        testCreatePath(context, "$string", null);    // Declare and set to null

        assertTrue("Variable created",
                context.getVariables().isDeclaredVariable("string"));

        // Calls factory.declareVariable("stringArray"). The factory needs to create a collection
        testCreatePath(context, "$stringArray[2]", "");
        assertEquals("Created <" + "$stringArray[1]" + ">", "Value1", context.getValue("$stringArray[1]"));

        context.getVariables().declareVariable("array", new String[]{"Value1"});

        // Does not involve factory at all - just expands the collection
        testCreatePath(context, "$array[2]", "");

        // Make sure it is still the same array
        assertEquals("Created <" + "$array[1]" + ">", "Value1", context.getValue("$array[1]"));

        // Calls factory.declareVariable("test"). The factory should create a TestBean
        testCreatePath(context, "$test/boolean", Boolean.FALSE);

        // Calls factory.declareVariable("testArray").
        // The factory should create a collection of TestBeans.
        // Then calls factory.createObject(..., collection, "testArray", 1).
        // That one should produce an instance of TestBean and put it in the collection
        // at index 1.
        testCreatePath(context, "$testArray[2]/boolean", Boolean.FALSE);

        // Calls factory.createObject(..., TestBean, "nestedBean")
        testCreatePath(context, "/nestedBean/int", new Integer(1));

        // Calls factory.expandCollection(..., testBean, "beans", 2), then
        // factory.createObject(..., testBean, "beans", 2)
        testCreatePath(context, "/beans[2]/int", new Integer(1));

        // Another, but the collection already exists
        testCreatePath(context, "/beans[3]/int", new Integer(1));

        // Calls factory.expandCollection(..., testBean, "beans", 2), then
        // sets the value
        testCreatePath(context, "/nestedBean/strings[2]", "String 2");

        // Calls factory.createObject(..., testBean, "map"), then
        // sets the value
        testCreatePath(context, "/map[@name='TestKey1']", "");

        // Calls factory.createObject(..., testBean, "map"), then
        // then factory.createObject(..., map, "TestKey2"), then
        // sets the value
        testCreatePath(context, "/map[@name='TestKey2']/int", new Integer(1));

        testCreatePath(context, "/map/TestKey3[2]", null,
                "/map[@name='TestKey3'][2]");

        // Should be the same as the one before
        testCreatePath(context, "/map[@name='TestKey3'][3]", null);

        // Create an element of a dynamic map element, which is a collection
        testCreatePath(context, "/map/TestKey4[1]/int", new Integer(1),
                "/map[@name='TestKey4'][1]/int");

        tBean.getMap().remove("TestKey4");

        // Should be the same as the one before
        testCreatePath(context, "/map[@name='TestKey4'][1]/int", new Integer(1));

        // Create a DOM element
        testCreatePath(context, "/vendor/location[3]", "");

        // Create a DOM element with contents
        testCreatePath(context, "/vendor/location[3]/address/street", "",
                "/vendor/location[3]/address[1]/street[1]");

        // Comprehensive tests: map & bean
        tBean.setMap(null);
        testCreatePath(context, "/map[@name='TestKey5']/nestedBean/int", new Integer(1));
        tBean.setMap(null);
        testCreatePath(context, "/map[@name='TestKey5']/beans[2]/int", new Integer(1));
    }

    private void testCreatePath(JXPathContext context, String path, Object value){
        testCreatePath(context, path, value, path);
    }

    private void testCreatePath(JXPathContext context, String path,
                Object value, String expectedPath){
        Pointer ptr = context.createPath(path);
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
        TestBean tBean = createTestBeanWithDOM();
        tBean.setNestedBean(null);
        tBean.setBeans(null);
        tBean.setMap(null);
        JXPathContext context = JXPathContext.newContext(tBean);
        context.setFactory(new TestFactory());

        // Calls factory.declareVariable("string")
        testCreatePathAndSetValue(context, "$string", "Value");

        // Calls factory.declareVariable("stringArray"). The factory needs to create a collection
        testCreatePathAndSetValue(context, "$stringArray[2]", "Value2");
        assertEquals("Created <" + "$stringArray[1]" + ">", "Value1", context.getValue("$stringArray[1]"));

        context.getVariables().declareVariable("array", new String[]{"Value1"});

        // Does not involve factory at all - just expands the collection
        testCreatePathAndSetValue(context, "$array[2]", "Value2");

        // Make sure it is still the same array
        assertEquals("Created <" + "$array[1]" + ">", "Value1", context.getValue("$array[1]"));

        // Calls factory.declareVariable("test"). The factory should create a TestBean
        testCreatePathAndSetValue(context, "$test/boolean", Boolean.TRUE);

        // Calls factory.declareVariable("testArray").
        // The factory should create a collection of TestBeans.
        // Then calls factory.createObject(..., collection, "testArray", 1).
        // That one should produce an instance of TestBean and put it in the collection
        // at index 1.
        testCreatePathAndSetValue(context, "$testArray[2]/boolean", Boolean.TRUE);

        // Calls factory.createObject(..., TestBean, "nestedBean")
        testCreatePathAndSetValue(context, "nestedBean/int", new Integer(1));

        // Calls factory.expandCollection(..., testBean, "beans", 2), then
        // factory.createObject(..., testBean, "beans", 2)
        testCreatePathAndSetValue(context, "beans[2]/int", new Integer(2));

        // Another, but the collection already exists
        testCreatePathAndSetValue(context, "beans[3]/int", new Integer(3));

        // Calls factory.expandCollection(..., testBean, "beans", 2), then
        // sets the value
        testCreatePathAndSetValue(context, "nestedBean/strings[2]", "Test");

        // Calls factory.createObject(..., testBean, "map"), then
        // sets the value
        testCreatePathAndSetValue(context, "map[@name = 'TestKey1']", "Test");

        // Calls factory.createObject(..., testBean, "map"), then
        // then factory.createObject(..., map, "TestKey2"), then
        // sets the value
        testCreatePathAndSetValue(context, "map[@name = 'TestKey2']/int", new Integer(4));

        // Calls factory.expandCollection(..., map, "TestKey3", 2)
        testCreatePathAndSetValue(context, "map/TestKey3[2]", "Test");

        // Should be the same as the one before
        testCreatePathAndSetValue(context, "map[@name='TestKey3'][3]", "Test");

        // Create an element of a dynamic map element, which is a collection
        testCreatePathAndSetValue(context, "map/TestKey4[1]/int", new Integer(5));

        tBean.getMap().remove("TestKey4");

        // Should be the same as the one before
        testCreatePathAndSetValue(context, "map[@name = 'TestKey4'][1]/int", new Integer(5));

        // Create a DOM element
        testCreatePathAndSetValue(context, "vendor/location[3]", "");

        // Create a DOM element with contents
        testCreatePathAndSetValue(context, "vendor/location[3]/address/street", "Lemon Circle");

        // Comprehensive tests: map & bean
        tBean.setMap(null);
        testCreatePathAndSetValue(context, "map[@name = 'TestKey5']/nestedBean/int", new Integer(6));
        tBean.setMap(null);
        testCreatePathAndSetValue(context, "map[@name = 'TestKey5']/beans[2]/int", new Integer(7));
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
        TestBeanWithDOM tBean = createTestBeanWithDOM();
        JXPathContext context = JXPathContext.newContext(tBean);

        // Undeclare variable
        context.getVariables().declareVariable("temp", "temp");
        context.removePath("$temp");
        assertTrue("Undeclare variable",
                !context.getVariables().isDeclaredVariable("temp"));

        // Remove array element
        context.getVariables().
                declareVariable("temp", new String[]{"temp1", "temp2"});
        context.removePath("$temp[1]");
        assertEquals("Remove array element", "temp2",
                    context.getValue("$temp[1]"));

        // Remove list element
        context.getVariables().
                declareVariable("temp", list("temp1", "temp2"));
        context.removePath("$temp[1]");
        assertEquals("Remove collection element", "temp2",
                    context.getValue("$temp[1]"));

        // Remove property value
        context.removePath("nestedBean/int");
        assertEquals("Remove property value", new Integer(0),
                    context.getValue("nestedBean/int"));

        // Remove property value
        context.removePath("nestedBean/strings[1]");
        assertEquals("Remove property value", "String 2",
                    context.getValue("nestedBean/strings[1]"));

        context.removePath("nestedBean");
        assertEquals("Remove property value", null,
                    context.getValue("nestedBean"));

        tBean.getMap().put("TestKey1", "test");

        // Remove dynamic property
        context.removePath("map[@name = 'TestKey1']");
        assertEquals("Remove dynamic property value", null,
                    context.getValue("map[@name = 'TestKey1']"));

        tBean.getMap().put("TestKey2", new String[]{"temp1", "temp2"});
        context.removePath("map[@name = 'TestKey2'][1]");
        assertEquals("Remove dynamic property collection element", "temp2",
                    context.getValue("map[@name = 'TestKey2'][1]"));

        // Remove DOM nodes
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

    public void testNull(){
        if (!enabled){
            return;
        }
        JXPathContext context = JXPathContext.newContext(new TestNull());
        testGetValue(context, "nothing", null);
        testGetValue(context, "child/nothing", null);
        testGetValue(context, "array[2]", null);
        context.setLenient(true);
        testGetValue(context, "nothing/something", null);
        testGetValue(context, "array[2]/something", null);
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

    private static class Context implements ExpressionContext {
        private Object object;

        public Context(Object object){
            this.object = object;
        }

        public Pointer getContextNodePointer(){
            return NodePointer.newNodePointer(null, object, Locale.getDefault());
        }

        public List getContextNodeList(){
            return null;
        }

        public JXPathContext getJXPathContext(){
            return null;
        }

        public int getPosition(){
            return 0;
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
            assertEquals("test:new(1, x)", func.invoke(new Context(null), args).toString(), "foo=1; bar=x");

            args = new Object[]{"baz"};
            func = funcs.getFunction("test", "new", args);
            assertEquals("test:new('baz')", func.invoke(new Context(new Integer(1)), args).toString(), "foo=1; bar=baz");

            args = new Object[]{new Integer(1), "x"};
            func = funcs.getFunction("test", "build", args);
            assertEquals("test:build(1, x)", func.invoke(new Context(null), args).toString(), "foo=1; bar=x");

            args = new Object[]{"7", new Integer(1)};
            func = funcs.getFunction("test", "build", args);
            assertEquals("test:build('7', 1)", func.invoke(new Context(null), args).toString(), "foo=7; bar=1");

            args = new Object[]{test};
            func = funcs.getFunction("test", "getFoo", args);
            assertEquals("test:getFoo($test, 1, x)", func.invoke(new Context(null), args).toString(), "0");

            args = new Object[0];
            func = funcs.getFunction("test", "path", args);
            assertEquals("test:path()", func.invoke(new Context(new Integer(1)), args), "1");

            args = new Object[]{test};
            func = funcs.getFunction("test", "instancePath", args);
            assertEquals("test:instancePath()", func.invoke(new Context(new Integer(1)), args), "1");

            args = new Object[]{test, "*"};
            func = funcs.getFunction("test", "pathWithSuffix", args);
            assertEquals("test:pathWithSuffix('*')", func.invoke(new Context(new Integer(1)), args), "1*");
        }
    }

    public void testParserReferenceImpl() throws Exception {
        if (!enabled){
            return;
        }
        System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,
                "org.apache.commons.jxpath.ri.JXPathContextFactoryReferenceImpl");
        testParser(JXPathContextFactory.newInstance().newContext(null, bean), false);
    }

    public void testParser(JXPathContext ctx, boolean ignorePath) throws Exception {
        ctx.setLocale(Locale.US);
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

    private void testXPaths(JXPathContext ctx, XP xpath_tests[], boolean ignorePath) throws Exception{
        Exception exception = null;
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
                            Iterator it = ctx.iteratePointers(xpath_tests[i].xpath);
                            List paths = new ArrayList();
                            while (it.hasNext()){
                                paths.add(((Pointer)it.next()).asPath());
                            }
                            actual = paths;
                        }
                        else {
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
        return new XP(xpath, expected, false, true, false);
    }

    private static XP testEvalPath(String xpath, Object expected){
        return new XP(xpath, expected, true, true, false);
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
        testPath("$a", "$a"),
        test("$a = $b", Boolean.TRUE),
        test("$a = $test", Boolean.FALSE),

        // Traversal
        // ancestor::
        test("int/ancestor::root = /", Boolean.TRUE),
        test("count(beans/name/ancestor-or-self::node())", new Double(5)),
        test("beans/name/ancestor-or-self::node()[3] = /", Boolean.TRUE),

        // child::
        test("count(set)", new Double(3)),
        test("boolean", Boolean.FALSE),
//        test("boolean/class/name", "java.lang.Boolean"),
        testEval("foo:boolean", list()),
        test("count(@*)", new Double(0)),
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

        test("beans/strings[name(.)='strings'][2]", "String 2"),
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

        // Union - note corrected document order
        testEval("integers | beans[1]/strings",
            list("String 1", "String 2", "String 3",
              new Integer(1), new Integer(2), new Integer(3), new Integer(4))),

        test("count((integers | beans[1]/strings)[contains(., '1')])", new Double(2)),
        test("count((integers | beans[1]/strings)[name(.) = 'strings'])", new Double(3)),

        // Note that the following is different from "integer[2]" - it is a filter expression
        test("(integers)[2]", new Integer(2)),        // TBD

        // Core functions
        test("integers[last()]", new Integer(4)),
        test("integers[position() = last() - 1]", new Integer(3)),
        testEval("integers[position() < 3]", list(new Integer(1), new Integer(2))),
        test("count(beans/strings)", new Double(6)),
        test("integers[string() = '2.0']", new Integer(2)),

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
        test("null()", null),
        test("@xml:lang", "en-US"),
        test("count(@xml:*)", new Double(1)),
        testLenient("@foo", null),
        test("lang('en')", Boolean.TRUE),
        test("lang('fr')", Boolean.FALSE),


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
        test("//.[test:isMap()]/Key1", "Value 1"),
        test("count(//.[test:count(strings) = 3])", new Double(7)),

        test("/beans[contains(test:path(), '[2]')]/name", "Name 2"),

        // null
        testPath("$null", "$null"),
        testPath("$null[3]", "$null[3]"),
        testPath("$testnull/nothing", "$testnull/nothing"),
        testPath("$testnull/nothing[2]", "$testnull/nothing[2]"),
        testPath("beans[8]/int", "/beans[8]/int"),
        testEval("$testnull/nothing[1]", Collections.EMPTY_LIST),
    };

    private static List list(){
        return Collections.EMPTY_LIST;
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

    public void testDOM() throws Exception {
        if (!enabled){
            return;
        }
        System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,
                "org.apache.commons.jxpath.ri.JXPathContextFactoryReferenceImpl");
        XMLDocumentContainer docCtr = new XMLDocumentContainer(getClass().getResource("Vendor.xml"));
        Document doc = (Document)docCtr.getValue();
        JXPathContext ctx = JXPathContextFactory.newInstance().newContext(null, doc);
        ctx.setLocale(Locale.US);
        ctx.getVariables().declareVariable("dom", doc);
        ctx.getVariables().declareVariable("object", docCtr);
        ctx.getVariables().declareVariable("null", null);
        TestBeanWithDOM tbwdom = createTestBeanWithDOM();
        ctx.getVariables().declareVariable("test", tbwdom);
        testXPaths(ctx, dom_tests, false);
    }

    private TestBeanWithDOM createTestBeanWithDOM(){
        XMLDocumentContainer docCtr = new XMLDocumentContainer(getClass().getResource("Vendor.xml"));
        Document doc = (Document)docCtr.getValue();
        TestBeanWithDOM tbwdom = new TestBeanWithDOM();
        tbwdom.setVendor(doc.getDocumentElement());
        tbwdom.setObject(docCtr);
        return tbwdom;
    }

    static final XP[] dom_tests = new XP[]{
        test("vendor/location/address/street", "Orchard Road"),
        test("vendor/location[2]/address/street", "Tangerine Drive"),
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
        testPath("//product/processing-instruction('report')", "/vendor[1]/product[1]/processing-instruction('report')[1]"),
        test("name(//product/processing-instruction()[1])", "security"),

        test("//product/prix/@xml:lang", "fr"),
        test("//product/prix[lang('fr')]", "934.99"),
        test("//product/price:sale[lang('en')]/saleEnds", "never"),
        test("vendor/location/@manager", ""),
        testLenient("vendor/location/@missing", null),
        test("count(vendor/location[1]/@*)", new Double(3)),
        test("vendor/location[@id='101']//street", "Tangerine Drive"),
        test("$test/int", new Integer(1)),
        test("$test/vendor/location[1]//street", "Orchard Road"),
        testPath("$test/vendor/location[1]//street", "$test/vendor/location[1]/address[1]/street[1]"),
        test("$dom/vendor//street", "Orchard Road"),
        test("$test/object/vendor/location[1]//street", "Orchard Road"),
        testPath("$test/object/vendor/location[1]//street", "$test/object/vendor[1]/location[1]/address[1]/street[1]"),
        test("$object//street", "Orchard Road"),
        testPath("$object//street", "$object/vendor[1]/location[1]/address[1]/street[1]"),

        testEval("vendor/contact/following::location//street",
            list("Orchard Road", "Tangerine Drive")),
    };

    public void testTypeConversions(){
        for (int i=0; i < typeConversionTests.length; i++) {
            TypeConversionTest test = typeConversionTests[i];
            try {
                boolean can = TypeUtils.canConvert(test.from, test.toType);
                assertTrue("Can convert: " + test, can);
                Object result = TypeUtils.convert(test.from, test.toType);
                if (result.getClass().isArray()){
                    ArrayList list = new ArrayList();
                    for (int j = 0; j < Array.getLength(result); j++){
                        list.add(Array.get(result, j));
                    }
                    result = list;
                }
                assertEquals("Convert: " + test, test.expected, result);
            }
            catch (Exception ex){
                System.err.println("Exception during conversion test <" + test + ">");
                ex.printStackTrace();
            }
        }

    }

    private static class TypeConversionTest {
        public Object from;
        public Class toType;
        public Object expected;

        public TypeConversionTest(Object from, Class toType, Object expected){
            this.from = from;
            this.toType = toType;
            this.expected = expected;
        }
        public String toString(){
            return from.getClass() + " to " + toType;
        }
    }

    private TypeConversionTest[] typeConversionTests = new TypeConversionTest[]{
        new TypeConversionTest(new Integer(1), String.class, "1"),

        new TypeConversionTest(new int[]{1, 2}, List.class,
                Arrays.asList(new Object[]{new Integer(1), new Integer(2)})),

        new TypeConversionTest(new int[]{1, 2}, String[].class,
                list("1", "2")),

        new TypeConversionTest(list(new Integer(1), new Integer(2)), String[].class,
                list("1", "2")),
    };
}