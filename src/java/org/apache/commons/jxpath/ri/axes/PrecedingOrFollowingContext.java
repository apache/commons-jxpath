/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/PrecedingOrFollowingContext.java,v 1.10 2002/11/28 01:02:04 dmitri Exp $
 * $Revision: 1.10 $
 * $Date: 2002/11/28 01:02:04 $
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
package org.apache.commons.jxpath.ri.axes;

import java.util.Stack;

import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyIterator;

/**
 * EvalContext that walks the "preceding::" and "following::" axes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.10 $ $Date: 2002/11/28 01:02:04 $
 */
public class PrecedingOrFollowingContext extends EvalContext {
    private NodeTest nodeTest;
    private boolean setStarted = false;
    private boolean started = false;
    private Stack stack;
    private Stack nameStack;
    private NodePointer currentNodePointer;
    private NodePointer currentRootLocation;
    private boolean reverse;

    public PrecedingOrFollowingContext(EvalContext parentContext, NodeTest nodeTest, boolean reverse){
        super(parentContext);
        this.nodeTest = nodeTest;
        this.reverse = reverse;
    }

    public NodePointer getCurrentNodePointer(){
        return currentNodePointer;
    }

    public int getDocumentOrder(){
        return reverse ? -1 : 1;
    }

    public void reset(){
        super.reset();
        stack = new Stack();
        setStarted = false;
    }

    public boolean setPosition(int position){
        if (position < this.position){
            reset();
        }

        while (this.position < position){
            if (!nextNode()){
                return false;
            }
        }
        return true;
    }

    public boolean nextNode(){
        if (!setStarted){
            setStarted = true;
            currentRootLocation = parentContext.getCurrentNodePointer();
            NodePointer parent = getMaterialPointer(currentRootLocation.getParent());
            if (parent != null){
                // TBD: check type
                stack.push(parent.childIterator(null, reverse, currentRootLocation));
            }
        }

        while (true){
            if (stack.isEmpty()){
                currentRootLocation = getMaterialPointer(currentRootLocation.getParent());

                if (currentRootLocation == null || currentRootLocation.isRoot()){
                    break;
                }

                NodePointer parent = getMaterialPointer(currentRootLocation.getParent());
                if (parent != null){
                    stack.push(parent.childIterator(null, reverse, currentRootLocation));
                }
            }

            while (!stack.isEmpty()){
                if (!reverse){
                    NodeIterator it = (NodeIterator)stack.peek();
                    if (it.setPosition(it.getPosition() + 1)){
                        currentNodePointer = it.getNodePointer();
                        if (!currentNodePointer.isLeaf()){
                            stack.push(currentNodePointer.childIterator(null, reverse, null));
                        }
                        if (currentNodePointer.testNode(nodeTest)){
                            super.setPosition(getCurrentPosition() + 1);
                            return true;
                        }
                    }
                    else {
                        // We get here only if the name test failed and the iterator ended
                        stack.pop();
                    }
                }
                else {
                    NodeIterator it = (NodeIterator)stack.peek();
                    if (it.setPosition(it.getPosition() + 1)){
                        currentNodePointer = it.getNodePointer();
                        if (!currentNodePointer.isLeaf()){
                            stack.push(currentNodePointer.childIterator(null, reverse, null));
                        }
                        else if (currentNodePointer.testNode(nodeTest)){
                            super.setPosition(getCurrentPosition() + 1);
                            return true;
                        }
                    }
                    else {
                        stack.pop();
                        if (!stack.isEmpty()){
                            it = (PropertyIterator)stack.peek();
                            currentNodePointer = it.getNodePointer();
                            if (currentNodePointer.testNode(nodeTest)){
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

    /**
     * If the pointer is auxiliary, return the parent; otherwise - the pointer itself
     */
    private NodePointer getMaterialPointer(NodePointer pointer){
        while (pointer != null && pointer.isContainer()){
            pointer = pointer.getParent();
        }
        return pointer;
    }
}