/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/ParentContext.java,v 1.4 2002/04/21 21:52:32 dmitri Exp $
 * $Revision: 1.4 $
 * $Date: 2002/04/21 21:52:32 $
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

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.*;
import org.apache.commons.jxpath.ri.model.*;
import org.apache.commons.jxpath.ri.EvalContext;

import java.util.*;

/**
 * EvalContext that walks the "parent::" axis.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.4 $ $Date: 2002/04/21 21:52:32 $
 */
public class ParentContext extends EvalContext {
    private NodeTest nodeTest;
    private boolean setStarted = false;
    private NodePointer currentNodePointer;
    private HashSet visitedNodes = new HashSet();

    public ParentContext(EvalContext parentContext, NodeTest nodeTest){
        super(parentContext);
        this.nodeTest = nodeTest;
    }

    public NodePointer getCurrentNodePointer(){
        return currentNodePointer;
    }

    public int getCurrentPosition(){
        return 1;
    }

    public void reset(){
        super.reset();
        setStarted = false;
    }

    public boolean setPosition(int position){
        super.setPosition(position);
        return position == 1;
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
        // Each set contains exactly one node: the parent
        if (setStarted){
            return false;
        }
        setStarted = true;
        NodePointer thisLocation = parentContext.getCurrentNodePointer();
        currentNodePointer = thisLocation.getParent();
        while (currentNodePointer != null && !currentNodePointer.isNode()){
            currentNodePointer = currentNodePointer.getParent();
        }
        return currentNodePointer != null && currentNodePointer.testNode(nodeTest);
    }
}