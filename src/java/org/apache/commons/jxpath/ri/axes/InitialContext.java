/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/InitialContext.java,v 1.7 2002/04/28 04:35:48 dmitri Exp $
 * $Revision: 1.7 $
 * $Date: 2002/04/28 04:35:48 $
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
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * A single-set EvalContext that provides access to the current node of
 * the parent context and nothing else.  It does not pass the iteration
 * on to the parent context.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.7 $ $Date: 2002/04/28 04:35:48 $
 */
public class InitialContext extends EvalContext {
    private boolean startedSet = false;
    private boolean started = false;
    private boolean collection;
    private NodePointer nodePointer;

    public InitialContext(EvalContext parentContext){
        super(parentContext);
        NodePointer ptr = parentContext.getCurrentNodePointer();
        if (ptr != null){
            nodePointer = (NodePointer)ptr.clone();
            collection = (nodePointer.getIndex() == NodePointer.WHOLE_COLLECTION);
        }
    }

    public Pointer getSingleNodePointer(){
        return nodePointer;
    }

    public NodePointer getCurrentNodePointer(){
        return nodePointer;
    }

    public boolean nextNode(){
        return setPosition(position + 1);
    }

    public boolean setPosition(int position){
        this.position = position;
        if (collection){
            if (position >= 1 && position <= nodePointer.getLength()){
                nodePointer.setIndex(position - 1);
                return true;
            }
            return false;
        }
        else {
            return position == 1;
        }
    }

    public boolean nextSet(){
        if (started){
            return false;
        }
        started = true;
        return true;
    }
}