/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/pointers/Attic/NodePointer.java,v 1.5 2002/04/10 03:40:20 dmitri Exp $
 * $Revision: 1.5 $
 * $Date: 2002/04/10 03:40:20 $
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
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.*;

import java.lang.reflect.*;
import java.util.*;
import java.beans.*;
import org.w3c.dom.Node;

/**
 * Common superclass for Pointers of all kinds.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2002/04/10 03:40:20 $
 */
public abstract class NodePointer implements Pointer, Cloneable {

    public static int WHOLE_COLLECTION = Integer.MIN_VALUE;
    protected int index = WHOLE_COLLECTION;
    public static String UNKNOWN_NAMESPACE = "<<unknown namespace>>";

    public static NodePointer createNodePointer(QName name, Object bean, Locale locale){
        if (bean == null){
            return new NullPointer(name, locale);
        }
        NodePointerFactory[] factories = JXPathContextReferenceImpl.getNodePointerFactories();
        for (int i = 0; i < factories.length; i++){
            NodePointer pointer = factories[i].createNodePointer(name, bean, locale);
            if (pointer != null){
                return pointer;
            }
        }
        throw new RuntimeException("Could not allocate a NodePointer for object of " + bean.getClass());
    }

    public static NodePointer createNodePointer(NodePointer parent, QName name, Object bean){
        if (bean == null){
            return new NullPointer(parent, name);
        }
        NodePointerFactory[] factories = JXPathContextReferenceImpl.getNodePointerFactories();
        for (int i = 0; i < factories.length; i++){
            NodePointer pointer = factories[i].createNodePointer(parent, name, bean);
            if (pointer != null){
                return pointer;
            }
        }
        throw new RuntimeException("Could not allocate a NodePointer for object of " + bean.getClass());
    }

    /**
     * Returns a NodeIterator that iterates over all children or all children
     * with the given name.
     */
    public NodeIterator childIterator(NodeTest test, boolean reverse){
        return null;
    }

    /**
     * Returns a NodeIterator that iterates over all siblings or all siblings
     * with the given name starting with this pointer and excluding the value
     * currently pointed at.
     */
    public NodeIterator siblingIterator(NodeTest test, boolean reverse){
        return null;
    }

    /**
     * Returns a NodeIterator that iterates over all attributes of the current node
     * matching the supplied node name (could have a wildcard).
     * May return null if the object does not support the attributes.
     */
    public NodeIterator attributeIterator(QName qname){
        return null;
    }

    /**
     * Returns a NodeIterator that iterates over all namespaces of the value
     * currently pointed at.
     * May return null if the object does not support the namespaces.
     */
    public NodeIterator namespaceIterator(){
        return null;
    }

    /**
     * Returns a NodePointer for the specified namespace. Will return null
     * if namespaces are not supported. Will return UNKNOWN_NAMESPACE if there is no such namespace.
     */
    public NodePointer namespacePointer(String namespace){
        return null;
    }

    public String getNamespaceURI(String prefix){
        return null;
    }

    public String getNamespaceURI(){
        return null;
    }

    /**
     * Returns true if the supplied prefix represents the
     * default namespace in the context of the current node.
     */
    protected boolean isDefaultNamespace(String prefix){
        if (prefix == null){
            return true;
        }

        String namespace = getNamespaceURI(prefix);
        if (namespace == null){
            return false;       // undefined namespace
        }

        return namespace.equals(getDefaultNamespaceURI());
    }

    protected String getDefaultNamespaceURI(){
        return null;
    }

    public QName getExpandedName(){
        return getName();
    }

    protected NodePointer parent;
    protected Locale locale;

    protected NodePointer(NodePointer parent){
        this.parent = parent;
    }

    protected NodePointer(NodePointer parent, Locale locale){
        this.parent = parent;
        this.locale = locale;
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
//        System.err.println("SETTING: " + this.getClass() + " " + index);
//        new Exception().printStackTrace();
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

    /**
     * If this pointer manages a transparent container, like a variable,
     * this method returns the ponter to the contents.
     */
    public NodePointer getValuePointer(){
        if (this instanceof PropertyOwnerPointer){
            return this;
        }
        return null;
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
    public boolean isActual(){
        if (index == WHOLE_COLLECTION){
            return true;
        }
        else {
            return index >= 0 && index < getLength();
        }
    }


    public abstract QName getName();
    public abstract Object getBaseValue();
    public abstract void setValue(Object value);
    public abstract boolean testNode(NodeTest nodeTest);

    /**
     *  Called directly by JXPathContext. Must create path and
     *  set value.
     */
    public void createPath(JXPathContext context, Object value){
        setValue(value);
    }

    /**
     * Called by a child pointer if that child needs to assign the value
     * supplied in the createPath(context, value) call to a non-existent
     * collection element. This method must expand the collection and
     * assign the element.
     */
    public void createPath(JXPathContext context, int index, Object value){
        throw new RuntimeException("Cannot expand collection for path " + asPath() +
                ", or it is not a collection at all");
    }

    /**
     * Called by a child pointer when it needs to create a parent object.
     * Must create an object described by this pointer and return
     * a new pointer that properly describes the new object.
     */
    public NodePointer createPath(JXPathContext context){
        throw new RuntimeException("Cannot create an object for path " + asPath() +
                ", operation is not allowed for this type of node");
    }

    /**
     * Called by a child pointer when it needs to create a parent object
     * for a non-existent collection element.  Must expand the collection,
     * create an element object and return a new pointer describing the
     * newly created element.
     */
    public NodePointer createPath(JXPathContext context, int index){
        throw new RuntimeException("Cannot create an object for path " + asPath() +
                ", operation is not allowed for this type of node");
    }

    public Locale getLocale(){
        if (locale == null){
            if (parent != null){
                locale = parent.getLocale();
            }
        }
        return locale;
    }

    /**
     * Returns true if the selected locale name starts
     * with the specified prefix <i>lang</i>, case-insensitive.
     */
    public boolean isLanguage(String lang){
        Locale loc = getLocale();
        String name = loc.toString().replace('_', '-');
        return name.toUpperCase().startsWith(lang.toUpperCase());
    }

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
            buffer.append(name);
        }
        if (index != WHOLE_COLLECTION && isCollection()){
            buffer.append('[').append(index + 1).append(']');
        }
        return buffer.toString();
    }

    public Object clone(){
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex){
            // Of course it is supported
            ex.printStackTrace();
        }
        return null;
    }

    public String toString(){
        return asPath();
    }

    protected AbstractFactory getAbstractFactory(JXPathContext context){
        AbstractFactory factory = context.getFactory();
        if (factory == null){
            throw new RuntimeException("Factory is not set on the JXPathContext - cannot create path: " + asPath());
        }
        return factory;
    }
}