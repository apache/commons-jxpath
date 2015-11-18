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

import java.util.Collections;
import java.util.List;

import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.jdom.Document;
import org.jdom.Element;

/**
 * An iterator of children of a JDOM Node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class JDOMNodeIterator implements NodeIterator {
    private NodePointer parent;
    private NodeTest nodeTest;

    private boolean reverse;
    private int position = 0;
    private int index = 0;
    private List children;
    private Object child;

    /**
     * Create a new JDOMNodeIterator.
     * @param parent pointer
     * @param nodeTest test
     * @param reverse whether to iterate in reverse
     * @param startWith starting pointer
     */
    public JDOMNodeIterator(
            NodePointer parent, NodeTest nodeTest,
            boolean reverse, NodePointer startWith) {
        this.parent = parent;
        if (startWith != null) {
            this.child = startWith.getNode();
        }
        // TBD: optimize me for different node tests
        Object node = parent.getNode();
        if (node instanceof Document) {
            this.children = ((Document) node).getContent();
        }
        else if (node instanceof Element) {
            this.children = ((Element) node).getContent();
        }
        else {
            this.children = Collections.EMPTY_LIST;
        }
        this.nodeTest = nodeTest;
        this.reverse = reverse;
    }

    public NodePointer getNodePointer() {
        if (child == null) {
            if (!setPosition(1)) {
                return null;
            }
            position = 0;
        }

        return new JDOMNodePointer(parent, child);
    }

    public int getPosition() {
        return position;
    }

    public boolean setPosition(int position) {
        while (this.position < position) {
            if (!next()) {
                return false;
            }
        }
        while (this.position > position) {
            if (!previous()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This is actually never invoked during the normal evaluation
     * of xpaths - an iterator is always going forward, never backwards.
     * So, this is implemented only for completeness and perhaps for
     * those who use these iterators outside of XPath evaluation.
     * @return boolean
     */
    private boolean previous() {
        position--;
        if (!reverse) {
            while (--index >= 0) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
        }
        else {
            for (; index < children.size(); index++) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Iterate to next pointer.
     * @return whether valid
     */
    private boolean next() {
        position++;
        if (!reverse) {
            if (position == 1) {
                index = 0;
                if (child != null) {
                    index = children.indexOf(child) + 1;
                }
            }
            else {
                index++;
            }
            for (; index < children.size(); index++) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
            return false;
        }
        else {
            if (position == 1) {
                index = children.size() - 1;
                if (child != null) {
                    index = children.indexOf(child) - 1;
                }
            }
            else {
                index--;
            }
            for (; index >= 0; index--) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Test a child node.
     * @return whether test passes.
     */
    private boolean testChild() {
        return JDOMNodePointer.testNode(parent, child, nodeTest);
    }
}
