/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/dom/DOMAttributePointer.java,v 1.7 2002/10/20 03:47:18 dmitri Exp $
 * $Revision: 1.7 $
 * $Date: 2002/10/20 03:47:18 $
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
package org.apache.commons.jxpath.ri.model.dom;

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.TypeUtils;
import org.w3c.dom.Attr;

/**
 * A Pointer that points to a DOM node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.7 $ $Date: 2002/10/20 03:47:18 $
 */
public class DOMAttributePointer extends NodePointer {
    private Attr attr;

    public DOMAttributePointer(NodePointer parent, Attr attr){
        super(parent);
        this.attr = attr;
    }

    public QName getName(){
        return new QName(DOMNodePointer.getPrefix(attr), DOMNodePointer.getLocalName(attr));
    }

    public QName getExpandedName(){
        return new QName(getNamespaceURI(),  DOMNodePointer.getLocalName(attr));
    }

    public String getNamespaceURI(){
        String prefix = DOMNodePointer.getPrefix(attr);
        if (prefix == null){
            return null;
        }
        return parent.getNamespaceURI(prefix);
    }

    public Object getBaseValue(){
        return attr;
    }
    
    public boolean isCollection(){
        return false;
    }
    
    public int getLength(){
        return 1;
    }    

    public Object getNode(){
        String value = attr.getValue();
        if (value == null){
            return null;
        }
        if (value.equals("") && !attr.getSpecified()){
            return null;
        }
        return value;
    }

    public boolean isActual(){
        return true;
    }

    public boolean isLeaf(){
        return true;
    }

    public boolean testNode(NodeTest nodeTest){
        return nodeTest == null ||
                ((nodeTest instanceof NodeTypeTest) &&
                    ((NodeTypeTest)nodeTest).getNodeType() == Compiler.NODE_TYPE_NODE);
    }

    /**
     * Sets the value of this attribute.
     */
    public void setValue(Object value){
        attr.setValue((String)TypeUtils.convert(value, String.class));
    }

    public void remove(){
        attr.getOwnerElement().removeAttributeNode(attr);
    }

    /**
     */
    public String asPath(){
        StringBuffer buffer = new StringBuffer();
        if (parent != null){
            buffer.append(parent.asPath());
            if (buffer.length() == 0 ||
                    buffer.charAt(buffer.length()-1) != '/'){
            	buffer.append('/');
            }
        }
        buffer.append('@');
        buffer.append(getName());
        return buffer.toString();
    }

    public int hashCode(){
        return System.identityHashCode(attr);
    }

    public boolean equals(Object object){
        if (object == this){
            return true;
        }

        if (!(object instanceof DOMAttributePointer)){
            return false;
        }

        DOMAttributePointer other = (DOMAttributePointer)object;
        return attr == other.attr;
    }

    public int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2){
        // Won't happen - attributes don't have children
        return 0;
    }
}