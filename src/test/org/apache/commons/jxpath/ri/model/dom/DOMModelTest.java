/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/ri/model/dom/DOMModelTest.java,v 1.8 2003/10/09 21:31:44 rdonkin Exp $
 * $Revision: 1.8 $
 * $Date: 2003/10/09 21:31:44 $
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

package org.apache.commons.jxpath.ri.model.dom;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.ri.model.XMLModelTestCase;
import org.apache.commons.jxpath.xml.DocumentContainer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tests JXPath with DOM
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.8 $ $Date: 2003/10/09 21:31:44 $
 */

public class DOMModelTest extends XMLModelTestCase {
    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public DOMModelTest(String name) {
        super(name);
    }

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(DOMModelTest.class));
    }

    protected String getModel() {
        return DocumentContainer.MODEL_DOM;
    }

    protected AbstractFactory getAbstractFactory() {
        return new TestDOMFactory();
    }

    protected String getXMLSignature(
        Object node,
        boolean elements,
        boolean attributes,
        boolean text,
        boolean pi) 
    {
        StringBuffer buffer = new StringBuffer();
        appendXMLSignature(buffer, node, elements, attributes, text, pi);
        return buffer.toString();
    }

    private void appendXMLSignature(
        StringBuffer buffer,
        Object object,
        boolean elements,
        boolean attributes,
        boolean text,
        boolean pi) 
    {
        Node node = (Node) object;
        int type = node.getNodeType();
        switch (type) {
            case Node.DOCUMENT_NODE :
                buffer.append("<D>");
                appendXMLSignature(
                    buffer,
                    node.getChildNodes(),
                    elements,
                    attributes,
                    text,
                    pi);
                buffer.append("</D");
                break;

            case Node.ELEMENT_NODE :
                String tag = elements ? ((Element) node).getTagName() : "E";
                buffer.append("<");
                buffer.append(tag);
                buffer.append(">");
                appendXMLSignature(
                    buffer,
                    node.getChildNodes(),
                    elements,
                    attributes,
                    text,
                    pi);
                buffer.append("</");
                buffer.append(tag);
                buffer.append(">");
                break;

            case Node.TEXT_NODE :
            case Node.CDATA_SECTION_NODE :
                if (text) {
                    String string = node.getNodeValue();
                    string = string.replace('\n', '=');
                    buffer.append(string);
                }
                break;
        }
    }

    private void appendXMLSignature(
        StringBuffer buffer,
        NodeList children,
        boolean elements,
        boolean attributes,
        boolean text,
        boolean pi) 
    {
        for (int i = 0; i < children.getLength(); i++) {
            appendXMLSignature(
                buffer,
                children.item(i),
                elements,
                attributes,
                text,
                pi);
        }
    }
}