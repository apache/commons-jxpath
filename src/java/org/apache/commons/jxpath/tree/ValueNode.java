/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/ValueNode.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
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
import java.beans.PropertyDescriptor;

/**
 * A TEXT node associated with a value.  The textual representation of this
 * node is the result of conversion of that value to String.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public class ValueNode extends AbstractBeanNode implements Text {

    /**
     * @param parent is the parent Node
     * @param value is a value to be associated with the node. It can be null.
     */
    public ValueNode(Node parent, Object value, String name){
        super(parent, null, 0, value, name);
    }

    /**
     * @param parent is the parent Node
     * @param bean is a bean whose property is associated with the node.
     */
    public ValueNode(Node parent, Object bean, PropertyDescriptor propertyDescriptor){
        super(parent, null, 0, bean, propertyDescriptor);
    }

    /**
     * This node does not have any children.
     */
    public NodeList getChildNodes() {
        return EMPTY_LIST;
    }

    /**
     * This is a TEXT node.
     */
    public short getNodeType() {
        return Node.TEXT_NODE;
    }

    /**
     * This node does not have a name.
     */
    public String getNodeName() {
        return null;
    }

    /**
     * Returns the result of conversion of the node's value to string.
     */
    public String getNodeValue() throws DOMException {
        return String.valueOf(getValue());
    }

    /**
     * Returns <code>null</code>.
     */
    public Node getPreviousSibling() {
        return null;
    }

    /**
     * Returns <code>null</code>.
     */
    public Node getNextSibling() {
        return null;
    }

    /**
     * Unsupported
     */
    public Text splitText(int i) {
        throw JXPathException.unsupported();
    }

    /**
     * The character data of the node that implements this interface. The DOM
     * implementation may not put arbitrary limits on the amount of data
     * that may be stored in a <code>CharacterData</code> node. However,
     * implementation limits may mean that the entirety of a node's data may
     * not fit into a single <code>DOMString</code>. In such cases, the user
     * may call <code>substringData</code> to retrieve the data in
     * appropriately sized pieces.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     * @exception DOMException
     *   DOMSTRING_SIZE_ERR: Raised when it would return more characters than
     *   fit in a <code>DOMString</code> variable on the implementation
     *   platform.
     */
    public String getData() throws DOMException {
        return String.valueOf(getValue());
    }

    public void setData(String data) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * The number of 16-bit units that are available through <code>data</code>
     * and the <code>substringData</code> method below. This may have the
     * value zero, i.e., <code>CharacterData</code> nodes may be empty.
     */
    public int getLength() {
        return 0;
    }

    /**
     *  Unsupported
     */
    public String substringData(int offset, int count) throws DOMException {
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public void appendData(String arg) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public void insertData(int offset, String arg) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public void deleteData(int offset, int count) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Unsupported
     */
    public void replaceData(int offset, int count, String arg) throws DOMException {
        throw JXPathException.unmodifiable();
    }

    /**
     * Implementation of an empty Node List.
     */
    private static final NodeList EMPTY_LIST = new NodeList (){
        public int getLength(){ return 0; }
        public Node item(int i) { return null; }
    };
}