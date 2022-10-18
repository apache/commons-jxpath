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
import org.apache.commons.jxpath.Pointer;
import org.jdom.Element;

/**
 * Test AbstractFactory.
 */
public class TestJDOMFactory extends AbstractFactory {

    /**
     * Create a new instance and put it in the collection on the parent object.
     * Return <b>false</b> if this factory cannot create the requested object.
     */
    @Override
    public boolean createObject(
        final JXPathContext context,
        final Pointer pointer,
        final Object parent,
        final String name,
        final int index)
    {
        if (name.equals("location")
            || name.equals("address")
            || name.equals("street")) {
            addJDOMElement((Element) parent, index, name, null);
            return true;
        }
        if (name.startsWith("price:")) {
            final String namespaceURI = context.getNamespaceURI("price");
            addJDOMElement((Element) parent, index, name, namespaceURI);
            return true;
        }

        return false;
    }

    private void addJDOMElement(final Element parent, final int index, String tag, final String namespaceURI) {
        final List children = parent.getContent();
        int count = 0;
        for (final Object child : children) {
            if (child instanceof Element
                && ((Element) child).getQualifiedName().equals(tag)) {
                count++;
            }
        }

        // Keep inserting new elements until we have index + 1 of them
        while (count <= index) {
            // In a real factory we would need to do the right thing with
            // the namespace prefix.
            Element newElement;
            if (namespaceURI != null) {
                final String prefix = tag.substring(0, tag.indexOf(':'));
                tag = tag.substring(tag.indexOf(':') + 1);
                newElement = new Element(tag, prefix, namespaceURI);
            }
            else {
                newElement = new Element(tag);
            }
            parent.addContent(newElement);
            count++;
        }
    }

    @Override
    public boolean declareVariable(final JXPathContext context, final String name) {
        return false;
    }
}
