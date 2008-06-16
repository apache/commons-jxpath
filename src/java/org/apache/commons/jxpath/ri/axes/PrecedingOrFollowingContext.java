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

import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * EvalContext that walks the "preceding::" and "following::" axes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class PrecedingOrFollowingContext extends EvalContext {
    private NodeTest nodeTest;
    private boolean setStarted = false;
    private Stack stack = null;
    private NodePointer currentNodePointer;
    private NodePointer currentRootLocation;
    private boolean reverse;

    /**
     * Create a new PrecedingOrFollowingContext.
     * @param parentContext parent context
     * @param nodeTest test
     * @param reverse whether to iterate in reverse order
     */
    public PrecedingOrFollowingContext(
        EvalContext parentContext,
        NodeTest nodeTest,
        boolean reverse) {
        super(parentContext);
        this.nodeTest = nodeTest;
        this.reverse = reverse;
    }

    public NodePointer getCurrentNodePointer() {
        return currentNodePointer;
    }

    public int getDocumentOrder() {
        return reverse ? -1 : 1;
    }

    public void reset() {
        super.reset();
        setStarted = false;
    }

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

    public boolean nextNode() {
        if (!setStarted) {
            setStarted = true;
            if (stack == null) {
                stack = new Stack();
            } else {
                stack.clear();
            }
            currentRootLocation = parentContext.getCurrentNodePointer();
            NodePointer parent = currentRootLocation.getParent();
            if (parent != null) {
                // TBD: check type
                stack.push(
                    parent.childIterator(null, reverse, currentRootLocation));
            }
        }

        while (true) {
            if (stack.isEmpty()) {
                currentRootLocation = currentRootLocation.getParent();

                if (currentRootLocation == null
                    || currentRootLocation.isRoot()) {
                    break;
                }

                NodePointer parent = currentRootLocation.getParent();
                if (parent != null) {
                    stack.push(
                        parent.childIterator(
                            null,
                            reverse,
                            currentRootLocation));
                }
            }

            while (!stack.isEmpty()) {
                if (!reverse) {
                    NodeIterator it = (NodeIterator) stack.peek();
                    if (it.setPosition(it.getPosition() + 1)) {
                        currentNodePointer = it.getNodePointer();
                        if (!currentNodePointer.isLeaf()) {
                            stack.push(
                                currentNodePointer.childIterator(
                                    null,
                                    reverse,
                                    null));
                        }
                        if (currentNodePointer.testNode(nodeTest)) {
                            super.setPosition(getCurrentPosition() + 1);
                            return true;
                        }
                    }
                    else {
                        // We get here only if the name test failed
                        // and the iterator ended
                        stack.pop();
                    }
                }
                else {
                    NodeIterator it = (NodeIterator) stack.peek();
                    if (it.setPosition(it.getPosition() + 1)) {
                        currentNodePointer = it.getNodePointer();
                        if (!currentNodePointer.isLeaf()) {
                            stack.push(
                                currentNodePointer.childIterator(
                                    null,
                                    reverse,
                                    null));
                        }
                        else if (currentNodePointer.testNode(nodeTest)) {
                            super.setPosition(getCurrentPosition() + 1);
                            return true;
                        }
                    }
                    else {
                        stack.pop();
                        if (!stack.isEmpty()) {
                            it = (NodeIterator) stack.peek();
                            currentNodePointer = it.getNodePointer();
                            if (currentNodePointer.testNode(nodeTest)) {
                                super.setPosition(getCurrentPosition() + 1);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
