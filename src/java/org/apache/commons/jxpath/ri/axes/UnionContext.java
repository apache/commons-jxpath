/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/UnionContext.java,v 1.7 2002/05/29 00:41:32 dmitri Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/29 00:41:32 $
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * EvalContext that represents a union between other contexts - result
 * of a union operation like (a | b)
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.7 $ $Date: 2002/05/29 00:41:32 $
 */
public class UnionContext extends EvalContext {
    private boolean startedSet = false;
    private EvalContext contexts[];
    private List list;

    public UnionContext(EvalContext parentContext, EvalContext contexts[]){
        super(parentContext);
        this.contexts = contexts;
    }

    public int getDocumentOrder(){
        if (contexts.length > 1){
            return 1;
        }
        return super.getDocumentOrder();
    }

    public NodePointer getCurrentNodePointer(){
        if (position == 0){
            if (!setPosition(1)){
                return null;
            }
        }
        return (NodePointer)list.get(position - 1);
    }

    public boolean setPosition(int position){
        super.setPosition(position);
        if (list == null){
            prepareList();
        }
        return position >= 1 && position <= list.size();
    }

    public boolean nextSet(){
        if (startedSet){
            return false;
        }
        startedSet = true;
        return true;
    }

    public boolean nextNode(){
        return setPosition(position + 1);
    }

    private void prepareList(){
        list = new ArrayList();
        HashSet set = new HashSet();
        for (int i = 0; i < contexts.length; i++){
            EvalContext ctx = (EvalContext)contexts[i];
            while (ctx.nextSet()){
                while (ctx.nextNode()){
                    NodePointer ptr = ctx.getCurrentNodePointer();
                    if (!set.contains(ptr)){
                        ptr = (NodePointer)ptr.clone();
                        list.add(ptr);
                        set.add(ptr);
                    }
                }
            }
        }
    }
}