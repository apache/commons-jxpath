/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/TreeCompiler.java,v 1.2 2001/09/21 23:22:44 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2001/09/21 23:22:44 $
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
import org.apache.commons.jxpath.ri.Compiler;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2001/09/21 23:22:44 $
 */
public class TreeCompiler implements Compiler {

    public Object number(String value){
        return new Constant(new Double(value));
    }

    public Object literal(String value){
        return new Constant(value);
    }

    public Object qname(String prefix, String name){
        return new QName(prefix, name);
    }

    /*
    public Object nodeTypeTest(int nodeType){
        return new NodeTypeTest(nodeType);
    }

    public Object processingInstructionTest(String value){
        return new ProcessingInstructionTest(value);
    }

    public Object traverse(boolean recursive, int axis, Object nodeTest, Object[] predicates){
        return new Step(recursive, axis, (NodeTest)nodeTest, toExpressionArray(predicates));
    }
*/
    public Object sum(Object[] arguments){
        return new CoreOperation(Expression.OP_SUM, toExpressionArray(arguments));
    }

    public Object minus(Object left, Object right){
        return new CoreOperation(Expression.OP_MINUS, (Expression)left, (Expression)right);
    }

    public Object multiply(Object left, Object right){
        return new CoreOperation(Expression.OP_MULT, (Expression)left, (Expression)right);
    }

    public Object divide(Object left, Object right){
        return new CoreOperation(Expression.OP_DIV, (Expression)left, (Expression)right);
    }

    public Object mod(Object left, Object right){
        return new CoreOperation(Expression.OP_MOD, (Expression)left, (Expression)right);
    }

    public Object lessThan(Object left, Object right){
        return new CoreOperation(Expression.OP_LT, (Expression)left, (Expression)right);
    }

    public Object lessThanOrEqual(Object left, Object right){
        return new CoreOperation(Expression.OP_LTE, (Expression)left, (Expression)right);
    }

    public Object greaterThan(Object left, Object right){
        return new CoreOperation(Expression.OP_GT, (Expression)left, (Expression)right);
    }

    public Object greaterThanOrEqual(Object left, Object right){
        return new CoreOperation(Expression.OP_GTE, (Expression)left, (Expression)right);
    }

    public Object equal(Object left, Object right){
        return new CoreOperation(Expression.OP_EQ, (Expression)left, (Expression)right);
    }

    public Object notEqual(Object left, Object right){
        return new CoreOperation(Expression.OP_NE, (Expression)left, (Expression)right);
    }

    public Object minus(Object argument){
        return new CoreOperation(Expression.OP_UNARY_MINUS, (Expression)argument);
    }

    public Object variableReference(Object qName){
        return new VariableReference((QName)qName);
    }

    public Object function(int code, Object[] args){
        return new CoreFunction(code, toExpressionArray(args));
    }

    public Object function(Object name, Object[] args){
        return new ExtensionFunction((QName)name, toExpressionArray(args));
    }

    public Object and(Object arguments[]){
        return new CoreOperation(Expression.OP_AND, toExpressionArray(arguments));
    }

    public Object or(Object arguments[]){
        return new CoreOperation(Expression.OP_OR, toExpressionArray(arguments));
    }

    public Object union(Object[] arguments){
        return new CoreOperation(Expression.OP_UNION, toExpressionArray(arguments));
    }

    public Object locationPath(boolean absolute, Object[] steps){
        return new LocationPath(absolute, toStepArray(steps));
    }

    public Object expressionPath(Object expression, Object[] predicates, Object[] steps){
        return new ExpressionPath((Expression)expression, toExpressionArray(predicates), toStepArray(steps));
    }

    public Object nodeNameTest(Object qname){
        return new NodeNameTest((QName)qname);
    }

    public Object nodeTypeTest(int nodeType){
        return new NodeTypeTest(nodeType);
    }

    public Object processingInstructionTest(String instruction){
        return new ProcessingInstructionTest(instruction);
    }

    public Object step(int axis, Object nodeTest, Object[] predicates){
        return new Step(axis, (NodeTest)nodeTest, toExpressionArray(predicates));
    }

    private Expression[] toExpressionArray(Object[] array){
        Expression expArray[] = null;
        if (array != null){
            expArray = new Expression[array.length];
            for (int i = 0; i < expArray.length; i++){
                expArray[i] = (Expression)array[i];
            }
        }
        return expArray;
    }

    private Step[] toStepArray(Object[] array){
        Step stepArray[] = null;
        if (array != null){
            stepArray = new Step[array.length];
            for (int i = 0; i < stepArray.length; i++){
                stepArray[i] = (Step)array[i];
            }
        }
        return stepArray;
    }
}