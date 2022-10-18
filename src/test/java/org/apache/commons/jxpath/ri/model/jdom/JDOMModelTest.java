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
package org.apache.commons.jxpath.ri.model.jdom;

import java.util.List;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.XMLModelTestCase;
import org.apache.commons.jxpath.xml.DocumentContainer;

import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;

/**
 * Tests JXPath with JDOM
 */
public class JDOMModelTest extends XMLModelTestCase {

    protected String getModel() {
        return DocumentContainer.MODEL_JDOM;
    }
    
    public void testGetNode() {
        assertXPathNodeType(context, "/", Document.class);
        assertXPathNodeType(context, "/vendor/location", Element.class);
        assertXPathNodeType(context, "//location/@name", Attribute.class);
        assertXPathNodeType(context, "//vendor", Element.class); //bugzilla #38586
    }

    public void testGetElementDescendantOrSelf() {
        JXPathContext childContext = context.getRelativeContext(context.getPointer("/vendor"));
        assertTrue(childContext.getContextBean() instanceof Element);
        assertXPathNodeType(childContext, "//vendor", Element.class);
    }

    public void testID() {
        // id() is not supported by JDOM
    }

    protected AbstractFactory getAbstractFactory() {
        return new TestJDOMFactory();
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
        if (object instanceof Document) {
            buffer.append("<D>");
            appendXMLSignature(
                buffer,
                ((Document) object).getContent(),
                elements,
                attributes,
                text,
                pi);
            buffer.append("</D");
        }
        else if (object instanceof Element) {
            String tag = elements ? ((Element) object).getName() : "E";
            buffer.append("<");
            buffer.append(tag);
            buffer.append(">");
            appendXMLSignature(
                buffer,
                ((Element) object).getContent(),
                elements,
                attributes,
                text,
                pi);
            buffer.append("</");
            buffer.append(tag);
            buffer.append(">");
        }
        else if (object instanceof Text || object instanceof CDATA) {
            if (text) {
                String string = ((Text) object).getText();
                string = string.replace('\n', '=');
                buffer.append(string);
            }
        }
    }

    private void appendXMLSignature(
        StringBuffer buffer,
        List children,
        boolean elements,
        boolean attributes,
        boolean text,
        boolean pi) 
    {
        for (int i = 0; i < children.size(); i++) {
            appendXMLSignature(
                buffer,
                children.get(i),
                elements,
                attributes,
                text,
                pi);
        }
    }
}
