/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/pointers/Attic/NodePointer.java,v 1.1 2001/08/23 00:47:00 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/08/23 00:47:00 $
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

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:00 $
 */
public abstract class NodePointer implements Pointer, Cloneable {

    public static NodePointer createNodePointer(QName name, Object bean){
        if (bean == null){
            return new NullPointer(name);
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

    public static int WHOLE_COLLECTION = Integer.MIN_VALUE;
    public static int UNSPECIFIED = Integer.MIN_VALUE;
    protected static Object UNKNOWN = new Object();

    protected NodePointer parent;
    protected int index = WHOLE_COLLECTION;
    protected Object value = UNKNOWN;

    protected NodePointer(NodePointer parent){
        this.parent = parent;
    }

    public NodePointer getParent(){
        return parent;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
        this.value = UNKNOWN;
    }

    public boolean isRoot(){
        return parent == null;
    }

    public boolean isAtomic(){
        Object value = getValue();
        return value == null || JXPathIntrospector.getBeanInfo(value.getClass()).isAtomic();
    }

    public boolean isDynamic(){
        Object value = getValue();
        return value == null || JXPathIntrospector.getBeanInfo(value.getClass()).isDynamic();
    }

    public boolean isCollection(){
        Object value = getPropertyValue();
        return value != null && PropertyAccessHelper.isCollection(value);
    }

    public Object getValue(){
        if (value == UNKNOWN){
            if (index == WHOLE_COLLECTION){
                value = getPropertyValue();
            }
            else {
                value = PropertyAccessHelper.getValue(getPropertyValue(), index);
            }
        }
        return value;
    }

    public abstract QName getName();
    public abstract void setValue(Object value);
    public abstract String asPath();
    public abstract Object clone();

    public PropertyPointer getPropertyPointer(){
        Object value = getValue();
        if (value == null){
            return new NullPropertyPointer(this);
        }

        JXPathBeanInfo bi = JXPathIntrospector.getBeanInfo(value.getClass());
        if (bi.isDynamic()){
            DynamicPropertyHandler handler = PropertyAccessHelper.getDynamicPropertyHandler(bi.getDynamicPropertyHandlerClass());
            return new DynamicPropertyPointer(this, handler);
        }
        else {
            return new BeanPropertyPointer(this, bi);
        }
    }

    public abstract Object getPropertyValue();
    public abstract int getLength();
}