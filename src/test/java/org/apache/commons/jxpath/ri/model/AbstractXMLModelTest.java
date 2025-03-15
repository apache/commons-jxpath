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

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.AbstractJXPathTest;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.xml.DocumentContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Abstract superclass for pure XPath 1.0.  Subclasses
 * apply the same XPaths to contexts using different models:
 * DOM, JDOM etc.
 */

public abstract class AbstractXMLModelTest extends AbstractJXPathTest {
    protected JXPathContext context;

    @Override
    @BeforeEach
    public void setUp() {
        if (context == null) {
            final DocumentContainer docCtr = createDocumentContainer();
            context = createContext();
            final Variables vars = context.getVariables();
            vars.declareVariable("document", docCtr.getValue());
            vars.declareVariable("container", docCtr);
            vars.declareVariable("element", context.getPointer("vendor/location/address/street").getNode());
        }
    }

    protected abstract String getModel();

    protected DocumentContainer createDocumentContainer() {
        return new DocumentContainer(
                AbstractJXPathTest.class.getResource("Vendor.xml"),
                getModel());
    }

    protected abstract AbstractFactory getAbstractFactory();

    protected JXPathContext createContext() {
        final JXPathContext context =
            JXPathContext.newContext(createDocumentContainer());
        context.setFactory(getAbstractFactory());
        context.registerNamespace("product", "productNS");
        return context;
    }

    /**
     * An XML signature is used to determine if we have the right result
     * after a modification of XML by JXPath.  It is basically a piece
     * of simplified XML.
     */
    protected abstract String getXMLSignature(
        Object node,
        boolean elements,
        boolean attributes,
        boolean text,
        boolean pi);

    protected void assertXMLSignature(
        final JXPathContext context,
        final String path,
        final String signature,
        final boolean elements,
        final boolean attributes,
        final boolean text,
        final boolean pi)
    {
        final Object node = context.getPointer(path).getNode();
        final String sig = getXMLSignature(node, elements, attributes, text, pi);
        assertEquals(signature, sig, "XML Signature mismatch: ");
    }

    @Test
    public void testDocumentOrder() {
        assertDocumentOrder(
            context,
            "vendor/location",
            "vendor/location/address/street",
            -1);

        assertDocumentOrder(
            context,
            "vendor/location[@id = '100']",
            "vendor/location[@id = '101']",
            -1);

        assertDocumentOrder(
            context,
            "vendor//price:amount",
            "vendor/location",
            1);
    }

    @Test
    public void testSetValue() {
        assertXPathSetValue(
            context,
            "vendor/location[@id = '100']",
            "New Text");

        assertXMLSignature(
            context,
            "vendor/location[@id = '100']",
            "<E>New Text</E>",
            false,
            false,
            true,
            false);

        assertXPathSetValue(
            context,
            "vendor/location[@id = '101']",
            "Replacement Text");

        assertXMLSignature(
            context,
            "vendor/location[@id = '101']",
            "<E>Replacement Text</E>",
            false,
            false,
            true,
            false);
    }

    /**
     * Test JXPathContext.createPath() with various arguments
     */
    @Test
    public void testCreatePath() {
        // Create a DOM element
        assertXPathCreatePath(
            context,
            "/vendor[1]/location[3]",
            "",
            "/vendor[1]/location[3]");

        // Create a DOM element with contents
        assertXPathCreatePath(
            context,
            "/vendor[1]/location[3]/address/street",
            "",
            "/vendor[1]/location[3]/address[1]/street[1]");

        // Create a DOM attribute
        assertXPathCreatePath(
            context,
            "/vendor[1]/location[2]/@manager",
            "",
            "/vendor[1]/location[2]/@manager");

        assertXPathCreatePath(
            context,
            "/vendor[1]/location[1]/@name",
            "local",
            "/vendor[1]/location[1]/@name");

         assertXPathCreatePathAndSetValue(
            context,
            "/vendor[1]/location[4]/@manager",
            "",
            "/vendor[1]/location[4]/@manager");

         context.registerNamespace("price", "priceNS");

         // Create a DOM element
         assertXPathCreatePath(
             context,
             "/vendor[1]/price:foo/price:bar",
             "",
             "/vendor[1]/price:foo[1]/price:bar[1]");
    }

    /**
     * Test JXPath.createPathAndSetValue() with various arguments
     */
    @Test
    public void testCreatePathAndSetValue() {
        // Create a XML element
        assertXPathCreatePathAndSetValue(
            context,
            "vendor/location[3]",
            "",
            "/vendor[1]/location[3]");

        // Create a DOM element with contents
        assertXPathCreatePathAndSetValue(
            context,
            "vendor/location[3]/address/street",
            "Lemon Circle",
            "/vendor[1]/location[3]/address[1]/street[1]");

        // Create an attribute
        assertXPathCreatePathAndSetValue(
            context,
            "vendor/location[2]/@manager",
            "John Doe",
            "/vendor[1]/location[2]/@manager");

        assertXPathCreatePathAndSetValue(
            context,
            "vendor/location[1]/@manager",
            "John Doe",
            "/vendor[1]/location[1]/@manager");

        assertXPathCreatePathAndSetValue(
            context,
            "/vendor[1]/location[4]/@manager",
            "James Dow",
            "/vendor[1]/location[4]/@manager");

        assertXPathCreatePathAndSetValue(
            context,
            "vendor/product/product:name/attribute::price:language",
            "English",
            "/vendor[1]/product[1]/product:name[1]/@price:language");

        context.registerNamespace("price", "priceNS");

        // Create a DOM element
        assertXPathCreatePathAndSetValue(
            context,
            "/vendor[1]/price:foo/price:bar",
            "123.20",
            "/vendor[1]/price:foo[1]/price:bar[1]");
    }

    /**
     * Test JXPathContext.removePath() with various arguments
     */
    @Test
    public void testRemovePath() {
        // Remove XML nodes
        context.removePath("vendor/location[@id = '101']//street/text()");
        assertEquals(
            "",
            context.getValue("vendor/location[@id = '101']//street"),
            "Remove DOM text");

        context.removePath("vendor/location[@id = '101']//street");
        assertEquals(
            Double.valueOf(0),
            context.getValue("count(vendor/location[@id = '101']//street)"),
            "Remove DOM element");

        context.removePath("vendor/location[@id = '100']/@name");
        assertEquals(
            Double.valueOf(0),
            context.getValue("count(vendor/location[@id = '100']/@name)"),
            "Remove DOM attribute");
    }

    @Test
    public void testID() {
        context.setIdentityManager((context, id) -> {
            NodePointer ptr = (NodePointer) context.getPointer("/");
            ptr = ptr.getValuePointer(); // Unwrap the container
            return ptr.getPointerByID(context, id);
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

    @Test
    public void testAxisChild() {
        assertXPathValue(
            context,
            "vendor/location/address/street",
            "Orchard Road");

        // child:: - first child does not match, need to search
        assertXPathValue(
            context,
            "vendor/location/address/city",
            "Fruit Market");

        // local-name(qualified)
        assertXPathValue(
            context,
            "local-name(vendor/product/price:amount)",
            "amount");

        // local-name(non-qualified)
        assertXPathValue(context, "local-name(vendor/location)", "location");

        // name (qualified)
        assertXPathValue(
            context,
            "name(vendor/product/price:amount)",
            "value:amount");

        // name (non-qualified)
        assertXPathValue(
            context,
            "name(vendor/location)",
            "location");

        // namespace-uri (qualified)
        assertXPathValue(
            context,
            "namespace-uri(vendor/product/price:amount)",
            "priceNS");

        // default namespace does not affect search
        assertXPathValue(context, "vendor/product/prix", "934.99");

        assertXPathValue(context, "/vendor/contact[@name='jim']", "Jim");

        assertThrows(JXPathException.class, () -> {
            context.setLenient(false);
            context.getValue("/vendor/contact[@name='jane']");
        }, "No such value: /vendor/contact[@name='jim']");

        assertThrows(JXPathException.class, () -> {
            context.setLenient(false);
            context.getValue("/vendor/contact[@name='jane']/*");
        }, "No such value: /vendor/contact[@name='jane']/*");

        // child:: with a wildcard
        assertXPathValue(
            context,
            "count(vendor/product/price:*)",
            Double.valueOf(2));

        // child:: with the default namespace
        assertXPathValue(context, "count(vendor/product/*)", Double.valueOf(4));

        // child:: with a qualified name
        assertXPathValue(context, "vendor/product/price:amount", "45.95");

        // null default namespace
        context.registerNamespace("x", "temp");
        assertXPathValue(context, "vendor/x:pos//number", "109");
    }

    @Test
    public void testAxisChildIndexPredicate() {
        assertXPathValue(
            context,
            "vendor/location[2]/address/street",
            "Tangerine Drive");
    }

    @Test
    public void testAxisDescendant() {
        // descendant::
        assertXPathValue(context, "//street", "Orchard Road");

        // descendent:: with a namespace and wildcard
        assertXPathValue(context, "count(//price:*)", Double.valueOf(2));

        assertXPathValueIterator(context, "vendor//saleEnds", list("never"));

        assertXPathValueIterator(context, "vendor//promotion", list(""));

        assertXPathValueIterator(
            context,
            "vendor//saleEnds[../@stores = 'all']",
            list("never"));

        assertXPathValueIterator(
            context,
            "vendor//promotion[../@stores = 'all']",
            list(""));
    }
//
//    @Test
//    public void testAxisDescendantDocumentOrder() {
//        Iterator iter = context.iteratePointers("//*");
//        while (iter.hasNext()) {
//            System.err.println(iter.next());
//        }
//    }

    @Test
    public void testAxisParent() {
        // parent::
        assertXPathPointer(
            context,
            "//street/..",
            "/vendor[1]/location[1]/address[1]");

        // parent:: (note reverse document order)
        assertXPathPointerIterator(
            context,
            "//street/..",
            list(
                "/vendor[1]/location[2]/address[1]",
                "/vendor[1]/location[1]/address[1]"));

        // parent:: with a namespace and wildcard
        assertXPathValue(
            context,
            "vendor/product/price:sale/saleEnds/parent::price:*" + "/saleEnds",
            "never");
    }

    @Test
    public void testAxisFollowingSibling() {
        // following-sibling::
        assertXPathValue(
            context,
            "vendor/location[.//employeeCount = 10]/"
                + "following-sibling::location//street",
            "Tangerine Drive");

        // following-sibling:: produces the correct pointer
        assertXPathPointer(
            context,
            "vendor/location[.//employeeCount = 10]/"
                + "following-sibling::location//street",
            "/vendor[1]/location[2]/address[1]/street[1]");
    }

    @Test
    public void testAxisPrecedingSibling() {
        // preceding-sibling:: produces the correct pointer
        assertXPathPointer(
            context,
            "//location[2]/preceding-sibling::location//street",
            "/vendor[1]/location[1]/address[1]/street[1]");
    }

    @Test
    public void testAxisPreceding() {
        // preceding::
        assertXPathPointer(
                context,
                "//location[2]/preceding-sibling::location//street",
        "/vendor[1]/location[1]/address[1]/street[1]");
        assertXPathPointer(context, "//location[2]/preceding::*[1]", "/vendor[1]/location[1]/employeeCount[1]");
        assertXPathPointer(context, "//location[2]/preceding::node()[3]", "/vendor[1]/location[1]/employeeCount[1]/text()[1]");
        assertXPathPointer(context, "//location[2]/preceding::node()[4]", "/vendor[1]/location[1]/employeeCount[1]");
    }

    @Test
    public void testAxisAttribute() {
        // attribute::
        assertXPathValue(context, "vendor/location/@id", "100");

        // attribute:: produces the correct pointer
        assertXPathPointer(
            context,
            "vendor/location/@id",
            "/vendor[1]/location[1]/@id");

        // iterate over attributes
        assertXPathValueIterator(
            context,
            "vendor/location/@id",
            list("100", "101"));

        // Using different prefixes for the same namespace
        assertXPathValue(
            context,
            "vendor/product/price:amount/@price:discount",
            "10%");

        // namespace uri for an attribute
        assertXPathValue(
            context,
            "namespace-uri(vendor/product/price:amount/@price:discount)",
            "priceNS");

        // local name of an attribute
        assertXPathValue(
            context,
            "local-name(vendor/product/price:amount/@price:discount)",
            "discount");

        // name for an attribute
        assertXPathValue(
            context,
            "name(vendor/product/price:amount/@price:discount)",
            "price:discount");

        // attribute:: with the default namespace
        assertXPathValue(
            context,
            "vendor/product/price:amount/@discount",
            "20%");

        // namespace uri of an attribute with the default namespace
        assertXPathValue(
            context,
            "namespace-uri(vendor/product/price:amount/@discount)",
            "");

        // local name of an attribute with the default namespace
        assertXPathValue(
            context,
            "local-name(vendor/product/price:amount/@discount)",
            "discount");

        // name of an attribute with the default namespace
        assertXPathValue(
            context,
            "name(vendor/product/price:amount/@discount)",
            "discount");

        // attribute:: with a namespace and wildcard
        assertXPathValueIterator(
            context,
            "vendor/product/price:amount/@price:*",
            list("10%"));

        // attribute:: with a wildcard
        assertXPathValueIterator(
            context,
            "vendor/location[1]/@*",
            set("100", "", "local"));

        // attribute:: with default namespace and wildcard
        assertXPathValueIterator(
                context,
                "vendor/product/price:amount/@*",
                //use a set because DOM returns attrs sorted by name, JDOM by occurrence order:
                set("10%", "20%"));

        // attribute::node()
        assertXPathValueIterator(
                context,
                "vendor/product/price:amount/attribute::node()",
                //use a set because DOM returns attrs sorted by name, JDOM by occurrence order:
                set("10%", "20%"));

        // attribute:: select non-ns'd attributes only
        assertXPathValueIterator(
            context,
            "vendor/product/price:amount/@*[namespace-uri() = '']",
            list("20%"));

        // Empty attribute
        assertXPathValue(context, "vendor/location/@manager", "");

        // Missing attribute
        assertXPathValueLenient(context, "vendor/location/@missing", null);

        // Missing attribute with namespace
        assertXPathValueLenient(context, "vendor/location/@miss:missing", null);

        // Using attribute in a predicate
        assertXPathValue(
            context,
            "vendor/location[@id='101']//street",
            "Tangerine Drive");

        assertXPathValueIterator(
            context,
            "/vendor/location[1]/@*[name()!= 'manager']", list("100",
            "local"));
    }

    @Test
    public void testAxisNamespace() {
        // namespace::
        assertXPathValueAndPointer(
            context,
            "vendor/product/prix/namespace::price",
            "priceNS",
            "/vendor[1]/product[1]/prix[1]/namespace::price");

        // namespace::*
        assertXPathValue(
            context,
            "count(vendor/product/namespace::*)",
            Double.valueOf(3));

        // name of namespace
        assertXPathValue(
            context,
            "name(vendor/product/prix/namespace::price)",
            "price");

        // local name of namespace
        assertXPathValue(
            context,
            "local-name(vendor/product/prix/namespace::price)",
            "price");
    }

    @Test
    public void testAxisAncestor() {
        // ancestor::
        assertXPathValue(
            context,
            "vendor/product/price:sale/saleEnds/"
                + "ancestor::price:sale/saleEnds",
            "never");

        // ancestor:: with a wildcard
        assertXPathValue(
            context,
            "vendor/product/price:sale/saleEnds/ancestor::price:*"
                + "/saleEnds",
            "never");
    }

    @Test
    public void testAxisAncestorOrSelf() {
        // ancestor-or-self::
        assertXPathValue(
            context,
            "vendor/product/price:sale/"
                + "ancestor-or-self::price:sale/saleEnds",
            "never");
    }

    @Test
    public void testAxisFollowing() {
        assertXPathValueIterator(
            context,
            "vendor/contact/following::location//street",
            list("Orchard Road", "Tangerine Drive"));

        // following:: with a namespace
        assertXPathValue(
            context,
            "//location/following::price:sale/saleEnds",
            "never");
        assertXPathPointer(context, "//location[2]/following::node()[2]", "/vendor[1]/product[1]");
    }

    @Test
    public void testAxisSelf() {
        // self:: with a namespace
        assertXPathValue(
            context,
            "//price:sale/self::price:sale/saleEnds",
            "never");

        // self:: with an unmatching name
        assertXPathValueLenient(context, "//price:sale/self::x/saleEnds", null);
    }

    @Test
    public void testNodeTypeComment() {
        // comment()
        assertXPathValue(
            context,
            "//product/comment()",
            "We are not buying this product, ever");
    }

    @Test
    public void testNodeTypeText() {
        // text()
        //Note that this is questionable as the XPath spec tells us "." is short for self::node() and text() is by definition _not_ a node:
        assertXPathValue(
            context,
            "//product/text()[. != '']",
            "We love this product.");

        // text() pointer
        assertXPathPointer(
            context,
            "//product/text()",
            "/vendor[1]/product[1]/text()[1]");

    }

    @Test
    public void testNodeTypeProcessingInstruction() {
        // processing-instruction() without an argument
        assertXPathValue(
            context,
            "//product/processing-instruction()",
            "do not show anybody");

        // processing-instruction() with an argument
        assertXPathValue(
            context,
            "//product/processing-instruction('report')",
            "average only");

        // processing-instruction() pointer without an argument
        assertXPathPointer(
            context,
            "//product/processing-instruction('report')",
            "/vendor[1]/product[1]/processing-instruction('report')[1]");

        // processing-instruction name
        assertXPathValue(
            context,
            "name(//product/processing-instruction()[1])",
            "security");
    }

    @Test
    public void testLang() {
        // xml:lang built-in attribute
        assertXPathValue(context, "//product/prix/@xml:lang", "fr");

        // lang() used the built-in xml:lang attribute
        assertXPathValue(context, "//product/prix[lang('fr')]", "934.99");

        // Default language
        assertXPathValue(
            context,
            "//product/price:sale[lang('en')]/saleEnds",
            "never");
    }

    @Test
    public void testDocument() {
        assertXPathValue(
            context,
            "$document/vendor/location[1]//street",
            "Orchard Road");

        assertXPathPointer(
            context,
            "$document/vendor/location[1]//street",
            "$document/vendor[1]/location[1]/address[1]/street[1]");

        assertXPathValue(context, "$document/vendor//street", "Orchard Road");
    }

    @Test
    public void testContainer() {
        assertXPathValue(context, "$container/vendor//street", "Orchard Road");

        assertXPathValue(context, "$container//street", "Orchard Road");

        assertXPathPointer(
            context,
            "$container//street",
            "$container/vendor[1]/location[1]/address[1]/street[1]");

        // Conversion to number
        assertXPathValue(
            context,
            "number(vendor/location/employeeCount)",
            Double.valueOf(10));
    }

    @Test
    public void testElementInVariable() {
        assertXPathValue(context, "$element", "Orchard Road");
    }

    @Test
    public void testTypeConversions() {
        // Implicit conversion to number
        assertXPathValue(
            context,
            "vendor/location/employeeCount + 1",
            Double.valueOf(11));

        // Implicit conversion to boolean
        assertXPathValue(
            context,
            "vendor/location/employeeCount and true()",
            Boolean.TRUE);
    }

    @Test
    public void testBooleanFunction() {
        assertXPathValue(
            context,
            "boolean(vendor//saleEnds[../@stores = 'all'])",
            Boolean.TRUE);

        assertXPathValue(
            context,
            "boolean(vendor//promotion[../@stores = 'all'])",
            Boolean.TRUE);

        assertXPathValue(
            context,
            "boolean(vendor//promotion[../@stores = 'some'])",
            Boolean.FALSE);
    }

    @Test
    public void testFunctionsLastAndPosition() {
        assertXPathPointer(
                context,
                "vendor//location[last()]",
                "/vendor[1]/location[2]");
    }

    @Test
    public void testNamespaceMapping() {
        context.registerNamespace("rate", "priceNS");
        context.registerNamespace("goods", "productNS");

        assertEquals(
                "priceNS",
                context.getNamespaceURI("price"),
                "Context node namespace resolution");

        assertEquals(
                "priceNS",
                context.getNamespaceURI("rate"),
                "Registered namespace resolution");

        // child:: with a namespace and wildcard
        assertXPathValue(context,
                "count(vendor/product/rate:*)",
                Double.valueOf(2));

        assertXPathValue(context,
                "vendor[1]/product[1]/rate:amount[1]/@rate:discount", "10%");
        assertXPathValue(context,
                "vendor[1]/product[1]/rate:amount[1]/@price:discount", "10%");
        assertXPathValue(context,
                "vendor[1]/product[1]/price:amount[1]/@rate:discount", "10%");
        assertXPathValue(context,
                "vendor[1]/product[1]/price:amount[1]/@price:discount", "10%");

        // Preference for externally registered namespace prefix
        assertXPathValueAndPointer(context,
                "//product:name",
                "Box of oranges",
                "/vendor[1]/product[1]/goods:name[1]");

        // Same, but with a child context
        final JXPathContext childCtx =
            JXPathContext.newContext(context, context.getContextBean());
        assertXPathValueAndPointer(childCtx,
                "//product:name",
                "Box of oranges",
                "/vendor[1]/product[1]/goods:name[1]");

        // Same, but with a relative context
        final JXPathContext relativeCtx =
            context.getRelativeContext(context.getPointer("/vendor"));
        assertXPathValueAndPointer(relativeCtx,
                "product/product:name",
                "Box of oranges",
                "/vendor[1]/product[1]/goods:name[1]");
    }

    @Test
    public void testUnion() {
        assertXPathValue(context, "/vendor[1]/contact[1] | /vendor[1]/contact[4]", "John");
        assertXPathValue(context, "/vendor[1]/contact[4] | /vendor[1]/contact[1]", "John");
    }

    @Test
    public void testNodes() {
        final Pointer pointer = context.getPointer("/vendor[1]/contact[1]");
        assertNotEquals(pointer.getNode(), pointer.getValue());
    }
}