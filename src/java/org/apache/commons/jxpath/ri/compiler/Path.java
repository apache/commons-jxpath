/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/Path.java,v 1.11 2004/01/23 01:10:20 dmitri Exp $
 * $Revision: 1.11 $
 * $Date: 2004/01/23 01:10:20 $
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.axes.AncestorContext;
import org.apache.commons.jxpath.ri.axes.AttributeContext;
import org.apache.commons.jxpath.ri.axes.ChildContext;
import org.apache.commons.jxpath.ri.axes.DescendantContext;
import org.apache.commons.jxpath.ri.axes.InitialContext;
import org.apache.commons.jxpath.ri.axes.NamespaceContext;
import org.apache.commons.jxpath.ri.axes.ParentContext;
import org.apache.commons.jxpath.ri.axes.PrecedingOrFollowingContext;
import org.apache.commons.jxpath.ri.axes.PredicateContext;
import org.apache.commons.jxpath.ri.axes.SelfContext;
import org.apache.commons.jxpath.ri.axes.SimplePathInterpreter;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.11 $ $Date: 2004/01/23 01:10:20 $
 */
public abstract class Path extends Expression {

    private Step[] steps;
    private boolean basicKnown = false;
    private boolean basic;

    public Path(Step[] steps) {
        this.steps = steps;
    }

    public Step[] getSteps() {
        return steps;
    }

    public boolean computeContextDependent() {
        if (steps != null) {
            for (int i = 0; i < steps.length; i++) {
                if (steps[i].isContextDependent()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Recognized  paths formatted as <code>foo/bar[3]/baz[@name = 'biz']
     * </code>.  The evaluation of such "simple" paths is optimized and
     * streamlined.
     */
    public boolean isSimplePath() {
        if (!basicKnown) {
            basicKnown = true;
            basic = true;
            Step[] steps = getSteps();
            for (int i = 0; i < steps.length; i++) {
                if (!isSimpleStep(steps[i])){
                    basic = false;
                    break;
                }
            }
        }
        return basic;
    }

    /**
     * A Step is "simple" if it takes one of these forms: ".", "/foo",
     * "@bar", "/foo[3]". If there are predicates, they should be 
     * context independent for the step to still be considered simple.
     */
    protected boolean isSimpleStep(Step step) {
        if (step.getAxis() == Compiler.AXIS_SELF) {
            NodeTest nodeTest = step.getNodeTest();
            if (!(nodeTest instanceof NodeTypeTest)) {
                return false;
            }
            int nodeType = ((NodeTypeTest) nodeTest).getNodeType();
            if (nodeType != Compiler.NODE_TYPE_NODE) {
                return false;
            }
            return areBasicPredicates(step.getPredicates());
        }
        else if (step.getAxis() == Compiler.AXIS_CHILD
                || step.getAxis() == Compiler.AXIS_ATTRIBUTE) {
            NodeTest nodeTest = step.getNodeTest();
            if (!(nodeTest instanceof NodeNameTest)){
                return false;
            }
            
            String name = ((NodeNameTest) nodeTest).getNodeName().getName();
            if (name.equals("*")) {
                return false;
            }
            return areBasicPredicates(step.getPredicates());
        }
        return false;
    }

    protected boolean areBasicPredicates(Expression predicates[]) {
        if (predicates != null && predicates.length != 0) {
            boolean firstIndex = true;
            for (int i = 0; i < predicates.length; i++) {
                if (predicates[i] instanceof NameAttributeTest) {
                    if (((NameAttributeTest) predicates[i])
                        .getNameTestExpression()
                        .isContextDependent()) {
                        return false;
                    }
                }
                else if (predicates[i].isContextDependent()) {
                    return false;
                }
                else {
                    if (!firstIndex) {
                        return false;
                    }
                    firstIndex = false;
                }
            }
        }
        return true;
    }

    /**
     * Given a root context, walks a path therefrom and finds the
     * pointer to the first element matching the path.
     */
    protected Pointer getSingleNodePointerForSteps(EvalContext context) {
        if (steps.length == 0) {
            return context.getSingleNodePointer();
        }

        if (isSimplePath()) {
            NodePointer ptr = (NodePointer) context.getSingleNodePointer();
            return SimplePathInterpreter.interpretSimpleLocationPath(
                context,
                ptr,
                steps);
        }
        else {
            return searchForPath(context);
        }
    }

    /**
     * The idea here is to return a NullPointer rather than null if that's at
     * all possible. Take for example this path: "//map/key". Let's say, "map"
     * is an existing node, but "key" is not there. We will create a
     * NullPointer that can be used to set/create the "key" property.
     * <p>
     * However, a path like "//key" would still produce null, because we have
     * no way of knowing where "key" would be if it existed.
     * </p>
     * <p>
     * To accomplish this, we first try the path itself. If it does not find
     * anything, we chop off last step of the path, as long as it is a simple
     * one like child:: or attribute:: and try to evaluate the truncated path.
     * If it finds exactly one node - create a NullPointer and return. If it
     * fails, chop off another step and repeat. If it finds more than one
     * location - return null.
     * </p>
     */
    private Pointer searchForPath(EvalContext context) {
        EvalContext ctx = buildContextChain(context, steps.length, true);
        Pointer pointer = ctx.getSingleNodePointer();
        
        if (pointer != null) {
            return pointer;
        }
        
        for (int i = steps.length; --i > 0;) {
            if (!isSimpleStep(steps[i])) {
                return null;
            }
            ctx = buildContextChain(context, i, true);
            if (ctx.hasNext()) {
                Pointer partial = (Pointer) ctx.next();
                if (ctx.hasNext()) {
                    // If we find another location - the search is
                    // ambiguous, so we report failure
                    return null;
                }
                if (partial instanceof NodePointer) {
                    return SimplePathInterpreter.createNullPointer(
                            context,
                            (NodePointer) partial,
                            steps,
                            i);
                }
            }
        }
        return null;
    }

    /**
     * Given a root context, walks a path therefrom and builds a context
     * that contains all nodes matching the path.
     */
    protected EvalContext evalSteps(EvalContext context) {
        return buildContextChain(context, steps.length, false);
    }

    private EvalContext buildContextChain(
            EvalContext context,
            int stepCount,
            boolean createInitialContext) 
    {
        if (createInitialContext) {
            context = new InitialContext(context);
        }
        if (steps.length == 0) {
            return context;
        }
        for (int i = 0; i < stepCount; i++) {
            context =
                createContextForStep(
                    context,
                    steps[i].getAxis(),
                    steps[i].getNodeTest());
            Expression predicates[] = steps[i].getPredicates();
            if (predicates != null) {
                for (int j = 0; j < predicates.length; j++) {
                    context = new PredicateContext(context, predicates[j]);
                }
            }
        }
        return context;
    }
    
    /**
     * Different axes are serviced by different contexts. This method
     * allocates the right context for the supplied step.
     */
    protected EvalContext createContextForStep(
        EvalContext context,
        int axis,
        NodeTest nodeTest) 
    {
        switch (axis) {
        case Compiler.AXIS_ANCESTOR :
            return new AncestorContext(context, false, nodeTest);
        case Compiler.AXIS_ANCESTOR_OR_SELF :
            return new AncestorContext(context, true, nodeTest);
        case Compiler.AXIS_ATTRIBUTE :
            return new AttributeContext(context, nodeTest);
        case Compiler.AXIS_CHILD :
            return new ChildContext(context, nodeTest, false, false);
        case Compiler.AXIS_DESCENDANT :
            return new DescendantContext(context, false, nodeTest);
        case Compiler.AXIS_DESCENDANT_OR_SELF :
            return new DescendantContext(context, true, nodeTest);
        case Compiler.AXIS_FOLLOWING :
            return new PrecedingOrFollowingContext(context, nodeTest, false);
        case Compiler.AXIS_FOLLOWING_SIBLING :
            return new ChildContext(context, nodeTest, true, false);
        case Compiler.AXIS_NAMESPACE :
            return new NamespaceContext(context, nodeTest);
        case Compiler.AXIS_PARENT :
            return new ParentContext(context, nodeTest);
        case Compiler.AXIS_PRECEDING :
            return new PrecedingOrFollowingContext(context, nodeTest, true);
        case Compiler.AXIS_PRECEDING_SIBLING :
            return new ChildContext(context, nodeTest, true, true);
        case Compiler.AXIS_SELF :
            return new SelfContext(context, nodeTest);
        }
        return null; // Never happens
    }
}