/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/PredicateContext.java,v 1.5 2002/04/21 21:52:32 dmitri Exp $
 * $Revision: 1.5 $
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


import java.util.*;

import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.model.*;
import org.apache.commons.jxpath.ri.model.beans.*;
import org.apache.commons.jxpath.ri.EvalContext;

/**
 * EvalContext that checks predicates.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2002/04/21 21:52:32 $
 */
public class PredicateContext extends EvalContext {
    private Expression expression;
    private boolean done = false;
    private Expression dynamicPropertyNameExpression;
    private PropertyPointer dynamicPropertyPointer;

    public PredicateContext(EvalContext parentContext, Expression expression){
        super(parentContext);
        this.expression = expression;
        dynamicPropertyNameExpression = (Expression)expression.
            getEvaluationHint(CoreOperation.DYNAMIC_PROPERTY_ACCESS_HINT);
    }

    public boolean next(){
        if (done){
            return false;
        }
        while (parentContext.next()){
            if (setupDynamicPropertyPointer()){
                Object pred = parentContext.eval(dynamicPropertyNameExpression);
                if (pred instanceof NodePointer){
                    pred = ((NodePointer)pred).getCanonicalValue();
                }
                dynamicPropertyPointer.setPropertyName(stringValue(pred));
                done = true;
                return true;
            }
            else {
                Object pred = parentContext.eval(expression);
                if (pred instanceof NodePointer){
                    pred = ((NodePointer)pred).getValue();
                }
                if (pred instanceof Number){
                    int pos = (int)doubleValue(pred);
                    position++;
                    done = true;
                    return parentContext.setPosition(pos);
                }
                else if (booleanValue(pred)){
                    position++;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Used for an optimized access to dynamic properties using the
     * "map[@name = 'name']" syntax
     */
    private boolean setupDynamicPropertyPointer(){
        if (dynamicPropertyNameExpression == null){
            return false;
        }

        NodePointer parent = parentContext.getCurrentNodePointer();
        if (!(parent instanceof PropertyOwnerPointer)){
            return false;
        }
        dynamicPropertyPointer = ((PropertyOwnerPointer)parent).getPropertyPointer();
        return true;
    }

    public NodePointer getCurrentNodePointer(){
        if (position == 0){
            if (!setPosition(1)){
                return null;
            }
        }
        if (dynamicPropertyPointer != null){
            return dynamicPropertyPointer;
        }
        else {
            return parentContext.getCurrentNodePointer();
        }
    }

    public void reset(){
        super.reset();
        done = false;
    }

    public boolean nextSet(){
        reset();
        return parentContext.nextSet();
    }

    public boolean setPosition(int position){
        if (this.position > position){
            reset();
        }

        while (this.position < position){
            if (!next()){
                return false;
            }
        }
        return true;
    }
}