/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/AncestorContext.java,v 1.6 2002/04/24 04:06:46 dmitri Exp $
 * $Revision: 1.6 $
 * $Date: 2002/04/24 04:06:46 $
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

import java.util.HashSet;

import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * EvalContext that walks the "ancestor::" and "ancestor-or-self::" axes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2002/04/24 04:06:46 $
 */
public class AncestorContext extends EvalContext {
    private NodeTest nodeTest;
    private boolean setStarted = false;
    private NodePointer currentNodePointer;
    private boolean includeSelf;
    private HashSet visitedNodes = new HashSet();

    /**
     * @param parentContext represents the previous step on the path
     * @param includeSelf differentiates between "ancestor::" and "ancestor-or-self::" axes
     * @param nameTest is the name of the element(s) we are looking for
     */
    public AncestorContext(EvalContext parentContext, boolean includeSelf, NodeTest nodeTest){
        super(parentContext);
        this.includeSelf = includeSelf;
        this.nodeTest = nodeTest;
    }

    public NodePointer getCurrentNodePointer(){
        return currentNodePointer;
    }

    public void reset(){
        super.reset();
        setStarted = false;
    }

    public boolean setPosition(int position){
        if (position < getCurrentPosition()){
            reset();
        }

        while (getCurrentPosition() < position){
            if (!next()){
                return false;
            }
        }
        return true;
    }

    public boolean next(){
        while (nextIgnoreDuplicates()){
            NodePointer location = getCurrentNodePointer();
            if (!visitedNodes.contains(location)){
                visitedNodes.add(location.clone());
                position++;
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there is another object in the current set, even
     * if that object has already been encountered in the same iteration.
     */
    private boolean nextIgnoreDuplicates(){
        if (!setStarted){
            setStarted = true;
            currentNodePointer = parentContext.getCurrentNodePointer();
            if (includeSelf){
                if (currentNodePointer.testNode(nodeTest)){
                    return true;
                }
            }
        }

        while(true){
            currentNodePointer = currentNodePointer.getParent();

            if (currentNodePointer == null){
                return false;
            }

            if (currentNodePointer.testNode(nodeTest)){
                return true;
            }
        }
    }
}