/*
 * $Header: /home/cvs/jakarta-commons/jxpath/src/java/org/apache/commons/jxpath/ri/EvalContext.java,v 1.10 2002/04/24 04:05:40 dmitri Exp $
 * $Revision: 1.10 $
 * $Date: 2002/04/24 04:05:40 $
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
package org.apache.commons.jxpath.ri.axes;

import java.util.*;

import org.apache.commons.jxpath.*;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.*;
import org.apache.commons.jxpath.ri.InfoSetUtil;

/**
 * An evaluation mechanism for simple XPaths, which
 * is much faster than the usual process. It is only used for
 * xpaths which have no context-dependent parts, consist entirely of
 * <code>child::name</code> and <code>self::node()</code> steps with
 * predicates that either integer or have the form <code>[@name = ...]</code>.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.10 $ $Date: 2002/04/24 04:05:40 $
 */
public class SimplePathInterpreter {

    // Because of the complexity caused by the variety of situations
    // that need to be addressed by this class, we attempt to break up
    // the class into individual methods addressing those situations
    // individually.  The names of the methods are supposed to
    // give brief descriptions of those situations.

    private static final QName QNAME_NAME = new QName(null, "name");
    private static final int PERFECT_MATCH = 1000;

    // Uncomment this variable and the PATH = ... lines in
    // the two following methods in order to be able to print the
    // currently evaluated path for debugging of this class
//    private static String PATH;       // Debugging

    /**
     * Interpret a simple path that starts with the given root and
     * follows the given steps. All steps must have the axis "child::"
     * and a name test.  They can also optionally have predicates
     * of type [@name=expression] or simply [expression] interpreted
     * as an index.
     */
    public static NodePointer interpretSimpleLocationPath(
            EvalContext context, NodePointer root, Step steps[])
    {
//        PATH = createNullPointer(context, root, steps, 0).toString();  // Dbg
        NodePointer pointer = doStep(context, root, steps, 0);
        return valuePointer(pointer);
    }

    /**
     * Interpret the steps of a simple expression path that
     * starts with the given root, which is the result of evaluation
     * of the root expression of the expression path, applies the
     * given predicates to it and then follows the given steps.
     * All steps must have the axis "child::"
     * and a name test.  They can also optionally have predicates
     * of type [@name=...] or simply [...] interpreted as an index.
     */
    public static NodePointer interpretSimpleExpressionPath(
                EvalContext context, NodePointer root,
                Expression predicates[], Step[] steps)
    {
//        PATH = createNullPointerForPredicates(context, root,
//                    steps, -1, predicates, 0).toString();  // Debugging
        return doPredicate(context, root, steps, -1, predicates, 0);
    }

    /**
     * Recursive evaluation of a path. The general plan is:
     * Look at the current step,
     * find nodes that match it,
     * iterate over those nodes and
     * for each of them call doStep again for subsequent steps.
     */
    private static NodePointer doStep(
            EvalContext context, NodePointer parent,
            Step steps[], int current_step)
    {
        parent = valuePointer(parent);

        if (parent == null){
            return null;
        }

        if (current_step == steps.length){
            // We have reached the end of the list of steps
            return parent;
        }

        Step step = steps[current_step];
        Expression predicates[] = step.getPredicates();

        // Divide and conquer: the process is broken out into
        // four major use cases.
        // 1. Current step has no predicates and
        //    the root is a property owner (e.g. bean or map)
        // 2. Current step has predicates and
        //    the root is a property owner (e.g. bean or map)
        // 3. Current step has no predicates and
        //    the root is an InfoSet standard node (e.g. DOM Node)
        // 4. Current step has predicates and
        //    the root is an InfoSet standard node (e.g. DOM Node)

        if (parent instanceof PropertyOwnerPointer){
            if (predicates == null || predicates.length == 0){
                return doStep_noPredicates_propertyOwner(
                    context, (PropertyOwnerPointer)parent, steps, current_step);
            }
            else {
                return doStep_predicates_propertyOwner(
                    context, (PropertyOwnerPointer)parent, steps, current_step);
            }
        }
        else {
            if (predicates == null || predicates.length == 0){
                 return doStep_noPredicates_standard(
                    context, parent, steps, current_step);
            }
            else {
                return doStep_predicates_standard(
                    context, parent, steps, current_step);
            }
        }
    }

    /**
     * We have a step that starts with a property owner (bean, map, etc) and has
     * no predicates.  The name test of the step may map to a scalar property
     * or to a collection.  If it is a collection, we should apply the tail of
     * the path to each element until we find a match. If we don't find
     * a perfect match, we should return the "best quality" pointer, which
     * has the longest chain of steps mapping to existing nodes and the shortes
     * tail of Null* pointers.
     */
    private static NodePointer doStep_noPredicates_propertyOwner(
                EvalContext context, PropertyOwnerPointer parentPointer,
                Step[] steps, int current_step)
    {
        Step step = steps[current_step];
        NodePointer childPointer;
        if (step.getAxis() == Compiler.AXIS_CHILD){
            // Treat the name test as a property name
            QName name = ((NodeNameTest)step.getNodeTest()).getNodeName();

            childPointer = parentPointer.getPropertyPointer();
            ((PropertyPointer)childPointer).setPropertyName(name.toString());
        }
        else {
            childPointer = parentPointer;
        }

        if (!childPointer.isActual()){
            // The property does not exist - create a null pointer.
            return createNullPointer(
                            context, parentPointer, steps, current_step);
        }
        else if (current_step == steps.length - 1){
            // If this is the last step - we are done, we found it
            return childPointer;
        }
        else if (childPointer.isCollection()){
            // Iterate over all values and
            // execute remaining steps for each node,
            // looking for the best quality match
            int bestQuality = 0;
            NodePointer bestMatch = null;
            int count = childPointer.getLength();
            for (int i = 0; i < count; i++){
                childPointer.setIndex(i);
                NodePointer pointer = doStep(
                        context, childPointer, steps, current_step + 1);
                int quality = computeQuality(pointer);
                if (quality == PERFECT_MATCH){
                    return pointer;
                }
                else if (quality > bestQuality){
                    bestQuality = quality;
                    bestMatch = (NodePointer)pointer.clone();
                }
            }
            if (bestMatch != null){
                return bestMatch;
            }
            // This step did not find anything - return a null pointer
            return createNullPointer(
                        context, childPointer, steps, current_step);
        }
        else {
            // Evaluate subsequent steps
            return doStep(
                       context, childPointer, steps, current_step + 1);
        }
    }

    /**
     * A path that starts with a standard InfoSet node (e.g. DOM Node) and
     * has no predicates.  Get a child iterator and apply the tail of
     * the path to each element until we find a match. If we don't find
     * a perfect match, we should return the "best quality" pointer, which
     * has the longest chain of steps mapping to existing nodes and the shortes
     * tail of Null* pointers.
     */
    private static NodePointer doStep_noPredicates_standard(
                EvalContext context, NodePointer parentPointer,
                Step[] steps, int current_step)
    {
        Step step = steps[current_step];
        int bestQuality = 0;
        NodePointer bestMatch = null;
        NodeIterator it =
                parentPointer.childIterator(step.getNodeTest(), false, null);
        if (it != null){
            for (int i = 1; it.setPosition(i); i++){
                NodePointer childPointer = it.getNodePointer();
                if (steps.length == current_step + 1){
                    // If this is the last step - we are done, we found it
                    return childPointer;
                }
                NodePointer pointer = doStep(
                        context, childPointer, steps, current_step + 1);
                int quality = computeQuality(pointer);
                if (quality == PERFECT_MATCH){
                    return pointer;
                }
                else if (quality > bestQuality){
                    bestQuality = quality;
                    bestMatch = (NodePointer)pointer.clone();
                }
            }
        }

        if (bestMatch != null){
            return bestMatch;
        }

        return createNullPointer(
                context, parentPointer, steps, current_step);
    }

    /**
     * A path that starts with a property owner. The method evaluates
     * the first predicate in a special way and then forwards to
     * a general predicate processing method.
     */
    private static NodePointer doStep_predicates_propertyOwner(
            EvalContext context, PropertyOwnerPointer parentPointer,
            Step[] steps, int current_step)
    {
        Step step = steps[current_step];
        Expression predicates[] = step.getPredicates();

        NodePointer childPointer;
        if (step.getAxis() == Compiler.AXIS_CHILD){
            QName name = ((NodeNameTest)step.getNodeTest()).getNodeName();
            childPointer = parentPointer.getPropertyPointer();
            ((PropertyPointer)childPointer).setPropertyName(name.toString());
        }
        else {
            childPointer = parentPointer;
        }
        if (!childPointer.isActual()){
            // Property does not exist - return a null pointer
            return createNullPointer(
                        context, parentPointer, steps, current_step);
        }

        // Evaluate predicates
        return doPredicate(
            context, childPointer, steps, current_step, predicates, 0);
    }

    /**
     * A path that starts with a standard InfoSet node, e.g. a DOM Node.
     * The method evaluates the first predicate in a special way and
     * then forwards to a general predicate processing method.
     */
    private static NodePointer doStep_predicates_standard(
            EvalContext context, NodePointer parent,
            Step[] steps, int current_step)
    {
        Step step = steps[current_step];
        Expression predicates[] = step.getPredicates();

        if (step.getAxis() == Compiler.AXIS_SELF){
            return doPredicate(context, parent,
                steps, current_step, predicates, 0);
        }

        Expression predicate = predicates[0];

        // Optimize for a single predicate to avoid building a list
        // and to allow the direct access to the index'th element
        // in the case of a simple subscript predecate
        // It is a very common use case, so it deserves individual
        // attention
        if (predicates.length == 1){
            NodeIterator it = parent.childIterator(
                    step.getNodeTest(), false, null);
            NodePointer pointer = null;
            if (it != null){
                if (predicate instanceof NameAttributeTest){ // [@name = key]
                    String key = keyFromPredicate(context, predicate);
                    for (int i = 1; it.setPosition(i); i++){
                        NodePointer ptr = it.getNodePointer();
                        if (isNameAttributeEqual(ptr, key)){
                            pointer = ptr;
                            break;
                        }
                    }
                }
                else {
                    int index = indexFromPredicate(context, predicate);
                    if (it.setPosition(index + 1)){
                        pointer = it.getNodePointer();
                    }
                }
            }
            if (pointer != null){
                return doStep(context, pointer, steps, current_step + 1);
            }
        }
        else {
            NodeIterator it = parent.childIterator(
                    step.getNodeTest(), false, null);
            if (it != null){
                List list = new ArrayList();
                for (int i = 1; it.setPosition(i); i++){
                    list.add(it.getNodePointer());
                }
                NodePointer pointer = doPredicates_standard(context, list,
                    steps, current_step, predicates, 0);
                if (pointer != null){
                    return pointer;
                }
            }
        }
        return createNullPointer(
                    context, parent, steps, current_step);
    }

    /**
     * Evaluates predicates and proceeds with the subsequent steps
     * of the path.
     */
    private static NodePointer doPredicate(
                EvalContext context, NodePointer parent,
                Step[] steps, int current_step,
                Expression predicates[], int current_predicate)
    {
        if (current_predicate == predicates.length){
            return doStep(context, parent, steps, current_step + 1);
        }

        Expression predicate = predicates[current_predicate];
        if (predicate instanceof NameAttributeTest){ // [@name = key1]
            return doPredicate_name(context, parent,
                    steps, current_step, predicates, current_predicate);
        }
        else {      // [index]
            return doPredicate_index(context, parent,
                    steps, current_step, predicates, current_predicate);
        }
    }

    private static NodePointer doPredicate_name(
            EvalContext context, NodePointer parent,
            Step[] steps, int current_step,
            Expression[] predicates, int current_predicate)
    {
        Expression predicate = predicates[current_predicate];
        String key = keyFromPredicate(context, predicate);
        NodePointer child = valuePointer(parent);
        if (child instanceof PropertyOwnerPointer){
            PropertyPointer pointer =
               ((PropertyOwnerPointer)child).getPropertyPointer();
            pointer.setPropertyName(key);
            if (pointer.isActual()){
                return doPredicate(
                        context, pointer, steps, current_step,
                        predicates, current_predicate + 1);
            }
        }
        else if (child.isCollection()){
            // For each node in the collection, perform the following:
            // if the node is a property owner, apply this predicate to it;
            // if the node is a collection, apply this predicate to each elem.;
            // if the node is not a prop owner or a collection,
            //  see if it has the attribute "name" with the right value,
            //  if so - proceed to the next predicate
            NodePointer bestMatch = null;
            int bestQuality = 0;
            int count = child.getLength();
            for (int i = 0; i < count; i++){
                child.setIndex(i);
                NodePointer valuePointer = valuePointer(child);
                if (valuePointer == child){
                    valuePointer = (NodePointer)child.clone();
                }
                NodePointer pointer;
                if ((valuePointer instanceof PropertyOwnerPointer) ||
                        valuePointer.isCollection()){
                    pointer = doPredicate_name(
                            context, valuePointer, steps, current_step,
                            predicates, current_predicate);
                }
                else if (isNameAttributeEqual(valuePointer, key)){
                    pointer = doPredicate(
                            context, valuePointer, steps, current_step,
                            predicates, current_predicate + 1);
                }
                else {
                    pointer = null;
                }
                if (pointer != null){
                    int quality = computeQuality(pointer);
                    if (quality == PERFECT_MATCH){
                        return pointer;
                    }
                    if (quality > bestQuality){
                        bestMatch = (NodePointer)pointer.clone();
                        bestQuality = quality;
                    }
                }
            }
            if (bestMatch != null){
                return bestMatch;
            }
        }
        else {
            // If the node is a standard InfoSet node (e.g. DOM Node),
            // employ doPredicates_standard, which will iterate through
            // the node's children and apply all predicates
            NodePointer found = doPredicates_standard(context,
                    Collections.singletonList(child), steps,
                    current_step, predicates, current_predicate);
            if (found != null){
                return found;
            }
        }
        // If nothing worked - return a null pointer
        return createNullPointerForPredicates(
                context, child, steps, current_step,
                predicates, current_predicate);
    }

    /**
     * Called exclusively for standard InfoSet nodes, e.g. DOM nodes
     * to evaluate predicate sequences like [@name=...][@name=...][index].
     */
    private static NodePointer doPredicates_standard(
                EvalContext context, List parents,
                Step[] steps, int current_step,
                Expression predicates[], int current_predicate)
    {
        if (parents.size() == 0){
            return null;
        }

        // If all predicates have been processed, take the first
        // element from the list of results and proceed to the
        // remaining steps with that element.
        if (current_predicate == predicates.length){
            NodePointer pointer = (NodePointer)parents.get(0);
            return doStep(context, pointer, steps, current_step + 1);
        }

        Expression predicate = predicates[current_predicate];
        if (predicate instanceof NameAttributeTest){
            String key = keyFromPredicate(context, predicate);
            List newList = new ArrayList();
            for (int i = 0; i < parents.size(); i++){
                NodePointer pointer = (NodePointer)parents.get(i);
                if (isNameAttributeEqual(pointer, key)){
                    newList.add(pointer);
                }
            }
            if (newList.size() == 0){
                return null;
            }
            return doPredicates_standard(context, newList,
                    steps, current_step,
                    predicates, current_predicate + 1);
        }
        else {
            // For a subscript, simply take the corresponding
            // element from the list of results and
            // proceed to the remaining predicates with that element
            int index = indexFromPredicate(context, predicate);
            if (index < 0 || index >= parents.size()){
                return null;
            }
            NodePointer ptr = (NodePointer)parents.get(index);
            return doPredicate(context, ptr, steps, current_step,
                predicates, current_predicate + 1);
        }
    }

    /**
     * Evaluate a subscript predicate: see if the node is a collection and
     * if the index is inside the collection
     */
    private static NodePointer doPredicate_index(
            EvalContext context, NodePointer parent,
            Step[] steps, int current_step,
            Expression[] predicates, int current_predicate)
    {
        Expression predicate = predicates[current_predicate];
        int index = indexFromPredicate(context, predicate);
        NodePointer pointer = parent;
        if (isCollectionElement(pointer, index)){
            pointer.setIndex(index);
            return doPredicate(context, valuePointer(pointer),
                    steps, current_step, predicates, current_predicate + 1);
        }
        return createNullPointerForPredicates(context, parent,
                steps, current_step, predicates, current_predicate);
    }

    /**
     * Extract an integer from a subscript predicate. The returned index
     * starts with 0, even though the subscript starts with 1.
     */
    private static int indexFromPredicate(
            EvalContext context, Expression predicate)
    {
        Object value = predicate.computeValue(context);
        if (value instanceof EvalContext){
            value = ((EvalContext)value).getSingleNodePointer();
        }
        if (value instanceof NodePointer){
            value = ((NodePointer)value).getValue();
        }
        if (value == null){
            throw new JXPathException("Predicate value is null");
        }

        if (value instanceof Number){
            return (int)(InfoSetUtil.doubleValue(value) + 0.5) - 1;
        }
        else if (InfoSetUtil.booleanValue(value)){
            return 0;
        }

        return -1;
    }

    /**
     * Extracts the string value of the expression from a predicate like
     * [@name=expression].
     */
    private static String keyFromPredicate(EvalContext context,
                Expression predicate){
        Expression expr = ((NameAttributeTest)predicate).
                                    getNameTestExpression();
        return InfoSetUtil.stringValue(expr.computeValue(context));
    }

    /**
     * For a pointer that matches an actual node, returns 0.
     * For a pointer that does not match an actual node, but whose
     * parent pointer does returns -1, etc.
     */
    private static int computeQuality(NodePointer pointer){
        int quality = PERFECT_MATCH;
        while (pointer != null && !pointer.isActual()){
            quality--;
            pointer = pointer.getParent();
        }
        return quality;
    }

    /**
     * Returns true if the pointer has an attribute called "name" and
     * its value is equal to the supplied string.
     */
    private static boolean isNameAttributeEqual(
            NodePointer pointer, String name)
    {
        NodeIterator it = pointer.attributeIterator(QNAME_NAME);
        return it != null && it.setPosition(1) &&
                name.equals(it.getNodePointer().getValue());
    }

    /**
     * Returns true if the pointer is a collection and the index is
     * withing the bounds of the collection.
     */
    private static boolean isCollectionElement(NodePointer pointer, int index){
        return pointer.isActual() && (index == 0 ||
            (pointer.isCollection() &&
                    index >= 0 && index < pointer.getLength()));
    }

    /**
     * For an intermediate pointer (e.g. PropertyPointer, ContainerPointer)
     * returns a pointer for the contained value.
     */
    private static NodePointer valuePointer(NodePointer pointer){
        while (pointer != null && !pointer.isNode()){
            pointer = pointer.getValuePointer();
        }
        return pointer;
    }

    /**
     * Creates a "null pointer" that
     * a) represents the requested path and
     * b) can be used for creation of missing nodes in the path.
     */
    private static NodePointer createNullPointer(
            EvalContext context, NodePointer parent, Step[] steps,
            int current_step)
    {
        parent = valuePointer(parent);

        if (current_step == steps.length){
            return parent;
        }

        Step step = steps[current_step];

        if (step.getAxis() == Compiler.AXIS_CHILD){
            NullPropertyPointer pointer = new NullPropertyPointer(parent);
            QName name = ((NodeNameTest)step.getNodeTest()).getNodeName();
            pointer.setPropertyName(name.toString());
            parent = pointer;
        }
        // else { it is self::node() }

        Expression predicates[] = step.getPredicates();
        return createNullPointerForPredicates(context, parent,
                steps, current_step, predicates, 0);
    }

    /**
     * Creates a "null pointer" that starts with predicates.
     */
    private static NodePointer createNullPointerForPredicates(
            EvalContext context, NodePointer parent,
            Step[] steps, int current_step,
            Expression predicates[], int current_predicate)
    {
        for (int i = current_predicate; i < predicates.length; i++){
            Expression predicate = predicates[i];
            if (predicate instanceof NameAttributeTest){
                String key = keyFromPredicate(context, predicate);
                parent = valuePointer(parent);
                NullPropertyPointer pointer = new NullPropertyPointer(parent);
                pointer.setNameAttributeValue(key);
                parent = pointer;
            }
            else {
                int index = indexFromPredicate(context, predicate);
                if (parent instanceof NullPropertyPointer){
                    parent.setIndex(index);
                }
                else {
                    parent = new NullElementPointer(parent, index);
                }
            }
        }
        // Proceed with the remaining steps
        return createNullPointer(
                    context, parent, steps, current_step + 1);
    }
}