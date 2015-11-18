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

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;

/**
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class TreeCompiler implements Compiler {

    private static final QName QNAME_NAME = new QName(null, "name");

    public Object number(String value) {
        return new Constant(new Double(value));
    }

    public Object literal(String value) {
        return new Constant(value);
    }

    public Object qname(String prefix, String name) {
        return new QName(prefix, name);
    }

    public Object sum(Object[] arguments) {
        return new CoreOperationAdd(toExpressionArray(arguments));
    }

    public Object minus(Object left, Object right) {
        return new CoreOperationSubtract(
            (Expression) left,
            (Expression) right);
    }

    public Object multiply(Object left, Object right) {
        return new CoreOperationMultiply((Expression) left, (Expression) right);
    }

    public Object divide(Object left, Object right) {
        return new CoreOperationDivide((Expression) left, (Expression) right);
    }

    public Object mod(Object left, Object right) {
        return new CoreOperationMod((Expression) left, (Expression) right);
    }

    public Object lessThan(Object left, Object right) {
        return new CoreOperationLessThan((Expression) left, (Expression) right);
    }

    public Object lessThanOrEqual(Object left, Object right) {
        return new CoreOperationLessThanOrEqual(
            (Expression) left,
            (Expression) right);
    }

    public Object greaterThan(Object left, Object right) {
        return new CoreOperationGreaterThan(
            (Expression) left,
            (Expression) right);
    }

    public Object greaterThanOrEqual(Object left, Object right) {
        return new CoreOperationGreaterThanOrEqual(
            (Expression) left,
            (Expression) right);
    }

    public Object equal(Object left, Object right) {
        return isNameAttributeTest((Expression) left)
                ? new NameAttributeTest((Expression) left, (Expression) right)
                : new CoreOperationEqual((Expression) left, (Expression) right);
    }

    public Object notEqual(Object left, Object right) {
        return new CoreOperationNotEqual((Expression) left, (Expression) right);
    }

    public Object minus(Object argument) {
        return new CoreOperationNegate((Expression) argument);
    }

    public Object variableReference(Object qName) {
        return new VariableReference((QName) qName);
    }

    public Object function(int code, Object[] args) {
        return new CoreFunction(code, toExpressionArray(args));
    }

    public Object function(Object name, Object[] args) {
        return new ExtensionFunction((QName) name, toExpressionArray(args));
    }

    public Object and(Object[] arguments) {
        return new CoreOperationAnd(toExpressionArray(arguments));
    }

    public Object or(Object[] arguments) {
        return new CoreOperationOr(toExpressionArray(arguments));
    }

    public Object union(Object[] arguments) {
        return new CoreOperationUnion(toExpressionArray(arguments));
    }

    public Object locationPath(boolean absolute, Object[] steps) {
        return new LocationPath(absolute, toStepArray(steps));
    }

    public Object expressionPath(Object expression, Object[] predicates,
            Object[] steps) {
        return new ExpressionPath(
            (Expression) expression,
            toExpressionArray(predicates),
            toStepArray(steps));
    }

    public Object nodeNameTest(Object qname) {
        return new NodeNameTest((QName) qname);
    }

    public Object nodeTypeTest(int nodeType) {
        return new NodeTypeTest(nodeType);
    }

    public Object processingInstructionTest(String instruction) {
        return new ProcessingInstructionTest(instruction);
    }

    public Object step(int axis, Object nodeTest, Object[] predicates) {
        return new Step(
            axis,
            (NodeTest) nodeTest,
            toExpressionArray(predicates));
    }

    /**
     * Get an Object[] as an Expression[].
     * @param array Object[]
     * @return Expression[]
     */
    private Expression[] toExpressionArray(Object[] array) {
        Expression[] expArray = null;
        if (array != null) {
            expArray = new Expression[array.length];
            for (int i = 0; i < expArray.length; i++) {
                expArray[i] = (Expression) array[i];
            }
        }
        return expArray;
    }

    /**
     * Get an Object[] as a Step[].
     * @param array Object[]
     * @return Step[]
     */
    private Step[] toStepArray(Object[] array) {
        Step[] stepArray = null;
        if (array != null) {
            stepArray = new Step[array.length];
            for (int i = 0; i < stepArray.length; i++) {
                stepArray[i] = (Step) array[i];
            }
        }
        return stepArray;
    }

    /**
     * Learn whether arg is a name attribute test.
     * @param arg Expression to test
     * @return boolean
     */
    private boolean isNameAttributeTest(Expression arg) {
        if (!(arg instanceof LocationPath)) {
            return false;
        }

        Step[] steps = ((LocationPath) arg).getSteps();
        if (steps.length != 1) {
            return false;
        }
        if (steps[0].getAxis() != Compiler.AXIS_ATTRIBUTE) {
            return false;
        }
        NodeTest test = steps[0].getNodeTest();
        if (!(test instanceof NodeNameTest)) {
            return false;
        }
        if (!((NodeNameTest) test).getNodeName().equals(QNAME_NAME)) {
            return false;
        }
        return true;
    }
}
