/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/AbstractBeanElement.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
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
 * Common superclass for light-weight read-only DOM elements. An AbstractBeanElement
 * is initialized with a NodeSequencer, which lets the element navigate
 * to its neighbours in a NodeList or NodeSequence containing the element.
 * By virtue of inheritance from ValueHandle, an AbstractBeanElement is associated
 * with a named value or a property of a JavaBean.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public abstract class AbstractBeanElement extends AbstractBeanNode implements Element {

    protected AbstractBeanElement(Node parent, NodeSequencer sequencer, int index, Object bean, PropertyDescriptor propertyDescriptor){
        super(parent, sequencer, index, bean, propertyDescriptor);
    }

    protected AbstractBeanElement(Node parent, NodeSequencer sequencer, int index, Object bean, String name){
        super(parent, sequencer, index, bean, name);
    }

    /**
     * Returns the value name.
     */
    public String getTagName() {
        return getValueName();
    }

    /**
     * Returns the value name.
     */
    public String getNodeName() {
        return getValueName();
    }

    /**
     * Returns ELEMENT_NODE
     */
    public short getNodeType() {
        return Node.ELEMENT_NODE;
    }

    /**
     * Unsupported
     */
    public String getAttribute(String name) {
        return null;
    }

    /**
     * Unsupported
     */
    public void setAttribute(String name, String value) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public void removeAttribute(String name) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported.
     */
    public Attr getAttributeNode(String name) {
        return null;
    }


    /**
     * Unsupported
     */
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException{
        throw JXPathException.unmodifiable();
    }

    /**
     * TBD
     */
    public NodeList getElementsByTagName(String name){
        return null;
    }

    /**
     * Unsupported
     */
    public String getAttributeNS(String namespaceURI, String localName){
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public void setAttributeNS(String namespaceURI,  String qualifiedName,
                               String value) throws DOMException{
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException{
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public Attr getAttributeNodeNS(String namespaceURI, String localName){
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException{
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName){
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported. Returns false.
     */
    public boolean hasAttribute(String name){
        return false;
    }

    /**
     * Returns false.
     */
    public boolean hasAttributeNS(String namespaceURI, String localName){
        return false;
    }
}