/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/Path.java,v 1.5 2002/08/10 01:39:29 dmitri Exp $
 * $Revision: 1.5 $
 * $Date: 2002/08/10 01:39:29 $
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
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.axes.*;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2002/08/10 01:39:29 $
 */
public abstract class Path extends Expression {

    private Step[] steps;
    private boolean basicKnown = false;
    private boolean basic;

    public Path(int typeCode, Step[] steps){
        super(typeCode);
        this.steps = steps;
    }

    public Step[] getSteps(){
        return steps;
    }

    public boolean computeContextDependent(){
        if (steps != null){
            for (int i = 0; i < steps.length; i++){
                if (steps[i].isContextDependent()){
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Recognized paths formatted as <code>foo/bar[3]/baz[@name = 'biz']</code>.  The
     * evaluation of such "simple" paths is optimized and streamlined.
     */
    public boolean isSimplePath(){
        if (!basicKnown){
            basicKnown = true;
            basic = true;
            Step[] steps = getSteps();
            for (int i = 0; i < steps.length; i++){
                boolean accepted = false;
                if (steps[i].getAxis() == Compiler.AXIS_SELF &&
                        (steps[i].getNodeTest() instanceof NodeTypeTest) &&
                        ((NodeTypeTest)steps[i].getNodeTest()).getNodeType() ==
                                Compiler.NODE_TYPE_NODE){
                    accepted = true;
                }
                else if (steps[i].getAxis() == Compiler.AXIS_CHILD &&
                        (steps[i].getNodeTest() instanceof NodeNameTest) &&
                        !((NodeNameTest)steps[i].getNodeTest()).
                                    getNodeName().getName().equals("*")){
                    accepted = true;
                }
                if (accepted){
                    accepted = areBasicPredicates(steps[i].getPredicates());
                }
                if (!accepted){
                    basic = false;
                    break;
                }
            }
        }
        return basic;
    }

    protected boolean areBasicPredicates(Expression predicates[]){
        if (predicates != null && predicates.length != 0){
            boolean firstIndex = true;
            for (int i = 0; i < predicates.length; i++){
                if (predicates[i] instanceof NameAttributeTest){
                    if (((NameAttributeTest)predicates[i]).
                                getNameTestExpression().isContextDependent()){
                        return false;
                    }
                }
                else if (predicates[i].isContextDependent()){
                    return false;
                }
                else {
                    if (!firstIndex){
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
    protected Pointer getSingleNodePointerForSteps(EvalContext context){
        if (steps.length == 0){
            return context.getSingleNodePointer();
        }

        if (isSimplePath()){
            NodePointer ptr = (NodePointer)context.getSingleNodePointer();
            return SimplePathInterpreter.interpretSimpleLocationPath(context, ptr, steps);
        }
        else {
            return searchForPath(context);
        }
    }

    private Pointer searchForPath(EvalContext context) {
        for (int i = 0; i < steps.length; i++){
            context = createContextForStep(context, steps[i].getAxis(), steps[i].getNodeTest());
            Expression predicates[] = steps[i].getPredicates();
            if (predicates != null){
                for (int j = 0; j < predicates.length; j++){
                    context = new PredicateContext(context, predicates[j]);
                }
            }
        }

        return context.getSingleNodePointer();
    }

    /**
     * Given a root context, walks a path therefrom and builds a context
     * that contains all nodes matching the path.
     */
    protected EvalContext evalSteps(EvalContext context){
        if (steps.length == 0){
            return context;
        }

        for (int i = 0; i < steps.length; i++){
            context = createContextForStep(context, steps[i].getAxis(), steps[i].getNodeTest());
            Expression predicates[] = steps[i].getPredicates();
            if (predicates != null){
                for (int j = 0; j < predicates.length; j++){
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
}