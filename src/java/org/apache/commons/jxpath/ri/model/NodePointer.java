/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/NodePointer.java,v 1.10 2002/08/10 16:13:03 dmitri Exp $
 * $Revision: 1.10 $
 * $Date: 2002/08/10 16:13:03 $
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
package org.apache.commons.jxpath.ri.model;

import java.util.Locale;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.beans.NullElementPointer;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Common superclass for Pointers of all kinds.  A NodePointer maps to
 * a deterministic XPath that represents the location of a node in an object graph.
 * This XPath uses only simple axes: child, namespace and attribute and only simple,
 * context-independent predicates.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.10 $ $Date: 2002/08/10 16:13:03 $
 */
public abstract class NodePointer implements Pointer, Cloneable, Comparable {

    public static int WHOLE_COLLECTION = Integer.MIN_VALUE;
    protected int index = WHOLE_COLLECTION;
    public static String UNKNOWN_NAMESPACE = "<<unknown namespace>>";

    /**
     * Allocates an entirely new NodePointer by iterating through all installed
     * NodePointerFactories until it finds one that can create a pointer.
     */
    public static NodePointer newNodePointer(
        QName name,
        Object bean,
        Locale locale) {
        if (bean == null) {
            return new NullPointer(name, locale);
        }
        NodePointerFactory[] factories =
            JXPathContextReferenceImpl.getNodePointerFactories();
        for (int i = 0; i < factories.length; i++) {
            NodePointer pointer = factories[i].createNodePointer(name, bean, locale);
            if (pointer != null) {
                return pointer;
            }
        }
        throw new JXPathException(
            "Could not allocate a NodePointer for object of " + bean.getClass());
    }

    /**
     * Allocates an new child NodePointer by iterating through all installed
     * NodePointerFactories until it finds one that can create a pointer.
     */
    public static NodePointer newChildNodePointer(NodePointer parent, QName name, Object bean) {
        NodePointerFactory[] factories =
            JXPathContextReferenceImpl.getNodePointerFactories();
        for (int i = 0; i < factories.length; i++) {
            NodePointer pointer = factories[i].createNodePointer(parent, name, bean);
            if (pointer != null) {
                return pointer;
            }
        }
        throw new JXPathException(
            "Could not allocate a NodePointer for object of " + bean.getClass());
    }

    protected NodePointer parent;
    protected Locale locale;

    protected NodePointer(NodePointer parent) {
        this.parent = parent;
    }

    protected NodePointer(NodePointer parent, Locale locale) {
        this.parent = parent;
        this.locale = locale;
    }

    public NodePointer getParent() {
        return parent;
    }

    /**
     * Returns true if this Pointer has no parent.
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * If true, this node does not have children
     */
    public boolean isLeaf() {
        Object value = getNode();
        return value == null
            || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }

    /**
     * If false, this node is axiliary and can only be used as an intermediate
     * in the chain of pointers.
     */
    public boolean isNode() {
        return true;
    }

    /**
     * If the pointer represents a collection, the index identifies
     * an element of that collection.  The default value of <code>index</code>
     * is <code>WHOLE_COLLECTION</code>, which just means that the pointer
     * is not indexed at all.
     * Note: the index on NodePointer starts with 0, not 1.
     */
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Returns <code>true</code> if the value of the pointer is an array or
     * a Collection.
     */
    public boolean isCollection() {
        Object value = getBaseValue();
        return value != null && ValueUtils.isCollection(value);
    }

    /**
     * If the pointer represents a collection (or collection element),
     * returns the length of the collection. Otherwise returns 1 (even if the value is null).
     */
    public int getLength() {
        Object value = getBaseValue();
        if (value == null) {
            return 1;
        }
        return ValueUtils.getLength(value);
    }

    /**
     * By default, returns <code>getNodeValue()</code>, can be overridden to
     * return a "canonical" value, like for instance a DOM element should
     * return its string value.
     */
    public Object getValue() {
        return getNode();
    }

    /**
     * If this pointer manages a transparent container, like a variable,
     * this method returns the pointer to the contents.
     * Only an auxiliary (non-node) pointer can (and should) return a
     * value pointer other than itself.
     */
    public NodePointer getValuePointer() {
        return this;
    }

    /**
     * An actual pointer points to an existing part of an object graph, even
     * if it is null. A non-actual pointer represents a part that does not exist
     * at all.
     * For instance consider the pointer "/address/street".
     * If both <em>address</em> and <em>street</em> are not null, the pointer is actual.
     * If <em>address</em> is not null, but <em>street</em> is null, the pointer is still actual.
     * If <em>address</em> is null, the pointer is not actual.
     * (In JavaBeans) if <em>address</em> is not a property of the root bean, a Pointer
     * for this path cannot be obtained at all - actual or otherwise.
     */
    public boolean isActual() {
        if (index == WHOLE_COLLECTION) {
            return true;
        }
        else {
            return index >= 0 && index < getLength();
        }
    }

    /**
     * Returns the name of this node. Can be null.
     */
    public abstract QName getName();

    /**
     * Returns the value represented by the pointer before indexing.
     * So, if the node represents an element of a collection, this
     * method returns the collection itself.
     */
    public abstract Object getBaseValue();

    /**
     * Returns the object the pointer points to; does not convert it
     * to a "canonical" type.
     * 
     * @deprecated 1.1 Please use getNode()
     */
    public Object getNodeValue(){
    	return getNode();
    }

    /**
     * Returns the object the pointer points to; does not convert it
     * to a "canonical" type.
     */
    public abstract Object getNode();
    
    /**
     * Converts the value to the required type and changes the corresponding
     * object to that value.
     */
    public abstract void setValue(Object value);

    /**
     * Compares two child NodePointers and returns a positive number,
     * zero or a positive number according to the order of the pointers.
     */
    public abstract int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2);

    /**
     * Checks if this Pointer matches the supplied NodeTest.
     */
    public boolean testNode(NodeTest test) {
        if (test == null) {
            return true;
        }
        else if (test instanceof NodeNameTest) {
            if (!isNode()) {
                return false;
            }
            QName testName = ((NodeNameTest) test).getNodeName();
            QName nodeName = getName();
            String testPrefix = testName.getPrefix();
            String nodePrefix = nodeName.getPrefix();
            if (!equalStrings(testPrefix, nodePrefix)) {
                String testNS = getNamespaceURI(testPrefix);
                String nodeNS = getNamespaceURI(nodePrefix);
                if (!equalStrings(testNS, nodeNS)) {
                    return false;
                }
            }
            String testLocalName = testName.getName();
            if (testLocalName.equals("*")) {
                return true;
            }
            return testLocalName.equals(nodeName.getName());
        }
        else if (test instanceof NodeTypeTest) {
            if (((NodeTypeTest) test).getNodeType() == Compiler.NODE_TYPE_NODE) {
                return isNode();
            }
        }
        return false;
    }

    private static boolean equalStrings(String s1, String s2) {
        if (s1 == null && s2 != null) {
            return false;
        }
        if (s1 != null && !s1.equals(s2)) {
            return false;
        }
        return true;
    }

    /**
     *  Called directly by JXPathContext. Must create path and
     *  set value.
     */
    public NodePointer createPath(JXPathContext context, Object value) {
        setValue(value);
        return this;
    }

    /**
     * Remove the node of the object graph this pointer points to.
     */
    public void remove(){
        // It is a no-op

//        System.err.println("REMOVING: " + asPath() + " " + getClass());
//        printPointerChain();
    }

    /**
     * Called by a child pointer when it needs to create a parent object.
     * Must create an object described by this pointer and return
     * a new pointer that properly describes the new object.
     */
    public NodePointer createPath(JXPathContext context) {
        return this;
    }

    /**
     * Called by a child pointer if that child needs to assign the value
     * supplied in the createPath(context, value) call to a non-existent
     * node. This method must may have to expand the collection in order to
     * assign the element.
     */
    public NodePointer createChild(JXPathContext context, QName name,
                            int index, Object value) {
        throw new JXPathException(
            "Cannot create an object for path "
                + asPath()
                + ", operation is not allowed for this type of node");
    }

    /**
     * Called by a child pointer when it needs to create a parent object
     * for a non-existent collection element.  It may have to expand the collection,
     * then create an element object and return a new pointer describing the
     * newly created element.
     */
    public NodePointer createChild(JXPathContext context, QName name, int index) {
        throw new JXPathException(
            "Cannot create an object for path "
                + asPath()
                + ", operation is not allowed for this type of node");
    }

    /**
     * If the Pointer has a parent, returns the parent's locale;
     * otherwise returns the locale specified when this Pointer
     * was created.
     */
    public Locale getLocale() {
        if (locale == null) {
            if (parent != null) {
                locale = parent.getLocale();
            }
        }
        return locale;
    }

    /**
     * Returns true if the selected locale name starts
     * with the specified prefix <i>lang</i>, case-insensitive.
     */
    public boolean isLanguage(String lang) {
        Locale loc = getLocale();
        String name = loc.toString().replace('_', '-');
        return name.toUpperCase().startsWith(lang.toUpperCase());
    }

    /**
     * Returns a NodeIterator that iterates over all children or all children
     * that match the given NodeTest, starting with the specified one.
     */
    public NodeIterator childIterator(
                    NodeTest test, boolean reverse, NodePointer startWith) {
        NodePointer valuePointer = getValuePointer();
        if (valuePointer != null && valuePointer != this){
            return valuePointer.childIterator(test, reverse, startWith);
        }
        return null;
    }

    /**
     * Returns a NodeIterator that iterates over all attributes of the current node
     * matching the supplied node name (could have a wildcard).
     * May return null if the object does not support the attributes.
     */
    public NodeIterator attributeIterator(QName qname) {
        NodePointer valuePointer = getValuePointer();
        if (valuePointer != null && valuePointer != this){
            return valuePointer.attributeIterator(qname);
        }
        return null;
    }

    /**
     * Returns a NodeIterator that iterates over all namespaces of the value
     * currently pointed at.
     * May return null if the object does not support the namespaces.
     */
    public NodeIterator namespaceIterator() {
        return null;
    }

    /**
     * Returns a NodePointer for the specified namespace. Will return null
     * if namespaces are not supported. Will return UNKNOWN_NAMESPACE if there is no such namespace.
     */
    public NodePointer namespacePointer(String namespace) {
        return null;
    }

    /**
     * Decodes a namespace prefix to the corresponding URI.
     */
    public String getNamespaceURI(String prefix) {
        return null;
    }

    /**
     * Returns the namespace URI associated with this Pointer.
     */
    public String getNamespaceURI() {
        return null;
    }

    /**
     * Returns true if the supplied prefix represents the
     * default namespace in the context of the current node.
     */
    protected boolean isDefaultNamespace(String prefix) {
        if (prefix == null) {
            return true;
        }

        String namespace = getNamespaceURI(prefix);
        if (namespace == null) {
            return false; // undefined namespace
        }

        return namespace.equals(getDefaultNamespaceURI());
    }

    protected String getDefaultNamespaceURI() {
        return null;
    }

    /**
     * Returns a name that consists of the namespaceURI and the local name
     * of the node.  For non-XML pointers, returns the Pointer's qualified name.
     */
    public QName getExpandedName() {
        return getName();
    }

    /**
     * Locates a node by ID.
     */
    public Pointer getPointerByID(JXPathContext context, String id){
        return context.getPointerByID(id);
    }

    /**
     * Locates a node by key and value.
     */
    public Pointer getPointerByKey(JXPathContext context,
                                      String key, String value){
        return context.getPointerByKey(key, value);
    }

    /**
     * Returns an XPath that maps to this Pointer.
     */
    public String asPath() {
        StringBuffer buffer = new StringBuffer();
        if (getParent() != null) {
            buffer.append(getParent().asPath());
            // TBD: the following needs to be redesigned.  What this condition says is
            // "if the parent of this node has already appended this node's name,
            // don't do it again".  However, I would hate to add an ugly API like
            // "isResponsibleForAppendingChildName()".
            if (getParent().isNode() || (parent instanceof NullElementPointer)) {
                QName name = getName();
                if (name != null) {
                    buffer.append('/');
                    buffer.append(name);
                }
            }
        }
        else {
            QName name = getName();
            buffer.append(name);
        }
        if (index != WHOLE_COLLECTION && isCollection()) {
            buffer.append('[').append(index + 1).append(']');
        }
        return buffer.toString();
    }

    public static int count = 0;
    public Object clone() {
        count ++;
        try {
            NodePointer ptr = (NodePointer)super.clone();
            if (parent != null){
                ptr.parent = (NodePointer)parent.clone();
            }
            return ptr;
        }
        catch (CloneNotSupportedException ex) {
            // Of course it is supported
            ex.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return asPath();
    }

    public int compareTo(Object object){
        NodePointer pointer = (NodePointer) object;         // Let it throw a ClassCastException
        if (parent == pointer.parent){
            if (parent == null){
                return 0;
            }
            return parent.compareChildNodePointers(this, pointer);
        }

        // Task 1: find the common parent
        int depth1 = 0;
        NodePointer p1 = this;
        while (p1 != null){
            depth1 ++;
            p1 = p1.parent;
        }
        int depth2 = 0;
        NodePointer p2 = pointer;
        while (p2 != null){
            depth2 ++;
            p2 = p2.parent;
        }
        return compareNodePointers(this, depth1, pointer, depth2);
    }

    private int compareNodePointers(NodePointer p1, int depth1, NodePointer p2, int depth2){
//        System.err.println("Comparing " + p1.asPath() + " (" + depth1 + ") ~ " +
//                p2.asPath() + " (" + depth2 + ")");
        if (depth1 < depth2){
            int r = compareNodePointers(p1, depth1, p2.parent, depth2-1);
            if (r != 0){
                return r;
            }
            return -1;
        }
        else if (depth1 > depth2){
            int r = compareNodePointers(p1.parent, depth1-1, p2, depth2);
            if (r != 0){
                return r;
            }
            return 1;
        }
        if (p1 == null && p2 == null){
            return 0;
        }

        if (p1 != null && p1.equals(p2)){
            return 0;
        }

        if (depth1 == 1){
            throw new JXPathException(
                "Cannot compare pointers that do not belong to the same tree: '"
                + p1 + "' and '" + p2 + "'");
        }
        int r = compareNodePointers(p1.parent, depth1 - 1, p2.parent, depth2 - 1);
        if (r != 0){
            return r;
        }

        return p1.parent.compareChildNodePointers(p1, p2);
    }

    /**
     * Print internal structure of a pointer for debugging
     */
    public void printPointerChain(){
        printDeep(this, "");
    }

    private static void printDeep(NodePointer pointer, String indent){
        if (indent.length() == 0){
            System.err.println("POINTER: " + pointer + "(" +
                    pointer.getClass().getName() + ")");
        }
        else {
            System.err.println(indent + " of " + pointer + "(" +
                    pointer.getClass().getName() + ")");
        }
        if (pointer.getParent() != null){
            printDeep(pointer.getParent(), indent + "  ");
        }
    }
}