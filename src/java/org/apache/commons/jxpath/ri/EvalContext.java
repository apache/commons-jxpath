/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/EvalContext.java,v 1.19 2002/11/26 01:20:06 dmitri Exp $
 * $Revision: 1.19 $
 * $Date: 2002/11/26 01:20:06 $
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

import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.axes.RootContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * An XPath evaluation context.
 *
 * When evaluating a path, a chain of EvalContexts is created, each
 * context in the chain representing a step of the path. Subclasses of EvalContext
 * implement behavior of various XPath axes: "child::", "parent::" etc.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.19 $ $Date: 2002/11/26 01:20:06 $
 */
public abstract class EvalContext implements ExpressionContext, Iterator {
    protected EvalContext parentContext;
    protected RootContext rootContext;
    protected int position = 0;
    private boolean startedSetIteration = false;
    private boolean done = false;
    private boolean hasPerformedIteratorStep = false;
    private Iterator pointerIterator;

    // Sorts in the reverse order to the one defined by the Comparable
    // interface.
    private static final Comparator REVERSE_COMPARATOR = new Comparator(){
        public int compare(Object o1, Object o2){
            return ((Comparable)o2).compareTo(o1);
        }
    };

    public EvalContext(EvalContext parentContext){
        this.parentContext = parentContext;
    }

    public Pointer getContextNodePointer(){
        return getCurrentNodePointer();
    }

    public JXPathContext getJXPathContext(){
        return getRootContext().getJXPathContext();
    }

    public int getPosition(){
        return position;
    }

    /**
     * Determines the document order for this context.
     *
     * @return 1 ascending order, -1 descending order,
     *  0 - does not require ordering
     */
    public int getDocumentOrder(){
        // Default behavior: if the parent needs to be ordered,
        // this one needs to be ordered too
        if (parentContext != null && parentContext.getDocumentOrder() != 0){
            return 1;
        }
        return 0;
    }

    /**
     * Returns true if there are mode nodes matching the context's constraints.
     */
    public boolean hasNext(){
        if (pointerIterator != null){
            return pointerIterator.hasNext();
        }

        if (getDocumentOrder() != 0){
            return constructIterator();
        }
        else {
            if (!done && !hasPerformedIteratorStep){
                performIteratorStep();
            }
            return !done;
        }
    }

    /**
     * Returns the next node pointer in the context
     */
    public Object next(){
        if (pointerIterator != null){
            return pointerIterator.next();
        }

        if (getDocumentOrder() != 0){
            if (!constructIterator()){
                throw new NoSuchElementException();
            }
            return pointerIterator.next();
        }
        else {
            if (!done && !hasPerformedIteratorStep){
                performIteratorStep();
            }
            if (done){
                throw new NoSuchElementException();
            }
            hasPerformedIteratorStep = false;
            return (NodePointer)getCurrentNodePointer().clone();
        }
    }

    /**
     * Moves the iterator forward by one position
     */
    private void performIteratorStep(){
        done = true;
        if (position != 0 && nextNode()){
            done = false;
        }
        else {
            while (nextSet()){
                if (nextNode()){
                    done = false;
                    break;
                }
            }
        }
        hasPerformedIteratorStep = true;
    }

    /**
     * Operation is not supported
     */
    public void remove(){
        throw new UnsupportedOperationException(
            "JXPath iterators cannot remove nodes");
    }

    private boolean constructIterator(){
        HashSet set = new HashSet();
        ArrayList list = new ArrayList();
        while (nextSet()){
            while (nextNode()){
                NodePointer pointer = getCurrentNodePointer();
                if (!set.contains(pointer)){
                    Pointer cln = (Pointer)pointer.clone();
                    set.add(cln);
                    list.add(cln);
                }
            }
        }
        if (list.isEmpty()){
            return false;
        }

        if (getDocumentOrder() == 1){
            Collections.sort(list);
        }
        else {
            Collections.sort(list, REVERSE_COMPARATOR);
        }
        pointerIterator = list.iterator();
        return true;
    }

    /**
     * Returns the list of all Pointers in this context for the current
     * position of the parent context.
     */
    public List getContextNodeList() {
        int pos = position;
        if (pos != 0) {
            reset();
        }
        List list = new ArrayList();
        while (nextNode()) {
            list.add(getCurrentNodePointer());
        }
        if (pos != 0) {
            setPosition(pos);
        }
        else {
            reset();
        }
        return list;
    }

    /**
     * Returns the list of all Pointers in this context for all positions
     * of the parent contexts.
     */
    public List getPointerList() {
        int pos = position;
        if (pos != 0) {
            reset();
        }
        List list = new ArrayList();
        while (nextSet()){
            while (nextNode()) {
                list.add(getCurrentNodePointer());
            }
        }
        if (pos != 0) {
            setPosition(pos);
        }
        else {
            reset();
        }
        return list;
    }

    public String toString() {
        Pointer ptr = getContextNodePointer();
        if (ptr == null) {
            return "Empty expression context";
        }
        else {
            return "Expression context [" + getPosition() + "] " + ptr.asPath();
        }
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
     */
    public void reset(){
        position = 0;
    }

    public int getCurrentPosition(){
        return position;
    }

    /**
     * Returns the first encountered Pointer that matches the current
     * context's criteria.
     */
    public Pointer getSingleNodePointer(){
        reset();
        while(nextSet()){
            if (nextNode()){
                return getCurrentNodePointer();
            }
        }
        return null;
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
    public boolean nextSet() {
        reset(); // Restart iteration within the set

        // Most of the time you have one set per parent node
        // First time this method is called, we should look for
        // the first parent set that contains at least one node.
        if (!startedSetIteration) {
            startedSetIteration = true;
            while (parentContext.nextSet()) {
                if (parentContext.nextNode()) {
                    return true;
                }
            }
            return false;
        }

        // In subsequent calls, we see if the parent context
        // has any nodes left in the current set
        if (parentContext.nextNode()) {
            return true;
        }

        // If not, we look for the next set that contains
        // at least one node
        while (parentContext.nextSet()) {
            if (parentContext.nextNode()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there is another object in the current set.
     * Switches the current position and node to the next object.
     */
    public abstract boolean nextNode();

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
}