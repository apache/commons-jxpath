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
package org.apache.commons.jxpath.ri;

/**
 * The Compiler APIs are completely agnostic to the actual types of objects
 * produced and consumed by the APIs.  Arguments and return values are
 * declared as java.lang.Object.
 * <p>
 * Since objects returned by Compiler methods are passed as arguments to other
 * Compiler methods, the descriptions of these methods use virtual types.  There
 * are four virtual object types: EXPRESSION, QNAME, STEP and NODE_TEST.
 * <p>
 * The following example illustrates this notion.  This sequence compiles
 * the xpath "foo[round(1 div 2)]/text()":
 * <blockquote><pre>
 *      Object qname1 = compiler.qname(null, "foo")
 *      Object expr1 = compiler.number("1");
 *      Object expr2 = compiler.number("2");
 *      Object expr3 = compiler.div(expr1, expr2);
 *      Object expr4 = compiler.
 *              coreFunction(Compiler.FUNCTION_ROUND, new Object[]{expr3});
 *      Object test1 = compiler.nodeNameTest(qname1);
 *      Object step1 = compiler.
 *              step(Compiler.AXIS_CHILD, test1, new Object[]{expr4});
 *      Object test2 = compiler.nodeTypeTest(Compiler.NODE_TYPE_TEXT);
 *      Object step2 = compiler.nodeTypeTest(Compiler.AXIS_CHILD, test2, null);
 *      Object expr5 = compiler.locationPath(false, new Object[]{step1, step2});
 * </pre></blockquote>
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public interface Compiler {

    public static final int NODE_TYPE_NODE = 1;
    public static final int NODE_TYPE_TEXT = 2;
    public static final int NODE_TYPE_COMMENT = 3;
    public static final int NODE_TYPE_PI = 4;

    public static final int AXIS_SELF = 1;
    public static final int AXIS_CHILD = 2;
    public static final int AXIS_PARENT = 3;
    public static final int AXIS_ANCESTOR = 4;
    public static final int AXIS_ATTRIBUTE = 5;
    public static final int AXIS_NAMESPACE = 6;
    public static final int AXIS_PRECEDING = 7;
    public static final int AXIS_FOLLOWING = 8;
    public static final int AXIS_DESCENDANT = 9;
    public static final int AXIS_ANCESTOR_OR_SELF = 10;
    public static final int AXIS_FOLLOWING_SIBLING = 11;
    public static final int AXIS_PRECEDING_SIBLING = 12;
    public static final int AXIS_DESCENDANT_OR_SELF = 13;

    public static final int FUNCTION_LAST = 1;
    public static final int FUNCTION_POSITION = 2;
    public static final int FUNCTION_COUNT = 3;
    public static final int FUNCTION_ID = 4;
    public static final int FUNCTION_LOCAL_NAME = 5;
    public static final int FUNCTION_NAMESPACE_URI = 6;
    public static final int FUNCTION_NAME = 7;
    public static final int FUNCTION_STRING = 8;
    public static final int FUNCTION_CONCAT = 9;
    public static final int FUNCTION_STARTS_WITH = 10;
    public static final int FUNCTION_CONTAINS = 11;
    public static final int FUNCTION_SUBSTRING_BEFORE = 12;
    public static final int FUNCTION_SUBSTRING_AFTER = 13;
    public static final int FUNCTION_SUBSTRING = 14;
    public static final int FUNCTION_STRING_LENGTH = 15;
    public static final int FUNCTION_NORMALIZE_SPACE = 16;
    public static final int FUNCTION_TRANSLATE = 17;
    public static final int FUNCTION_BOOLEAN = 18;
    public static final int FUNCTION_NOT = 19;
    public static final int FUNCTION_TRUE = 20;
    public static final int FUNCTION_FALSE = 21;
    public static final int FUNCTION_LANG = 22;
    public static final int FUNCTION_NUMBER = 23;
    public static final int FUNCTION_SUM = 24;
    public static final int FUNCTION_FLOOR = 25;
    public static final int FUNCTION_CEILING = 26;
    public static final int FUNCTION_ROUND = 27;
    public static final int FUNCTION_NULL = 28;
    public static final int FUNCTION_KEY = 29;
    public static final int FUNCTION_FORMAT_NUMBER = 30;

    /**
     * Produces an EXPRESSION object that represents a numeric constant.
     */
    Object number(String value);

    /**
     * Produces an EXPRESSION object that represents a string constant.
     */
    Object literal(String value);

    /**
     * Produces an QNAME that represents a name with an optional prefix.
     */
    Object qname(String prefix, String name);

    /**
     * Produces an EXPRESSION object representing the sum of all argumens
     *
     * @param arguments are EXPRESSION objects
     */
    Object sum(Object[] arguments);

    /**
     * Produces an EXPRESSION object representing <i>left</i> minus <i>right</i>
     *
     * @param left is an EXPRESSION object
     * @param right is an EXPRESSION object
     */
    Object minus(Object left, Object right);

    /**
     * Produces  an EXPRESSION object representing <i>left</i> multiplied by
     * <i>right</i>
     *
     * @param left is an EXPRESSION object
     * @param right is an EXPRESSION object
     */
    Object multiply(Object left, Object right);

    /**
     * Produces  an EXPRESSION object representing <i>left</i> divided by
     * <i>right</i>
     *
     * @param left is an EXPRESSION object
     * @param right is an EXPRESSION object
     */
    Object divide(Object left, Object right);

    /**
     * Produces  an EXPRESSION object representing <i>left</i> modulo
     * <i>right</i>
     *
     * @param left is an EXPRESSION object
     * @param right is an EXPRESSION object
     */
    Object mod(Object left, Object right);

    /**
     * Produces an EXPRESSION object representing the comparison:
     * <i>left</i> less than <i>right</i>
     *
     * @param left is an EXPRESSION object
     * @param right is an EXPRESSION object
     */
    Object lessThan(Object left, Object right);

    /**
     * Produces an EXPRESSION object representing the comparison:
     * <i>left</i> less than or equal to <i>right</i>
     *
     * @param left is an EXPRESSION object
     * @param right is an EXPRESSION object
     */
    Object lessThanOrEqual(Object left, Object right);

    /**
     * Produces an EXPRESSION object representing the comparison:
     * <i>left</i> greater than <i>right</i>
     *
     * @param left is an EXPRESSION object
     * @param right is an EXPRESSION object
     */
    Object greaterThan(Object left, Object right);

    /**
     * Produces an EXPRESSION object representing the comparison:
     * <i>left</i> greater than or equal to <i>right</i>
     *
     * @param left is an EXPRESSION object
     * @param right is an EXPRESSION object
     */
    Object greaterThanOrEqual(Object left, Object right);

    /**
     * Produces an EXPRESSION object representing the comparison:
     * <i>left</i> equals to <i>right</i>
     *
     * @param left is an EXPRESSION object
     * @param right is an EXPRESSION object
     */
    Object equal(Object left, Object right);

    /**
     * Produces an EXPRESSION object representing the comparison:
     * <i>left</i> is not equal to <i>right</i>
     *
     * @param left is an EXPRESSION object
     * @param right is an EXPRESSION object
     */
    Object notEqual(Object left, Object right);

    /**
     * Produces an EXPRESSION object representing unary negation of the argument
     *
     * @param argument is an EXPRESSION object
     */
    Object minus(Object argument);

    /**
     * Produces an EXPRESSION object representing variable reference
     *
     * @param qname is a QNAME object
     */
    Object variableReference(Object qname);

    /**
     * Produces an EXPRESSION object representing the computation of
     * a core function with the supplied arguments.
     *
     * @param code is one of FUNCTION_... constants
     * @param args are EXPRESSION objects
     */
    Object function(int code, Object[] args);

    /**
     * Produces an EXPRESSION object representing the computation of
     * a library function with the supplied arguments.
     *
     * @param name is a QNAME object (function name)
     * @param args are EXPRESSION objects
     */
    Object function(Object name, Object[] args);

    /**
     * Produces an EXPRESSION object representing logical conjunction of
     * all arguments
     *
     * @param arguments are EXPRESSION objects
     */
    Object and(Object arguments[]);

    /**
     * Produces an EXPRESSION object representing logical disjunction of
     * all arguments
     *
     * @param arguments are EXPRESSION objects
     */
    Object or(Object arguments[]);

    /**
     * Produces an EXPRESSION object representing union of all node sets
     *
     * @param arguments are EXPRESSION objects
     */
    Object union(Object[] arguments);

    /**
     * Produces a NODE_TEST object that represents a node name test.
     *
     * @param qname is a QNAME object
     */
    Object nodeNameTest(Object qname);

    /**
     * Produces a NODE_TEST object that represents a node type test.
     *
     * @param nodeType is a NODE_TEST object
     */
    Object nodeTypeTest(int nodeType);

    /**
     * Produces  a NODE_TEST object that represents a processing instruction
     * test.
     *
     * @param instruction is a NODE_TEST object
     */
    Object processingInstructionTest(String instruction);

    /**
     * Produces a STEP object that represents a node test.
     *
     * @param axis is one of the AXIS_... constants
     * @param nodeTest is a NODE_TEST object
     * @param predicates are EXPRESSION objects
     */
    Object step(int axis, Object nodeTest, Object[] predicates);

    /**
     * Produces an EXPRESSION object representing a location path
     *
     * @param absolute indicates whether the path is absolute
     * @param steps are STEP objects
     */
    Object locationPath(boolean absolute, Object[] steps);

    /**
     * Produces an EXPRESSION object representing a filter expression
     *
     * @param expression is an EXPRESSION object
     * @param predicates are EXPRESSION objects
     * @param steps are STEP objects
     */
    Object expressionPath(
        Object expression,
        Object[] predicates,
        Object[] steps);
}