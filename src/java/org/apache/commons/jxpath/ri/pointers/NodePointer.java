/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/pointers/Attic/NodePointer.java,v 1.2 2001/09/03 01:22:31 dmitri Exp $
 * $Revision: 1.2 $
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
import org.w3c.dom.Node;

/**
 * Common superclass for Poitners of all kinds.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2001/09/03 01:22:31 $
 */
public abstract class NodePointer implements Pointer, Cloneable {

    public static int WHOLE_COLLECTION = Integer.MIN_VALUE;
    protected int index = WHOLE_COLLECTION;

    public static NodePointer createNodePointer(QName name, Object bean){
        if (bean == null){
            return new NullPointer(name);
        }
        if (bean instanceof Node){
            return new DOMNodePointer((Node)bean);
        }
        if (bean instanceof Container){
            return new ContainerPointer((Container)bean);
        }

        JXPathBeanInfo bi = JXPathIntrospector.getBeanInfo(bean.getClass());
        if (bi.isDynamic()){
            DynamicPropertyHandler handler = PropertyAccessHelper.getDynamicPropertyHandler(bi.getDynamicPropertyHandlerClass());
            return new DynamicPointer(name, bean, handler);
        }
        else {
            return new BeanPointer(name, bean, bi);
        }
    }

    public static NodePointer createNodePointer(NodePointer parent, QName name, Object bean){
        if (bean == null){
            return new NullPointer(parent, name);
        }
        if (bean instanceof Node){
            return new DOMNodePointer(parent, (Node)bean);
        }
        if (bean instanceof Container){
            return new ContainerPointer(parent, (Container)bean);
        }

        JXPathBeanInfo bi = JXPathIntrospector.getBeanInfo(bean.getClass());
        if (bi.isDynamic()){
            DynamicPropertyHandler handler = PropertyAccessHelper.getDynamicPropertyHandler(bi.getDynamicPropertyHandlerClass());
            return new DynamicPointer(parent, name, bean, handler);
        }
        else {
            return new BeanPointer(parent, name, bean, bi);
        }
    }

    /**
     * Returns a NodeIterator that iterates over all children or all children
     * with the given name.
     */
    public abstract NodeIterator childIterator(QName name, boolean reverse);

    /**
     * Returns a NodeIterator that iterates over all siblings or all siblings
     * with the given name starting with this pointer and excluding the value
     * currently pointed at.
     */
    public abstract NodeIterator siblingIterator(QName name, boolean reverse);

    /**
     * Returns a NodeIterator that iterates over all attributes of the value
     * currently pointed at.
     * May return null if the object does not support the attributes.
     */
    public NodeIterator attributeIterator(){
        return null;
    }

    /**
     * Returns a NodePointer for the specified attribute. May return null
     * if attributes are not supported or if there is no such attribute.
     */
    public NodePointer attributePointer(QName attribute){
        return null;
    }

    protected NodePointer parent;

    protected NodePointer(NodePointer parent){
        this.parent = parent;
    }

    public NodePointer getParent(){
        return parent;
    }

    public boolean isRoot(){
        return parent == null;
    }

    /**
     * If true, this node does not have children
     */
    public boolean isLeaf(){
        Object value = getValue();
        return value == null || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public boolean isCollection(){
        Object value = getBaseValue();
        return value != null && PropertyAccessHelper.isCollection(value);
    }

    public int getLength(){
        Object value = getBaseValue();
        if (value == null){
            return 1;
        }
        return PropertyAccessHelper.getLength(value);
    }

    public abstract QName getName();
    public abstract Object getBaseValue();
    public abstract void setValue(Object value);
    public abstract Object clone();

    public String asPath(){
        StringBuffer buffer = new StringBuffer();
        if (getParent() != null){
            buffer.append(getParent().asPath());
        }
        QName name = getName();
        if (name != null){
            if (getParent() != null){
                buffer.append('/');
            }
            buffer.append(name.asString());
        }
        if (index != WHOLE_COLLECTION && isCollection()){
            buffer.append('[').append(index + 1).append(']');
        }
        return buffer.toString();
    }
}