/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/DescendantContext.java,v 1.6 2002/04/24 04:05:39 dmitri Exp $
 * $Revision: 1.6 $
 * $Date: 2002/04/24 04:05:39 $
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

/**
 * An EvalContext that walks the "descendant::" and "descendant-or-self::"
 * axes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2002/04/24 04:05:39 $
 */
public class DescendantContext extends EvalContext {
    private NodeTest nodeTest;
    private boolean setStarted = false;
    private Stack stack;
    private NodePointer currentNodePointer;
    private boolean includeSelf;

    public DescendantContext(EvalContext parentContext, boolean includeSelf, NodeTest nodeTest){
        super(parentContext);
        this.includeSelf = includeSelf;
        this.nodeTest = nodeTest;
    }

    public NodePointer getCurrentNodePointer(){
        if (position == 0){
            if (!setPosition(1)){
                return null;
            }
        }
        return currentNodePointer;
    }

    public void reset(){
        super.reset();
        setStarted = false;
    }

    public boolean setPosition(int position){
//        System.err.println("POSITION: " + position + " this.position=" + this.position);
        if (position < this.position){
            reset();
        }

        while (this.position < position){
            if (!next()){
                return false;
            }
        }
        return true;
    }

    public boolean next(){
        if (!setStarted){
            setStarted = true;
            stack = new Stack();
            currentNodePointer = parentContext.getCurrentNodePointer();
            if (currentNodePointer != null){
                if (!currentNodePointer.isLeaf()){
                    stack.push(currentNodePointer.childIterator(null, false, null));
                }
                if (includeSelf){
                    if (currentNodePointer.testNode(nodeTest)){
                        position++;
                        return true;
                    }
                }
            }
        }

        while (!stack.isEmpty()){
            NodeIterator it = (NodeIterator)stack.peek();
            if (it.setPosition(it.getPosition() + 1)){
                currentNodePointer = it.getNodePointer();
                if (!currentNodePointer.isLeaf()){
                    stack.push(currentNodePointer.childIterator(null, false, null));
                }
                if (currentNodePointer.testNode(nodeTest)){
                    position++;
                    return true;
                }
            }
            else {
                // We get here only if the name test failed and the iterator ended
                stack.pop();
            }
        }
        return false;
    }
}