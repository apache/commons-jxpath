/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/ChildContext.java,v 1.7 2002/04/24 04:05:39 dmitri Exp $
 * $Revision: 1.7 $
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

import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * EvalContext that can walk the "child::", "following-sibling::" and
 * "preceding-sibling::" axes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.7 $ $Date: 2002/04/24 04:05:39 $
 */
public class ChildContext extends EvalContext {
    private NodeTest nodeTest;
    private boolean startFromParentLocation;
    private boolean reverse;
    private NodeIterator iterator;

    public ChildContext(EvalContext parentContext, NodeTest nodeTest, boolean startFromParentLocation, boolean reverse){
        super(parentContext);
        this.nodeTest = nodeTest;
        this.startFromParentLocation = startFromParentLocation;
        this.reverse = reverse;
    }

    public NodePointer getCurrentNodePointer(){
        if (position == 0){
            if (!setPosition(1)){
                return null;
            }
        }
        if (iterator != null){
            return iterator.getNodePointer();
        }
        else {
            return null;
        }
    }

    /**
     * This method is called on the last context on the path when only
     * one value is needed.  Note that this will return the whole property,
     * even if it is a collection. It will not extract the first element
     * of the collection.  For example, "books" will return the collection
     * of books rather than the first book from that collection.
     */
    public Pointer getSingleNodePointer(){
        if (position == 0){
            while(nextSet()){
                prepare();
                if (iterator == null){
                    return null;
                }
                // See if there is a property there, singular or collection
                if (iterator.getNodePointer() != null){
                    break;
                }
            }
        }
        return getCurrentNodePointer();
    }

    public boolean next(){
        return setPosition(getCurrentPosition() + 1);
    }

    public void reset(){
        super.reset();
        iterator = null;
    }

    public boolean setPosition(int position){
        int oldPosition = getCurrentPosition();
        super.setPosition(position);
        if (oldPosition == 0){
            prepare();
        }
        if (iterator == null){
            return false;
        }
        return iterator.setPosition(position);
    }

    /**
     * Allocates a PropertyIterator.
     */
    private void prepare(){
        NodePointer parent = parentContext.getCurrentNodePointer();
        if (parent == null){
            return;
        }
        if (startFromParentLocation){
            NodePointer pointer = parent.getParent();
            while (pointer != null && !pointer.isNode()){
                pointer = pointer.getParent();
            }

            iterator = pointer.childIterator(nodeTest, reverse, parent);
        }
        else {
            iterator = parent.childIterator(nodeTest, reverse, null);
        }
    }
}