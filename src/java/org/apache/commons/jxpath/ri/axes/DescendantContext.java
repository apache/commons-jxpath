/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/DescendantContext.java,v 1.1 2001/08/23 00:46:59 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/08/23 00:46:59 $
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
import org.apache.commons.jxpath.ri.pointers.*;
import org.apache.commons.jxpath.ri.EvalContext;
import java.util.*;

/**
 * An EvalContext that walks the "descendant::" and "descendant-or-self::"
 * axes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:46:59 $
 */
public class DescendantContext extends EvalContext {
    private QName nameTest;
    private boolean setStarted = false;
    private boolean started = false;
    private Stack stack;
    private NodePointer currentNodePointer;
    private boolean includeSelf;

    public DescendantContext(EvalContext parentContext, boolean includeSelf, QName nameTest){
        super(parentContext);
        this.includeSelf = includeSelf;
        if (nameTest != null && !nameTest.getName().equals("*")){
            this.nameTest = nameTest;
        }
        reset();
    }

    public NodePointer getCurrentNodePointer(){
        return currentNodePointer;
    }

    public boolean setPosition(int position){
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

    public boolean nextSet(){
        reset();
        // First time this method is called, we should look for
        // the first parent set that contains at least one node.
        if (!started){
            started = true;
            while (parentContext.nextSet()){
                if (parentContext.next()){
                    return true;
                }
            }
            return false;
        }

        // In subsequent calls, we see if the parent context
        // has any nodes left in the current set
        if (parentContext.next()){
            return true;
        }

        // If not, we look for the next set that contains
        // at least one node
        while (parentContext.nextSet()){
            if (parentContext.next()){
                return true;
            }
        }
        return false;
    }

    public boolean next(){
        if (!setStarted){
            setStarted = true;
            currentNodePointer = parentContext.getCurrentNodePointer();
            if (!currentNodePointer.isAtomic()){
                stack.push(PropertyIterator.iterator(currentNodePointer, null, false));
            }
            if (includeSelf){
                if (nameTest == null || nameTest.equals(currentNodePointer.getName())){
                    position++;
                    return true;
                }
            }
        }

        while (!stack.isEmpty()){
            PropertyIterator it = (PropertyIterator)stack.peek();
            if (it.next()){
                currentNodePointer = it.getCurrentNodePointer();
                if (!currentNodePointer.isAtomic()){
                    stack.push(PropertyIterator.iterator(currentNodePointer, null, false));
                }
                if (nameTest == null || nameTest.equals(currentNodePointer.getName())){
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

    protected void reset(){
        super.reset();
        stack = new Stack();
        setStarted = false;
    }
}