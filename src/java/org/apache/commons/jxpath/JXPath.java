/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/Attic/JXPath.java,v 1.2 2002/04/10 03:40:19 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2002/04/10 03:40:19 $
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
package org.apache.commons.jxpath;

import java.util.List;
import org.w3c.dom.Node;

/**
 * This class defines convenience methods for XPath traversal. Each method
 * creates a temporary {@link JXPathContext JXPathContext} and forwards the
 * request to it. Use JXPathContext APIs instead of JXPath APIs if any of
 * the following requirements exist:
 * <ul>
 * <li>There is a need for the support of variables.  JXPathContext has a method
 * that allows registering of a pool of variables.
 * <li>There is a need to use extension functions other than
 * Java method calls using the default syntax (see {@link PackageFunctions
 * PackageFunctions}
 * <li>There is a need to use an AbstractFactory, which can create new objects.
 * <li>There is a need to use a hierarchy of evaluation contexts.
 * </ul>
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2002/04/10 03:40:19 $
 */
public final class JXPath {

    /**
     * Traverses the xpath and returns the resulting object. Primitive
     * types are wrapped into objects.
     */
    public static Object getValue(Object bean, String xpath){
        return JXPathContext.newContext(bean).getValue(xpath);
    }

    /**
     * Modifies the value of the property described by the supplied xpath.
     * Will throw an exception if one of the following conditions occurs:
     * <ul>
     * <li>The xpath does not in fact describe an existing property
     * <li>The property is not writable (no public, non-static set method)
     * </ul>
     */
    public static void setValue(Object bean, String xpath, Object value){
        JXPathContext.newContext(bean).setValue(xpath, value);
    }

    /**
     * Traverses the xpath and returns a List of objects. Even if
     * there is only one object that matches the xpath, it will be returned
     * as a collection with one element.  If the xpath matches no properties
     * in the graph, the List will be empty.
     */
    public static List eval(Object bean, String xpath){
        return JXPathContext.newContext(bean).eval(xpath);
    }
}