/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.ri.axes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.InfoSetUtil;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.Expression;
import org.apache.commons.jxpath.ri.compiler.NameAttributeTest;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.Step;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.LangAttributePointer;
import org.apache.commons.jxpath.ri.model.beans.NullElementPointer;
import org.apache.commons.jxpath.ri.model.beans.NullPropertyPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyOwnerPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;

/**
 * An evaluation mechanism for simple XPaths, which
 * is much faster than the usual process. It is only used for
 * xpaths which have no context-dependent parts, consist entirely of
 * <code>child::name</code> and <code>self::node()</code> steps with
 * predicates that either integer or have the form <code>[@name = ...]</code>.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
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
     * @param context evaluation context
     * @param root root pointer
     * @param steps path steps
     * @return NodePointer
     */
    public static NodePointer interpretSimpleLocationPath(
            EvalContext context, NodePointer root, Step[] steps) {
//        PATH = createNullPointer(context, root, steps, 0).toString();  // Dbg
        NodePointer pointer = doStep(context, root, steps, 0);
//        return valuePointer(pointer);
        return pointer;
    }

    /**
     * Interpret the steps of a simple expression path that
     * starts with the given root, which is the result of evaluation
     * of the root expression of the expression path, applies the
     * given predicates to it and then follows the given steps.
     * All steps must have the axis "child::" or "attribute::"
     * and a name test.  They can also optionally have predicates
     * of type [@name=...] or simply [...] interpreted as an index.
     * @param context evaluation context
     * @param root root pointer
     * @param predicates predicates corresponding to <code>steps</code>
     * @param steps path steps
     * @return NodePointer
     */
    public static NodePointer interpretSimpleExpressionPath(
                EvalContext context, NodePointer root,
                Expression[] predicates, Step[] steps) {
//        PATH = createNullPointerForPredicates(context, root,
//                    steps, -1, predicates, 0).toString();  // Debugging
        NodePointer pointer =
            doPredicate(context, root, steps, -1, predicates, 0);
//        return valuePointer(pointer);
        return pointer;
    }

    /**
     * Recursive evaluation of a path. The general plan is:
     * Look at the current step,
     * find nodes that match it,
     * iterate over those nodes and
     * for each of them call doStep again for subsequent steps.
     * @param context evaluation context
     * @param parent parent pointer
     * @param steps path steps
     * @param currentStep step number
     * @return NodePointer
     */
    private static NodePointer doStep(
            EvalContext context, NodePointer parent,
            Step[] steps, int currentStep) {
        if (parent == null) {
            return null;
        }

        if (currentStep == steps.length) {
            // We have reached the end of the list of steps
            return parent;
        }

        // Open all containers
        parent = valuePointer(parent);

        Step step = steps[currentStep];
        Expression[] predicates = step.getPredicates();

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

        if (parent instanceof PropertyOwnerPointer) {
            if (predicates == null || predicates.length == 0) {
                return doStepNoPredicatesPropertyOwner(
                    context,
                    (PropertyOwnerPointer) parent,
                    steps,
                    currentStep);
            }
            return doStepPredicatesPropertyOwner(
                context,
                (PropertyOwnerPointer) parent,
                steps,
                currentStep);
        }
        if (predicates == null || predicates.length == 0) {
            return doStepNoPredicatesStandard(
                context,
                parent,
                steps,
                currentStep);
        }
        return doStepPredicatesStandard(
            context,
            parent,
            steps,
            currentStep);
    }

    /**
     * We have a step that starts with a property owner (bean, map, etc) and has
     * no predicates.  The name test of the step may map to a scalar property
     * or to a collection.  If it is a collection, we should apply the tail of
     * the path to each element until we find a match. If we don't find
     * a perfect match, we should return the "best quality" pointer, which
     * has the longest chain of steps mapping to existing nodes and the shortes
     * tail of Null* pointers.
     * @param context evaluation context
     * @param parentPointer property owner pointer
     * @param steps path steps
     * @param currentStep step number
     * @return NodePointer
     */
    private static NodePointer doStepNoPredicatesPropertyOwner(
                EvalContext context, PropertyOwnerPointer parentPointer,
                Step[] steps, int currentStep) {
        Step step = steps[currentStep];
        NodePointer childPointer =
            createChildPointerForStep(parentPointer, step);

        if (childPointer == null) {
            return null;
        }
        if (!childPointer.isActual()) {
            // The property does not exist - create a null pointer.
            return createNullPointer(
                context,
                parentPointer,
                steps,
                currentStep);
        }
        if (currentStep == steps.length - 1) {
            // If this is the last step - we are done, we found it
            return childPointer;
        }
        if (childPointer.isCollection()) {
            // Iterate over all values and
            // execute remaining steps for each node,
            // looking for the best quality match
            int bestQuality = 0;
            childPointer = (NodePointer) childPointer.clone();
            NodePointer bestMatch = null;
            int count = childPointer.getLength();
            for (int i = 0; i < count; i++) {
                childPointer.setIndex(i);
                NodePointer pointer =
                    doStep(context, childPointer, steps, currentStep + 1);
                int quality = computeQuality(pointer);
                if (quality == PERFECT_MATCH) {
                    return pointer;
                }
                else if (quality > bestQuality) {
                    bestQuality = quality;
                    bestMatch = (NodePointer) pointer.clone();
                }
            }
            if (bestMatch != null) {
                return bestMatch;
            }
            // This step did not find anything - return a null pointer
            return createNullPointer(context, childPointer, steps, currentStep);
        }
        // Evaluate subsequent steps
        return doStep(context, childPointer, steps, currentStep + 1);
    }

    /**
     * A path that starts with a standard InfoSet node (e.g. DOM Node) and
     * has no predicates.  Get a child iterator and apply the tail of
     * the path to each element until we find a match. If we don't find
     * a perfect match, we should return the "best quality" pointer, which
     * has the longest chain of steps mapping to existing nodes and the shortes
     * tail of Null* pointers.
     * @param context evaluation context
     * @param parentPointer parent pointer
     * @param steps path steps
     * @param currentStep step number
     * @return NodePointer
     */
    private static NodePointer doStepNoPredicatesStandard(
                EvalContext context, NodePointer parentPointer,
                Step[] steps, int currentStep) {
        Step step = steps[currentStep];

        if (step.getAxis() == Compiler.AXIS_SELF) {
            return doStep(context, parentPointer, steps, currentStep + 1);
        }

        int bestQuality = 0;
        NodePointer bestMatch = null;
        NodeIterator it = getNodeIterator(context, parentPointer, step);
        if (it != null) {
            for (int i = 1; it.setPosition(i); i++) {
                NodePointer childPointer = it.getNodePointer();
                if (steps.length == currentStep + 1) {
                    // If this is the last step - we are done, we found it
                    return childPointer;
                }
                NodePointer pointer = doStep(
                        context, childPointer, steps, currentStep + 1);
                int quality = computeQuality(pointer);
                if (quality == PERFECT_MATCH) {
                    return pointer;
                }
                if (quality > bestQuality) {
                    bestQuality = quality;
                    bestMatch = (NodePointer) pointer.clone();
                }
            }
        }
        return bestMatch != null ? bestMatch
                : createNullPointer(context, parentPointer, steps, currentStep);
    }

    /**
     * A path that starts with a property owner. The method evaluates
     * the first predicate in a special way and then forwards to
     * a general predicate processing method.
     * @param context evaluation context
     * @param parentPointer parent pointer
     * @param steps path steps
     * @param currentStep step number
     * @return NodePointer
     */
    private static NodePointer doStepPredicatesPropertyOwner(
            EvalContext context, PropertyOwnerPointer parentPointer,
            Step[] steps, int currentStep) {
        Step step = steps[currentStep];
        Expression[] predicates = step.getPredicates();

        NodePointer childPointer =
            createChildPointerForStep(parentPointer, step);
        if (!childPointer.isActual()) {
            // Property does not exist - return a null pointer
            return createNullPointer(
                context,
                parentPointer,
                steps,
                currentStep);
        }

        // Evaluate predicates
        return doPredicate(
            context,
            childPointer,
            steps,
            currentStep,
            predicates,
            0);
    }

    /**
     * Create the child pointer for a given step.
     * @param parentPointer parent pointer
     * @param step associated step
     * @return NodePointer
     */
    private static NodePointer createChildPointerForStep(
                PropertyOwnerPointer parentPointer, Step step) {
        int axis = step.getAxis();
        if (axis == Compiler.AXIS_CHILD || axis == Compiler.AXIS_ATTRIBUTE) {
            QName name = ((NodeNameTest) step.getNodeTest()).getNodeName();
            if (axis == Compiler.AXIS_ATTRIBUTE && isLangAttribute(name)) {
                return new LangAttributePointer(parentPointer);
            }
            if (parentPointer.isValidProperty(name)) {
                NodePointer childPointer = parentPointer.getPropertyPointer();
                ((PropertyPointer) childPointer).setPropertyName(
                        name.toString());
                childPointer.setAttribute(axis == Compiler.AXIS_ATTRIBUTE);
                return childPointer;
            }
            //invalid property gets nothing, not even a NullPointer
            return null;
        }
        return parentPointer;
    }

    /**
     * A path that starts with a standard InfoSet node, e.g. a DOM Node.
     * The method evaluates the first predicate in a special way and
     * then forwards to a general predicate processing method.
     * @param context evaluation context
     * @param parent parent pointer
     * @param steps path steps
     * @param currentStep step number
     * @return NodePointer
     */
    private static NodePointer doStepPredicatesStandard(
            EvalContext context, NodePointer parent,
            Step[] steps, int currentStep) {
        Step step = steps[currentStep];
        Expression[] predicates = step.getPredicates();

        int axis = step.getAxis();
        if (axis == Compiler.AXIS_SELF) {
            return doPredicate(
                context,
                parent,
                steps,
                currentStep,
                predicates,
                0);
        }

        Expression predicate = predicates[0];

        // Optimize for a single predicate to avoid building a list
        // and to allow the direct access to the index'th element
        // in the case of a simple subscript predecate
        // It is a very common use case, so it deserves individual
        // attention
        if (predicates.length == 1) {
            NodeIterator it = getNodeIterator(context, parent, step);
            NodePointer pointer = null;
            if (it != null) {
                if (predicate instanceof NameAttributeTest) { // [@name = key]
                    String key = keyFromPredicate(context, predicate);
                    for (int i = 1; it.setPosition(i); i++) {
                        NodePointer ptr = it.getNodePointer();
                        if (isNameAttributeEqual(ptr, key)) {
                            pointer = ptr;
                            break;
                        }
                    }
                }
                else {
                    int index = indexFromPredicate(context, predicate);
                    if (it.setPosition(index + 1)) {
                        pointer = it.getNodePointer();
                    }
                }
            }
            if (pointer != null) {
                return doStep(context, pointer, steps, currentStep + 1);
            }
        }
        else {
            NodeIterator it = getNodeIterator(context, parent, step);
            if (it != null) {
                List list = new ArrayList();
                for (int i = 1; it.setPosition(i); i++) {
                    list.add(it.getNodePointer());
                }
                NodePointer pointer =
                    doPredicatesStandard(
                        context,
                        list,
                        steps,
                        currentStep,
                        predicates,
                        0);
                if (pointer != null) {
                    return pointer;
                }
            }
        }
        return createNullPointer(context, parent, steps, currentStep);
    }

    /**
     * Evaluates predicates and proceeds with the subsequent steps
     * of the path.
     * @param context evaluation context
     * @param parent parent pointer
     * @param steps path steps
     * @param currentStep step number
     * @param predicates predicate expressions
     * @param currentPredicate int predicate number
     * @return NodePointer
     */
    private static NodePointer doPredicate(
                EvalContext context, NodePointer parent,
                Step[] steps, int currentStep,
                Expression[] predicates, int currentPredicate) {
        if (currentPredicate == predicates.length) {
            return doStep(context, parent, steps, currentStep + 1);
        }

        Expression predicate = predicates[currentPredicate];
        if (predicate instanceof NameAttributeTest) { // [@name = key1]
            return doPredicateName(
                context,
                parent,
                steps,
                currentStep,
                predicates,
                currentPredicate);
        }
        // else [index]
        return doPredicateIndex(
            context,
            parent,
            steps,
            currentStep,
            predicates,
            currentPredicate);
    }

    /**
     * Execute a NameAttributeTest predicate
     * @param context evaluation context
     * @param parent parent pointer
     * @param steps path steps
     * @param currentStep int step number
     * @param predicates predicates
     * @param currentPredicate int predicate number
     * @return NodePointer
     */
    private static NodePointer doPredicateName(
            EvalContext context, NodePointer parent,
            Step[] steps, int currentStep,
            Expression[] predicates, int currentPredicate) {
        Expression predicate = predicates[currentPredicate];
        String key = keyFromPredicate(context, predicate);
        NodePointer child = valuePointer(parent);
        if (child instanceof PropertyOwnerPointer) {
            PropertyPointer pointer =
                ((PropertyOwnerPointer) child).getPropertyPointer();
            pointer.setPropertyName(key);
            if (pointer.isActual()) {
                return doPredicate(
                    context,
                    pointer,
                    steps,
                    currentStep,
                    predicates,
                    currentPredicate + 1);
            }
        }
        else if (child.isCollection()) {
            // For each node in the collection, perform the following:
            // if the node is a property owner, apply this predicate to it;
            // if the node is a collection, apply this predicate to each elem.;
            // if the node is not a prop owner or a collection,
            //  see if it has the attribute "name" with the right value,
            //  if so - proceed to the next predicate
            NodePointer bestMatch = null;
            int bestQuality = 0;
            child = (NodePointer) child.clone();
            int count = child.getLength();
            for (int i = 0; i < count; i++) {
                child.setIndex(i);
                NodePointer valuePointer = valuePointer(child);
                NodePointer pointer;
                if ((valuePointer instanceof PropertyOwnerPointer)
                    || valuePointer.isCollection()) {
                    pointer =
                        doPredicateName(
                            context,
                            valuePointer,
                            steps,
                            currentStep,
                            predicates,
                            currentPredicate);
                }
                else if (isNameAttributeEqual(valuePointer, key)) {
                    pointer =
                        doPredicate(
                            context,
                            valuePointer,
                            steps,
                            currentStep,
                            predicates,
                            currentPredicate + 1);
                }
                else {
                    pointer = null;
                }
                if (pointer != null) {
                    int quality = computeQuality(pointer);
                    if (quality == PERFECT_MATCH) {
                        return pointer;
                    }
                    if (quality > bestQuality) {
                        bestMatch = (NodePointer) pointer.clone();
                        bestQuality = quality;
                    }
                }
            }
            if (bestMatch != null) {
                return bestMatch;
            }
        }
        else {
            // If the node is a standard InfoSet node (e.g. DOM Node),
            // employ doPredicates_standard, which will iterate through
            // the node's children and apply all predicates
            NodePointer found =
                doPredicatesStandard(
                    context,
                    Collections.singletonList(child),
                    steps,
                    currentStep,
                    predicates,
                    currentPredicate);
            if (found != null) {
                return found;
            }
        }
        // If nothing worked - return a null pointer
        return createNullPointerForPredicates(
            context,
            child,
            steps,
            currentStep,
            predicates,
            currentPredicate);
    }

    /**
     * Called exclusively for standard InfoSet nodes, e.g. DOM nodes
     * to evaluate predicate sequences like [@name=...][@name=...][index].
     * @param context evaluation context
     * @param parents List of parent pointers
     * @param steps path steps
     * @param currentStep step number
     * @param predicates predicates
     * @param currentPredicate int predicate number
     * @return NodePointer
     */
    private static NodePointer doPredicatesStandard(
                EvalContext context, List parents,
                Step[] steps, int currentStep,
                Expression[] predicates, int currentPredicate) {
        if (parents.size() == 0) {
            return null;
        }

        // If all predicates have been processed, take the first
        // element from the list of results and proceed to the
        // remaining steps with that element.
        if (currentPredicate == predicates.length) {
            NodePointer pointer = (NodePointer) parents.get(0);
            return doStep(context, pointer, steps, currentStep + 1);
        }

        Expression predicate = predicates[currentPredicate];
        if (predicate instanceof NameAttributeTest) {
            String key = keyFromPredicate(context, predicate);
            List newList = new ArrayList();
            for (int i = 0; i < parents.size(); i++) {
                NodePointer pointer = (NodePointer) parents.get(i);
                if (isNameAttributeEqual(pointer, key)) {
                    newList.add(pointer);
                }
            }
            if (newList.size() == 0) {
                return null;
            }
            return doPredicatesStandard(
                context,
                newList,
                steps,
                currentStep,
                predicates,
                currentPredicate + 1);
        }
        else {
            // For a subscript, simply take the corresponding
            // element from the list of results and
            // proceed to the remaining predicates with that element
            int index = indexFromPredicate(context, predicate);
            if (index < 0 || index >= parents.size()) {
                return null;
            }
            NodePointer ptr = (NodePointer) parents.get(index);
            return doPredicate(
                context,
                ptr,
                steps,
                currentStep,
                predicates,
                currentPredicate + 1);
        }
    }

    /**
     * Evaluate a subscript predicate: see if the node is a collection and
     * if the index is inside the collection.
     * @param context evaluation context
     * @param parent parent pointer
     * @param steps path steps
     * @param currentStep step number
     * @param predicates predicates
     * @param currentPredicate int predicate number
     * @return NodePointer
     */
    private static NodePointer doPredicateIndex(
            EvalContext context, NodePointer parent,
            Step[] steps, int currentStep,
            Expression[] predicates, int currentPredicate) {
        Expression predicate = predicates[currentPredicate];
        int index = indexFromPredicate(context, predicate);
        NodePointer pointer = parent;
        if (isCollectionElement(pointer, index)) {
            pointer = (NodePointer) pointer.clone();
            pointer.setIndex(index);
            return doPredicate(
                context,
                pointer,
                steps,
                currentStep,
                predicates,
                currentPredicate + 1);
        }
        return createNullPointerForPredicates(
            context,
            parent,
            steps,
            currentStep,
            predicates,
            currentPredicate);
    }

    /**
     * Extract an integer from a subscript predicate. The returned index
     * starts with 0, even though the subscript starts with 1.
     * @param context evaluation context
     * @param predicate to evaluate
     * @return calculated index
     */
    private static int indexFromPredicate(
        EvalContext context,
        Expression predicate) {
        Object value = predicate.computeValue(context);
        if (value instanceof EvalContext) {
            value = ((EvalContext) value).getSingleNodePointer();
        }
        if (value instanceof NodePointer) {
            value = ((NodePointer) value).getValue();
        }
        if (value == null) {
            throw new JXPathException("Predicate value is null: " + predicate);
        }

        if (value instanceof Number) {
            final double round = 0.5;
            return (int) (InfoSetUtil.doubleValue(value) + round) - 1;
        }
        return InfoSetUtil.booleanValue(value) ? 0 : -1;
    }

    /**
     * Extracts the string value of the expression from a predicate like
     * [@name=expression].
     * @param context evaluation context
     * @param predicate predicate to evaluate
     * @return String key extracted
     */
    private static String keyFromPredicate(EvalContext context,
                Expression predicate) {
        Expression expr =
            ((NameAttributeTest) predicate).getNameTestExpression();
        return InfoSetUtil.stringValue(expr.computeValue(context));
    }

    /**
     * For a pointer that matches an actual node, returns 0.
     * For a pointer that does not match an actual node, but whose
     * parent pointer does returns -1, etc.
     * @param pointer input pointer
     * @return int match quality code
     */
    private static int computeQuality(NodePointer pointer) {
        int quality = PERFECT_MATCH;
        while (pointer != null && !pointer.isActual()) {
            quality--;
            pointer = pointer.getImmediateParentPointer();
        }
        return quality;
    }

    /**
     * Returns true if the pointer has an attribute called "name" and
     * its value is equal to the supplied string.
     * @param pointer input pointer
     * @param name name to check
     * @return boolean
     */
    private static boolean isNameAttributeEqual(
        NodePointer pointer,
        String name) {
        NodeIterator it = pointer.attributeIterator(QNAME_NAME);
        return it != null
            && it.setPosition(1)
            && name.equals(it.getNodePointer().getValue());
    }

    /**
     * Returns true if the pointer is a collection and the index is
     * withing the bounds of the collection.
     * @param pointer input pointer
     * @param index to check
     * @return boolean
     */
    private static boolean isCollectionElement(
        NodePointer pointer,
        int index) {
        return pointer.isActual()
            && (index == 0
                || (pointer.isCollection()
                    && index >= 0
                    && index < pointer.getLength()));
    }

    /**
     * For an intermediate pointer (e.g. PropertyPointer, ContainerPointer)
     * returns a pointer for the contained value.
     * @param pointer input pointer
     * @return NodePointer
     */
    private static NodePointer valuePointer(NodePointer pointer) {
        return pointer == null ? null : pointer.getValuePointer();
    }

    /**
     * Creates a "null pointer" that
     * a) represents the requested path and
     * b) can be used for creation of missing nodes in the path.
     * @param context evaluation context
     * @param parent parent pointer
     * @param steps path steps
     * @param currentStep step number
     * @return NodePointer
     */
    public static NodePointer createNullPointer(
            EvalContext context, NodePointer parent, Step[] steps,
            int currentStep) {
        if (currentStep == steps.length) {
            return parent;
        }

        parent = valuePointer(parent);

        Step step = steps[currentStep];

        int axis = step.getAxis();
        if (axis == Compiler.AXIS_CHILD || axis == Compiler.AXIS_ATTRIBUTE) {
            NullPropertyPointer pointer = new NullPropertyPointer(parent);
            QName name = ((NodeNameTest) step.getNodeTest()).getNodeName();
            pointer.setPropertyName(name.toString());
            pointer.setAttribute(axis == Compiler.AXIS_ATTRIBUTE);
            parent = pointer;
        }
        // else { it is self::node() }

        Expression[] predicates = step.getPredicates();
        return createNullPointerForPredicates(
            context,
            parent,
            steps,
            currentStep,
            predicates,
            0);
    }

    /**
     * Creates a "null pointer" that starts with predicates.
     * @param context evaluation context
     * @param parent parent pointer
     * @param steps path steps
     * @param currentStep step number
     * @param predicates predicates
     * @param currentPredicate int predicate number
     * @return NodePointer
     */
    private static NodePointer createNullPointerForPredicates(
            EvalContext context, NodePointer parent,
            Step[] steps, int currentStep,
            Expression[] predicates, int currentPredicate) {
        for (int i = currentPredicate; i < predicates.length; i++) {
            Expression predicate = predicates[i];
            if (predicate instanceof NameAttributeTest) {
                String key = keyFromPredicate(context, predicate);
                parent = valuePointer(parent);
                NullPropertyPointer pointer = new NullPropertyPointer(parent);
                pointer.setNameAttributeValue(key);
                parent = pointer;
            }
            else {
                int index = indexFromPredicate(context, predicate);
                if (parent instanceof NullPropertyPointer) {
                    parent.setIndex(index);
                }
                else {
                    parent = new NullElementPointer(parent, index);
                }
            }
        }
        // Proceed with the remaining steps
        return createNullPointer(
                    context, parent, steps, currentStep + 1);
    }

    /**
     * Get a NodeIterator.
     * @param context evaluation context
     * @param pointer owning pointer
     * @param step triggering step
     * @return NodeIterator
     */
    private static NodeIterator getNodeIterator(
        EvalContext context,
        NodePointer pointer,
        Step step) {
        if (step.getAxis() == Compiler.AXIS_CHILD) {
            NodeTest nodeTest = step.getNodeTest();
            QName qname = ((NodeNameTest) nodeTest).getNodeName();
            String prefix = qname.getPrefix();
            if (prefix != null) {
                String namespaceURI = context.getJXPathContext()
                        .getNamespaceURI(prefix);
                nodeTest = new NodeNameTest(qname, namespaceURI);
            }
            return pointer.childIterator(nodeTest, false, null);
        }
        // else Compiler.AXIS_ATTRIBUTE
        if (!(step.getNodeTest() instanceof NodeNameTest)) {
            throw new UnsupportedOperationException(
                "Not supported node test for attributes: "
                    + step.getNodeTest());
        }
        return pointer.attributeIterator(
            ((NodeNameTest) step.getNodeTest()).getNodeName());
    }

    /**
     * Learn whether <code>name</code> is a lang attribute.
     * @param name to compare
     * @return boolean
     */
    private static boolean isLangAttribute(QName name) {
        return name.getPrefix() != null
            && name.getPrefix().equals("xml")
            && name.getName().equals("lang");
    }
}