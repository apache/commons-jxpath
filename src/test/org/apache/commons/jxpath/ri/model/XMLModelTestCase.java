/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/ri/model/XMLModelTestCase.java,v 1.16 2004/01/19 20:44:52 dmitri Exp $
 * $Revision: 1.16 $
 * $Date: 2004/01/19 20:44:52 $
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

package org.apache.commons.jxpath.ri.model;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.IdentityManager;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.xml.DocumentContainer;

/**
 * Abstract superclass for pure XPath 1.0.  Subclasses
 * apply the same XPaths to contexts using different models:
 * DOM, JDOM etc.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.16 $ $Date: 2004/01/19 20:44:52 $
 */

public abstract class XMLModelTestCase extends JXPathTestCase {
    private JXPathContext context;

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public XMLModelTestCase(String name) {
        super(name);
    }

    public void setUp() {
        if (context == null) {
            DocumentContainer docCtr = createDocumentContainer();
            context = createContext();
            Variables vars = context.getVariables();
            vars.declareVariable("document", docCtr.getValue());
            vars.declareVariable("container", docCtr);
            vars.declareVariable(
                "element",
                context.getPointer("vendor/location/address/street").getNode());
        }
    }

    protected abstract String getModel();

    protected DocumentContainer createDocumentContainer() {
        return new DocumentContainer(
                JXPathTestCase.class.getResource("Vendor.xml"),
                getModel());
    }
    
    protected abstract AbstractFactory getAbstractFactory();
        
    protected JXPathContext createContext() {
        JXPathContext context =
            JXPathContext.newContext(createDocumentContainer());
        context.setFactory(getAbstractFactory());
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
        JXPathContext context,
        String path,
        String signature,
        boolean elements,
        boolean attributes,
        boolean text,
        boolean pi) 
    {
        Object node = context.getPointer(path).getNode();
        String sig = getXMLSignature(node, elements, attributes, text, pi);
        assertEquals("XML Signature mismatch: ", signature, sig);
    }

    // ------------------------------------------------ Individual Test Methods

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
    }

    /**
     * Test JXPath.createPathAndSetValue() with various arguments
     */
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
            "vendor/product/name/attribute::price:language",
            "English",
            "/vendor[1]/product[1]/name[1]/@price:language");
    }

    /**
     * Test JXPathContext.removePath() with various arguments
     */
    public void testRemovePath() {
        // Remove XML nodes
        context.removePath("vendor/location[@id = '101']//street/text()");
        assertEquals(
            "Remove DOM text",
            "",
            context.getValue("vendor/location[@id = '101']//street"));

        context.removePath("vendor/location[@id = '101']//street");
        assertEquals(
            "Remove DOM element",
            new Double(0),
            context.getValue("count(vendor/location[@id = '101']//street)"));

        context.removePath("vendor/location[@id = '100']/@name");
        assertEquals(
            "Remove DOM attribute",
            new Double(0),
            context.getValue("count(vendor/location[@id = '100']/@name)"));
    }

    public void testID() {
        context.setIdentityManager(new IdentityManager() {
            public Pointer getPointerByID(JXPathContext context, String id) {
                NodePointer ptr = (NodePointer) context.getPointer("/");
                ptr = ptr.getValuePointer(); // Unwrap the container
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
            "priceNS:amount");

        // name (non-qualified)
        assertXPathValue(context, "name(vendor/location)", "location");

        // namespace-uri (qualified)
        assertXPathValue(
            context,
            "namespace-uri(vendor/product/price:amount)",
            "priceNS");

        // default namespace does not affect search
        assertXPathValue(context, "vendor/product/prix", "934.99");
        
        // child:: with a wildcard
        assertXPathValue(
            context,
            "count(vendor/product/price:*)",
            new Double(2));

        // child:: with a namespace and wildcard
        assertXPathValue(
            context,
            "count(vendor/product/value:*)",
            new Double(2));

        // child:: with the default namespace
        assertXPathValue(context, "count(vendor/product/*)", new Double(4));

        // child:: with a qualified name
        assertXPathValue(context, "vendor/product/price:amount", "45.95");
    }

    public void testAxisChildIndexPredicate() {
        assertXPathValue(
            context,
            "vendor/location[2]/address/street",
            "Tangerine Drive");
    }

    public void testAxisDescendant() {
        // descendant::
        assertXPathValue(context, "//street", "Orchard Road");

        // descendent:: with a namespace and wildcard
        assertXPathValue(context, "count(//price:*)", new Double(2));

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
    
//    public void testAxisDescendantDocumentOrder() {
//        Iterator iter = context.iteratePointers("//*");
//        while (iter.hasNext()) {
//            System.err.println(iter.next());
//        }
//    }

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

    public void testAxisPrecedingSibling() {
        // preceding-sibling:: produces the correct pointer
        assertXPathPointer(
            context,
            "//location[2]/preceding-sibling::location//street",
            "/vendor[1]/location[1]/address[1]/street[1]");
    }

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
        assertXPathValue(
            context,
            "vendor/product/value:amount/@value:discount",
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
            "priceNS:discount");

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
    }

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
            new Double(3));

        // name of namespace
        assertXPathValue(
            context,
            "name(vendor/product/prix/namespace::price)",
            "priceNS:price");

        // local name of namespace
        assertXPathValue(
            context,
            "local-name(vendor/product/prix/namespace::price)",
            "price");
    }

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

    public void testAxisAncestorOrSelf() {
        // ancestor-or-self::
        assertXPathValue(
            context,
            "vendor/product/price:sale/"
                + "ancestor-or-self::price:sale/saleEnds",
            "never");
    }

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
    }

    public void testAxisSelf() {
        // self:: with a namespace
        assertXPathValue(
            context,
            "//price:sale/self::price:sale/saleEnds",
            "never");

        // self:: with an unmatching name
        assertXPathValueLenient(context, "//price:sale/self::x/saleEnds", null);
    }

    public void testNodeTypeComment() {
        // comment()
        assertXPathValue(
            context,
            "//product/comment()",
            "We are not buying this product, ever");
    }

    public void testNodeTypeText() {
        // text()
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
            new Double(10));
    }

    public void testElementInVariable() {
        assertXPathValue(context, "$element", "Orchard Road");
    }

    public void testTypeConversions() {
        // Implicit conversion to number
        assertXPathValue(
            context,
            "vendor/location/employeeCount + 1",
            new Double(11));

        // Implicit conversion to boolean
        assertXPathValue(
            context,
            "vendor/location/employeeCount and true()",
            Boolean.TRUE);
    }

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
    
    public void testFunctionsLastAndPosition() {
        assertXPathPointer(
                context,
                "vendor//location[last()]",
                "/vendor[1]/location[2]");
    }

// TODO: either complete the external namespace functionality, or get rid of
// this test.
//
//    public void testExternalNamespace() {
//        if (isExternalNamespaceSupported()) {
//             DocumentContainer container = new DocumentContainer(
//                    XMLModelTestCase.class.getResource("ExternalNamespaceTest.xml"),
//                    getModel());
//            JXPathContext context = JXPathContext.newContext(container);             
//            NamespaceManager nsm = context.getNamespaceManager();
//            nsm.registerNamespace("quality", "qualityNS");
//            nsm.registerNamespace("money", "priceNS");
//            
//            assertXPathValueAndPointer(
//                    context,
//                    "//quality:color",
//                    "orange",
//                    "/vendor[1]/product[1]/quality:color[1]");
//            
//            // It is supposed to figure out that the prefixes "money" and
//            // "value" map to the same namespaceURI
//            assertXPathValueAndPointer(
//                    context,
//                    "//value:price",
//                    "1000.00",
//                    "/vendor[1]/product[1]/money:price[1]");
//            
//            assertXPathValue(
//                    context,
//                    "local-name(vendor/product/value:price)",
//                    "price");
//            
//            assertXPathValue(
//                    context,
//                    "name(vendor/product/quality:color)",
//                    "qualityNS:color");
//
//            assertXPathValue(
//                    context,
//                    "namespace-uri(vendor/product/value:price)",
//                    "priceNS");
//        }
//    }
}