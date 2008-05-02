/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.axes.InitialContext;
import org.apache.commons.jxpath.ri.axes.NodeSetContext;
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
 * @version $Revision$ $Date$
 */
public class ExpressionPath extends Path {

    private Expression expression;
    private Expression[] predicates;

    private boolean basicKnown = false;
    private boolean basic;

    /**
     * Create a new ExpressionPath.
     * @param expression Expression
     * @param predicates to execute
     * @param steps navigation
     */
    public ExpressionPath(Expression expression, Expression[] predicates,
            Step[] steps) {
        super(steps);
        this.expression = expression;
        this.predicates = predicates;
    }

    /**
     * Get the expression.
     * @return Expression
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Predicates are the expressions in brackets that may follow
     * the root expression of the path.
     * @return Expression[]
     */
    public Expression[] getPredicates() {
        return predicates;
    }

    /**
     * Returns true if the root expression or any of the
     * predicates or the path steps are context dependent.
     * @return boolean
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
     * @return boolean
     */
    public synchronized boolean isSimpleExpressionPath() {
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

        Step[] steps = getSteps();
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
     * @param evalContext base context
     * @param firstMatch whether to return the first match found
     * @return Object found
     */
    protected Object expressionPath(EvalContext evalContext, boolean firstMatch) {
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
            && !(context instanceof NodeSetContext)) {
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
                if (j != 0) {
                    context = new UnionContext(context, new EvalContext[]{context});
                }
                context = new PredicateContext(context, predicates[j]);
            }
        }
        return firstMatch ? (Object) getSingleNodePointerForSteps(context)
                : evalSteps(context);
    }
}
