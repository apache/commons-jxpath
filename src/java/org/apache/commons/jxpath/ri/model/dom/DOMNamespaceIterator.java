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
package org.apache.commons.jxpath.ri.model.dom;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * An iterator of namespaces of a DOM Node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class DOMNamespaceIterator implements NodeIterator {
    private NodePointer parent;
    private List attributes;
    private int position = 0;

    /**
     * Create a new DOMNamespaceIterator.
     * @param parent parent pointer
     */
    public DOMNamespaceIterator(NodePointer parent) {
        this.parent = parent;
        attributes = new ArrayList();
        collectNamespaces(attributes, (Node) parent.getNode());
    }

    /**
     * Collect namespaces from attribute nodes.
     * @param attributes attribute list
     * @param node target node
     */
    private void collectNamespaces(List attributes, Node node) {
        Node parent = node.getParentNode();
        if (parent != null) {
            collectNamespaces(attributes, parent);
        }
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            node = ((Document) node).getDocumentElement();
        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap map = node.getAttributes();
            int count = map.getLength();
            for (int i = 0; i < count; i++) {
                Attr attr = (Attr) map.item(i);
                String prefix = DOMNodePointer.getPrefix(attr);
                String name = DOMNodePointer.getLocalName(attr);
                if ((prefix != null && prefix.equals("xmlns"))
                    || (prefix == null && name.equals("xmlns"))) {
                    attributes.add(attr);
                }
            }
        }
    }

    public NodePointer getNodePointer() {
        if (position == 0) {
            if (!setPosition(1)) {
                return null;
            }
            position = 0;
        }
        int index = position - 1;
        if (index < 0) {
            index = 0;
        }
        String prefix = "";
        Attr attr = (Attr) attributes.get(index);
        String name = attr.getPrefix();
        if (name != null && name.equals("xmlns")) {
            prefix = DOMNodePointer.getLocalName(attr);
        }
        return new NamespacePointer(parent, prefix, attr.getValue());
    }

    public int getPosition() {
        return position;
    }

    public boolean setPosition(int position) {
        this.position = position;
        return position >= 1 && position <= attributes.size();
    }
}
