/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/Expression.java,v 1.1 2001/08/23 00:46:59 dmitri Exp $
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
 * Common superclass for several types of nodes in the parse tree. Provides
 * APIs for optimization of evaluation of expressions.  Specifically, an
 * expression only needs to executed once during the evaluation of an xpath
 * if that expression is context-independent.  Expression.isContextDependent()
 * provides that hint.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:46:59 $
 */
public abstract class Expression {

    public static final int OP_SUM = 1;
    public static final int OP_MINUS = 2;
    public static final int OP_MULT = 3;
    public static final int OP_DIV = 4;

    public static final int OP_CONSTANT = 5;
    public static final int OP_STEP = 6;

    public static final int OP_AND = 7;
    public static final int OP_OR = 8;

    public static final int OP_UNARY_MINUS = 9;
    public static final int OP_MOD = 10;

    public static final int OP_LT = 11;
    public static final int OP_GT = 12;
    public static final int OP_LTE = 13;
    public static final int OP_GTE = 14;
    public static final int OP_EQ = 15;
    public static final int OP_NE = 16;

    public static final int OP_VAR = 17;

    public static final int OP_FUNCTION = 18;

    public static final int OP_UNION = 19;
    public static final int OP_LOCATION_PATH = 20;
    public static final int OP_EXPRESSION_PATH = 21;

    public static final int OP_CORE_FUNCTION = 22;

    private int typeCode;

    private boolean contextDependencyKnown = false;
    private boolean contextDependent;

    public static final int EVALUATION_MODE_ONCE = 0;
    public static final int EVALUATION_MODE_ONCE_AND_SAVE = 1;
    public static final int EVALUATION_MODE_ALWAYS = 2;
    private int evaluationMode;

    private int id = -1;

    protected Expression(int typeCode){
        this.typeCode = typeCode;
    }

    /**
     * Expression IDs are used with context-independent expressions
     * for identifying the register holding the intermediate result of
     * this expression evaluation.
     */
    public void setID(int id){
        this.id = id;
    }

    /**
     * @see #setID
     */
    public int getID(){
        return id;
    }

    public int getExpressionTypeCode(){
        return typeCode;
    }

    protected Expression[] getArguments(){
        return null;
    }

    /**
     * Returns true if this expression should be re-evaluated
     * each time the current position in the context changes.
     */
    public boolean isContextDependent(){
        if (!contextDependencyKnown){
            contextDependent = computeContextDependent();
            contextDependencyKnown = true;
        }
        return contextDependent;
    }

    /**
     * Implemented by subclasses and result is cached by isContextDependent()
     */
    public abstract boolean computeContextDependent();

    /**
     * Evaluation mode can be EVALUATION_MODE_ONCE, EVALUATION_MODE_ONCE_AND_SAVE
     * or EVALUATION_MODE_ALWAYS, depending on whether or not this expression
     * is context-dependent.  The compiler calls setEvaluationMode(EVALUATION_MODE_ONCE)
     * on the root Expression.  That expression recursively computes
     * the evaluation mode for its children and calls setEvaluationMode on
     * each of them.
     */
    public void setEvaluationMode(int mode){
        this.evaluationMode = mode;
    }

    /**
     * @see #setEvaluationMode
     */
    public int getEvaluationMode(){
        return evaluationMode;
    }

    /**
     * Some expressions return optimization hints that
     * help the interpreter choose between alternative
     * evaluation algorithms.
     */
    public Object getEvaluationHint(String hint){
        return null;
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append('(');
        buffer.append(opCodeToString());
        Expression args[] = getArguments();
        if (args != null){
            buffer.append(' ');
            for (int i = 0; i < args.length; i++){
                if (i > 0){
                    buffer.append(", ");
                }
                buffer.append(args[i]);
            }
        }
        buffer.append(')');
        return buffer.toString();
    }

    protected String opCodeToString(){
        switch(typeCode){
            case OP_CONSTANT: return "CONST";
            case OP_STEP: return "STEP";
            case OP_SUM: return "SUM";
            case OP_UNION: return "UNION";
            case OP_MINUS: return "MINUS";
            case OP_UNARY_MINUS: return "UNARY_MINUS";
            case OP_MULT: return "MULT";
            case OP_DIV: return "DIV";
            case OP_MOD: return "MOD";
            case OP_AND: return "AND";
            case OP_OR: return "OR";
            case OP_LT: return "LT";
            case OP_GT: return "GT";
            case OP_LTE: return "LTE";
            case OP_GTE: return "GTE";
            case OP_EQ: return "EQ";
            case OP_NE: return "NE";
            case OP_VAR: return "VAR";
            case OP_FUNCTION: return "FUNCTION";
            case OP_LOCATION_PATH: return "LOCATION_PATH";
            case OP_EXPRESSION_PATH: return "EXPRESSION_PATH";
            case OP_CORE_FUNCTION: return "CORE_FUNCTION";
        }
        return "UNKNOWN";
    }
}