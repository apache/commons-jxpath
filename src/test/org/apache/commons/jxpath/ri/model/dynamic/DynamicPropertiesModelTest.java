/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/ri/model/dynamic/DynamicPropertiesModelTest.java,v 1.1 2002/11/28 01:01:30 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2002/11/28 01:01:30 $
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

package org.apache.commons.jxpath.ri.model.dynamic;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.TestBean;

/**
 * @todo more iterator testing with maps
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2002/11/28 01:01:30 $
 */

public class DynamicPropertiesModelTest extends JXPathTestCase
{
    private static boolean enabled = true;
    private JXPathContext context;

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public DynamicPropertiesModelTest(String name){
        super(name);
    }

    public void setUp(){
        if (context == null){
            context = JXPathContext.newContext(new TestBean());
            context.setFactory(new TestDynamicPropertyFactory());
        }
    }

    public void testAxisChild(){
        assertXPathValue(context,
                "map/Key1",
                "Value 1");

        assertXPathPointer(context,
                "map/Key1",
                "/map[@name='Key1']");

        assertXPathValue(context,
                "map/Key2/name",
                "Name 6");

        assertXPathPointer(context,
                "map/Key2/name",
                "/map[@name='Key2']/name");
    }

    public void testAxisDescendant(){
        assertXPathValue(context,
                "//Key1",
                "Value 1");
    }

    /**
     * Testing the pseudo-attribute "name" that dynamic property
     * objects appear to have.
     */
    public void testAttributeName(){
        assertXPathValue(context,
                "map[@name = 'Key1']",
                "Value 1");

        assertXPathPointer(context,
                "map[@name = 'Key1']",
                "/map[@name='Key1']");

        assertXPathPointerLenient(context,
                "map[@name = 'Key&quot;&apos;&quot;&apos;1']",
                "/map[@name='Key&quot;&apos;&quot;&apos;1']");

        assertXPathValue(context,
                "/.[@name='map']/Key2/name",
                "Name 6");

        assertXPathPointer(context,
                "/.[@name='map']/Key2/name",
                "/map[@name='Key2']/name");

        // Bean in a map
        assertXPathValue(context,
                "/map[@name='Key2'][@name='name']",
                "Name 6");

        assertXPathPointer(context,
                "/map[@name='Key2'][@name='name']",
                "/map[@name='Key2']/name");

        // Map in a bean in a map
        assertXPathValue(context,
                "/.[@name='map'][@name='Key2'][@name='name']",
                "Name 6");

        assertXPathPointer(context,
                "/.[@name='map'][@name='Key2'][@name='name']",
                "/map[@name='Key2']/name");
    }
    
    public void testSetPrimitiveValue(){
        assertXPathSetValue(context, 
                "map/Key1",
                new Integer(6));
    }
    
    public void testSetCollection(){
        // See if we can assign a whole collection        
        context.setValue(
                "map/Key1", 
                new Integer[]{new Integer(7), new Integer(8)});
        
        // And then an element in that collection
        assertXPathSetValue(context,
                "map/Key1[1]", 
                new Integer(9));
    }
    
    public void testSetNewKey(){
        assertXPathSetValue(context,
                "map/Key4", 
                new Integer(7));
    }
    
    public void testCreatePath(){
        TestBean bean = (TestBean)context.getContextBean();
        bean.setMap(null);
        
        // Calls factory.createObject(..., testBean, "map"), then
        // sets the value
        assertXPathCreatePath(context, 
                "/map[@name='TestKey1']", 
                "", 
                "/map[@name='TestKey1']");
    }
    
    public void testCreatePathAndSetValue(){
        TestBean bean = (TestBean)context.getContextBean();
        bean.setMap(null);
        
        // Calls factory.createObject(..., testBean, "map"), then
        // sets the value
        assertXPathCreatePathAndSetValue(context, 
                "/map[@name='TestKey1']", 
                "Test", 
                "/map[@name='TestKey1']");
    }
    
    public void testCreatePathCreateBean(){
        TestBean bean = (TestBean)context.getContextBean();
        bean.setMap(null);
        
        // Calls factory.createObject(..., testBean, "map"), then
        // then factory.createObject(..., map, "TestKey2"), then
        // sets the value
        assertXPathCreatePath(context, 
                "/map[@name='TestKey2']/int", 
                new Integer(1),
                "/map[@name='TestKey2']/int");
    }
    
    public void testCreatePathAndSetValueCreateBean(){
        TestBean bean = (TestBean)context.getContextBean();
        bean.setMap(null);
        
        // Calls factory.createObject(..., testBean, "map"), then
        // then factory.createObject(..., map, "TestKey2"), then
        // sets the value
        assertXPathCreatePathAndSetValue(context, 
                "/map[@name='TestKey2']/int", 
                new Integer(4),
                "/map[@name='TestKey2']/int");
    }
    
    public void testCreatePathCollectionElement(){
        TestBean bean = (TestBean)context.getContextBean();
        bean.setMap(null);
        

        assertXPathCreatePath(context, 
                "/map/TestKey3[2]", 
                null,
                "/map[@name='TestKey3'][2]");

        // Should be the same as the one before
        assertXPathCreatePath(context, 
                "/map[@name='TestKey3'][3]", 
                null,
                "/map[@name='TestKey3'][3]");
    }
    
    public void testCreatePathAndSetValueCollectionElement(){
        TestBean bean = (TestBean)context.getContextBean();
        bean.setMap(null);
        

        assertXPathCreatePathAndSetValue(context, 
                "/map/TestKey3[2]", 
                "Test1",
                "/map[@name='TestKey3'][2]");

        // Should be the same as the one before
        assertXPathCreatePathAndSetValue(context, 
                "/map[@name='TestKey3'][3]", 
                "Test2",
                "/map[@name='TestKey3'][3]");
    }
    
    public void testCreatePathNewCollectionElement(){
        TestBean bean = (TestBean)context.getContextBean();
        bean.setMap(null);
        
        // Create an element of a dynamic map element, which is a collection
        assertXPathCreatePath(context, 
                "/map/TestKey4[1]/int", 
                new Integer(1),
                "/map[@name='TestKey4'][1]/int");

        bean.getMap().remove("TestKey4");

        // Should be the same as the one before
        assertXPathCreatePath(context, 
                "/map/TestKey4[1]/int", 
                new Integer(1),
                "/map[@name='TestKey4'][1]/int");
    }
    
    public void testCreatePathAndSetValueNewCollectionElement(){
        TestBean bean = (TestBean)context.getContextBean();
        bean.setMap(null);
        
        // Create an element of a dynamic map element, which is a collection
        assertXPathCreatePathAndSetValue(context, 
                "/map/TestKey4[1]/int", 
                new Integer(2),
                "/map[@name='TestKey4'][1]/int");

        bean.getMap().remove("TestKey4");

        // Should be the same as the one before
        assertXPathCreatePathAndSetValue(context, 
                "/map/TestKey4[1]/int", 
                new Integer(3),
                "/map[@name='TestKey4'][1]/int");
    }
    
    public void testRemovePath(){
        TestBean bean = (TestBean)context.getContextBean();
        bean.getMap().put("TestKey1", "test");

        // Remove dynamic property
        context.removePath("map[@name = 'TestKey1']");
        assertEquals("Remove dynamic property value", null,
                    context.getValue("map[@name = 'TestKey1']"));
    }
    
    public void testRemovePathArrayElement(){
        TestBean bean = (TestBean)context.getContextBean();

        bean.getMap().put("TestKey2", new String[]{"temp1", "temp2"});
        context.removePath("map[@name = 'TestKey2'][1]");
        assertEquals("Remove dynamic property collection element", "temp2",
                    context.getValue("map[@name = 'TestKey2'][1]"));
    }
}