/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/ExpressionPath.java,v 1.1 2001/08/23 00:46:59 dmitri Exp $
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
package org.apache.commons.jxpath.ri.compiler;

import java.util.*;

/**
 * An element of the parse tree that represents an expression path, which is
 * a path that starts with an expression like a function call: <code>getFoo(.)/bar</code>.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:46:59 $
 */
public class ExpressionPath extends Path {

    private Expression expression;
    private Expression predicates[];

    public ExpressionPath(Expression expression, Expression[] predicates, Step[] steps){
        super(Expression.OP_EXPRESSION_PATH, steps);
        this.expression = expression;
        this.predicates = predicates;
    }

    public Expression getExpression(){
        return expression;
    }

    /**
     * Predicates are the expressions in brackets that may follow
     * the root expression of the path.
     */
    public Expression[] getPredicates(){
        return predicates;
    }

    /**
     * Returns true if the root expression or any of the
     * predicates or the path steps are context dependent.
     */
    public boolean computeContextDependent(){
        if (expression.isContextDependent()){
            return true;
        }
        if (predicates != null){
            for (int i = 0; i < predicates.length; i++){
                if (predicates[i].isContextDependent()){
                    return true;
                }
            }
        }
        return super.computeContextDependent();
    }

    /**
     * Based on the supplied argument computes the evaluation mode
     * for the base expression, predicates and steps.
     */
    public void setEvaluationMode(int mode){
        super.setEvaluationMode(mode);

        switch(mode){
            case EVALUATION_MODE_ALWAYS:
                if (expression.isContextDependent()){
                    expression.setEvaluationMode(Expression.EVALUATION_MODE_ALWAYS);
                }
                else {
                    expression.setEvaluationMode(Expression.EVALUATION_MODE_ONCE_AND_SAVE);
                }
                break;
            case EVALUATION_MODE_ONCE:
            case EVALUATION_MODE_ONCE_AND_SAVE:
                expression.setEvaluationMode(Expression.EVALUATION_MODE_ONCE);
                break;
        }

        if (predicates != null){
            for (int i = 0; i < predicates.length; i++){
                switch(mode){
                    case EVALUATION_MODE_ALWAYS:
                        if (predicates[i].isContextDependent()){
                            predicates[i].setEvaluationMode(Expression.EVALUATION_MODE_ALWAYS);
                        }
                        else {
                            predicates[i].setEvaluationMode(Expression.EVALUATION_MODE_ONCE_AND_SAVE);
                        }
                        break;
                    case EVALUATION_MODE_ONCE:
                    case EVALUATION_MODE_ONCE_AND_SAVE:
                        predicates[i].setEvaluationMode(Expression.EVALUATION_MODE_ONCE);
                        break;
                }
            }
        }
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("(EXPRESSION-PATH ");
        buffer.append(expression);

        if (predicates != null){
            buffer.append(' ');
            for (int i = 0; i < predicates.length; i++){
                buffer.append('[');
                buffer.append(predicates[i]);
                buffer.append(']');
            }
        }

        Step steps[] = getSteps();
        if (steps != null){
            buffer.append(' ');
            for (int i = 0; i < steps.length; i++){
                if (i > 0){
                    buffer.append(", ");
                }
                buffer.append(steps[i]);
            }
        }
        buffer.append(')');
        return buffer.toString();
    }
}