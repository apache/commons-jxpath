/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/ChildContext.java,v 1.1 2001/08/23 00:46:59 dmitri Exp $
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

import org.apache.commons.jxpath.*;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.ri.pointers.*;
import org.apache.commons.jxpath.ri.EvalContext;

import java.lang.reflect.*;
import java.util.*;
import java.beans.*;

/**
 * EvalContext that can walk the "child::", "following-sibling::" and
 * "preceding-sibling::" axes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:46:59 $
 */
public class ChildContext extends EvalContext {
    private boolean started = false;
    private QName property;
    boolean startFromParentLocation;
    boolean reverse;
    private PropertyIterator iterator;

    public ChildContext(EvalContext parentContext, QName property, boolean startFromParentLocation, boolean reverse){
        super(parentContext);
        this.property = property;
        this.startFromParentLocation = startFromParentLocation;
        this.reverse = reverse;
    }

    public NodePointer getCurrentNodePointer(){
        return iterator.getCurrentNodePointer();
    }

    /**
     * This method is called on the last context on the path when only
     * one value is needed.  Note that this will return the whole property,
     * even if it is a collection. It will not extract the first element
     * of the collection.  For example, "books" will return the collection
     * of books rather than the first book from that collection.
     */
    public Pointer getContextNodePointer(){
        if (iterator == null){
            boolean ok = false;
            while(parentContext.nextSet()){
                if (parentContext.next()){
                    ok = true;
                    break;
                }
            }
            if (!ok){
                return null;
            }
            prepare();
        }
        return iterator.getFirstNodePointer();
    }

    public boolean next(){
        if (iterator == null){
            prepare();
        }
        return iterator.next();
    }

    public boolean setPosition(int position){
        if (iterator == null){
            prepare();
        }
        return iterator.setPosition(position);
    }

    public int getCurrentPosition(){
        return iterator.getCurrentPosition();
    }

    public boolean nextSet(){
        iterator = null;

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

    /**
     * Allocates a PropertyIterator.
     */
    private void prepare(){
        String name;
        if (property == null || property.getName().equals("*")){
            name = null;
        }
        else {
            name = property.getName();
        }
        if (startFromParentLocation){
            // TBD: check type
            PropertyPointer parent = (PropertyPointer)parentContext.getCurrentNodePointer();
            iterator = PropertyIterator.iteratorStartingAt(parent, name, reverse);
        }
        else {
            iterator = PropertyIterator.iterator(parentContext.getCurrentNodePointer(), name, reverse);
        }
    }
}