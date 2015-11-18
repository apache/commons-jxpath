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
package org.apache.commons.jxpath.ri.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.NestedTestBean;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.TestFunctions;
import org.apache.commons.jxpath.ri.model.beans.PropertyOwnerPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;
import org.apache.commons.jxpath.ri.model.dynabeans.DynaBeanModelTest;

/**
 * Abstract superclass for Bean access with JXPath.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public abstract class BeanModelTestCase extends JXPathTestCase {
    private JXPathContext context;

    public void setUp() {
//        if (context == null) {
            context = JXPathContext.newContext(createContextBean());
            context.setLocale(Locale.US);
            context.setFactory(getAbstractFactory());
//        }
    }

    protected abstract Object createContextBean();
    protected abstract AbstractFactory getAbstractFactory();

    /**
     * Test property iterators, the core of the graph traversal engine
     */
    public void testIndividualIterators() {
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

    private void testIndividual(
        int relativePropertyIndex,
        int offset,
        boolean useStartLocation,
        boolean reverse,
        int expected) 
    {
        PropertyOwnerPointer root =
            (PropertyOwnerPointer) NodePointer.newNodePointer(
                new QName(null, "root"),
                createContextBean(),
                Locale.getDefault());

        NodeIterator it;

        PropertyPointer start = null;

        if (useStartLocation) {
            start = root.getPropertyPointer();
            start.setPropertyIndex(
                relativeProperty(start, relativePropertyIndex));
            start.setIndex(offset);
        }
        it =
            root.childIterator(
                new NodeNameTest(new QName(null, "integers")),
                reverse,
                start);

        int size = 0;
        while (it.setPosition(it.getPosition() + 1)) {
            size++;
        }
        assertEquals(
            "ITERATIONS: Individual, relativePropertyIndex="
                + relativePropertyIndex
                + ", offset="
                + offset
                + ", useStartLocation="
                + useStartLocation
                + ", reverse="
                + reverse,
            expected,
            size);
    }

    /**
     * Test property iterators with multiple properties returned
     */
    public void testMultipleIterators() {
        testMultiple(0, 0, true, false, 20);

        testMultiple(3, 0, true, false, 16);
        testMultiple(3, -1, true, true, 8);
        testMultiple(3, 0, true, true, 4);
        testMultiple(0, 0, false, false, 21);
        testMultiple(0, 0, false, true, 21);

        testMultiple(3, 1, true, false, 15);
        testMultiple(3, 3, true, false, 13);
    }

    private void testMultiple(
        int propertyIndex,
        int offset,
        boolean useStartLocation,
        boolean reverse,
        int expected) 
    {
        PropertyOwnerPointer root =
            (PropertyOwnerPointer) NodePointer.newNodePointer(
                new QName(null, "root"),
                createContextBean(),
                Locale.getDefault());
        NodeIterator it;

        PropertyPointer start = null;

        if (useStartLocation) {
            start = root.getPropertyPointer();
            start.setPropertyIndex(propertyIndex);
            start.setIndex(offset);
        }
        it = root.childIterator(null, reverse, start);

        int size = 0;
        while (it.setPosition(it.getPosition() + 1)) {
//            System.err.println("LOC: " + it.getCurrentNodePointer());
            size++;
        }
        assertEquals(
            "ITERATIONS: Multiple, propertyIndex="
                + propertyIndex
                + ", offset="
                + offset
                + ", useStartLocation="
                + useStartLocation
                + ", reverse="
                + reverse,
            expected,
            size);
    }

    private int relativeProperty(PropertyPointer holder, int offset) {
        String[] names = holder.getPropertyNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals("integers")) {
                return i + offset;
            }
        }
        return -1;
    }

    public void testIteratePropertyArrayWithHasNext() {
        JXPathContext context = JXPathContext.newContext(createContextBean());
        Iterator it = context.iteratePointers("/integers");
        List actual = new ArrayList();
        while (it.hasNext()) {
            actual.add(((Pointer) it.next()).asPath());
        }
        assertEquals(
            "Iterating 'hasNext'/'next'<" + "/integers" + ">",
            list(
                "/integers[1]",
                "/integers[2]",
                "/integers[3]",
                "/integers[4]"),
            actual);
    }

    public void testIteratePropertyArrayWithoutHasNext() {
        JXPathContext context = JXPathContext.newContext(createContextBean());
        Iterator it = context.iteratePointers("/integers");
        List actual = new ArrayList();
        for (int i = 0; i < 4; i++) {
            actual.add(it.next().toString());
        }
        assertEquals(
            "Iterating 'next'<" + "/integers" + ">",
            list(
                "/integers[1]",
                "/integers[2]",
                "/integers[3]",
                "/integers[4]"),
            actual);
    }

    public void testIterateAndSet() {
        JXPathContext context = JXPathContext.newContext(createContextBean());

        Iterator it = context.iteratePointers("beans/int");
        int i = 5;
        while (it.hasNext()) {
            NodePointer pointer = (NodePointer) it.next();
            pointer.setValue(new Integer(i++));
        }

        it = context.iteratePointers("beans/int");
        List actual = new ArrayList();
        while (it.hasNext()) {
            actual.add(((Pointer) it.next()).getValue());
        }
        assertEquals(
            "Iterating <" + "beans/int" + ">",
            list(new Integer(5), new Integer(6)),
            actual);
    }

    /**
     * Test contributed by Kate Dvortsova
     */
    public void testIteratePointerSetValue() {
        JXPathContext context = JXPathContext.newContext(createContextBean());

        assertXPathValue(context, "/beans[1]/name", "Name 1");
        assertXPathValue(context, "/beans[2]/name", "Name 2");

        // Test setting via context
        context.setValue("/beans[2]/name", "Name 2 set");
        assertXPathValue(context, "/beans[2]/name", "Name 2 set");

        // Restore original value
        context.setValue("/beans[2]/name", "Name 2");
        assertXPathValue(context, "/beans[2]/name", "Name 2");

        int iterCount = 0;
        Iterator iter = context.iteratePointers("/beans/name");
        while (iter.hasNext()) {
            iterCount++;
            Pointer pointer = (Pointer) iter.next();
            String s = (String) pointer.getValue();
            s = s + "suffix";
            pointer.setValue(s);
            assertEquals("pointer.getValue", s, pointer.getValue());
            // fails right here, the value isn't getting set in the bean.
            assertEquals(
                "context.getValue",
                s,
                context.getValue(pointer.asPath()));
        }
        assertEquals("Iteration count", 2, iterCount);

        assertXPathValue(context, "/beans[1]/name", "Name 1suffix");
        assertXPathValue(context, "/beans[2]/name", "Name 2suffix");
    }

    public void testRoot() {
        assertXPathValueAndPointer(context, "/", context.getContextBean(), "/");
    }

    public void testAxisAncestor() {
        // ancestor::
        assertXPathValue(context, "int/ancestor::root = /", Boolean.TRUE);

        assertXPathValue(
            context,
            "count(beans/name/ancestor-or-self::node())",
            new Double(5));

        assertXPathValue(
            context,
            "beans/name/ancestor-or-self::node()[3] = /",
            Boolean.TRUE);
    }

    public void testAxisChild() {
        assertXPathValue(context, "boolean", Boolean.FALSE);

        assertXPathPointer(context, "boolean", "/boolean");

        assertXPathPointerIterator(context, "boolean", list("/boolean"));

        // Count elements in a child collection
        assertXPathValue(context, "count(set)", new Double(3));

//        assertXPathValue(context,"boolean/class/name", "java.lang.Boolean");

        // Child with namespace - should not find any
        assertXPathValueIterator(context, "foo:boolean", list());

        // Count all children with a wildcard
        assertXPathValue(context, "count(*)", new Double(21));

        // Same, constrained by node type = node()
        assertXPathValue(context, "count(child::node())", new Double(21));
    }

    public void testAxisChildNestedBean() {
        // Nested bean
        assertXPathValue(context, "nestedBean/name", "Name 0");

        assertXPathPointer(context, "nestedBean/name", "/nestedBean/name");

        assertXPathPointerIterator(
            context,
            "nestedBean/name",
            list("/nestedBean/name"));
    }

    public void testAxisChildNestedCollection() {
        assertXPathValueIterator(
            context,
            "integers",
            list(
                new Integer(1),
                new Integer(2),
                new Integer(3),
                new Integer(4)));

        assertXPathPointer(context, "integers", "/integers");

        assertXPathPointerIterator(
            context,
            "integers",
            list(
                "/integers[1]",
                "/integers[2]",
                "/integers[3]",
                "/integers[4]"));
    }

    public void testIndexPredicate() {
        assertXPathValue(context, "integers[2]", new Integer(2));

        assertXPathPointer(context, "integers[2]", "/integers[2]");

        assertXPathPointerIterator(
            context,
            "integers[2]",
            list("/integers[2]"));

        assertXPathValue(context, "beans[1]/name", "Name 1");

        assertXPathPointer(context, "beans[1]/name", "/beans[1]/name");

        assertXPathValueIterator(
            context,
            "beans[1]/strings",
            list("String 1", "String 2", "String 3"));

        assertXPathValueIterator(
            context,
            "beans/strings[2]",
            list("String 2", "String 2"));

        // Find the first match
        assertXPathValue(context, "beans/strings[2]", "String 2");

        // Indexing in a set collected from a UnionContext
        assertXPathValue(context, "(beans/strings[2])[1]", "String 2");
    }

    public void testAxisDescendant() {
        // descendant::
        assertXPathValue(context, "count(descendant::node())", new Double(65));

        // Should not find any descendants with name root
        assertXPathValue(context, "count(descendant::root)", new Double(0));

        assertXPathValue(context, "count(descendant::name)", new Double(7));
    }

    public void testAxisDescendantOrSelf() {
        // descendant-or-self::
        assertXPathValueIterator(
            context,
            "descendant-or-self::name",
            set(
                "Name 1",
                "Name 2",
                "Name 3",
                "Name 6",
                "Name 0",
                "Name 5",
                "Name 4"));

        // Same - abbreviated syntax
        assertXPathValueIterator(
            context,
            "//name",
            set(
                "Name 1",
                "Name 2",
                "Name 3",
                "Name 6",
                "Name 0",
                "Name 5",
                "Name 4"));

        // See that it actually finds self
        assertXPathValue(
            context,
            "count(descendant-or-self::root)",
            new Double(1));

        // Combine descendant-or-self:: and and self::
        assertXPathValue(context, "count(nestedBean//.)", new Double(7));

        // Combine descendant-or-self:: and and self::name
        assertXPathValue(context, "count(//self::beans)", new Double(2));

        // Count all nodes in the tree
        assertXPathValue(
            context,
            "count(descendant-or-self::node())",
            new Double(66));

    }

    public void testAxisFollowing() {
        // following::
        assertXPathValue(
            context,
            "count(nestedBean/strings[2]/following::node())",
            new Double(21));

        assertXPathValue(
            context,
            "count(nestedBean/strings[2]/following::strings)",
            new Double(7));
    }

    public void testAxisFollowingSibling() {
        // following-sibling::
        assertXPathValue(
            context,
            "count(/nestedBean/following-sibling::node())",
            new Double(8));

        assertXPathValue(
            context,
            "count(/nestedBean/following-sibling::object)",
            new Double(1));

        // Combine parent:: and following-sibling::
        assertXPathValue(
            context,
            "count(/nestedBean/boolean/../following-sibling::node())",
            new Double(8));

        assertXPathValue(
            context,
            "count(/nestedBean/boolean/../following-sibling::object)",
            new Double(1));

        // Combine descendant:: and following-sibling::
        assertXPathValue(
            context,
            "count(/descendant::boolean/following-sibling::node())",
            new Double(53));

        assertXPathValue(
            context,
            "count(/descendant::boolean/following-sibling::name)",
            new Double(7));
    }

    public void testAxisParent() {
        // parent::
        assertXPathValue(context, "count(/beans/..)", new Double(1));

        assertXPathValue(context, "count(//..)", new Double(9));

        assertXPathValue(context, "count(//../..)", new Double(2));

        assertXPathValueIterator(
            context,
            "//parent::beans/name",
            list("Name 1", "Name 2"));
    }

    public void testAxisPreceding() {
        // preceding::
        assertXPathValue(
            context,
            "count(beans[2]/int/preceding::node())",
            new Double(8));

        assertXPathValue(
            context,
            "count(beans[2]/int/preceding::boolean)",
            new Double(2));
    }

    public void testAxisPrecedingSibling() {
        // preceding-sibling::
        assertXPathValue(
            context,
            "count(/boolean/preceding-sibling::node())",
            new Double(2));

        assertXPathValue(
            context,
            "count(/nestedBean/int/../preceding-sibling::node())",
            new Double(12));

        assertXPathValue(
            context,
            "count(/descendant::int/preceding-sibling::node())",
            new Double(10));
    }

    public void testAxisSelf() {
        // self::
        assertXPathValue(context, "self::node() = /", Boolean.TRUE);

        assertXPathValue(context, "self::root = /", Boolean.TRUE);
    }

    public void testUnion() {
        // Union - note corrected document order
        assertXPathValueIterator(
            context,
            "integers | beans[1]/strings",
            list(
                "String 1",
                "String 2",
                "String 3",
                new Integer(1),
                new Integer(2),
                new Integer(3),
                new Integer(4)));

        assertXPathValue(
            context,
            "count((integers | beans[1]/strings)[contains(., '1')])",
            new Double(2));

        assertXPathValue(
            context,
            "count((integers | beans[1]/strings)[name(.) = 'strings'])",
            new Double(3));

        // Note that the following is different from "integer[2]" -
        // it is a filter expression
        assertXPathValue(context, "(integers)[2]", new Integer(2));
    }

    public void testAxisAttribute() {
        // Attributes are just like children to beans
        assertXPathValue(context, "count(@*)", new Double(21.0));

        // Unknown attribute
        assertXPathValueLenient(context, "@foo", null);
    }

    /**
     * Testing the pseudo-attribute "name" that java beans
     * objects appear to have.
     */
    public void testAttributeName() {
        assertXPathValue(context, "nestedBean[@name = 'int']", new Integer(1));

        assertXPathPointer(
            context,
            "nestedBean[@name = 'int']",
            "/nestedBean/int");
    }

    public void testAttributeLang() {

        assertXPathValue(context, "@xml:lang", "en-US");

        assertXPathValue(context, "count(@xml:*)", new Double(1));

        assertXPathValue(context, "lang('en')", Boolean.TRUE);

        assertXPathValue(context, "lang('fr')", Boolean.FALSE);
    }

    public void testCoreFunctions() {

        assertXPathValue(context, "boolean(boolean)", Boolean.TRUE);

        assertXPathValue(context, "boolean(boolean = false())", Boolean.TRUE);

        assertXPathValue(
            context,
            "boolean(integers[position() < 3])",
            Boolean.TRUE);

        assertXPathValue(
            context,
            "boolean(integers[position() > 4])",
            Boolean.FALSE);

        assertXPathValue(context, "sum(integers)", new Double(10));        

        assertXPathValueAndPointer(
                context,
                "integers[last()]",
                new Integer(4),
                "/integers[4]");

        assertXPathValueAndPointer(
                context,
                "//strings[last()]",
                "String 3",
                "/beans[1]/strings[3]");
    }

    public void testBooleanPredicate() {
        // use child axis

        // bean[1]/int = 1
        // bean[2]/int = 3

        assertXPathValue(context, "beans[int > 2]/name", "Name 2");

        assertXPathValueIterator(
            context,
            "beans[int > 2]/name",
            list("Name 2"));

        assertXPathValueIterator(
            context,
            "beans[int >= 1]/name",
            list("Name 1", "Name 2"));

        assertXPathValueIterator(
            context,
            "beans[int < 2]/name",
            list("Name 1"));

        assertXPathValueIterator(
            context,
            "beans[int <= 3]/name",
            list("Name 1", "Name 2"));

        assertXPathValueIterator(
            context,
            "beans[1]/strings[string-length() = 8]",
            list("String 1", "String 2", "String 3"));

        // use some fancy axis and the child axis in the predicate
        assertXPathValueIterator(
            context,
            "//self::node()[name = 'Name 0']/name",
            list("Name 0"));

        // use context-dependent function in the predicate
        assertXPathValue(
            context,
            "beans/strings[name(.)='strings'][2]",
            "String 2");

        // use context-independent function in the predicate
        assertXPathValueIterator(
            context,
            "//self::node()[name(.) = concat('n', 'a', 'm', 'e')]",
            list(
                "Name 1",
                "Name 2",
                "Name 3",
                "Name 6",
                "Name 0",
                "Name 5",
                "Name 4"));

        assertXPathValueIterator(
            context,
            "integers[position()<3]",
            list(new Integer(1), new Integer(2)));
            
        context.getVariables().declareVariable(
            "temp",
            context.getValue("beans"));
        
        assertXPathValueIterator(
            context,
            "$temp[int < 2]/int",
            list(new Integer(1)));
    }

    public void testDocumentOrder() {
        assertDocumentOrder(context, "boolean", "int", -1);

        assertDocumentOrder(context, "integers[1]", "integers[2]", -1);

        assertDocumentOrder(context, "integers[1]", "integers[1]", 0);

        assertDocumentOrder(context, "nestedBean/int", "nestedBean", 1);

        assertDocumentOrder(
            context,
            "nestedBean/int",
            "nestedBean/strings",
            -1);

        assertDocumentOrder(context, "nestedBean/int", "object/int", -1);
    }

    public void testSetPropertyValue() {
        // Simple property
        assertXPathSetValue(context, "int", new Integer(2));

        // Simple property with conversion from string
        assertXPathSetValue(context, "int", "3", new Integer(3));

        // Simple property with conversion from array
        assertXPathSetValue(context, "int", new int[] { 4 }, new Integer(4));

        // Attribute (which is the same as a child for beans
        assertXPathSetValue(context, "@int", new Integer(10));
    }

    public void testSetCollectionElement() {
        // Collection element
        assertXPathSetValue(context, "integers[2]", new Integer(5));

        // Collection element with conversion
        assertXPathSetValue(
            context,
            "integers[2]",
            new int[] { 6 },
            new Integer(6));
    }

    public void testSetContextDependentNode() {
        // Find node without using SimplePathInterpreter
        assertXPathSetValue(
            context,
            "integers[position() = 1]",
            new Integer(8));

        // Find node without using SimplePathInterpreter and set its property
        assertXPathSetValue(
            context,
            "beans[name = 'Name 1']/int",
            new Integer(9));

    }

    public void testSetNonPrimitiveValue() {
        // First, let's see if we can set a collection element to null
        assertXPathSetValue(context, "beans[2]", null);

        // Now, assign it a whole bean
        context.setValue("beans[2]", new NestedTestBean("Name 9"));

        assertEquals(
            "Modified <" + "beans[2]/name" + ">",
            "Name 9",
            context.getValue("beans[2]/name"));
    }

    public void testCreatePath() {
        context.setValue("nestedBean", null);

        // Calls factory.createObject(..., TestBean, "nestedBean")
        assertXPathCreatePath(
            context,
            "/nestedBean/int",
            new Integer(1),
            "/nestedBean/int");

        boolean ex = false;
        try {
            assertXPathCreatePath(
                context,
                "/nestedBean/beans[last() + 1]",
                new Integer(1),
                "/nestedBean/beans[last() + 1]");
        }
        catch (Exception e) {
            ex = true;
        }
        assertTrue("Exception thrown on invalid path for creation", ex);
        
    }

    public void testCreatePathAndSetValue() {
        context.setValue("nestedBean", null);

        // Calls factory.createObject(..., TestBean, "nestedBean")
        assertXPathCreatePathAndSetValue(
            context,
            "/nestedBean/int",
            new Integer(2),
            "/nestedBean/int");
    }

    public void testCreatePathExpandNewCollection() {
        context.setValue("beans", null);

        // Calls factory.createObject(..., testBean, "beans", 2), 
        // then  factory.createObject(..., testBean, "beans", 2)
        assertXPathCreatePath(
            context,
            "/beans[2]/int",
            new Integer(1),
            "/beans[2]/int");
    }

    public void testCreatePathAndSetValueExpandNewCollection() {
        context.setValue("beans", null);

        // Calls factory.createObject(..., testBean, "beans", 2), 
        // then factory.createObject(..., testBean, "beans", 2)
        assertXPathCreatePathAndSetValue(
            context,
            "/beans[2]/int",
            new Integer(2),
            "/beans[2]/int");
    }

    public void testCreatePathExpandExistingCollection() {
        // Calls factory.createObject(..., TestBean, "integers", 5)
        // to expand collection
        assertXPathCreatePathAndSetValue(
            context,
            "/integers[5]",
            new Integer(3),
            "/integers[5]");
    }

    public void testCreatePathExpandExistingCollectionAndSetProperty() {
        // Another, but the collection already exists
        assertXPathCreatePath(
            context,
            "/beans[3]/int",
            new Integer(1),
            "/beans[3]/int");
    }

    public void testCreatePathAndSetValueExpandExistingCollection() {
        // Another, but the collection already exists
        assertXPathCreatePathAndSetValue(
            context,
            "/beans[3]/int",
            new Integer(2),
            "/beans[3]/int");
    }

    public void testCreatePathCreateBeanExpandCollection() {
        context.setValue("nestedBean", null);

        // Calls factory.createObject(..., TestBean, "nestedBean")
        // Calls factory.createObject(..., nestedBean, "strings", 2)
        assertXPathCreatePath(
            context,
            "/nestedBean/strings[2]",
            "String 2",
            "/nestedBean/strings[2]");
    }

    public void testCreatePathAndSetValueCreateBeanExpandCollection() {
        context.setValue("nestedBean", null);

        // Calls factory.createObject(..., TestBean, "nestedBean")
        // Calls factory.createObject(..., nestedBean, "strings", 2)
        assertXPathCreatePathAndSetValue(
            context,
            "/nestedBean/strings[2]",
            "Test",
            "/nestedBean/strings[2]");
    }

    public void testRemovePathPropertyValue() {
        // Remove property value
        context.removePath("nestedBean/int");
        assertEquals(
            "Remove property value",
            new Integer(0),
            context.getValue("nestedBean/int"));
    }

    public void testRemovePathArrayElement() {
        // Assigns a new array to the property
        context.removePath("nestedBean/strings[1]");
        assertEquals(
            "Remove array element",
            "String 2",
            context.getValue("nestedBean/strings[1]"));
    }

    public void testRemoveAllArrayElements() {
        context.removeAll("nestedBean/strings");
        assertXPathValueIterator(
            context,
            "nestedBean/strings",
            list());
    }

    public void testRemoveAllListElements() {
        context.removeAll("list");
        assertXPathValueIterator(
            context,
            "list",
            this instanceof DynaBeanModelTest ? list(null, null, null) : list());
    }

    public void testRemoveAllMapEntries() {
        context.removeAll("map/*");
        assertXPathValue(
            context,
            "map",
            Collections.EMPTY_MAP);
    }

    public void testRemovePathBeanValue() {
        context.removePath("nestedBean");
        assertEquals(
            "Remove collection element",
            null,
            context.getValue("nestedBean"));
    }
    
    public void testRelativeContextRelativePath() {
        JXPathContext relative =
            context.getRelativeContext(context.getPointer("nestedBean"));
        
        assertXPathValueAndPointer(relative, 
            "int", 
            new Integer(1), 
            "/nestedBean/int");
    }

    public void testRelativeContextAbsolutePath() {
        JXPathContext relative =
            context.getRelativeContext(context.getPointer("nestedBean"));
        
        assertXPathValueAndPointer(relative, 
            "/integers[2]", 
            new Integer(2), 
            "/integers[2]");
    }

    public void testRelativeContextParent() {
        JXPathContext relative =
            context.getRelativeContext(context.getPointer("nestedBean"));
        
        assertXPathValueAndPointer(relative, 
            "../integers[2]", 
            new Integer(2), 
            "/integers[2]");
    }
    
    public void testRelativeContextInheritance() {
        context.setFunctions(new ClassFunctions(TestFunctions.class, "test"));
        JXPathContext relative =
            context.getRelativeContext(context.getPointer("nestedBean"));
        
        assertXPathValue(relative, 
            "test:countPointers(strings)", 
            new Integer(3));
    }
}