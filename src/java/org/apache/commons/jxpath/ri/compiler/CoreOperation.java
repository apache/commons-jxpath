/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/CoreOperation.java,v 1.6 2002/08/26 22:19:51 dmitri Exp $
 * $Revision: 1.6 $
 * $Date: 2002/08/26 22:19:51 $
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
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.InfoSetUtil;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.axes.InitialContext;
import org.apache.commons.jxpath.ri.axes.SelfContext;
import org.apache.commons.jxpath.ri.axes.UnionContext;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

/**
 * A compile tree element representing one of the core operations like "+",
 * "-", "*" etc.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2002/08/26 22:19:51 $
 */
public class CoreOperation extends Operation {
    public CoreOperation(int code, Expression args[]){
        super(code, args);
    }

    public CoreOperation(int code, Expression arg){
        super(code, new Expression[]{arg});
    }

    public CoreOperation(int code, Expression arg1, Expression arg2){
        super(code, new Expression[]{arg1, arg2});
    }

    public Expression getArg1(){
        return args[0];
    }

    public Expression getArg2(){
        return args[1];
    }

    public Object compute(EvalContext context){
        return computeValue(context);
    }

    public Object computeValue(EvalContext context){
        switch (getExpressionTypeCode()){
            case Expression.OP_UNION:
                return union(context, args[0], args[1]);

            case Expression.OP_UNARY_MINUS:
                return minus(context, args[0]);

            case Expression.OP_SUM:
                return sum(context, args);

            case Expression.OP_MINUS:
                return minus(context, args[0], args[1]);

            case Expression.OP_MULT:
                return mult(context, args[0], args[1]);

            case Expression.OP_DIV:
                return div(context, args[0], args[1]);

            case Expression.OP_MOD:
                return mod(context, args[0], args[1]);

            case Expression.OP_LT:
                return lt(context, args[0], args[1]);

            case Expression.OP_GT:
                return gt(context, args[0], args[1]);

            case Expression.OP_LTE:
                return lte(context, args[0], args[1]);

            case Expression.OP_GTE:
                return gte(context, args[0], args[1]);

            case Expression.OP_EQ:
                return eq(context, args[0], args[1]);

            case Expression.OP_NE:
                return ne(context, args[0], args[1]);

            case Expression.OP_AND:
                return and(context, args);

            case Expression.OP_OR:
                return or(context, args);
        }
        return null; // Should never happen
    }

    /**
     * Computes <code>"left | right"<code>
     */
    protected Object union(EvalContext context, Expression left, Expression right){
        Object l = left.compute(context);
        Object r = right.compute(context);
        EvalContext lctx;
        if (l instanceof EvalContext){
            lctx = (EvalContext)l;
        }
        else {
            lctx = context.getRootContext().getConstantContext(l);
        }
        EvalContext rctx;
        if (r instanceof EvalContext){
            rctx = (EvalContext)r;
        }
        else {
            rctx = context.getRootContext().getConstantContext(r);
        }
        return new UnionContext(context.getRootContext(), new EvalContext[]{lctx, rctx});
    }

    /**
     * Computes <code>"-arg"<code>
     */
    protected Object minus(EvalContext context, Expression arg){
        double a = InfoSetUtil.doubleValue(arg.computeValue(context));
        return new Double(-a);
    }

    /**
     * Computes <code>"a + b + c + d"<code>
     */
    protected Object sum(EvalContext context, Expression[] arguments){
        double s = 0.0;
        for (int i = 0; i < arguments.length; i++){
            s += InfoSetUtil.doubleValue(arguments[i].computeValue(context));
        }
        return new Double(s);
    }

    /**
     * Computes <code>"left - right"<code>
     */
    protected Object minus(EvalContext context, Expression left, Expression right){
        double l = InfoSetUtil.doubleValue(left.computeValue(context));
        double r = InfoSetUtil.doubleValue(right.computeValue(context));
        return new Double(l - r);
    }

    /**
     * Computes <code>"left div right"<code>
     */
    protected Object div(EvalContext context, Expression left, Expression right){
        double l = InfoSetUtil.doubleValue(left.computeValue(context));
        double r = InfoSetUtil.doubleValue(right.computeValue(context));
        return new Double(l/r);
    }

    /**
     * Computes <code>"left * right"<code>
     */
    protected Object mult(EvalContext context, Expression left, Expression right){
        double l = InfoSetUtil.doubleValue(left.computeValue(context));
        double r = InfoSetUtil.doubleValue(right.computeValue(context));
        return new Double(l*r);
    }

    /**
     * Computes <code>"left mod right"<code>
     */
    protected Object mod(EvalContext context, Expression left, Expression right){
        long l = (long)InfoSetUtil.doubleValue(left.computeValue(context));
        long r = (long)InfoSetUtil.doubleValue(right.computeValue(context));
        return new Double(l%r);
    }

    /**
     * Computes <code>"left &lt; right"<code>
     */
    protected Object lt(EvalContext context, Expression left, Expression right){
        double l = InfoSetUtil.doubleValue(left.computeValue(context));
        double r = InfoSetUtil.doubleValue(right.computeValue(context));
        return l < r ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Computes <code>"left &gt; right"<code>
     */
    protected Object gt(EvalContext context, Expression left, Expression right){
        double l = InfoSetUtil.doubleValue(left.computeValue(context));
        double r = InfoSetUtil.doubleValue(right.computeValue(context));
        return l > r ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Computes <code>"left &lt;= right"<code>
     */
    protected Object lte(EvalContext context, Expression left, Expression right){
        double l = InfoSetUtil.doubleValue(left.computeValue(context));
        double r = InfoSetUtil.doubleValue(right.computeValue(context));
        return l <= r ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Computes <code>"left &gt;= right"<code>
     */
    protected Object gte(EvalContext context, Expression left, Expression right){
        double l = InfoSetUtil.doubleValue(left.computeValue(context));
        double r = InfoSetUtil.doubleValue(right.computeValue(context));
        return l >= r ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Computes <code>"left = right"<code>
     */
    protected Object eq(EvalContext context, Expression left, Expression right){
        return equal(context, left, right) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Computes <code>"left != right"<code>
     */
    protected Object ne(EvalContext context, Expression left, Expression right){
        return equal(context, left, right) ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * Compares two values
     */
    protected boolean equal(EvalContext context, Expression left, Expression right){
        Object l = left.compute(context);
        Object r = right.compute(context);

//        System.err.println("COMPARING: " +
//            (l == null ? "null" : l.getClass().getName()) + " " +
//            (r == null ? "null" : r.getClass().getName()));

        if (l instanceof InitialContext || l instanceof SelfContext){
            l = ((EvalContext)l).getSingleNodePointer();
        }

        if (r instanceof InitialContext || r instanceof SelfContext){
            r = ((EvalContext)r).getSingleNodePointer();
        }

        if (l instanceof Collection){
            l = ((Collection)l).iterator();
        }

        if (r instanceof Collection){
            r = ((Collection)r).iterator();
        }

        if ((l instanceof Iterator) && !(r instanceof Iterator)){
            return contains((Iterator)l, r);
        }
        else if (!(l instanceof Iterator) && (r instanceof Iterator)){
            return contains((Iterator)r, l);
        }
        else if (l instanceof Iterator && r instanceof Iterator){
            return findMatch((Iterator)l, (Iterator)r);
        }

        return equal(l, r);
    }

    protected boolean contains(Iterator it, Object value){
        while (it.hasNext()){
            Object element = it.next();
            if (equal(element, value)){
                return true;
            }
        }
        return false;
    }

    protected boolean findMatch(Iterator lit, Iterator rit){
        HashSet left = new HashSet();
        while (lit.hasNext()){
            left.add(lit.next());
        }
        while (rit.hasNext()){
            if (contains(left.iterator(), rit.next())){
                return true;
            }
        }
        return false;
    }

    protected boolean equal(Object l, Object r){
        if (l instanceof Pointer && r instanceof Pointer){
            if (l.equals(r)){
                return true;
            }
        }

        if (l instanceof Pointer){
            l = ((Pointer)l).getValue();
        }

        if (r instanceof Pointer){
            r = ((Pointer)r).getValue();
        }

        if (l == r){
            return true;
        }

//        System.err.println("COMPARING VALUES: " + l + " " + r);
        if (l instanceof Boolean || r instanceof Boolean){
            return (InfoSetUtil.booleanValue(l) == InfoSetUtil.booleanValue(r));
        }
        else if (l instanceof Number || r instanceof Number){
            return (InfoSetUtil.doubleValue(l) == InfoSetUtil.doubleValue(r));
        }
        else if (l instanceof String || r instanceof String){
            return (InfoSetUtil.stringValue(l).equals(InfoSetUtil.stringValue(r)));
        }
        else if (l == null){
            return r == null;
        }
        return l.equals(r);
    }

    /**
     * Extracts all values from a context
     */
    private Set valueSet(EvalContext context){
        HashSet set = new HashSet();
        while(context.hasNext()){
            context.next();
            NodePointer pointer = context.getCurrentNodePointer();
            set.add(pointer.getValue());
        }
        return set;
    }

    /**
     * Computes <code>"left and right"<code>
     */
    protected Object and(EvalContext context, Expression[] arguments){
        for (int i = 0; i < arguments.length; i++){
            if (!InfoSetUtil.booleanValue(arguments[i].computeValue(context))){
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Computes <code>"left or right"<code>
     */
    protected Object or(EvalContext context, Expression[] arguments){
        for (int i = 0; i < arguments.length; i++){
            if (InfoSetUtil.booleanValue(arguments[i].computeValue(context))){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}