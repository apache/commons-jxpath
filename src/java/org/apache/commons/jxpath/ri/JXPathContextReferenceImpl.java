/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/JXPathContextReferenceImpl.java,v 1.10 2002/04/24 03:32:48 dmitri Exp $
 * $Revision: 1.10 $
 * $Date: 2002/04/24 03:32:48 $
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


import java.lang.ref.SoftReference;
import java.util.*;

import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.Functions;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.PackageFunctions;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.axes.RootContext;
import org.apache.commons.jxpath.ri.compiler.Expression;
import org.apache.commons.jxpath.ri.compiler.TreeCompiler;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.apache.commons.jxpath.ri.model.VariablePointer;
import org.apache.commons.jxpath.ri.model.beans.BeanPointerFactory;
import org.apache.commons.jxpath.ri.model.beans.CollectionPointerFactory;
import org.apache.commons.jxpath.ri.model.beans.DynamicPointerFactory;
import org.apache.commons.jxpath.ri.model.container.ContainerPointerFactory;
import org.apache.commons.jxpath.ri.model.dom.DOMPointerFactory;
import org.apache.commons.jxpath.util.TypeUtils;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.10 $ $Date: 2002/04/24 03:32:48 $
 */
public class JXPathContextReferenceImpl extends JXPathContext
{
    private static final Compiler compiler = new TreeCompiler();
    private static final Map compiled = new HashMap();
    private static final PackageFunctions genericFunctions = new PackageFunctions("", null);
    private static boolean useSoftCache = true;
    private static int cleanupCount = 0;
    private static Vector nodeFactories = new Vector();
    private static NodePointerFactory nodeFactoryArray[] = null;
    static {
        nodeFactories.add(new CollectionPointerFactory());
        nodeFactories.add(new BeanPointerFactory());
        nodeFactories.add(new DynamicPointerFactory());
        nodeFactories.add(new DOMPointerFactory());
        nodeFactories.add(new ContainerPointerFactory());
        createNodeFactoryArray();
    }
    private NodePointer rootPointer;

    // The frequency of the cache cleanup
    private static final int CLEANUP_THRESHOLD = 500;

    protected JXPathContextReferenceImpl(JXPathContext parentContext, Object contextBean){
        super(parentContext, contextBean);
        synchronized (nodeFactories){
            createNodeFactoryArray();
        }
    }

    private static void createNodeFactoryArray() {
        if (nodeFactoryArray == null) {
            nodeFactoryArray =
                (NodePointerFactory[]) nodeFactories.toArray(new NodePointerFactory[0]);
            Arrays.sort(nodeFactoryArray, new Comparator() {
                public int compare(Object a, Object b) {
                    int orderA = ((NodePointerFactory) a).getOrder();
                    int orderB = ((NodePointerFactory) b).getOrder();
                    return orderA - orderB;
                }
            });
        }
    }

    /**
     * Call this with a custom NodePointerFactory to add support for
     * additional types of objects.  Make sure the factory returns
     * a name that puts it in the right position on the list of factories.
     */
    public static void addNodePointerFactory(NodePointerFactory factory){
        synchronized (nodeFactories){
            nodeFactories.add(factory);
            nodeFactoryArray = null;
        }
    }

    public static NodePointerFactory[] getNodePointerFactories(){
        return nodeFactoryArray;
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
//        System.gc();
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
//        System.err.println("XPATH: " + xpath);
        Object result = eval(xpath, true);
        if (result == null && !lenient){
            throw new RuntimeException("No value for xpath: " + xpath);
        }

        if (result instanceof EvalContext){
            EvalContext ctx = (EvalContext)result;
            result = ctx.getSingleNodePointer();
        }
        if (result instanceof NodePointer){
            result = ((NodePointer)result).getCanonicalValue();
        }
        return result;
    }

    /**
     * Calls getValue(xpath), converts the result to the required type
     * and returns the result of the conversion.
     */
    public Object getValue(String xpath, Class requiredType){
        Object value = getValue(xpath);
        if (value != null && requiredType != null){
            if (!TypeUtils.canConvert(value, requiredType)){
                throw new RuntimeException("Invalid expression type. '" + xpath +
                    "' returns " + value.getClass().getName() +
                    ". It cannot be converted to " + requiredType.getName());
            }
            value = TypeUtils.convert(value, requiredType);
        }
        return value;
    }

    /**
     * Traverses the xpath and returns a List of objects. Even if
     * there is only one object that matches the xpath, it will be returned
     * as a collection with one element.  If the xpath matches no properties
     * in the graph, the List will be empty.
     */
    public List eval(String xpath){
//        System.err.println("XPATH: " + xpath);
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
//        System.err.println("XPATH: " + xpath);
        Object result = eval(xpath, true);
        if (result instanceof EvalContext){
            result = ((EvalContext)result).getSingleNodePointer();
        }
        if (result instanceof Pointer){
            return (Pointer)result;
        }
        else {
            return NodePointer.newNodePointer(null, result, getLocale());
        }
    }

    /**
     */
    public void setValue(String xpath, Object value){
        try {
            setValue(xpath, value, false);
        }
        catch (Throwable ex){
            throw new RuntimeException("Exception trying to set value with xpath " +
                    xpath + ". " + ex.getMessage());
        }
    }

    /**
     */
    public void createPath(String xpath, Object value){
//        System.err.println("CREATING XPATH: " + xpath);
        try {
            setValue(xpath, value, true);
        }
        catch (Throwable ex){
            ex.printStackTrace();
            throw new RuntimeException("Exception trying to create xpath " +
                    xpath + ". " + ex.getMessage());
        }
    }

    private void setValue(String xpath, Object value, boolean create){
        Object result = eval(xpath, true);
//        System.err.println("RESULT: " + result);
        Pointer pointer = null;

        if (result instanceof Pointer){
            pointer = (Pointer)result;
        }
        else if (result instanceof EvalContext){
            EvalContext ctx = (EvalContext)result;
            pointer = ctx.getSingleNodePointer();
        }
        else {
            // This should never happen
            throw new RuntimeException("Cannot set value for xpath: " + xpath);
        }
        if (create){
            ((NodePointer)pointer).createPath(this, value);
        }
        else {
            pointer.setValue(value);
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
            list.add(NodePointer.newNodePointer(null, result, getLocale()));
        }
        return list;
    }

    private Object eval(String xpath, boolean firstMatchLookup) {
        Expression expr = compile(xpath);
        return getRootContext().eval(expr, firstMatchLookup);
    }

    private void printPointer(NodePointer pointer){
        Pointer p = pointer;
        while (p != null){
            System.err.println((p == pointer ? "POINTER: " : " PARENT: ") + p.getClass() + " " + p.asPath());
            if (p instanceof NodePointer){
                p = ((NodePointer)p).getParent();
            }
        }
    }

    private synchronized NodePointer getRootPointer(){
        if (rootPointer == null){
            rootPointer = NodePointer.newNodePointer(new QName(null, "root"),
                getContextBean(), getLocale());
        }
        return rootPointer;
    }

    private EvalContext getRootContext(){
        return new RootContext(this, getRootPointer());
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
        String varName = name.toString();
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
            return new VariablePointer(name);
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
        throw new RuntimeException("Undefined function: " + functionName.toString());
    }
}