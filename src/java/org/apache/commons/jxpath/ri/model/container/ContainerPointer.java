/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/container/ContainerPointer.java,v 1.3 2002/08/10 16:13:04 dmitri Exp $
 * $Revision: 1.3 $
 * $Date: 2002/08/10 16:13:04 $
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
package org.apache.commons.jxpath.ri.model.container;

import java.util.Locale;

import org.apache.commons.jxpath.Container;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Transparent pointer to a Container. The getValue() method
 * returns the contents of the container, rather than the container
 * itself.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.3 $ $Date: 2002/08/10 16:13:04 $
 */
public class ContainerPointer extends NodePointer {
    private Container container;
    private NodePointer valuePointer;

    public ContainerPointer(Container container, Locale locale){
        super(null, locale);
        this.container = container;
    }

    public ContainerPointer(NodePointer parent, Container container){
        super(parent);
        this.container = container;
    }

    /**
     * This type of node is auxiliary.
     */
    public boolean isNode(){
        return false;
    }

    public QName getName(){
        return null;
    }

    public Object getBaseValue(){
        return container.getValue();
    }

    public Object getNode(){
        Object value = getBaseValue();
        if (index != WHOLE_COLLECTION){
            if (index >= 0 && index < getLength()){
                return ValueUtils.getValue(value, index);
            }
            else {
                return null;
            }
        }
        return value;
    }

    public void setValue(Object value){
        container.setValue(value);
    }

    public NodePointer getValuePointer(){
        if (valuePointer == null){
            Object value = getNode();
            valuePointer = NodePointer.newChildNodePointer(this, getName(), value).getValuePointer();
        }
        return valuePointer;
    }

    public int hashCode(){
        return System.identityHashCode(container) + index;
    }

    public boolean equals(Object object){
        if (object == this){
            return true;
        }

        if (!(object instanceof ContainerPointer)){
            return false;
        }

        ContainerPointer other = (ContainerPointer)object;
        return container == other.container &&
                index == other.index;
    }

    public NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith){
        return getValuePointer().childIterator(test, reverse, startWith);
    }

    public NodeIterator attributeIterator(QName name){
        return getValuePointer().attributeIterator(name);
    }

    public NodeIterator namespaceIterator(){
        return getValuePointer().namespaceIterator();
    }

    public NodePointer namespacePointer(String namespace){
        return getValuePointer().namespacePointer(namespace);
    }

    public boolean testNode(NodeTest nodeTest){
        return getValuePointer().testNode(nodeTest);
    }
 
    public int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2){
        return pointer1.getIndex() - pointer2.getIndex();
    }
 }