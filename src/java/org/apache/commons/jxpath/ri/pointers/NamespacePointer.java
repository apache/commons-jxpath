/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/pointers/Attic/NamespacePointer.java,v 1.2 2002/04/12 02:28:06 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2002/04/12 02:28:06 $
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

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.*;

import java.util.*;

/**
 * Represents a namespace node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2002/04/12 02:28:06 $
 */
public class NamespacePointer extends NodePointer {
    private String prefix;
    private String namespaceURI;

    public NamespacePointer(NodePointer parent, String prefix){
        super(parent);
        this.prefix = prefix;
    }

    public NamespacePointer(NodePointer parent, String prefix, String namespaceURI){
        super(parent);
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
    }

    public QName getName(){
        return new QName(getNamespaceURI(), prefix);
    }

    public Object getBaseValue(){
        return null;
    }

    public Object getValue(){
        return getNamespaceURI();
    }

    public String getNamespaceURI(){
        if (namespaceURI == null){
            namespaceURI = parent.getNamespaceURI(prefix);
        }
        return namespaceURI;
    }

    public boolean isLeaf(){
        return true;
    }

    /**
     * Throws UnsupportedOperationException.
     */
    public void setValue(Object value){
        throw new UnsupportedOperationException("Cannot modify DOM trees");
    }

    public boolean testNode(NodeTest nodeTest){
        return nodeTest == null ||
                ((nodeTest instanceof NodeTypeTest) &&
                    ((NodeTypeTest)nodeTest).getNodeType() == Compiler.NODE_TYPE_NODE);
    }

    public String asPath(){
        StringBuffer buffer = new StringBuffer();
        if (parent != null){
            buffer.append(parent.asPath());
            buffer.append('/');
        }
        buffer.append("namespace::");
        buffer.append(prefix);
        return buffer.toString();
    }

    public int hashCode(){
        String nsURI = getNamespaceURI();
        if (nsURI == null){
            return 0;
        }
        else {
            return nsURI.hashCode();
        }
    }

    public boolean equals(Object object){
        if (object == this){
            return true;
        }

        if (!(object instanceof NamespacePointer)){
            return false;
        }

        NamespacePointer other = (NamespacePointer)object;
        String nsURI = getNamespaceURI();
        String otherNSURI = other.getNamespaceURI();
        return (nsURI == null && otherNSURI == null) ||
               (nsURI != null && nsURI.endsWith(otherNSURI));
    }
}