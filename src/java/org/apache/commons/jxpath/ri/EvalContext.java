/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/EvalContext.java,v 1.3 2001/09/21 23:22:43 dmitri Exp $
 * $Revision: 1.3 $
 * $Date: 2001/09/21 23:22:43 $
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

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.ri.pointers.*;
import org.apache.commons.jxpath.ri.axes.*;
import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.Pointer;
import java.util.*;
import org.w3c.dom.*;

/**
 * An XPath evaluation context.
 *
 * When evaluating a path, a chain of EvalContexts is created, each
 * context in the chain representing a step of the path. Subclasses of EvalContext
 * implement behavior of various XPath axes: "child::", "parent::" etc.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.3 $ $Date: 2001/09/21 23:22:43 $
 */
public abstract class EvalContext implements ExpressionContext {
    protected EvalContext parentContext;
    protected RootContext rootContext;
    protected int position = 0;
    private boolean startedSetIteration = false;

    public EvalContext(EvalContext parentContext){
        this.parentContext = parentContext;
    }

    /**
     * Returns the root context of the path, which provides easy
     * access to variables and functions.
     */
    public RootContext getRootContext(){
        if (rootContext == null){
            rootContext = parentContext.getRootContext();
        }
        return rootContext;
    }

    /**
     * Sets current position = 0, which is the pre-iteration state.
     * @deprecated
     */
    protected void reset(){
        position = 0;
    }

    public int getCurrentPosition(){
        return position;
    }

    /**
     * If the iteration has not started yet, starts the iteration and
     * returns the first encountered Pointer that matches the current
     * step's criteria.  Otherwise, returns the current pointer.
     */
    public Pointer getContextNodePointer(){
        if (position == 0){
            while(nextSet()){
                if (next()){
                    return getCurrentNodePointer();
                }
            }
            return null;
        }
        else {
            return getCurrentNodePointer();
        }
    }

    /**
     * Iterates through the current context collecting
     * pointers to all elements.
     */
    public List getContextNodeList(){
        List list = new ArrayList();
        while(nextSet()){
            while(next()){
                list.add(getCurrentNodePointer());
            }
        }
        return list;
    }

    /**
     * Returns the current context node. Undefined before the beginning
     * of the iteration.
     */
    public abstract NodePointer getCurrentNodePointer();

    /**
     * Returns true if there is another sets of objects to interate over.
     * Resets the current position and node.
     */
    public boolean nextSet(){
        setPosition(0);     // Restart iteration within the set

        // Most of the time you have one set per parent node
        // First time this method is called, we should look for
        // the first parent set that contains at least one node.
        if (!startedSetIteration){
            startedSetIteration = true;
            while (parentContext.nextSet()){
                if (parentContext.next()){
                    return true;
                }
            }
            return false;
        }

        // In subsequent calls, we see if the parent context
        // has any nodes left in the current set
        if (parentContext.next()){
            return true;
        }

        // If not, we look for the next set that contains
        // at least one node
        while (parentContext.nextSet()){
            if (parentContext.next()){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there is another object in the current set.
     * Switches the current position and node to the next object.
     */
    public abstract boolean next();

    /**
     * Moves the current position to the specified index. Used with integer
     * predicates to quickly get to the n'th element of the node set.
     * Returns false if the position is out of the node set range.
     * You can call it with 0 as the position argument to restart the iteration.
     */
    public boolean setPosition(int position){
        this.position = position;
        return true;
    }

    /**
     * Creates an EvalContext with the value of the specified
     * variable as its context node.
     */
    protected EvalContext getVariable(QName variableName){
        return getRootContext().getVariableContext(variableName);
    }

    protected static Double ZERO = new Double(0);
    protected static Double ONE = new Double(1);
    protected static Double NaN = new Double(Double.NaN);

    /**
     * Evaluates the expression. If the result is a node set, returns
     * the first element of the node set.
     */
    public Object eval(Expression expression){
        return eval(expression, true);
    }

    /**
     * Evaluates the expression. If the result is a node set, returns
     * the whole set if firstMatch==false or the first element otherwise.
     */
    public Object eval(Expression expression, boolean firstMatch){
        Object value = null;
        switch (expression.getEvaluationMode()){
            case Expression.EVALUATION_MODE_ONCE:
            case Expression.EVALUATION_MODE_ALWAYS:
                value = evalExpression(expression, firstMatch);
                break;
            case Expression.EVALUATION_MODE_ONCE_AND_SAVE:
                RootContext root = getRootContext();
                int id = expression.getID();
                if (id == -1){
                    // evaluate the expression and save the intermediate result
                    value = evalExpression(expression, firstMatch);
                    id = root.setRegisteredValue(value);
                    expression.setID(id);
                }
                else {
                    value = root.getRegisteredValue(id);
                }
        }
        return value;
    }

    /**
     * Evaluates the expression. If the result is a node set, returns
     * the whole set if firstMatch==false or the first element otherwise.
     */
    private Object evalExpression(Expression expression, boolean firstMatch){
        int op = expression.getExpressionTypeCode();
        switch(op){
        case Expression.OP_CONSTANT:
            return ((Constant)expression).getValue();

        case Expression.OP_UNION:
            return union(((CoreOperation)expression).getArg1(),
                         ((CoreOperation)expression).getArg2());

        case Expression.OP_UNARY_MINUS:
            return minus(((CoreOperation)expression).getArg1());

        case Expression.OP_SUM:
            return sum(((CoreOperation)expression).getArguments());

        case Expression.OP_MINUS:
            return minus(((CoreOperation)expression).getArg1(),
                         ((CoreOperation)expression).getArg2());

        case Expression.OP_MULT:
            return mult(((CoreOperation)expression).getArg1(),
                        ((CoreOperation)expression).getArg2());

        case Expression.OP_DIV:
            return div(((CoreOperation)expression).getArg1(),
                       ((CoreOperation)expression).getArg2());

        case Expression.OP_MOD:
            return mod(((CoreOperation)expression).getArg1(),
                       ((CoreOperation)expression).getArg2());

        case Expression.OP_LT:
            return lt(((CoreOperation)expression).getArg1(),
                      ((CoreOperation)expression).getArg2());

        case Expression.OP_GT:
            return gt(((CoreOperation)expression).getArg1(),
                      ((CoreOperation)expression).getArg2());

        case Expression.OP_LTE:
            return lte(((CoreOperation)expression).getArg1(),
                       ((CoreOperation)expression).getArg2());

        case Expression.OP_GTE:
            return gte(((CoreOperation)expression).getArg1(),
                       ((CoreOperation)expression).getArg2());

        case Expression.OP_EQ:
            return eq(((CoreOperation)expression).getArg1(),
                      ((CoreOperation)expression).getArg2());

        case Expression.OP_NE:
            return ne(((CoreOperation)expression).getArg1(),
                      ((CoreOperation)expression).getArg2());

        case Expression.OP_AND:
            return and(((CoreOperation)expression).getArguments());

        case Expression.OP_OR:
            return or(((CoreOperation)expression).getArguments());

        case Expression.OP_VAR:
            return getVariable(((VariableReference)expression).getVariableName());

        case Expression.OP_CORE_FUNCTION:
            return coreFunction((CoreFunction)expression);

        case Expression.OP_LOCATION_PATH:
            return path((LocationPath)expression, firstMatch);

        case Expression.OP_EXPRESSION_PATH:
            return expressionPath(((ExpressionPath)expression).getExpression(),
                                  ((ExpressionPath)expression).getPredicates(),
                                  ((ExpressionPath)expression).getSteps(), firstMatch);

        case Expression.OP_FUNCTION:
            return function(((ExtensionFunction)expression).getFunctionName(),
                            ((ExtensionFunction)expression).getArguments());
        }
        return null;
    }

    /**
     * Computes <code>"left | right"<code>
     */
    protected Object union(Expression left, Expression right){
        Object l = eval(left, false);
        Object r = eval(right, false);
        EvalContext lctx;
        if (l instanceof EvalContext){
            lctx = (EvalContext)l;
        }
        else {
            lctx = getRootContext().getConstantContext(l);
        }
        EvalContext rctx;
        if (r instanceof EvalContext){
            rctx = (EvalContext)r;
        }
        else {
            rctx = getRootContext().getConstantContext(r);
        }
        return new UnionContext(getRootContext(), new EvalContext[]{lctx, rctx});
    }

    /**
     * Computes <code>"-arg"<code>
     */
    protected Object minus(Expression arg){
        double a = doubleValue(eval(arg));
        return new Double(-a);
    }

    /**
     * Computes <code>"a + b + c + d"<code>
     */
    protected Object sum(Expression[] arguments){
        double s = 0.0;
        for (int i = 0; i < arguments.length; i++){
            s += doubleValue(eval(arguments[i]));
        }
        return new Double(s);
    }

    /**
     * Computes <code>"left - right"<code>
     */
    protected Object minus(Expression left, Expression right){
        double l = doubleValue(eval(left));
        double r = doubleValue(eval(right));
        return new Double(l - r);
    }

    /**
     * Computes <code>"left div right"<code>
     */
    protected Object div(Expression left, Expression right){
        double l = doubleValue(eval(left));
        double r = doubleValue(eval(right));
        return new Double(l/r);
    }

    /**
     * Computes <code>"left * right"<code>
     */
    protected Object mult(Expression left, Expression right){
        double l = doubleValue(eval(left));
        double r = doubleValue(eval(right));
        return new Double(l*r);
    }

    /**
     * Computes <code>"left mod right"<code>
     */
    protected Object mod(Expression left, Expression right){
        long l = (long)doubleValue(eval(left));
        long r = (long)doubleValue(eval(right));
        return new Double(l%r);
    }

    /**
     * Computes <code>"left &lt; right"<code>
     */
    protected Object lt(Expression left, Expression right){
        double l = doubleValue(eval(left));
        double r = doubleValue(eval(right));
        return l < r ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Computes <code>"left &gt; right"<code>
     */
    protected Object gt(Expression left, Expression right){
        double l = doubleValue(eval(left));
        double r = doubleValue(eval(right));
        return l > r ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Computes <code>"left &lt;= right"<code>
     */
    protected Object lte(Expression left, Expression right){
        double l = doubleValue(eval(left));
        double r = doubleValue(eval(right));
        return l <= r ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Computes <code>"left &gt;= right"<code>
     */
    protected Object gte(Expression left, Expression right){
        double l = doubleValue(eval(left));
        double r = doubleValue(eval(right));
        return l >= r ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Computes <code>"left = right"<code>
     */
    protected Object eq(Expression left, Expression right){
        return equal(left, right) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Computes <code>"left != right"<code>
     */
    protected Object ne(Expression left, Expression right){
        return equal(left, right) ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * Compares two values
     */
    protected boolean equal(Expression left, Expression right){
        Object l = eval(left);
        Object r = eval(right);
        boolean result;
        if (l == r){
            return true;
        }

        if (l instanceof EvalContext && r instanceof EvalContext){
            Set lset = new HashSet(((EvalContext)l).valueSet());
            Set rset = new HashSet(((EvalContext)r).valueSet());
            return lset.equals(rset);
        }

        if (l instanceof EvalContext){
            l = ((EvalContext)l).getContextNodePointer();
        }
        if (r instanceof EvalContext){
            r = ((EvalContext)r).getContextNodePointer();
        }

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

        if (l instanceof Boolean || r instanceof Boolean){
            result = (booleanValue(l) == booleanValue(r));
        }
        else if (l instanceof Number || r instanceof Number){
            result = (doubleValue(l) == doubleValue(r));
        }
        else if (l instanceof String || r instanceof String){
            result = (stringValue(l).equals(stringValue(r)));
        }
        else if (l == null){
            return r == null;
        }
        else {
            result = l.equals(r);
        }
        return result;
    }

    /**
     * Extracts all values from a context
     */
    private Set valueSet(){
        HashSet set = new HashSet();
        while(nextSet()){
            while(next()){
                Pointer pointer = getCurrentNodePointer();
                set.add(pointer.getValue());
            }
        }
        return set;
    }

    /**
     * Computes <code>"left and right"<code>
     */
    protected Object and(Expression[] arguments){
        for (int i = 0; i < arguments.length; i++){
            if (!booleanValue(eval(arguments[i]))){
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Computes <code>"left or right"<code>
     */
    protected Object or(Expression[] arguments){
        for (int i = 0; i < arguments.length; i++){
            if (booleanValue(eval(arguments[i]))){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Converts the supplied object to String
     */
    public static String stringValue(Object object){
        if (object instanceof String){
            return (String)object;
        }
        else if (object instanceof Number){
            return String.valueOf(((Number)object).doubleValue());
        }
        else if (object instanceof Boolean){
            return ((Boolean)object).booleanValue() ? "true" : "false";
        }
        else if (object == null){
            return "";
        }
        else if (object instanceof Node){
            Node node = (Node)object;
            int nodeType = node.getNodeType();
            if (nodeType == Node.COMMENT_NODE){
                String text = ((Comment)node).getData();
                return text == null ? "" : text.trim();
            }
            else if (nodeType == Node.TEXT_NODE ||
                    nodeType == Node.CDATA_SECTION_NODE){
                String text = node.getNodeValue();
                return text == null ? "" : text.trim();
            }
            else if (nodeType == Node.PROCESSING_INSTRUCTION_NODE){
                String text = ((ProcessingInstruction)node).getData();
                return text == null ? "" : text.trim();
            }
            else {
                NodeList list = node.getChildNodes();
                StringBuffer buf = new StringBuffer(16);
                for(int i = 0; i < list.getLength();i++) {
                    Node child = list.item(i);
                    if (child.getNodeType() == Node.TEXT_NODE){
                        buf.append(child.getNodeValue());
                    }
                    else {
                        buf.append(stringValue(child));
                    }
                }
                return buf.toString().trim();
            }
        }
        else if (object instanceof NodePointer){
            return stringValue(((NodePointer)object).getValue());
        }
        else if (object instanceof EvalContext){
            EvalContext ctx = (EvalContext)object;
            Pointer ptr = ctx.getContextNodePointer();
            if (ptr != null){
                return stringValue(ptr);
            }
            return "";
        }
        return String.valueOf(object);
    }

    /**
     * Converts the supplied object to Number
     */
    protected Number number(Object object){
        if (object instanceof Number){
            return (Number)object;
        }
        else if (object instanceof Boolean){
            return ((Boolean)object).booleanValue() ? ONE : ZERO;
        }
        else if (object instanceof String){
            Double value;
            try {
                value = new Double((String)object);
            }
            catch (NumberFormatException ex){
                value = NaN;
            }
            return value;
        }
        else if (object instanceof Node){
            return number(stringValue(object));
        }
        else if (object instanceof EvalContext){
            return number(stringValue(object));
        }
        else if (object instanceof NodePointer){
            return number(((NodePointer)object).getValue());
        }
        return ZERO;
    }

    /**
     * Converts the supplied object to double
     */
    public static double doubleValue(Object object){
        if (object instanceof Number){
            return ((Number)object).doubleValue();
        }
        else if (object instanceof Boolean){
            return ((Boolean)object).booleanValue() ? 0.0 : 1.0;
        }
        else if (object instanceof String){
            if (object.equals("")){
                return 0.0;
            }

            double value;
            try {
                value = Double.parseDouble((String)object);
            }
            catch (NumberFormatException ex){
                value = Double.NaN;
            }
            return value;
        }
        else if (object instanceof Node){
            return doubleValue(stringValue(object));
        }
        else if (object instanceof NodePointer){
            return doubleValue(((NodePointer)object).getValue());
        }
        else if (object instanceof EvalContext){
            return doubleValue(stringValue(object));
        }
        return 0;
    }

    /**
     * Converts the supplied object to boolean
     */
    public static boolean booleanValue(Object object){
        if (object instanceof Number){
            double value = ((Number)object).doubleValue();
            return value != 0 && value != -0 && !Double.isNaN(value);
        }
        else if (object instanceof Boolean){
            return ((Boolean)object).booleanValue();
        }
        else if (object instanceof EvalContext){
            EvalContext ctx = (EvalContext)object;
            return ctx.nextSet() && ctx.next();
        }
        else if (object instanceof String){
            return ((String)object).length() != 0;
        }
        else if (object instanceof Node){
            return stringValue(object).length() != 0;
        }
        else if (object instanceof NodePointer){
            return booleanValue(((NodePointer)object).getValue());
        }
        return false;
    }

    /**
     * Walks a location path
     */
    protected Object path(LocationPath path, boolean firstMatch){
        Step steps[] = path.getSteps();

        // Create a chain of contexts
        EvalContext rootContext;
        if (path.isAbsolute()){
            rootContext = getRootContext();
        }
        else {
            rootContext = this;
        }
        if (firstMatch){
            boolean basic = path.getEvaluationHint(LocationPath.BASIC_PATH_HINT).equals(Boolean.TRUE);
            if (basic){
                Object result = tryBasicPath(new InitialContext(rootContext), steps);
                if (result != FAILURE){
                    return result;
                }
            }
        }
        EvalContext aContext = new InitialContext(rootContext);
        return evalSteps(aContext, steps, firstMatch);
    }


    private static final Object FAILURE = new Object();

    /**
     * Attempts to evaluate a simple path without traversing contexts -
     * straight from a NodePointer to a NodePointer. This only works
     * in some cases though. Specifically, it works with JavaBeans
     * and objects with Dynamic Properties, but does not work with
     * DOM objects.
     */
    private Object tryBasicPath(EvalContext context, Step steps[]){
        NodePointer ptr = (NodePointer)context.getContextNodePointer();
        if (ptr == null || !(ptr instanceof PropertyOwnerPointer)){
            return FAILURE;
        }
        PropertyOwnerPointer pointer = (PropertyOwnerPointer)ptr.clone();
        for (int i = 0; i < steps.length; i++){
            String propertyName = ((NodeNameTest)steps[i].getNodeTest()).getNodeName().getName();
            pointer = pointer.getPropertyPointer();
            ((PropertyPointer)pointer).setPropertyName(propertyName);

            Expression predicates[] = steps[i].getPredicates();
            if (predicates != null && predicates.length != 0){
                boolean dynamicProperty = false;
                Expression expr = (Expression)predicates[0].getEvaluationHint(CoreOperation.DYNAMIC_PROPERTY_ACCESS_HINT);
                if (expr != null){
                    String prop = stringValue(eval(expr, true));
                    pointer = pointer.getPropertyPointer();
                    ((PropertyPointer)pointer).setPropertyName(prop);
                }
                else {
                    Object predicate = eval(predicates[0], true);
                    if (predicate instanceof EvalContext){
                        predicate = ((EvalContext)predicate).getContextNodePointer();
                    }
                    if (predicate instanceof Pointer){
                        predicate = ((Pointer)predicate).getValue();
                    }
                    if (predicate == null){
                        throw new RuntimeException("Predicate is null: " + predicates[0]);
                    }
                    if (predicate instanceof Number){
                        int index = (int)(doubleValue(predicate) + 0.5);
                        if (index > 0 && index <= pointer.getLength()){
                            pointer.setIndex(index - 1);
                        }
                        else {
                            return null;
                        }
                    }
                    else if (!booleanValue(predicate)){
                        return null;
                    }
                }
            }
            else {
                return FAILURE;
            }
        }
        return pointer;
    }

    /**
     * Walks an expression path (a path that starts with an expression)
     */
    protected Object expressionPath(Expression expression, Expression predicates[], Step steps[], boolean firstMatch){
        Object value = eval(expression, false);
        EvalContext context;
        if (value instanceof InitialContext){
            // This is an optimization. We can avoid iterating through a collection
            // if the context bean is in fact one.
            context = (InitialContext)value;
        }
        else if (value instanceof EvalContext){
            // UnionContext will collect all values from the "value" context
            // and treat the whole thing as a big collection.
            context = new UnionContext(this, new EvalContext[]{(EvalContext)value});
        }
        else {
            context = getRootContext().getConstantContext(value);
        }
        if (predicates != null){
            for (int j = 0; j < predicates.length; j++){
                context = new PredicateContext(context, predicates[j]);
            }
        }
        return evalSteps(context, steps, firstMatch);
    }

    /**
     * Given a root context, walks a path therefrom
     */
    private Object evalSteps(EvalContext context, Step steps[], boolean firstMatch){
        for (int i = 0; i < steps.length; i++){
            context = createContextForStep(context, steps[i].getAxis(), steps[i].getNodeTest());
            Expression predicates[] = steps[i].getPredicates();
            if (predicates != null){
                for (int j = 0; j < predicates.length; j++){
                    context = new PredicateContext(context, predicates[j]);
                }
            }
        }

        if (firstMatch){
            Pointer ptr = context.getContextNodePointer();
//            System.err.println("GETTING CTX POINTER: " + context + " " + ptr);
            return ptr;
        }
        else {
            return context;
        }
    }

    /**
     * Different axes are serviced by different contexts. This method
     * allocates the right context for the supplied step.
     */
    protected EvalContext createContextForStep(EvalContext context, int axis, NodeTest nodeTest){
        switch(axis){
            case Compiler.AXIS_ANCESTOR:
                return new AncestorContext(context, false, nodeTest);
            case Compiler.AXIS_ANCESTOR_OR_SELF:
                return new AncestorContext(context, true, nodeTest);
            case Compiler.AXIS_ATTRIBUTE:
                return new AttributeContext(context, nodeTest);
            case Compiler.AXIS_CHILD:
                return new ChildContext(context, nodeTest, false, false);
            case Compiler.AXIS_DESCENDANT:
                return new DescendantContext(context, false, nodeTest);
            case Compiler.AXIS_DESCENDANT_OR_SELF:
                return new DescendantContext(context, true, nodeTest);
            case Compiler.AXIS_FOLLOWING:
                return new PrecedingOrFollowingContext(context, nodeTest, false);
            case Compiler.AXIS_FOLLOWING_SIBLING:
                return new ChildContext(context, nodeTest, true, false);
            case Compiler.AXIS_NAMESPACE:
                return new NamespaceContext(context, nodeTest);
            case Compiler.AXIS_PARENT:
                return new ParentContext(context, nodeTest);
            case Compiler.AXIS_PRECEDING:
                return new PrecedingOrFollowingContext(context, nodeTest, true);
            case Compiler.AXIS_PRECEDING_SIBLING:
                return new ChildContext(context, nodeTest, true, true);
            case Compiler.AXIS_SELF:
                return new SelfContext(context, nodeTest);
        }
        return null;        // Never happens
    }

    /**
     * Computes an extension function
     */
    protected Object function(QName functionName, Expression[] arguments){
        Object[] parameters = null;
        if (arguments != null){
            parameters = new Object[arguments.length];
            for (int i = 0; i < arguments.length; i++){
                parameters[i] = eval(arguments[i]);
            }
        }
        Function function = getRootContext().getFunction(functionName, parameters);
        if (function == null){
            throw new RuntimeException("No such function: " + functionName +
                 Arrays.asList(parameters));
        }

        return function.invoke(parameters);
    }

    /**
     * Computes a built-in function
     */
    protected Object coreFunction(CoreFunction function){
        int code = function.getFunctionCode();
        switch(code){
            case Compiler.FUNCTION_LAST:                return functionLast(function);
            case Compiler.FUNCTION_POSITION:            return functionPosition(function);
            case Compiler.FUNCTION_COUNT:               return functionCount(function);

            case Compiler.FUNCTION_LANG:
            case Compiler.FUNCTION_ID:
            {
                System.err.println("UNIMPLEMENTED: " + function);
                return null;
            }
            case Compiler.FUNCTION_LOCAL_NAME:          return functionLocalName(function);
            case Compiler.FUNCTION_NAMESPACE_URI:       return functionNamespaceURI(function);
            case Compiler.FUNCTION_NAME:                return functionName(function);
            case Compiler.FUNCTION_STRING:              return functionString(function);
            case Compiler.FUNCTION_CONCAT:              return functionConcat(function);
            case Compiler.FUNCTION_STARTS_WITH:         return functionStartsWith(function);
            case Compiler.FUNCTION_CONTAINS:            return functionContains(function);
            case Compiler.FUNCTION_SUBSTRING_BEFORE:    return functionSubstringBefore(function);
            case Compiler.FUNCTION_SUBSTRING_AFTER:     return functionSubstringAfter(function);
            case Compiler.FUNCTION_SUBSTRING:           return functionSubstring(function);
            case Compiler.FUNCTION_STRING_LENGTH:       return functionStringLength(function);
            case Compiler.FUNCTION_NORMALIZE_SPACE:     return functionNormalizeSpace(function);
            case Compiler.FUNCTION_TRANSLATE:           return functionTranslate(function);
            case Compiler.FUNCTION_BOOLEAN:             return functionBoolean(function);
            case Compiler.FUNCTION_NOT:                 return functionNot(function);
            case Compiler.FUNCTION_TRUE:                return functionTrue(function);
            case Compiler.FUNCTION_FALSE:               return functionFalse(function);
            case Compiler.FUNCTION_NULL:                return functionNull(function);
            case Compiler.FUNCTION_NUMBER:              return functionNumber(function);
            case Compiler.FUNCTION_SUM:                 return functionSum(function);
            case Compiler.FUNCTION_FLOOR:               return functionFloor(function);
            case Compiler.FUNCTION_CEILING:             return functionCeiling(function);
            case Compiler.FUNCTION_ROUND:               return functionRound(function);
//            case Compiler.FUNCTION_KEY:
//                System.err.println("UNIMPLEMENTED: " + function);
        }
        return null;
    }

    protected Object functionLast(CoreFunction function){
        assertArgCount(function, 0);
        // Move the position to the beginning and iterate through
        // the context to count nodes.
        int old = getCurrentPosition();
        setPosition(0);
        int count = 0;
        while(next()){
            count++;
        }

        // Restore the current position.
        if (old != 0){
            setPosition(old);
        }
        return new Double(count);
    }

    protected Object functionPosition(CoreFunction function){
        assertArgCount(function, 0);
        return new Integer(getCurrentPosition());
    }

    protected Object functionCount(CoreFunction function){
        assertArgCount(function, 1);
        Expression arg1 = function.getArg1();
        int count = 0;
        Object value = eval(arg1, false);
        if (value instanceof Pointer){
            value = ((Pointer)value).getValue();
        }
        if (value instanceof EvalContext){
            EvalContext ctx = (EvalContext)value;
            while(ctx.nextSet()){
                while(ctx.next()){
                    count++;
                }
            }
        }
        else if (value instanceof Collection){
            count = ((Collection)value).size();
        }
        else if (value == null){
            count = 0;
        }
        else {
            count = 1;
        }
        return new Double(count);
    }

    protected Object functionNamespaceURI(CoreFunction function){
        if (function.getArgumentCount() == 0){
            return getCurrentNodePointer();
        }
        assertArgCount(function, 1);
        Object set = eval(function.getArg1(), false);
        if (set instanceof EvalContext){
            EvalContext ctx = (EvalContext)set;
            if (ctx.nextSet() && ctx.next()){
                return ctx.getCurrentNodePointer().getNamespaceURI();
            }
        }
        return "";
    }

    protected Object functionLocalName(CoreFunction function){
        if (function.getArgumentCount() == 0){
            return getCurrentNodePointer();
        }
        assertArgCount(function, 1);
        Object set = eval(function.getArg1(), false);
        if (set instanceof EvalContext){
            EvalContext ctx = (EvalContext)set;
            if (ctx.nextSet() && ctx.next()){
                return ctx.getCurrentNodePointer().getName().getName();
            }
        }
        return "";
    }

    protected Object functionName(CoreFunction function){
        if (function.getArgumentCount() == 0){
            return getCurrentNodePointer();
        }
        assertArgCount(function, 1);
        Object set = eval(function.getArg1(), false);
        if (set instanceof EvalContext){
            EvalContext ctx = (EvalContext)set;
            if (ctx.nextSet() && ctx.next()){
                return ctx.getCurrentNodePointer().getExpandedName().toString();
            }
        }
        return "";
    }

    protected Object functionString(CoreFunction function){
        if (function.getArgumentCount() == 0){
            return stringValue(getCurrentNodePointer());
        }
        assertArgCount(function, 1);
        return stringValue(eval(function.getArg1()));
    }

    protected Object functionConcat(CoreFunction function){
        if (function.getArgumentCount() < 2){
            assertArgCount(function, 2);
        }
        StringBuffer buffer = new StringBuffer();
        Expression args[] = function.getArguments();
        for (int i = 0; i < args.length; i++){
            buffer.append(stringValue(eval(args[i])));
        }
        return buffer.toString();
    }

    protected Object functionStartsWith(CoreFunction function){
        assertArgCount(function, 2);
        String s1 = stringValue(eval(function.getArg1()));
        String s2 = stringValue(eval(function.getArg2()));
        return s1.startsWith(s2) ? Boolean.TRUE : Boolean.FALSE;
    }

    protected Object functionContains(CoreFunction function){
        assertArgCount(function, 2);
        String s1 = stringValue(eval(function.getArg1()));
        String s2 = stringValue(eval(function.getArg2()));
        return s1.indexOf(s2) != -1 ? Boolean.TRUE : Boolean.FALSE;
    }

    protected Object functionSubstringBefore(CoreFunction function){
        assertArgCount(function, 2);
        String s1 = stringValue(eval(function.getArg1()));
        String s2 = stringValue(eval(function.getArg2()));
        int index = s1.indexOf(s2);
        if (index == -1){
            return "";
        }
        return s1.substring(0, index);
    }

    protected Object functionSubstringAfter(CoreFunction function){
        assertArgCount(function, 2);
        String s1 = stringValue(eval(function.getArg1()));
        String s2 = stringValue(eval(function.getArg2()));
        int index = s1.indexOf(s2);
        if (index == -1){
            return "";
        }
        return s1.substring(index + s2.length());
    }

    protected Object functionSubstring(CoreFunction function){
        int ac = function.getArgumentCount();
        if (ac != 2 && ac != 3){
            assertArgCount(function, 2);
        }

        String s1 = stringValue(eval(function.getArg1()));
        double from = doubleValue(eval(function.getArg2()));
        if (Double.isNaN(from)){
            return "";
        }

        from = Math.round(from);
        if (ac == 2){
            if (from < 1){
                from = 1;
            }
            return s1.substring((int)from - 1);
        }
        else {
            double length = doubleValue(eval(function.getArg3()));
            length = Math.round(length);
            if (length < 0){
                return "";
            }

            double to = from + length;
            if (to < 1){
                return "";
            }

            if (to > s1.length() + 1){
                if (from < 1){
                    from = 1;
                }
                return s1.substring((int)from - 1);
            }

            if (from < 1){
                from = 1;
            }
            return s1.substring((int)from - 1, (int)(to - 1));
        }
    }

    protected Object functionStringLength(CoreFunction function){
        String s;
        if (function.getArgumentCount() == 0){
            s = stringValue(getCurrentNodePointer());
        }
        else {
            assertArgCount(function, 1);
            s = stringValue(eval(function.getArg1()));
        }
        return new Double(s.length());
    }

    protected Object functionNormalizeSpace(CoreFunction function){
        assertArgCount(function, 1);
        String s = stringValue(eval(function.getArg1()));
        char chars[] = s.toCharArray();
        int out = 0;
        int phase = 0;
        for (int in = 0; in < chars.length; in++){
            switch(chars[in]){
                case 0x20:
                case 0x9:
                case 0xD:
                case 0xA:
                    if (phase == 0){      // beginning
                        ;
                    }
                    else if (phase == 1){ // non-space
                        phase = 2;
                        chars[out++] = ' ';
                    }
                    break;
                default:
                    chars[out++] = chars[in];
                    phase = 1;
            }
        }
        if (phase == 2){ // trailing-space
            out--;
        }
        return new String(chars, 0, out);
    }

    protected Object functionTranslate(CoreFunction function){
        assertArgCount(function, 3);
        String s1 = stringValue(eval(function.getArg1()));
        String s2 = stringValue(eval(function.getArg2()));
        String s3 = stringValue(eval(function.getArg3()));
        char chars[] = s1.toCharArray();
        int out = 0;
        for (int in = 0; in < chars.length; in++){
            char c = chars[in];
            int inx = s2.indexOf(c);
            if (inx != -1){
                if (inx < s3.length()){
                    chars[out++] = s3.charAt(inx);
                }
            }
            else {
                chars[out++] = c;
            }
        }
        return new String(chars, 0, out);
    }

    protected Object functionBoolean(CoreFunction function){
        assertArgCount(function, 1);
        return booleanValue(eval(function.getArg1())) ? Boolean.TRUE : Boolean.FALSE;
    }

    protected Object functionNot(CoreFunction function){
        assertArgCount(function, 1);
        return booleanValue(eval(function.getArg1())) ? Boolean.FALSE : Boolean.TRUE;
    }

    protected Object functionTrue(CoreFunction function){
        assertArgCount(function, 0);
        return Boolean.TRUE;
    }

    protected Object functionFalse(CoreFunction function){
        assertArgCount(function, 0);
        return Boolean.FALSE;
    }

    protected Object functionNull(CoreFunction function){
        assertArgCount(function, 0);
        return null;
    }

    protected Object functionNumber(CoreFunction function){
        if (function.getArgumentCount() == 0){
            return number(getCurrentNodePointer());
        }
        assertArgCount(function, 1);
        return number(eval(function.getArg1()));
    }

    protected Object functionSum(CoreFunction function){
        assertArgCount(function, 1);
        Object v = eval(function.getArg1(), false);
        if (v == null){
            return ZERO;
        }
        else if (v instanceof EvalContext){
            double sum = 0.0;
            EvalContext ctx = (EvalContext)v;
            while (ctx.nextSet()){
                while (ctx.next()){
                    sum += doubleValue(ctx.getCurrentNodePointer());
                }
            }
            return new Double(sum);
        }
        throw new RuntimeException("Invalid argument type for 'sum': " + v.getClass().getName());
    }

    protected Object functionFloor(CoreFunction function){
        assertArgCount(function, 1);
        double v = doubleValue(eval(function.getArg1()));
        return new Double(Math.floor(v));
    }

    protected Object functionCeiling(CoreFunction function){
        assertArgCount(function, 1);
        double v = doubleValue(eval(function.getArg1()));
        return new Double(Math.ceil(v));
    }

    protected Object functionRound(CoreFunction function){
        assertArgCount(function, 1);
        double v = doubleValue(eval(function.getArg1()));
        return new Double(Math.round(v));
    }

    private void assertArgCount(CoreFunction function, int count){
        if (function.getArgumentCount() != count){
            throw new RuntimeException("Incorrect number of argument: " + function);
        }
    }
}