/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/Expression.java,v 1.3 2002/05/08 00:39:59 dmitri Exp $
 * $Revision: 1.3 $
 * $Date: 2002/05/08 00:39:59 $
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

import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.util.ValueUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

/**
 * Common superclass for several types of nodes in the parse tree. Provides
 * APIs for optimization of evaluation of expressions.  Specifically, an
 * expression only needs to executed once during the evaluation of an xpath
 * if that expression is context-independent.  Expression.isContextDependent()
 * provides that hint.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.3 $ $Date: 2002/05/08 00:39:59 $
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

    public static final int OP_KEY_LOOKUP = 23;

    protected static Double ZERO = new Double(0);
    protected static Double ONE = new Double(1);
    protected static Double NaN = new Double(Double.NaN);

    private int typeCode;

    private boolean contextDependencyKnown = false;
    private boolean contextDependent;

    protected Expression(int typeCode){
        this.typeCode = typeCode;
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

    /**
     * Evaluates the expression. If the result is a node set, returns
     * the first element of the node set.
     */
    public abstract Object computeValue(EvalContext context);
    public abstract Object compute(EvalContext context);

    public Iterator iterate(EvalContext context){
        Object result = compute(context);
        if (result instanceof EvalContext){
            return new ValueIterator((EvalContext)result);
        }
        return ValueUtils.iterate(result);
    }

    public Iterator iteratePointers(EvalContext context){
        Object result = compute(context);
        if (result == null){
            return Collections.EMPTY_LIST.iterator();
        }
        if (result instanceof EvalContext){
            return (EvalContext)result;
        }
        return new PointerIterator(ValueUtils.iterate(result),
                new QName(null, "value"),
                context.getRootContext().getCurrentNodePointer().getLocale());
    }

    public static class PointerIterator implements Iterator {
        private Iterator iterator;
        private QName qname;
        private Locale locale;

        public PointerIterator(Iterator it, QName qname, Locale locale){
            this.iterator = it;
            this.qname = qname;
            this.locale = locale;
        }

        public boolean hasNext(){
            return iterator.hasNext();
        }

        public Object next(){
            Object o = iterator.next();
            return NodePointer.newNodePointer(qname, o, locale);
        }

        public void remove(){
            throw new UnsupportedOperationException();
        }
    }

    public static class ValueIterator implements Iterator {
        private Iterator iterator;

        public ValueIterator(Iterator it){
            this.iterator = it;
        }

        public boolean hasNext(){
            return iterator.hasNext();
        }

        public Object next(){
            Object o = iterator.next();
            if (o instanceof Pointer){
                return ((Pointer)o).getValue();
            }
            return o;
        }

        public void remove(){
            throw new UnsupportedOperationException();
        }
    }
}