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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * An iterator of namespaces of a DOM Node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class JDOMNamespaceIterator implements NodeIterator {
    private NodePointer parent;
    private List namespaces;
    private Set prefixes;
    private int position = 0;

    /**
     * Create a new JDOMNamespaceIterator.
     * @param parent the parent NodePointer.
     */
    public JDOMNamespaceIterator(NodePointer parent) {
        this.parent = parent;
        Object node = parent.getNode();
        if (node instanceof Document) {
            node = ((Document) node).getRootElement();
        }
        if (node instanceof Element) {
            namespaces = new ArrayList();
            prefixes = new HashSet();
            collectNamespaces((Element) node);
        }
    }

    /**
     * Collect the namespaces from a JDOM Element.
     * @param element the source Element
     */
    private void collectNamespaces(Element element) {
        Namespace ns = element.getNamespace();
        if (ns != null && !prefixes.contains(ns.getPrefix())) {
            namespaces.add(ns);
            prefixes.add(ns.getPrefix());
        }
        List others = element.getAdditionalNamespaces();
        for (int i = 0; i < others.size(); i++) {
            ns = (Namespace) others.get(i);
            if (ns != null && !prefixes.contains(ns.getPrefix())) {
                namespaces.add(ns);
                prefixes.add(ns.getPrefix());
            }
        }
        Object elementParent = element.getParent();
        if (elementParent instanceof Element) {
            collectNamespaces((Element) elementParent);
        }
    }

    /**
     * {@inheritDoc}
     */
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
        Namespace ns = (Namespace) namespaces.get(index);
        return new JDOMNamespacePointer(parent, ns.getPrefix(), ns.getURI());
    }

    /**
     * {@inheritDoc}
     */
    public int getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    public boolean setPosition(int position) {
        if (namespaces == null) {
            return false;
        }
        this.position = position;
        return position >= 1 && position <= namespaces.size();
    }
}