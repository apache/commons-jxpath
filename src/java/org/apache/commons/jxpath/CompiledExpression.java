/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/CompiledExpression.java,v 1.5 2003/10/09 21:31:38 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/10/09 21:31:38 $
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * individuals on behalf of the Apache Software Foundation.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.jxpath;

import java.util.Iterator;

/**
 * Represents a compiled XPath. The interpretation of compiled XPaths
 * may be faster, because it bypasses the compilation step. The reference
 * implementation of JXPathContext also globally caches some of the
 * results of compilation, so the direct use of JXPathContext is not
 * always less efficient than the use of CompiledExpression.
 * <p>
 * Use CompiledExpression only when there is a need to evaluate the
 * same expression multiple times and the CompiledExpression can be
 * conveniently cached.
 * <p>
 * To acqure a CompiledExpression, call {@link JXPathContext#compile
 * JXPathContext.compile}
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2003/10/09 21:31:38 $
 */
public interface CompiledExpression {

    /**
     * Evaluates the xpath and returns the resulting object. Primitive
     * types are wrapped into objects.
     */
    Object getValue(JXPathContext context);

    /**
     * Evaluates the xpath, converts the result to the specified class and
     * returns the resulting object.
     */
    Object getValue(JXPathContext context, Class requiredType);

    /**
     * Modifies the value of the property described by the supplied xpath.
     * Will throw an exception if one of the following conditions occurs:
     * <ul>
     * <li>The xpath does not in fact describe an existing property
     * <li>The property is not writable (no public, non-static set method)
     * </ul>
     */
    void setValue(JXPathContext context, Object value);

    /**
     * Creates intermediate elements of
     * the path by invoking an AbstractFactory, which should first be
     * installed on the context by calling "setFactory".
     */
    Pointer createPath(JXPathContext context);

    /**
     * The same as setValue, except it creates intermediate elements of
     * the path by invoking an AbstractFactory, which should first be
     * installed on the context by calling "setFactory".
     * <p>
     * Will throw an exception if one of the following conditions occurs:
     * <ul>
     * <li>Elements of the xpath aleady exist, by the path does not in
     *  fact describe an existing property
     * <li>The AbstractFactory fails to create an instance for an intermediate
     * element.
     * <li>The property is not writable (no public, non-static set method)
     * </ul>
     */
    Pointer createPathAndSetValue(JXPathContext context, Object value);

    /**
     * Traverses the xpath and returns a Iterator of all results found
     * for the path. If the xpath matches no properties
     * in the graph, the Iterator will not be null.
     */
    Iterator iterate(JXPathContext context);

    /**
     * Traverses the xpath and returns a Pointer.
     * A Pointer provides easy access to a property.
     * If the xpath matches no properties
     * in the graph, the pointer will be null.
     */
    Pointer getPointer(JXPathContext context, String xpath);

    /**
     * Traverses the xpath and returns an Iterator of Pointers.
     * A Pointer provides easy access to a property.
     * If the xpath matches no properties
     * in the graph, the Iterator be empty, but not null.
     */
    Iterator iteratePointers(JXPathContext context);

    /**
     * Remove the graph element described by this expression
     */
    void removePath(JXPathContext context);

    /**
     * Remove all graph elements described by this expression
     */
    void removeAll(JXPathContext context);
}
