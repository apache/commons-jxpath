/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jxpath.ri.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.AbstractJXPathTest;
import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.NestedTestBean;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.TestFunctions;
import org.apache.commons.jxpath.ri.model.beans.PropertyOwnerPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;
import org.apache.commons.jxpath.ri.model.dynabeans.DynaBeanModelTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Abstract superclass for Bean access with JXPath.
 */
public abstract class AbstractBeanModelTest extends AbstractJXPathTest {

    private JXPathContext context;

    protected abstract Object createContextBean();

    protected abstract AbstractFactory getAbstractFactory();

    private int relativeProperty(final PropertyPointer holder, final int offset) {
        final String[] names = holder.getPropertyNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals("integers")) {
                return i + offset;
            }
        }
        return -1;
    }

    @Override
    @BeforeEach
    public void setUp() {
//        if (context == null) {
        context = JXPathContext.newContext(createContextBean());
        context.setLocale(Locale.US);
        context.setFactory(getAbstractFactory());
//        }
    }

    @Test
    void testAttributeLang() {
        assertXPathValue(context, "@xml:lang", "en-US");
        assertXPathValue(context, "count(@xml:*)", Double.valueOf(1));
        assertXPathValue(context, "lang('en')", Boolean.TRUE);
        assertXPathValue(context, "lang('fr')", Boolean.FALSE);
    }

    /**
     * Testing the pseudo-attribute "name" that java beans objects appear to have.
     */
    @Test
    void testAttributeName() {
        assertXPathValue(context, "nestedBean[@name = 'int']", Integer.valueOf(1));
        assertXPathPointer(context, "nestedBean[@name = 'int']", "/nestedBean/int");
    }

    @Test
    void testAxisAncestor() {
        // ancestor::
        assertXPathValue(context, "int/ancestor::root = /", Boolean.TRUE);
        assertXPathValue(context, "count(beans/name/ancestor-or-self::node())", Double.valueOf(5));
        assertXPathValue(context, "beans/name/ancestor-or-self::node()[3] = /", Boolean.TRUE);
    }

    @Test
    void testAxisAttribute() {
        // Attributes are just like children to beans
        assertXPathValue(context, "count(@*)", Double.valueOf(21.0));
        // Unknown attribute
        assertXPathValueLenient(context, "@foo", null);
    }

    @Test
    void testAxisChild() {
        assertXPathValue(context, "boolean", Boolean.FALSE);
        assertXPathPointer(context, "boolean", "/boolean");
        assertXPathPointerIterator(context, "boolean", list("/boolean"));
        // Count elements in a child collection
        assertXPathValue(context, "count(set)", Double.valueOf(3));
//        assertXPathValue(context,"boolean/class/name", "java.lang.Boolean");
        // Child with namespace - should not find any
        assertXPathValueIterator(context, "foo:boolean", list());
        // Count all children with a wildcard
        assertXPathValue(context, "count(*)", Double.valueOf(21));
        // Same, constrained by node type = node()
        assertXPathValue(context, "count(child::node())", Double.valueOf(21));
    }

    @Test
    void testAxisChildNestedBean() {
        // Nested bean
        assertXPathValue(context, "nestedBean/name", "Name 0");
        assertXPathPointer(context, "nestedBean/name", "/nestedBean/name");
        assertXPathPointerIterator(context, "nestedBean/name", list("/nestedBean/name"));
    }

    @Test
    void testAxisChildNestedCollection() {
        assertXPathValueIterator(context, "integers", list(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)));
        assertXPathPointer(context, "integers", "/integers");
        assertXPathPointerIterator(context, "integers", list("/integers[1]", "/integers[2]", "/integers[3]", "/integers[4]"));
    }

    @Test
    void testAxisDescendant() {
        // descendant::
        assertXPathValue(context, "count(descendant::node())", Double.valueOf(65));
        // Should not find any descendants with name root
        assertXPathValue(context, "count(descendant::root)", Double.valueOf(0));
        assertXPathValue(context, "count(descendant::name)", Double.valueOf(7));
    }

    @Test
    void testAxisDescendantOrSelf() {
        // descendant-or-self::
        assertXPathValueIterator(context, "descendant-or-self::name", set("Name 1", "Name 2", "Name 3", "Name 6", "Name 0", "Name 5", "Name 4"));
        // Same - abbreviated syntax
        assertXPathValueIterator(context, "//name", set("Name 1", "Name 2", "Name 3", "Name 6", "Name 0", "Name 5", "Name 4"));
        // See that it actually finds self
        assertXPathValue(context, "count(descendant-or-self::root)", Double.valueOf(1));
        // Combine descendant-or-self:: and and self::
        assertXPathValue(context, "count(nestedBean//.)", Double.valueOf(7));
        // Combine descendant-or-self:: and and self::name
        assertXPathValue(context, "count(//self::beans)", Double.valueOf(2));
        // Count all nodes in the tree
        assertXPathValue(context, "count(descendant-or-self::node())", Double.valueOf(66));
    }

    @Test
    void testAxisFollowing() {
        // following::
        assertXPathValue(context, "count(nestedBean/strings[2]/following::node())", Double.valueOf(21));
        assertXPathValue(context, "count(nestedBean/strings[2]/following::strings)", Double.valueOf(7));
    }

    @Test
    void testAxisFollowingSibling() {
        // following-sibling::
        assertXPathValue(context, "count(/nestedBean/following-sibling::node())", Double.valueOf(8));
        assertXPathValue(context, "count(/nestedBean/following-sibling::object)", Double.valueOf(1));
        // Combine parent:: and following-sibling::
        assertXPathValue(context, "count(/nestedBean/boolean/../following-sibling::node())", Double.valueOf(8));
        assertXPathValue(context, "count(/nestedBean/boolean/../following-sibling::object)", Double.valueOf(1));
        // Combine descendant:: and following-sibling::
        assertXPathValue(context, "count(/descendant::boolean/following-sibling::node())", Double.valueOf(53));
        assertXPathValue(context, "count(/descendant::boolean/following-sibling::name)", Double.valueOf(7));
    }

    @Test
    void testAxisParent() {
        // parent::
        assertXPathValue(context, "count(/beans/..)", Double.valueOf(1));
        assertXPathValue(context, "count(//..)", Double.valueOf(9));
        assertXPathValue(context, "count(//../..)", Double.valueOf(2));
        assertXPathValueIterator(context, "//parent::beans/name", list("Name 1", "Name 2"));
    }

    @Test
    void testAxisPreceding() {
        // preceding::
        assertXPathValue(context, "count(beans[2]/int/preceding::node())", Double.valueOf(8));
        assertXPathValue(context, "count(beans[2]/int/preceding::boolean)", Double.valueOf(2));
    }

    @Test
    void testAxisPrecedingSibling() {
        // preceding-sibling::
        assertXPathValue(context, "count(/boolean/preceding-sibling::node())", Double.valueOf(2));
        assertXPathValue(context, "count(/nestedBean/int/../preceding-sibling::node())", Double.valueOf(12));
        assertXPathValue(context, "count(/descendant::int/preceding-sibling::node())", Double.valueOf(10));
    }

    @Test
    void testAxisSelf() {
        // self::
        assertXPathValue(context, "self::node() = /", Boolean.TRUE);
        assertXPathValue(context, "self::root = /", Boolean.TRUE);
    }

    @Test
    void testBooleanPredicate() {
        // use child axis
        // bean[1]/int = 1
        // bean[2]/int = 3
        assertXPathValue(context, "beans[int > 2]/name", "Name 2");
        assertXPathValueIterator(context, "beans[int > 2]/name", list("Name 2"));
        assertXPathValueIterator(context, "beans[int >= 1]/name", list("Name 1", "Name 2"));
        assertXPathValueIterator(context, "beans[int < 2]/name", list("Name 1"));
        assertXPathValueIterator(context, "beans[int <= 3]/name", list("Name 1", "Name 2"));
        assertXPathValueIterator(context, "beans[1]/strings[string-length() = 8]", list("String 1", "String 2", "String 3"));
        // use some fancy axis and the child axis in the predicate
        assertXPathValueIterator(context, "//self::node()[name = 'Name 0']/name", list("Name 0"));
        // use context-dependent function in the predicate
        assertXPathValue(context, "beans/strings[name(.)='strings'][2]", "String 2");
        // use context-independent function in the predicate
        assertXPathValueIterator(context, "//self::node()[name(.) = concat('n', 'a', 'm', 'e')]",
                list("Name 1", "Name 2", "Name 3", "Name 6", "Name 0", "Name 5", "Name 4"));
        assertXPathValueIterator(context, "integers[position()<3]", list(Integer.valueOf(1), Integer.valueOf(2)));
        context.getVariables().declareVariable("temp", context.getValue("beans"));
        assertXPathValueIterator(context, "$temp[int < 2]/int", list(Integer.valueOf(1)));
    }

    @Test
    void testCoreFunctions() {
        assertXPathValue(context, "boolean(boolean)", Boolean.TRUE);
        assertXPathValue(context, "boolean(boolean = false())", Boolean.TRUE);
        assertXPathValue(context, "boolean(integers[position() < 3])", Boolean.TRUE);
        assertXPathValue(context, "boolean(integers[position() > 4])", Boolean.FALSE);
        assertXPathValue(context, "sum(integers)", Double.valueOf(10));
        assertXPathValueAndPointer(context, "integers[last()]", Integer.valueOf(4), "/integers[4]");
        assertXPathValueAndPointer(context, "//strings[last()]", "String 3", "/beans[1]/strings[3]");
    }

    @Test
    void testCreatePath() {
        context.setValue("nestedBean", null);
        // Calls factory.createObject(..., TestBean, "nestedBean")
        assertXPathCreatePath(context, "/nestedBean/int", Integer.valueOf(1), "/nestedBean/int");
        assertThrows(Exception.class,
                () -> assertXPathCreatePath(context, "/nestedBean/beans[last() + 1]", Integer.valueOf(1), "/nestedBean/beans[last() + 1]"),
                "Exception thrown on invalid path for creation");
    }

    @Test
    void testCreatePathAndSetValue() {
        context.setValue("nestedBean", null);
        // Calls factory.createObject(..., TestBean, "nestedBean")
        assertXPathCreatePathAndSetValue(context, "/nestedBean/int", Integer.valueOf(2), "/nestedBean/int");
    }

    @Test
    void testCreatePathAndSetValueCreateBeanExpandCollection() {
        context.setValue("nestedBean", null);
        // Calls factory.createObject(..., TestBean, "nestedBean")
        // Calls factory.createObject(..., nestedBean, "strings", 2)
        assertXPathCreatePathAndSetValue(context, "/nestedBean/strings[2]", "Test", "/nestedBean/strings[2]");
    }

    @Test
    void testCreatePathAndSetValueExpandExistingCollection() {
        // Another, but the collection already exists
        assertXPathCreatePathAndSetValue(context, "/beans[3]/int", Integer.valueOf(2), "/beans[3]/int");
    }

    @Test
    void testCreatePathAndSetValueExpandNewCollection() {
        context.setValue("beans", null);
        // Calls factory.createObject(..., testBean, "beans", 2),
        // then factory.createObject(..., testBean, "beans", 2)
        assertXPathCreatePathAndSetValue(context, "/beans[2]/int", Integer.valueOf(2), "/beans[2]/int");
    }

    @Test
    void testCreatePathCreateBeanExpandCollection() {
        context.setValue("nestedBean", null);
        // Calls factory.createObject(..., TestBean, "nestedBean")
        // Calls factory.createObject(..., nestedBean, "strings", 2)
        assertXPathCreatePath(context, "/nestedBean/strings[2]", "String 2", "/nestedBean/strings[2]");
    }

    @Test
    void testCreatePathExpandExistingCollection() {
        // Calls factory.createObject(..., TestBean, "integers", 5)
        // to expand collection
        assertXPathCreatePathAndSetValue(context, "/integers[5]", Integer.valueOf(3), "/integers[5]");
    }

    @Test
    void testCreatePathExpandExistingCollectionAndSetProperty() {
        // Another, but the collection already exists
        assertXPathCreatePath(context, "/beans[3]/int", Integer.valueOf(1), "/beans[3]/int");
    }

    @Test
    void testCreatePathExpandNewCollection() {
        context.setValue("beans", null);
        // Calls factory.createObject(..., testBean, "beans", 2),
        // then factory.createObject(..., testBean, "beans", 2)
        assertXPathCreatePath(context, "/beans[2]/int", Integer.valueOf(1), "/beans[2]/int");
    }

    @Test
    void testDocumentOrder() {
        assertDocumentOrder(context, "boolean", "int", -1);
        assertDocumentOrder(context, "integers[1]", "integers[2]", -1);
        assertDocumentOrder(context, "integers[1]", "integers[1]", 0);
        assertDocumentOrder(context, "nestedBean/int", "nestedBean", 1);
        assertDocumentOrder(context, "nestedBean/int", "nestedBean/strings", -1);
        assertDocumentOrder(context, "nestedBean/int", "object/int", -1);
    }

    @Test
    void testIndexPredicate() {
        assertXPathValue(context, "integers[2]", Integer.valueOf(2));
        assertXPathPointer(context, "integers[2]", "/integers[2]");
        assertXPathPointerIterator(context, "integers[2]", list("/integers[2]"));
        assertXPathValue(context, "beans[1]/name", "Name 1");
        assertXPathPointer(context, "beans[1]/name", "/beans[1]/name");
        assertXPathValueIterator(context, "beans[1]/strings", list("String 1", "String 2", "String 3"));
        assertXPathValueIterator(context, "beans/strings[2]", list("String 2", "String 2"));
        // Find the first match
        assertXPathValue(context, "beans/strings[2]", "String 2");
        // Indexing in a set collected from a UnionContext
        assertXPathValue(context, "(beans/strings[2])[1]", "String 2");
    }

    private void testIndividual(final int relativePropertyIndex, final int offset, final boolean useStartLocation, final boolean reverse, final int expected) {
        final PropertyOwnerPointer root = (PropertyOwnerPointer) NodePointer.newNodePointer(new QName(null, "root"), createContextBean(), Locale.getDefault());
        NodeIterator it;
        PropertyPointer start = null;
        if (useStartLocation) {
            start = root.getPropertyPointer();
            start.setPropertyIndex(relativeProperty(start, relativePropertyIndex));
            start.setIndex(offset);
        }
        it = root.childIterator(new NodeNameTest(new QName(null, "integers")), reverse, start);
        int size = 0;
        while (it.setPosition(it.getPosition() + 1)) {
            size++;
        }
        assertEquals(expected, size, "ITERATIONS: Individual, relativePropertyIndex=" + relativePropertyIndex + ", offset=" + offset + ", useStartLocation="
                + useStartLocation + ", reverse=" + reverse);
    }

    /**
     * Test property iterators, the core of the graph traversal engine
     */
    @Test
    void testIndividualIterators() {
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

    @Test
    void testIterateAndSet() {
        final JXPathContext context = JXPathContext.newContext(createContextBean());
        Iterator<Pointer> it = context.iteratePointers("beans/int");
        int i = 5;
        while (it.hasNext()) {
            final NodePointer pointer = (NodePointer) it.next();
            pointer.setValue(Integer.valueOf(i++));
        }
        it = context.iteratePointers("beans/int");
        final List<Object> actual = new ArrayList<>();
        while (it.hasNext()) {
            actual.add(it.next().getValue());
        }
        assertEquals(list(Integer.valueOf(5), Integer.valueOf(6)), actual, "Iterating <" + "beans/int" + ">");
    }

    /**
     * Test contributed by Kate Dvortsova
     */
    @Test
    void testIteratePointerSetValue() {
        final JXPathContext context = JXPathContext.newContext(createContextBean());
        assertXPathValue(context, "/beans[1]/name", "Name 1");
        assertXPathValue(context, "/beans[2]/name", "Name 2");
        // Test setting via context
        context.setValue("/beans[2]/name", "Name 2 set");
        assertXPathValue(context, "/beans[2]/name", "Name 2 set");
        // Restore original value
        context.setValue("/beans[2]/name", "Name 2");
        assertXPathValue(context, "/beans[2]/name", "Name 2");
        int iterCount = 0;
        final Iterator<Pointer> iter = context.iteratePointers("/beans/name");
        while (iter.hasNext()) {
            iterCount++;
            final Pointer pointer = iter.next();
            String s = (String) pointer.getValue();
            s += "suffix";
            pointer.setValue(s);
            assertEquals(s, pointer.getValue(), "pointer.getValue");
            // fails right here, the value isn't getting set in the bean.
            assertEquals(s, context.getValue(pointer.asPath()), "context.getValue");
        }
        assertEquals(2, iterCount, "Iteration count");
        assertXPathValue(context, "/beans[1]/name", "Name 1suffix");
        assertXPathValue(context, "/beans[2]/name", "Name 2suffix");
    }

    @Test
    void testIteratePropertyArrayWithHasNext() {
        final JXPathContext context = JXPathContext.newContext(createContextBean());
        final Iterator<Pointer> it = context.iteratePointers("/integers");
        final List<String> actual = new ArrayList<>();
        while (it.hasNext()) {
            actual.add(it.next().asPath());
        }
        assertEquals(list("/integers[1]", "/integers[2]", "/integers[3]", "/integers[4]"), actual, "Iterating 'hasNext'/'next'<" + "/integers" + ">");
    }

    @Test
    void testIteratePropertyArrayWithoutHasNext() {
        final JXPathContext context = JXPathContext.newContext(createContextBean());
        final Iterator<Pointer> it = context.iteratePointers("/integers");
        final List<String> actual = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            actual.add(it.next().toString());
        }
        assertEquals(list("/integers[1]", "/integers[2]", "/integers[3]", "/integers[4]"), actual, "Iterating 'next'<" + "/integers" + ">");
    }

    private void testMultiple(final int propertyIndex, final int offset, final boolean useStartLocation, final boolean reverse, final int expected) {
        final PropertyOwnerPointer root = (PropertyOwnerPointer) NodePointer.newNodePointer(new QName(null, "root"), createContextBean(), Locale.getDefault());
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
        assertEquals(expected, size, "ITERATIONS: Multiple, propertyIndex=" + propertyIndex + ", offset=" + offset + ", useStartLocation=" + useStartLocation
                + ", reverse=" + reverse);
    }

    /**
     * Test property iterators with multiple properties returned
     */
    @Test
    void testMultipleIterators() {
        testMultiple(0, 0, true, false, 20);
        testMultiple(3, 0, true, false, 16);
        testMultiple(3, -1, true, true, 8);
        testMultiple(3, 0, true, true, 4);
        testMultiple(0, 0, false, false, 21);
        testMultiple(0, 0, false, true, 21);
        testMultiple(3, 1, true, false, 15);
        testMultiple(3, 3, true, false, 13);
    }

    @Test
    void testRelativeContextAbsolutePath() {
        final JXPathContext relative = context.getRelativeContext(context.getPointer("nestedBean"));
        assertXPathValueAndPointer(relative, "/integers[2]", Integer.valueOf(2), "/integers[2]");
    }

    @Test
    void testRelativeContextInheritance() {
        context.setFunctions(new ClassFunctions(TestFunctions.class, "test"));
        final JXPathContext relative = context.getRelativeContext(context.getPointer("nestedBean"));
        assertXPathValue(relative, "test:countPointers(strings)", Integer.valueOf(3));
    }

    @Test
    void testRelativeContextParent() {
        final JXPathContext relative = context.getRelativeContext(context.getPointer("nestedBean"));
        assertXPathValueAndPointer(relative, "../integers[2]", Integer.valueOf(2), "/integers[2]");
    }

    @Test
    void testRelativeContextRelativePath() {
        final JXPathContext relative = context.getRelativeContext(context.getPointer("nestedBean"));
        assertXPathValueAndPointer(relative, "int", Integer.valueOf(1), "/nestedBean/int");
    }

    @Test
    void testRemoveAllArrayElements() {
        context.removeAll("nestedBean/strings");
        assertXPathValueIterator(context, "nestedBean/strings", list());
    }

    @Test
    void testRemoveAllListElements() {
        context.removeAll("list");
        assertXPathValueIterator(context, "list", this instanceof DynaBeanModelTest ? list(null, null, null) : list());
    }

    @Test
    void testRemoveAllMapEntries() {
        context.removeAll("map/*");
        assertXPathValue(context, "map", Collections.EMPTY_MAP);
    }

    @Test
    void testRemovePathArrayElement() {
        // Assigns a new array to the property
        context.removePath("nestedBean/strings[1]");
        assertEquals("String 2", context.getValue("nestedBean/strings[1]"), "Remove array element");
    }

    @Test
    void testRemovePathBeanValue() {
        context.removePath("nestedBean");
        assertNull(context.getValue("nestedBean"), "Remove collection element");
    }

    @Test
    void testRemovePathPropertyValue() {
        // Remove property value
        context.removePath("nestedBean/int");
        assertEquals(Integer.valueOf(0), context.getValue("nestedBean/int"), "Remove property value");
    }

    @Test
    void testRoot() {
        assertXPathValueAndPointer(context, "/", context.getContextBean(), "/");
    }

    @Test
    void testSetCollectionElement() {
        // Collection element
        assertXPathSetValue(context, "integers[2]", Integer.valueOf(5));
        // Collection element with conversion
        assertXPathSetValue(context, "integers[2]", new int[] { 6 }, Integer.valueOf(6));
    }

    @Test
    void testSetContextDependentNode() {
        // Find node without using SimplePathInterpreter
        assertXPathSetValue(context, "integers[position() = 1]", Integer.valueOf(8));
        // Find node without using SimplePathInterpreter and set its property
        assertXPathSetValue(context, "beans[name = 'Name 1']/int", Integer.valueOf(9));
    }

    @Test
    void testSetNonPrimitiveValue() {
        // First, let's see if we can set a collection element to null
        assertXPathSetValue(context, "beans[2]", null);
        // Now, assign it a whole bean
        context.setValue("beans[2]", new NestedTestBean("Name 9"));
        assertEquals("Name 9", context.getValue("beans[2]/name"), "Modified <" + "beans[2]/name" + ">");
    }

    @Test
    void testSetPropertyValue() {
        // Simple property
        assertXPathSetValue(context, "int", Integer.valueOf(2));
        // Simple property with conversion from string
        assertXPathSetValue(context, "int", "3", Integer.valueOf(3));
        // Simple property with conversion from array
        assertXPathSetValue(context, "int", new int[] { 4 }, Integer.valueOf(4));
        // Attribute (which is the same as a child for beans
        assertXPathSetValue(context, "@int", Integer.valueOf(10));
    }

    @Test
    void testUnion() {
        // Union - note corrected document order
        assertXPathValueIterator(context, "integers | beans[1]/strings",
                list("String 1", "String 2", "String 3", Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)));
        assertXPathValue(context, "count((integers | beans[1]/strings)[contains(., '1')])", Double.valueOf(2));
        assertXPathValue(context, "count((integers | beans[1]/strings)[name(.) = 'strings'])", Double.valueOf(3));
        // Note that the following is different from "integer[2]" -
        // it is a filter expression
        assertXPathValue(context, "(integers)[2]", Integer.valueOf(2));
    }
}