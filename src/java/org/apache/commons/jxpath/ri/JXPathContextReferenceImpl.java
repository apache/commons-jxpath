/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/JXPathContextReferenceImpl.java,v 1.2 2001/09/03 01:22:30 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2001/09/03 01:22:30 $
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
package org.apache.commons.jxpath.ri;


import java.util.*;

import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Functions;
import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.PackageFunctions;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.pointers.*;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.axes.*;
import java.lang.ref.SoftReference;
import org.w3c.dom.*;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2001/09/03 01:22:30 $
 */
public class JXPathContextReferenceImpl extends JXPathContext
{
    private static final Compiler compiler = new TreeCompiler();
    private static final Map compiled = new HashMap();
    private static final PackageFunctions genericFunctions = new PackageFunctions("", null);
    private static boolean useSoftCache = true;
    private static int cleanupCount = 0;

    // The frequency of the cache cleanup
    private static final int CLEANUP_THRESHOLD = 500;

    protected JXPathContextReferenceImpl(JXPathContext parentContext, Object contextBean){
        super(parentContext, contextBean);
    }

    private static Expression compile(String xpath){
        Expression expr;
        if (useSoftCache){
            expr = null;
            SoftReference ref = (SoftReference)compiled.get(xpath);
            if (ref != null){
                expr = (Expression)ref.get();
            }
            if (expr == null){
                expr = (Expression)Parser.parseExpression(xpath, compiler);
                expr.setEvaluationMode(Expression.EVALUATION_MODE_ONCE);
                compiled.put(xpath, new SoftReference(expr));
                if (cleanupCount++ >= CLEANUP_THRESHOLD){
                    cleanupCache();
                }
            }
        }
        else {
            expr = (Expression)compiled.get(xpath);
            if (expr == null){
                expr = (Expression)Parser.parseExpression(xpath, compiler);
                expr.setEvaluationMode(Expression.EVALUATION_MODE_ONCE);
                compiled.put(xpath, expr);
            }
        }
//        }
        return expr;
    }

    private static void cleanupCache(){
        System.gc();
        Iterator it = compiled.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            if (((SoftReference)me.getValue()).get() == null){
                it.remove();
            }
        }
        cleanupCount = 0;
    }

    /**
     * Traverses the xpath and returns the resulting object. Primitive
     * types are wrapped into objects.
     */
    public Object getValue(String xpath){
        Object result = eval(xpath, true);
        if (result instanceof NodePointer){
            result = ((NodePointer)result).getValue();
        }
        else if (result instanceof EvalContext){
            EvalContext ctx = (EvalContext)result;
            while(ctx.nextSet()){
                if (ctx.next()){
                    result = ctx.getCurrentNodePointer().getValue();
                    break;
                }
            }
        }
        if (result instanceof Node){
            result = EvalContext.stringValue((Node)result);
        }
        return result;
    }

    /**
     * Traverses the xpath and returns a List of objects. Even if
     * there is only one object that matches the xpath, it will be returned
     * as a collection with one element.  If the xpath matches no properties
     * in the graph, the List will be empty.
     */
    public List eval(String xpath){
        Object result = eval(xpath, false);
        List list = new ArrayList();
        if (result instanceof EvalContext){
            EvalContext context = (EvalContext)result;
            while(context.nextSet()){
                while(context.next()){
                    Pointer pointer = context.getCurrentNodePointer();
                    list.add(pointer.getValue());
                }
            }
        }
        else if (result instanceof Pointer){
            list.add(((Pointer)result).getValue());
        }
        else {
            list.add(result);
        }
        return list;
    }

    public Pointer locateValue(String xpath){
        Object result = eval(xpath, true);
        if (result instanceof Pointer){
            return (Pointer)result;
        }
        else {
            return NodePointer.createNodePointer(null, result);
        }
    }

    /**
     */
    public void setValue(String xpath, Object value){
        Object result = eval(xpath, true);
        if (result instanceof Pointer){
            ((Pointer)result).setValue(value);
        }
        else if (result instanceof EvalContext){
            EvalContext ctx = (EvalContext)result;
            while(ctx.nextSet()){
                if (ctx.next()){
                    ctx.getCurrentNodePointer().setValue(value);
                    return;
                }
            }
            throw new RuntimeException("Cannot set value for xpath: " + xpath + ": no such property");
        }
        else {
            System.err.println("RESULT: " + result);
            throw new RuntimeException("Cannot set value for xpath: " + xpath);
        }
    }

    public List locate(String xpath){
        Object result = eval(xpath, false);
        List list = new ArrayList();
        if (result instanceof EvalContext){
            EvalContext context = (EvalContext)result;
            while(context.nextSet()){
                while(context.next()){
                    Pointer pointer = context.getCurrentNodePointer();
                    list.add(pointer);
                }
            }
        }
        else if (result instanceof Pointer){
            list.add((Pointer)result);
        }
        else {
            list.add(NodePointer.createNodePointer(null, result));
        }
        return list;
    }

    private Object eval(String xpath, boolean firstMatchLookup) {
        Expression expr = compile(xpath);
        NodePointer pointer = NodePointer.createNodePointer(new QName(null, "root"), getContextBean());
        EvalContext ctx = new RootContext(this, pointer);
//        System.err.println("XPATH = " + xpath);
        return ctx.eval(expr, firstMatchLookup);
    }

    private List resolveNodeSet(List list){
        List result = new ArrayList();
        for (int i = 0; i < list.size(); i++){
            Object element = list.get(i);
            if (element instanceof NodePointer){
                element = ((NodePointer)element).getValue();
            }
            result.add(element);
        }
        return result;
    }

    public NodePointer getVariablePointer(QName name){
        String varName = name.asString();
        JXPathContext varCtx = this;
        Variables vars = null;
        while (varCtx != null){
            vars = varCtx.getVariables();
            if (vars.isDeclaredVariable(varName)){
                break;
            }
            varCtx = varCtx.getParentContext();
            vars = null;
        }
        if (vars != null){
            return new VariablePointer(vars, name);
        }
        else {
            throw new RuntimeException("Undefined variable: " + varName);
        }
    }

    public Function getFunction(QName functionName, Object[] parameters){
        String namespace = functionName.getPrefix();
        String name = functionName.getName();
        JXPathContext funcCtx = this;
        Function func = null;
        Functions funcs;
        while (funcCtx != null){
            funcs = funcCtx.getFunctions();
            if (funcs != null){
                func = funcs.getFunction(namespace, name, parameters);
                if (func != null){
                    return func;
                }

                funcCtx = funcCtx.getParentContext();
            }
            else {
                break;
            }
        }
        func = genericFunctions.getFunction(namespace, name, parameters);
        if (func != null){
            return func;
        }
        throw new RuntimeException("Undefined function: " + functionName.asString());
    }
}