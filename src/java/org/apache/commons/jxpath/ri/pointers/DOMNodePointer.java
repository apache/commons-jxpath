/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/pointers/Attic/DOMNodePointer.java,v 1.1 2001/09/03 01:22:31 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/09/03 01:22:31 $
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
package org.apache.commons.jxpath.ri.pointers;

import org.apache.commons.jxpath.*;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.*;

import java.lang.reflect.*;
import java.util.*;
import java.beans.*;
import org.w3c.dom.*;

/**
 * A Pointer that points to a DOM node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/09/03 01:22:31 $
 */
public class DOMNodePointer extends NodePointer {
    private Node node;

    public DOMNodePointer(Node node){
        super(null);
        this.node = node;
    }

    public DOMNodePointer(NodePointer parent, Node node){
        super(parent);
        this.node = node;
    }

    public QName getName(){
        if (node.getNodeType() == Node.ELEMENT_NODE){
            return new QName(node.getNamespaceURI(), node.getNodeName());
        }
        return null;
    }

    public NodeIterator childIterator(QName name, boolean reverse){
        return new DOMNodeIterator(this, true, name, reverse);
    }

    public NodeIterator siblingIterator(QName name, boolean reverse){
        return new DOMNodeIterator(this, false, name, reverse);
    }

    public NodePointer attributePointer(QName name){
        NamedNodeMap map = node.getAttributes();
        Node attr;
        if (name.getPrefix() == null){
            attr = map.getNamedItem(name.getName());
        }
        else {
            attr = map.getNamedItemNS(name.getPrefix(), name.getName());
        }
        if (attr == null){
            return null;
        }
        else {
            return new DOMAttributePointer(this, (Attr)attr);
        }
    }

    public Object getBaseValue(){
        return node;
    }

    public Object getValue(){
        return node;
    }

    public boolean isLeaf(){
        return !node.hasChildNodes();
    }

    /**
     * Throws UnsupportedOperationException.
     */
    public void setValue(Object value){
        throw new UnsupportedOperationException("Cannot modify DOM trees");
    }

    /**
     */
    public String asPath(){
        StringBuffer buffer = new StringBuffer();
        if (parent != null){
            buffer.append(parent.asPath());
        }
        switch(node.getNodeType()){
            case Node.ELEMENT_NODE:
                buffer.append('/');
                buffer.append(getName().asString());
                buffer.append('[').append(getRelativePositionByName()).append(']');
                break;
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
                // TBD: position
                buffer.append("/self::text()");
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                // TBD: position
                String target = ((ProcessingInstruction)node).getTarget();
                buffer.append("/self::processing-instruction(").append(target).append(')');
                break;
            case Node.DOCUMENT_NODE:
                // That'll be empty
        }
        return buffer.toString();
    }

    private int getRelativePositionByName(){
        int count = 1;
        Node n = node.getPreviousSibling();
        while (n != null){
            if (n.getNodeType() == Node.ELEMENT_NODE){
                String ns = n.getNamespaceURI();
                String nm = n.getNodeName();
                if ((ns != null && ns.equals(node.getNamespaceURI())) ||
                    (ns == null && node.getNamespaceURI() == null)){
                    if ((nm != null && nm.equals(node.getNodeName())) ||
                        (nm == null && node.getNodeName() == null)){
                        count ++;
                    }
                }
            }
            n = n.getPreviousSibling();
        }
        return count;
    }

    public int hashCode(){
        return System.identityHashCode(node);
    }

    public boolean equals(Object object){
        if (object == this){
            return true;
        }

        if (!(object instanceof DOMNodePointer)){
            return false;
        }

        DOMNodePointer other = (DOMNodePointer)object;
        return node == other.node;
    }

    public String toString(){
        return node.toString();
    }

    public Object clone(){
        DOMNodePointer pointer = new DOMNodePointer(parent, node);
        return pointer;
    }
}