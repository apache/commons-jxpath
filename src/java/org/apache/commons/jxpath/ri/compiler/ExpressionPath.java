/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/ExpressionPath.java,v 1.7 2003/01/19 23:59:24 dmitri Exp $
 * $Revision: 1.7 $
 * $Date: 2003/01/19 23:59:24 $
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

import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.axes.InitialContext;
import org.apache.commons.jxpath.ri.axes.PredicateContext;
import org.apache.commons.jxpath.ri.axes.SimplePathInterpreter;
import org.apache.commons.jxpath.ri.axes.UnionContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * An  element of the parse tree that represents an expression path, which is a
 * path that starts with an expression like a function call: <code>getFoo(.)
 * /bar</code>.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.7 $ $Date: 2003/01/19 23:59:24 $
 */
public class ExpressionPath extends Path {

    private Expression expression;
    private Expression predicates[];

    private boolean basicKnown = false;
    private boolean basic;

    public ExpressionPath(
        Expression expression,
        Expression[] predicates,
        Step[] steps) 
    {
        super(steps);
        this.expression = expression;
        this.predicates = predicates;
    }

    public Expression getExpression() {
        return expression;
    }

    /**
     * Predicates are the expressions in brackets that may follow
     * the root expression of the path.
     */
    public Expression[] getPredicates() {
        return predicates;
    }

    /**
     * Returns true if the root expression or any of the
     * predicates or the path steps are context dependent.
     */
    public boolean computeContextDependent() {
        if (expression.isContextDependent()) {
            return true;
        }
        if (predicates != null) {
            for (int i = 0; i < predicates.length; i++) {
                if (predicates[i].isContextDependent()) {
                    return true;
                }
            }
        }
        return super.computeContextDependent();
    }

    /**
     * Recognized paths formatted as <code>$x[3]/foo[2]</code>.  The
     * evaluation of such "simple" paths is optimized and streamlined.
     */
    public boolean isSimpleExpressionPath() {
        if (!basicKnown) {
            basicKnown = true;
            basic = isSimplePath() && areBasicPredicates(getPredicates());
        }
        return basic;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (expression instanceof CoreOperation
            || expression instanceof ExpressionPath
            || expression instanceof LocationPath) {
            buffer.append('(');
            buffer.append(expression);
            buffer.append(')');
        }
        else {
            buffer.append(expression);
        }
        if (predicates != null) {
            for (int i = 0; i < predicates.length; i++) {
                buffer.append('[');
                buffer.append(predicates[i]);
                buffer.append(']');
            }
        }

        Step steps[] = getSteps();
        if (steps != null) {
            for (int i = 0; i < steps.length; i++) {
                buffer.append("/");
                buffer.append(steps[i]);
            }
        }
        return buffer.toString();
    }

    public Object compute(EvalContext context) {
        return expressionPath(context, false);
    }

    public Object computeValue(EvalContext context) {
        return expressionPath(context, true);
    }

    /**
     * Walks an expression path (a path that starts with an expression)
     */
    protected Object expressionPath(
        EvalContext evalContext,
        boolean firstMatch) 
    {
        Object value = expression.compute(evalContext);
        EvalContext context;
        if (value instanceof InitialContext) {
            // This is an optimization. We can avoid iterating through a 
            // collection if the context bean is in fact one.
            context = (InitialContext) value;
        }
        else if (value instanceof EvalContext) {
            // UnionContext will collect all values from the "value" context
            // and treat the whole thing as a big collection.
            context =
                new UnionContext(
                    evalContext,
                    new EvalContext[] {(EvalContext) value });
        }
        else {
            context = evalContext.getRootContext().getConstantContext(value);
        }

        if (firstMatch
            && isSimpleExpressionPath()
            && !(context instanceof UnionContext)) {
            EvalContext ctx = context;
            NodePointer ptr = (NodePointer) ctx.getSingleNodePointer();
            if (ptr != null
                && (ptr.getIndex() == NodePointer.WHOLE_COLLECTION
                    || predicates == null
                    || predicates.length == 0)) {
                return SimplePathInterpreter.interpretSimpleExpressionPath(
                    evalContext,
                    ptr,
                    predicates,
                    getSteps());
            }
        }
        if (predicates != null) {
            for (int j = 0; j < predicates.length; j++) {
                context = new PredicateContext(context, predicates[j]);
            }
        }
        if (firstMatch) {
            return getSingleNodePointerForSteps(context);
        }
        else {
            return evalSteps(context);
        }
    }
}