/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/AbstractBeanNode.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/08/23 00:47:01 $
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
package org.apache.commons.jxpath.tree;

import org.w3c.dom.*;
import org.w3c.dom.Element;
import java.beans.PropertyDescriptor;

/**
 * Superclass of JXPath DOM Nodes.  It is associated with a NodeSequencer, which
 * traverses lists of nodes.  This association makes it possible for AbstractBeanNodes to
 * be hosted either directly by ElementLists or by NodeSequences, which represent
 * sublists of ElementLists.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public abstract class AbstractBeanNode extends ValueHandle implements Node {
    private Node parent;
    private NodeSequencer sequencer;
    private int index;
    private Document owner;

    /**
     * @param parent is the parent Node
     * @param sequencer is the parent NodeSequencer. It can be either an ElementList
     *        or a NodeSequence
     * @param index is the index of this node within its parent sequencer. Note that
     *        if the parent sequencer is a NodeSequence, the index identifies the node
     *        in that sequence, not the overall NodeList.
     * @param bean is the JavaBean whose property is represented by this node
     * @param propertyDescriptor maps to a property of that JavaBean
     */
    protected AbstractBeanNode(Node parent, NodeSequencer sequencer, int index, Object bean, PropertyDescriptor propertyDescriptor){
        super(bean, propertyDescriptor);
        this.parent = parent;
        this.sequencer = sequencer;
        this.index = index;
    }

    /**
     * @param bean is the property value associated with this node
     * @param name is the name of that property
     */
    protected AbstractBeanNode(Node parent, NodeSequencer sequencer, int index, Object value, String name){
        super(value, name);
        this.parent = parent;
        this.sequencer = sequencer;
        this.index = index;
    }

    /**
     * A <code>NodeList</code> that contains all children of this node. If
     * there are no children, this is a <code>NodeList</code> containing no
     * nodes.
     */
    public abstract NodeList getChildNodes();

    public abstract short getNodeType();

    /**
     * The name of this node, depending on its type.
     */
    public abstract String getNodeName();

    /**
     * Returns the position of this Node in the parent NodeSequencer.
     */
    public int getIndex(){
        return index;
    }

    /**
     * Unsupported
     */
    public String getNodeValue() throws DOMException {
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public void setNodeValue(String nodeValue) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Returns the parent Node of this Node.
     */
    public Node getParentNode() {
        return parent;
    }

    /**
     * Returns true if this Node has child nodes.
     */
    public boolean hasChildNodes() {
        return getFirstChild() != null;
    }

    /**
     * The first child of this node. If there is no such node, this returns
     * <code>null</code>.
     */
    public Node getFirstChild() {
        if (getValue() == null){
            return null;
        }

        NodeList list = getChildNodes();
        if (list instanceof NodeSequencer){
            return ((NodeSequencer)list).getFirstNode();
        }
        return null;
    }

    /**
     * The last child of this node. If there is no such node, this returns
     * <code>null</code>.
     */
    public Node getLastChild() {
        if (getValue() == null){
            return null;
        }

        NodeList list = getChildNodes();
        if (list instanceof NodeSequencer){
            return ((NodeSequencer)list).getLastNode();
        }
        return null;
    }

    /**
     * The node immediately preceding this node. If there is no such node,
     * this returns <code>null</code>.
     */
    public Node getPreviousSibling() {
        if (sequencer != null){
            return sequencer.getPreviousNode(this);
        }
        return null;
    }

    /**
     * The node immediately following this node. If there is no such node,
     * this returns <code>null</code>.
     */
    public Node getNextSibling() {
        if (sequencer != null){
            return sequencer.getNextNode(this);
        }
        return null;
    }

    /**
     * A <code>NamedNodeMap</code> containing the attributes of this node (if
     * it is an <code>Element</code>) or <code>null</code> otherwise.
     */
    public NamedNodeMap getAttributes() {
        return null;
    }

    /**
     * Always returns null
     */
    public Document getOwnerDocument() {
        if (parent != null){
            return parent.getOwnerDocument();
        }
        else {
            if (owner == null){
                owner = new BeanDocument((Element)this);
            }
            return owner;
        }
    }

    /**
     * Unsupported
     */
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public Node removeChild(Node oldChild) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public Node appendChild(Node newChild) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public Node cloneNode(boolean deep) {
        throw JXPathException.unsupported();
    }

    /**
     * Does nothing
     */
    public void normalize() {
    }

    /**
     * Tests whether the DOM implementation implements a specific feature and
     * that feature is supported by this node.
     * @return Returns <code>false</code> for all features.
     */
    public boolean isSupported(String feature, String version) {
        return false;
    }

    /**
     * The namespace URI is unspecified.
     */
    public String getNamespaceURI() {
        return null;
    }

    /**
     * The namespace prefix is unspecified.
     */
    public String getPrefix() {
        return null;
    }

    /**
     * Unsupported
     */
    public void setPrefix(String prefix) throws DOMException {
        JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public String getLocalName() {
        return null;
    }

    /**
     * Returns whether this node (if it is an element) has any attributes.
     * @return <code>true</code> if this node has any attributes,
     *   <code>false</code> otherwise.
     * @since DOM Level 2
     */
    public boolean hasAttributes() {
        return false;
    }

    /**
     * Unsupported
     */
    public Attr setAttributeNode(org.w3c.dom.Attr attr){
        throw JXPathException.unmodifiable();
    }

    public String toString(){
        return getNodeName();
    }
}