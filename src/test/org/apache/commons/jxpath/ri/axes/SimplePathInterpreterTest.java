package org.apache.commons.jxpath.ri.axes;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.TestNull;
import org.apache.commons.jxpath.NestedTestBean;
import org.apache.commons.jxpath.ri.model.*;
import org.apache.commons.jxpath.ri.model.beans.*;
import org.apache.commons.jxpath.ri.model.dom.*;
import java.util.*;

public class SimplePathInterpreterTest extends TestCase {

    private TestBeanWithNode bean;
    private JXPathContext context;

    /**
     * Constructor for SimplePathInterpreterTest.
     */
    public SimplePathInterpreterTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SimplePathInterpreterTest.class);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        bean = TestBeanWithNode.createTestBeanWithDOM();
        HashMap submap = new HashMap();
        submap.put("key", new NestedTestBean("Name 9"));
        submap.put("strings", bean.getNestedBean().getStrings());
        bean.getList().add(new int[]{1, 2});
        bean.getList().add(bean.getVendor());
        bean.getMap().put("Key3",
            new Object[]{
                new NestedTestBean("some"),
                new Integer(2),
                bean.getVendor(),
                submap
            }
        );
        bean.getMap().put("Key4", bean.getVendor());
        bean.getMap().put("Key5", submap);
        bean.getMap().put("Key6", new Object[0]);
        context = JXPathContext.newContext(null, bean);
        context.setLenient(true);
        context.setFactory(new TestBeanFactory());
    }

    public void test_doStep_noPredicates_propertyOwner(){
        // Existing scalar property
        testValueAndPointer("/int",
                new Integer(1),
                "/int",
                "BbB");

        // self::
        testValueAndPointer("/./int",
                new Integer(1),
                "/int",
                "BbB");

        // Missing property
        testNullPointer("/foo",
                "/foo",
                "BnN");

        // existingProperty/existingScalarProperty
        testValueAndPointer("/nestedBean/int",
                new Integer(1),
                "/nestedBean/int",
                "BbBbB");

        // existingProperty/collectionProperty
        testValueAndPointer("/nestedBean/strings",
                bean.getNestedBean().getStrings(),
                "/nestedBean/strings",
                "BbBbC");

        // existingProperty/missingProperty
        testNullPointer("/nestedBean/foo",
                "/nestedBean/foo",
                "BbBnN");

        // map/missingProperty
        testNullPointer("/map/foo",
                "/map[@name='foo']",
                "BbDdN");

        // Existing property by search in collection
        testValueAndPointer("/list/int",
                new Integer(1),
                "/list[3]/int",
                "BbBbB");

        // Missing property by search in collection
        testNullPointer("/list/foo",
                "/list[1]/foo",
                "BbBnN");

        // existingProperty/missingProperty/missingProperty
        testNullPointer("/nestedBean/foo/bar",
                "/nestedBean/foo/bar",
                "BbBnNnN");

        // collection/existingProperty/missingProperty
        testNullPointer("/list/int/bar",
                "/list[3]/int/bar",
                "BbBbBnN");

        // collectionProperty/missingProperty/missingProperty
        testNullPointer("/list/foo/bar",
                "/list[1]/foo/bar",
                "BbBnNnN");

        // map/missingProperty/anotherStep
        testNullPointer("/map/foo/bar",
                "/map[@name='foo']/bar",
                "BbDdNnN");

        // Existing dynamic property
        testValueAndPointer("/map/Key1",
                "Value 1",
                "/map[@name='Key1']",
                "BbDdB");

        // collectionProperty
        testValueAndPointer("/integers",
                bean.getIntegers(),
                "/integers",
                "BbC");
    }

    public void test_doStep_noPredicates_standard(){
        // Existing DOM node
        testValueAndPointer("/vendor/location/address/city",
                "Fruit Market",
                "/vendor/location[2]/address[1]/city[1]",
                "BbMMMM");

        // Missing DOM node
        testNullPointer("/vendor/location/address/pity",
                "/vendor/location[1]/address[1]/pity",
                "BbMMMnN");

        // Missing DOM node inside a missing element
        testNullPointer("/vendor/location/address/itty/bitty",
                "/vendor/location[1]/address[1]/itty/bitty",
                "BbMMMnNnN");

        // Missing DOM node by search for the best match
        testNullPointer("/vendor/location/address/city/pretty",
                "/vendor/location[2]/address[1]/city[1]/pretty",
                "BbMMMMnN");
    }

    public void test_doStep_predicates_propertyOwner(){
        // missingProperty[@name=foo]
        testNullPointer("/foo[@name='foo']",
                "/foo[@name='foo']",
                "BnNnN");

        // missingProperty[index]
        testNullPointer("/foo[3]",
                "/foo[3]",
                "BnN");
    }

    public void test_doStep_predicates_standard(){
        // Looking for an actual XML attribute called "name"
        // nodeProperty/name[@name=value]
        testValueAndPointer("/vendor/contact[@name='jack']",
                "Jack",
                "/vendor/contact[2]",
                "BbMM");

        // Indexing in XML
        testValueAndPointer("/vendor/contact[2]",
                "Jack",
                "/vendor/contact[2]",
                "BbMM");

        // Indexing in XML, no result
        testNullPointer("/vendor/contact[5]",
                "/vendor/contact[5]",
                "BbMnN");

        // Combination of search by name and indexing in XML
        testValueAndPointer("/vendor/contact[@name='jack'][2]",
                "Jack Black",
                "/vendor/contact[4]",
                "BbMM");

        // Combination of search by name and indexing in XML
        testValueAndPointer("/vendor/contact[@name='jack'][2]",
                "Jack Black",
                "/vendor/contact[4]",
                "BbMM");
    }

    public void test_doPredicate_name(){
        // existingProperty[@name=existingProperty]
        testValueAndPointer("/nestedBean[@name='int']",
                new Integer(1),
                "/nestedBean/int",
                "BbBbB");

        // /self::node()[@name=existingProperty]
        testValueAndPointer("/.[@name='int']",
                new Integer(1),
                "/int",
                "BbB");

        // dynamicProperty[@name=existingProperty]
        testValueAndPointer("/map[@name='Key1']",
                "Value 1",
                "/map[@name='Key1']",
                "BbDdB");

        // existingProperty[@name=collectionProperty]
        testValueAndPointer("/nestedBean[@name='strings']",
                bean.getNestedBean().getStrings(),
                "/nestedBean/strings",
                "BbBbC");

        // existingProperty[@name=missingProperty]
        testNullPointer("/nestedBean[@name='foo']",
                "/nestedBean[@name='foo']",
                "BbBnN");

        // map[@name=collectionProperty]
        testValueAndPointer("/map[@name='Key3']",
                bean.getMap().get("Key3"),
                "/map[@name='Key3']",
                "BbDdC");

        // map[@name=missingProperty]
        testNullPointer("/map[@name='foo']",
                "/map[@name='foo']",
                "BbDdN");

        // collectionProperty[@name=...] (find node)
        testValueAndPointer("/list[@name='fruitco']",
                context.getValue("/vendor"),
                "/list[5]",
                "BbCM");

        // collectionProperty[@name=...] (find map entry)
        testValueAndPointer("/map/Key3[@name='key']/name",
                "Name 9",
                "/map[@name='Key3'][4][@name='key']/name",
                "BbDdCDdBbB");

        // map/collectionProperty[@name...]
        testValueAndPointer("map/Key3[@name='fruitco']",
                context.getValue("/vendor"),
                "/map[@name='Key3'][3]",
                "BbDdCM");

        // Bean property -> DOM Node, name match
        testValueAndPointer("/vendor[@name='fruitco']",
                context.getValue("/vendor"),
                "/vendor",
                "BbM");

        // Bean property -> DOM Node, name mismatch
        testNullPointer("/vendor[@name='foo']",
                "/vendor[@name='foo']",
                "BbMnN");

        testNullPointer("/vendor[@name='foo'][3]",
                "/vendor[@name='foo'][3]",
                "BbMnN");

        // existingProperty(bean)[@name=missingProperty]/anotherStep
        testNullPointer("/nestedBean[@name='foo']/bar",
                "/nestedBean[@name='foo']/bar",
                "BbBnNnN");

        // map[@name=missingProperty]/anotherStep
        testNullPointer("/map[@name='foo']/bar",
                "/map[@name='foo']/bar",
                "BbDdNnN");

        // existingProperty(node)[@name=missingProperty]/anotherStep
        testNullPointer("/vendor[@name='foo']/bar",
                "/vendor[@name='foo']/bar",
                "BbMnNnN");

        // existingProperty(node)[@name=missingProperty][index]/anotherStep
        testNullPointer("/vendor[@name='foo'][3]/bar",
                "/vendor[@name='foo'][3]/bar",
                "BbMnNnN");

        // Existing dynamic property + existing property
        testValueAndPointer("/map[@name='Key2'][@name='name']",
                "Name 6",
                "/map[@name='Key2']/name",
                "BbDdBbB");

        // Existing dynamic property + existing property + index
        testValueAndPointer("/map[@name='Key2'][@name='strings'][2]",
                "String 2",
                "/map[@name='Key2']/strings[2]",
                "BbDdBbB");

        // bean/map/map/property
        testValueAndPointer("map[@name='Key5'][@name='key']/name",
                "Name 9",
                "/map[@name='Key5'][@name='key']/name",
                "BbDdDdBbB");

        testNullPointer("map[@name='Key2'][@name='foo']",
                "/map[@name='Key2'][@name='foo']",
                "BbDdBnN");

        testNullPointer("map[@name='Key2'][@name='foo'][@name='bar']",
                "/map[@name='Key2'][@name='foo'][@name='bar']",
                "BbDdBnNnN");

        // bean/map/node
        testValueAndPointer("map[@name='Key4'][@name='fruitco']",
                context.getValue("/vendor"),
                "/map[@name='Key4']",
                "BbDdM");
    }

    public void test_doPredicates_standard(){
        // bean/map/collection/node
        testValueAndPointer("map[@name='Key3'][@name='fruitco']",
                context.getValue("/vendor"),
                "/map[@name='Key3'][3]",
                "BbDdCM");

        // bean/map/collection/missingNode
        testNullPointer("map[@name='Key3'][@name='foo']",
                "/map[@name='Key3'][4][@name='foo']",
                "BbDdCDdN");

        // bean/map/node
        testValueAndPointer("map[@name='Key4'][@name='fruitco']",
                context.getValue("/vendor"),
                "/map[@name='Key4']",
                "BbDdM");

        // bean/map/emptyCollection[@name=foo]
        testNullPointer("map[@name='Key6'][@name='fruitco']",
                "/map[@name='Key6'][@name='fruitco']",
                "BbDdCnN");

        // bean/node[@name=foo][index]
        testValueAndPointer("/vendor/contact[@name='jack'][2]",
                "Jack Black",
                "/vendor/contact[4]",
                "BbMM");

        // bean/node[@name=foo][missingIndex]
        testNullPointer("/vendor/contact[@name='jack'][5]",
                "/vendor/contact[@name='jack'][5]",
                "BbMnNnN");

        // bean/node/.[@name=foo][index]
        testValueAndPointer("/vendor/contact/.[@name='jack']",
                "Jack",
                "/vendor/contact[2]",
                "BbMM");
    }

    public void test_doPredicate_index(){
        // Existing dynamic property + existing property + index
        testValueAndPointer("/map[@name='Key2'][@name='strings'][2]",
                "String 2",
                "/map[@name='Key2']/strings[2]",
                "BbDdBbB");

        // existingProperty[@name=collectionProperty][index]
        testValueAndPointer("/nestedBean[@name='strings'][2]",
                bean.getNestedBean().getStrings()[1],
                "/nestedBean/strings[2]",
                "BbBbB");

        // existingProperty[@name=missingProperty][index]
        testNullPointer("/nestedBean[@name='foo'][3]",
                "/nestedBean[@name='foo'][3]",
                "BbBnN");

        // existingProperty[@name=collectionProperty][missingIndex]
        testNullPointer("/nestedBean[@name='strings'][5]",
                "/nestedBean/strings[5]",
                "BbBbEN");

        // map[@name=collectionProperty][index]
        testValueAndPointer("/map[@name='Key3'][2]",
                new Integer(2),
                "/map[@name='Key3'][2]",
                "BbDdB");

        // map[@name=collectionProperty][missingIndex]
        testNullPointer("/map[@name='Key3'][5]",
                "/map[@name='Key3'][5]",
                "BbDdEN");

        // map[@name=collectionProperty][missingIndex]/property
        testNullPointer("/map[@name='Key3'][5]/foo",
                "/map[@name='Key3'][5]/foo",
                "BbDdENnN");

        // map[@name=map][@name=collection][index]
        testValueAndPointer("/map[@name='Key5'][@name='strings'][2]",
                "String 2",
                "/map[@name='Key5'][@name='strings'][2]",
                "BbDdDdB");

        // map[@name=map][@name=collection][missingIndex]
        testNullPointer("/map[@name='Key5'][@name='strings'][5]",
                "/map[@name='Key5'][@name='strings'][5]",
                "BbDdDdEN");

        // Existing dynamic property + indexing
        testValueAndPointer("/map[@name='Key3'][2]",
                new Integer(2),
                "/map[@name='Key3'][2]",
                "BbDdB");

        // Existing dynamic property + indexing
        testValueAndPointer("/map[@name='Key3'][1]/name",
                "some",
                "/map[@name='Key3'][1]/name",
                "BbDdBbB");

        // map[@name=missingProperty][index]
        testNullPointer("/map[@name='foo'][3]",
                "/map[@name='foo'][3]",
                "BbDdEN");

        // collectionProperty[index]
        testValueAndPointer("/integers[2]",
                new Integer(2),
                "/integers[2]",
                "BbB");

        // existingProperty/collectionProperty[index]
        testValueAndPointer("/nestedBean/strings[2]",
                bean.getNestedBean().getStrings()[1],
                "/nestedBean/strings[2]",
                "BbBbB");

        // existingProperty[index]/existingProperty
        testValueAndPointer("/list[3]/int",
                new Integer(1),
                "/list[3]/int",
                "BbBbB");

        // existingProperty[missingIndex]
        testNullPointer("/list[6]",
                "/list[6]",
                "BbEN");

        // existingProperty/missingProperty[index]
        testNullPointer("/nestedBean/foo[3]",
                "/nestedBean/foo[3]",
                "BbBnN");

        // map[@name=missingProperty][index]
        testNullPointer("/map/foo[3]",
                "/map[@name='foo'][3]",
                "BbDdEN");

        // existingProperty/collectionProperty[missingIndex]
        testNullPointer("/nestedBean/strings[5]",
                "/nestedBean/strings[5]",
                "BbBbEN");

        // map/collectionProperty[missingIndex]/property
        testNullPointer("/map/Key3[5]/foo",
                "/map[@name='Key3'][5]/foo",
                "BbDdENnN");

        // map[@name=map]/collection[index]
        testValueAndPointer("/map[@name='Key5']/strings[2]",
                "String 2",
                "/map[@name='Key5'][@name='strings'][2]",
                "BbDdDdB");

        // map[@name=map]/collection[missingIndex]
        testNullPointer("/map[@name='Key5']/strings[5]",
                "/map[@name='Key5'][@name='strings'][5]",
                "BbDdDdEN");

        // scalarPropertyAsCollection[index]
        testValueAndPointer("/int[1]",
                new Integer(1),
                "/int",
                "BbB");

        // scalarPropertyAsCollection[index]
        testValueAndPointer(".[1]/int",
                new Integer(1),
                "/int",
                "BbB");
    }

    public void testInterpretExpressionPath(){
        context.getVariables().declareVariable("array", new String[]{"Value1"});
        context.getVariables().declareVariable("testnull", new TestNull());

        testNullPointer("$testnull/nothing[2]",
                "$testnull/nothing[2]",
                "VBbEN");
    }

    private void testValueAndPointer(
            String path, Object expectedValue, String expectedPath,
            String expectedSignature)
    {
        Object value = context.getValue(path);
        assertEquals("Checking value: " + path, expectedValue, value);

        Pointer pointer = context.getPointer(path);
        assertEquals("Checking pointer: " + path,
                expectedPath, pointer.toString());

        assertEquals("Checking signature: " + path,
                expectedSignature, pointerSignature(pointer));
    }


    private void testNullPointer(String path, String expectedPath,
            String expectedSignature)
    {
        Pointer pointer = context.getPointer(path);
        assertNotNull("Null path exists: " + path,
                    pointer);
        assertTrue("Null path is null: " + path,
                    !((NodePointer)pointer).isActual());
        assertEquals("Null path as path: " + path,
                    expectedPath, pointer.asPath());
        assertEquals("Checking Signature: " + path,
                expectedSignature, pointerSignature(pointer));
    }

    /**
     * Since we need to test the internal Signature of a pointer,
     * we will get a signature which will contain a single character
     * per pointer in the chain, representing that pointer's type.
     */
    private String pointerSignature(Pointer pointer){
        if (pointer == null){
            return "";
        }

        char type = '?';
        if (pointer instanceof NullPointer){                 type = 'N'; }
        else if (pointer instanceof NullPropertyPointer){    type = 'n'; }
        else if (pointer instanceof NullElementPointer){     type = 'E'; }
        else if (pointer instanceof VariablePointer){        type = 'V'; }
        else if (pointer instanceof CollectionPointer){      type = 'C'; }
        else if (pointer instanceof BeanPointer){            type = 'B'; }
        else if (pointer instanceof BeanPropertyPointer){    type = 'b'; }
        else if (pointer instanceof DynamicPointer){         type = 'D'; }
        else if (pointer instanceof DynamicPropertyPointer){ type = 'd'; }
        else if (pointer instanceof DOMNodePointer){         type = 'M'; }
        else {
            System.err.println("UNKNOWN TYPE: " + pointer.getClass());
        }
        return pointerSignature(((NodePointer)pointer).getParent()) + type;
    }
}

