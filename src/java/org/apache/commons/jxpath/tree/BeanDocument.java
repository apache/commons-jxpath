/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/BeanDocument.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
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

import java.util.*;
import org.apache.commons.jxpath.*;
import org.w3c.dom.*;
import org.w3c.dom.Element;
import java.beans.PropertyDescriptor;

/**
 * BeanDocument is a DOM Document for a JXPath tree. Since the JXPath tree is
 * unmodifiable, most methods of this class throw DOMException (unsupported or
 * umodifiable).
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public class BeanDocument implements Document {
    private Element documentElement;

    public BeanDocument(Element docElement){
        this.documentElement = docElement;
    }

    /**
     * Returns null.
     */
    public DocumentType getDoctype(){
        return null;
    }

    /**
     * Unsupported
     */
    public DOMImplementation getImplementation(){
        throw JXPathException.unsupported();
    }

    /**
     * Returns the root element
     */
    public Element getDocumentElement(){
        return documentElement;
    }

    /**
     * Unsupported
     */
    public Element createElement(String tagName) throws DOMException{
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public DocumentFragment createDocumentFragment(){
        throw JXPathException.unsupported();
    }


    /**
     * Unsupported
     */
    public Text createTextNode(String data){
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public Comment createComment(String data){
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public CDATASection createCDATASection(String data) throws DOMException {
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public ProcessingInstruction createProcessingInstruction(String target, String data)
                                                             throws DOMException {
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public Attr createAttribute(String name)
                                throws DOMException {
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public EntityReference createEntityReference(String name)
                                                 throws DOMException {
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public NodeList getElementsByTagName(String tagname) {
        throw JXPathException.unsupported();
    }


    /**
     * Unsupported
     */
    public Node importNode(Node importedNode, boolean deep)
                           throws DOMException {
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public Element createElementNS(String namespaceURI,
                                   String qualifiedName)
                                   throws DOMException{
        throw JXPathException.unsupported();
    }

    /**
     * Unsupported
     */
    public Attr createAttributeNS(String namespaceURI,
                                  String qualifiedName)
                                  throws DOMException {
        throw JXPathException.unsupported();
    }

    /**
     */
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName){
        throw JXPathException.unsupported();
    }

    /**
     * Returns null (for now).
     */
    public Element getElementById(String elementId){
        return null;
    }

    /**
     * Returns null
     */
    public String getNodeName(){
        return null;
    }

    /**
     * Returns null.
     */
    public String getNodeValue()
                                 throws DOMException{
        return null;
    }
    public void setNodeValue(String nodeValue)
                                 throws DOMException{
        throw JXPathException.unmodifiable();
    }

    /**
     * Returns DOCUMENT_NODE
     */
    public short getNodeType(){
        return Node.DOCUMENT_NODE;
    }

    /**
     * Returns null
     */
    public Node getParentNode(){
        return null;
    }

    /**
     * TBD
     */
    public NodeList getChildNodes(){
        return null;
    }

    /**
     * TBD
     */
    public Node getFirstChild(){
        return null;
    }

    /**
     * TBD
     */
    public Node getLastChild(){
        return null;
    }

    /**
     * Returns null
     */
    public Node getPreviousSibling(){
        return null;
    }

    /**
     * Returns null
     */
    public Node getNextSibling(){
        return null;
    }

    /**
     * Returns null
     */
    public NamedNodeMap getAttributes(){
        return null;
    }

    /**
     * Returns null
     */
    public Document getOwnerDocument(){
        return null;
    }

    /**
     * Unmodifiable
     */
    public Node insertBefore(Node newChild, Node refChild)
                             throws DOMException{
        throw JXPathException.unmodifiable();
    }

    /**
     * Unmodifiable
     */
    public Node replaceChild(Node newChild,
                             Node oldChild)
                             throws DOMException{
        throw JXPathException.unmodifiable();
    }

    /**
     * Unmodifiable
     */
    public Node removeChild(Node oldChild)
                            throws DOMException{
        throw JXPathException.unmodifiable();
    }

    /**
     * Unmodifiable
     */
    public Node appendChild(Node newChild)
                            throws DOMException{
        throw JXPathException.unmodifiable();
    }

    /**
     * Returns true
     */
    public boolean hasChildNodes(){
        return true;
    }

    /**
     * Returns <b>this</b>
     */
    public Node cloneNode(boolean deep){
        return this;
    }

    /**
     * No-op
     */
    public void normalize(){
    }

    /**
     * Returns false
     */
    public boolean isSupported(String feature, String version){
        return false;
    }

    /**
     * Returns null
     */
    public String getNamespaceURI(){
        return null;
    }

    /**
     * Returns null
     */
    public String getPrefix(){
        return null;
    }
    public void setPrefix(String prefix)
                               throws DOMException{
        throw JXPathException.unmodifiable();
    }

    /**
     * Returns null
     */
    public String getLocalName(){
        return null;
    }

    /**
     * Returns false
     */
    public boolean hasAttributes(){
        return false;
    }
}