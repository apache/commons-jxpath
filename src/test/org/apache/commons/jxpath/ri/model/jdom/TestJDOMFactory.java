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
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class TestJDOMFactory extends AbstractFactory {

    /**
     * Create a new instance and put it in the collection on the parent object.
     * Return <b>false</b> if this factory cannot create the requested object.
     */
    public boolean createObject(
        JXPathContext context,
        Pointer pointer,
        Object parent,
        String name,
        int index) 
    {
        if (name.equals("location")
            || name.equals("address")
            || name.equals("street")) {
            addJDOMElement((Element) parent, index, name, null);
            return true;
        }
        if (name.startsWith("price:")) {
            String namespaceURI = context.getNamespaceURI("price");
            addJDOMElement((Element) parent, index, name, namespaceURI);
            return true;
        }

        return false;
    }

    private void addJDOMElement(Element parent, int index, String tag, String namespaceURI) {
        List children = parent.getContent();
        int count = 0;
        for (int i = 0; i < children.size(); i++) {
            Object child = children.get(i);
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
                String prefix = tag.substring(0, tag.indexOf(':'));
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

    public boolean declareVariable(JXPathContext context, String name) {
        return false;
    }
}
