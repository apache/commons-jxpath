/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/pointers/Attic/PropertyOwnerPointer.java,v 1.2 2001/09/21 23:22:45 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2001/09/21 23:22:45 $
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
 * A pointer describing a node that has properties, each of which could be
 * a collection.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2001/09/21 23:22:45 $
 */
public abstract class PropertyOwnerPointer extends NodePointer {

    public NodeIterator childIterator(NodeTest test, boolean reverse){
        return nodeIterator(test, reverse, true);
    }

    public NodeIterator siblingIterator(NodeTest test, boolean reverse){
        return nodeIterator(test, reverse, false);
    }

    private NodeIterator nodeIterator(NodeTest test, boolean reverse, boolean children){
        if (test == null){
            return new PropertyIterator(this, children, null, reverse);
        }
        else if (test instanceof NodeNameTest){
            QName testName = ((NodeNameTest)test).getNodeName();
            String property;
            if (!isDefaultNamespace(testName.getPrefix())){
                return null;
            }
            else if (testName.getName().equals("*")){
                property = null;
            }
            else {
                property = testName.getName();
            }
            return new PropertyIterator(this, children, property, reverse);
        }
        else if (test instanceof NodeTypeTest){
            if (((NodeTypeTest)test).getNodeType() == Compiler.NODE_TYPE_NODE){
                return new PropertyIterator(this, children, null, reverse);
            }
        }
        return null;
    }

    public boolean testNode(NodeTest test){
        if (test == null){
            return true;
        }
        else if (test instanceof NodeNameTest){
            QName testName = ((NodeNameTest)test).getNodeName();
            QName nodeName = getName();
            String testPrefix = testName.getPrefix();
            String nodePrefix = nodeName.getPrefix();
            if (!equalStrings(testPrefix, nodePrefix)){
                String testNS = getNamespaceURI(testPrefix);
                String nodeNS = getNamespaceURI(nodePrefix);
                if (!equalStrings(testNS, nodeNS)){
                    return false;
                }
            }
            String testLocalName = testName.getName();
            if (testLocalName.equals("*")){
                return true;
            }
            return testLocalName.equals(nodeName.getName());
        }
        else if (test instanceof NodeTypeTest){
            if (((NodeTypeTest)test).getNodeType() == Compiler.NODE_TYPE_NODE){
                return true;
            }
        }
        return false;
    }

    private static boolean equalStrings(String s1, String s2){
        if (s1 == null && s2 != null){
            return false;
        }
        if (s1 != null && !s1.equals(s2)){
            return false;
        }
        return true;
    }

    public static int UNSPECIFIED_PROPERTY = Integer.MIN_VALUE;

    protected PropertyOwnerPointer(NodePointer parent){
        super(parent);
    }

    public boolean isCollection(){
        Object value = getBaseValue();
        return value != null && PropertyAccessHelper.isCollection(value);
    }

    public Object getValue(){
        Object value;
        if (index == WHOLE_COLLECTION){
            value = getBaseValue();
        }
        else {
            value = PropertyAccessHelper.getValue(getBaseValue(), index);
        }
        return value;
    }

    public abstract QName getName();
    public abstract void setValue(Object value);

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
}