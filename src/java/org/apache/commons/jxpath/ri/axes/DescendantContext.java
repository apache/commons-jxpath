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
package org.apache.commons.jxpath.ri.axes;

import java.util.Stack;

import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * An EvalContext that walks the "descendant::" and "descendant-or-self::"
 * axes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class DescendantContext extends EvalContext {
    private NodeTest nodeTest;
    private boolean setStarted = false;
    private Stack stack;
    private NodePointer currentNodePointer;
    private boolean includeSelf;
    private static final NodeTest ELEMENT_NODE_TEST =
            new NodeTypeTest(Compiler.NODE_TYPE_NODE);

    /**
     * Create a new DescendantContext.
     * @param parentContext parent context
     * @param includeSelf whether to include this node
     * @param nodeTest test
     */
    public DescendantContext(EvalContext parentContext, boolean includeSelf,
            NodeTest nodeTest) {
        super(parentContext);
        this.includeSelf = includeSelf;
        this.nodeTest = nodeTest;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isChildOrderingRequired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public NodePointer getCurrentNodePointer() {
        if (position == 0) {
            if (!setPosition(1)) {
                return null;
            }
        }
        return currentNodePointer;
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        super.reset();
        setStarted = false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean setPosition(int position) {
        if (position < this.position) {
            reset();
        }

        while (this.position < position) {
            if (!nextNode()) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean nextNode() {
        if (!setStarted) {
            setStarted = true;
            stack = new Stack();
            currentNodePointer = parentContext.getCurrentNodePointer();
            if (currentNodePointer != null) {
                if (!currentNodePointer.isLeaf()) {
                    stack.push(
                        currentNodePointer.childIterator(
                            ELEMENT_NODE_TEST,
                            false,
                            null));
                }
                if (includeSelf) {
                    if (currentNodePointer.testNode(nodeTest)) {
                        position++;
                        return true;
                    }
                }
            }
        }

        while (!stack.isEmpty()) {
            NodeIterator it = (NodeIterator) stack.peek();
            if (it.setPosition(it.getPosition() + 1)) {
                currentNodePointer = it.getNodePointer();
                if (!isRecursive()) {
                    if (!currentNodePointer.isLeaf()) {
                        stack.push(
                            currentNodePointer.childIterator(
                                ELEMENT_NODE_TEST,
                                false,
                                null));
                    }
                    if (currentNodePointer.testNode(nodeTest)) {
                        position++;
                        return true;
                    }
                }
            }
            else {
                // We get here only if the name test failed
                // and the iterator ended
                stack.pop();
            }
        }
        return false;
    }

    /**
     * Checks if we are reentering a bean we have already seen and if so
     * returns true to prevent infinite recursion.
     * @return boolean
     */
    private boolean isRecursive() {
        Object node = currentNodePointer.getNode();
        for (int i = stack.size() - 1; --i >= 0;) {
            NodeIterator it = (NodeIterator) stack.get(i);
            Pointer pointer = it.getNodePointer();
            if (pointer != null && pointer.getNode() == node) {
                return true;
            }
        }
        return false;
    }
}