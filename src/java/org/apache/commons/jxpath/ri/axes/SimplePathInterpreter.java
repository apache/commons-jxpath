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
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.*;

/**
 * An simple XPath evaluation mechanism, which works only for some xpaths
 * but is much faster than the usual process. It is only used for
 * xpaths which have no context-dependent parts, consist entirely of
 * child:: steps with predicates that either integer or have the form
 * <code>[@name = ...]</code>.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.10 $ $Date: 2002/04/24 04:05:40 $
 */
public class SimplePathInterpreter {

    /**
     * Walks a location path in a highly simplified fashion: from pointer to
     * pointer, no contexts.  This is only possible if the path consists of
     * simple steps like "/foo[3]" and is context-independent.
     */
    public static NodePointer interpretPath(EvalContext context, NodePointer parentPointer, Step steps[]){
        if (parentPointer == null){
            return null;
        }

        NodePointer pointer = (NodePointer)parentPointer.clone();
        while (pointer != null && !pointer.isNode()){
            pointer = pointer.getValuePointer();
        }

        for (int i = 0; i < steps.length; i++){
            Step step = steps[i];
            int defaultIndex = (i == steps.length - 1 ? -1 : 0);
            QName name = ((NodeNameTest)step.getNodeTest()).getNodeName();
            Expression predicates[] = step.getPredicates();

            // The following complicated logic is designed to translate
            // an xpath like "foo[@name='x'][@name='y'][3]/bar/baz[4]" into
            // a sequence of "single steps", each of which takes a node pointer,
            // a name and an optional index and gets you another node pointer.

            // Note: if the last step is not indexed, the default index used
            // for that very last step is "-1", that is "do not index at all",
            // not "0" as in all preceeding steps.

            int count = (predicates == null ? 0 : predicates.length);
            if (count == 0){
                pointer = singleStep(context, pointer, name, defaultIndex, false);
            }
            else {
                Expression lastIndexPredicate = null;
                if (predicates[count - 1].
                            getEvaluationHint(CoreOperation.DYNAMIC_PROPERTY_ACCESS_HINT) == null){
                    lastIndexPredicate = predicates[count - 1];
                }

                if (lastIndexPredicate != null){
                    int index = indexFromPredicate(context, lastIndexPredicate);
                    if (count == 1){
                        pointer = singleStep(context, pointer, name, index, false);
                    }
                    else {
                        pointer = singleStep(context, pointer, name, -1, false);
                        for (int j = 0; j < count - 1; j++){
                            String key = keyFromPredicate(context, predicates[j]);
                            if (j < count - 2){
                                pointer = singleStep(context, pointer, key, -1, true);
                            }
                            else {
                                pointer = singleStep(context, pointer, key, index, true);
                            }
                        }
                    }
                }
                else {
                    pointer = singleStep(context, pointer, name, -1, false);
                    for (int j = 0; j < count; j++){
                        String key = keyFromPredicate(context, predicates[j]);
                        if (j < count - 1){
                            pointer = singleStep(context, pointer, key, -1, true);
                        }
                        else {
                            pointer = singleStep(context, pointer, key, defaultIndex, true);
                        }
                    }
                }
            }
        }
        return pointer;
    }

    /**
     * Interprets predicates for the root expression of an Expression Path without creating
     * any intermediate contexts.  This is an option used for optimization when the path
     * has a simple structure and predicates are context-independent.
     */
    public static NodePointer interpretPredicates(EvalContext context, NodePointer pointer, Expression predicates[]){
        if (predicates == null || predicates.length == 0 || pointer == null){
            return pointer;
        }

        // The following complicated logic is designed to translate
        // an xpath like "$foo[@name='x'][@name='y'][3]" into
        // a sequence of "single steps", each of which takes a node pointer,
        // a name and an optional index and gets you another node pointer.

        int count = predicates.length;
        Expression lastIndexPredicate = null;
        if (predicates[count - 1].
                    getEvaluationHint(CoreOperation.DYNAMIC_PROPERTY_ACCESS_HINT) == null){
            lastIndexPredicate = predicates[count - 1];
        }

        if (lastIndexPredicate != null){
            int index = indexFromPredicate(context, lastIndexPredicate);
            if (count == 1){
                if (index >= 0 && index < pointer.getLength()){
                    pointer.setIndex(index);
                }
                else {
                    pointer = new NullElementPointer(pointer, index);
                }
            }
            else {
                for (int j = 0; j < count - 1; j++){
                    String key = keyFromPredicate(context, predicates[j]);
                    if (j < count - 2){
                        pointer = singleStep(context, pointer, key, -1, true);
                    }
                    else {
                        pointer = singleStep(context, pointer, key, index, true);
                    }
                }
            }
        }
        else {
            for (int j = 0; j < count; j++){
                String key = keyFromPredicate(context, predicates[j]);
                if (j < count - 1){
                    pointer = singleStep(context, pointer, key, -1, true);
                }
                else {
                    pointer = singleStep(context, pointer, key, -1, true);
                }
            }
        }
        return pointer;
    }

    /**
     * @param property can be either a name or a QName
     */
    private static NodePointer singleStep(EvalContext context, NodePointer parent, Object property, int index, boolean dynamic){
        if (parent instanceof PropertyOwnerPointer){
            PropertyPointer pointer = ((PropertyOwnerPointer)parent).getPropertyPointer();
            String name;
            if (property instanceof QName){
                name = ((QName)property).getName();
            }
            else {
                name = (String)property;
            }
            pointer.setPropertyName(name);
            if (pointer instanceof NullPropertyPointer && dynamic){
                ((NullPropertyPointer)pointer).setDynamic(true);
            }
            if (index != -1){
                if (index >= 0 && index < pointer.getLength()){
                    pointer.setIndex(index);
                    return pointer.getValuePointer();
                }
                else {
                    return new NullElementPointer(pointer, index).getValuePointer();
                }
            }
            else {
                return pointer.getValuePointer();
            }
        }
        else {
            QName name;
            if (property instanceof QName){
                name = (QName)property;
            }
            else {
                name = new QName(null, (String)property);
            }
            NodeIterator it = parent.childIterator(new NodeNameTest(name), false, null);
            if (it != null && it.setPosition(index == -1 ? 1 : index + 1)){
                return it.getNodePointer();
            }
            else {
                PropertyPointer pointer = new NullPropertyPointer(parent);
                pointer.setPropertyName(name.toString());
                pointer.setIndex(index);
                return pointer.getValuePointer();
            }
        }
    }

    private static int indexFromPredicate(EvalContext context, Expression predicate){
        Object value = context.eval(predicate, true);
        if (value instanceof EvalContext){
            value = ((EvalContext)value).getSingleNodePointer();
        }
        if (value instanceof NodePointer){
            value = ((NodePointer)value).getValue();
        }
        if (value == null){
            throw new RuntimeException("Predicate is null: " + value);
        }

        if (value instanceof Number){
            return (int)(context.doubleValue(value) + 0.5) - 1;
        }
        else if (context.booleanValue(value)){
            return 0;
        }

        return -1;
    }

    private static String keyFromPredicate(EvalContext context, Expression predicate){
        Expression expr = (Expression)predicate.
                getEvaluationHint(CoreOperation.DYNAMIC_PROPERTY_ACCESS_HINT);
        return context.stringValue(context.eval(expr));
    }
}