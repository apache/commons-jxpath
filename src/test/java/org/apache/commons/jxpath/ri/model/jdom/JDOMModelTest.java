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
import org.apache.commons.jxpath.ri.model.AbstractXMLModelTest;
import org.apache.commons.jxpath.xml.DocumentContainer;

import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Tests JXPath with JDOM
 */
public class JDOMModelTest extends AbstractXMLModelTest {

    @Override
    protected String getModel() {
        return DocumentContainer.MODEL_JDOM;
    }

    @Test
    public void testGetNode() {
        assertXPathNodeType(context, "/", Document.class);
        assertXPathNodeType(context, "/vendor/location", Element.class);
        assertXPathNodeType(context, "//location/@name", Attribute.class);
        assertXPathNodeType(context, "//vendor", Element.class); //bugzilla #38586
    }

    @Test
    public void testGetElementDescendantOrSelf() {
        final JXPathContext childContext = context.getRelativeContext(context.getPointer("/vendor"));
        assertInstanceOf(Element.class, childContext.getContextBean());
        assertXPathNodeType(childContext, "//vendor", Element.class);
    }

    @Override
    @Test
    @Disabled("id() is not supported by JDOM")
    public void testID() {
        // id() is not supported by JDOM
    }

    @Override
    protected AbstractFactory getAbstractFactory() {
        return new TestJDOMFactory();
    }

    @Override
    protected String getXMLSignature(
        final Object node,
        final boolean elements,
        final boolean attributes,
        final boolean text,
        final boolean pi)
    {
        final StringBuilder buffer = new StringBuilder();
        appendXMLSignature(buffer, node, elements, attributes, text, pi);
        return buffer.toString();
    }

    private void appendXMLSignature(
        final StringBuilder buffer,
        final Object object,
        final boolean elements,
        final boolean attributes,
        final boolean text,
        final boolean pi)
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
            final String tag = elements ? ((Element) object).getName() : "E";
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
        } else if ((object instanceof Text || object instanceof CDATA) && text) {
            String string = ((Text) object).getText();
            string = string.replace('\n', '=');
            buffer.append(string);
        }
    }

    private void appendXMLSignature(
        final StringBuilder buffer,
        final List children,
        final boolean elements,
        final boolean attributes,
        final boolean text,
        final boolean pi)
    {
        for (final Object child : children) {
            appendXMLSignature(
                buffer,
                child,
                elements,
                attributes,
                text,
                pi);
        }
    }
}
