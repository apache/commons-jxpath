/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/DynamicPropertyHandler.java,v 1.3 2003/03/11 00:59:12 dmitri Exp $
 * $Revision: 1.3 $
 * $Date: 2003/03/11 00:59:12 $
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

/**
 * A generic mechanism for accessing collections of name/value pairs.
 * Examples of such collections are HashMap, Properties,
 * ServletContext.  In order to add support for a new such collection
 * type to JXPath, perform the following two steps:
 * <ol>
 * <li>Build an implementation of the DynamicPropertyHandler interface
 * for the desired collection type.</li>
 * <li>Invoke the static method {@link JXPathIntrospector#registerDynamicClass
 * JXPathIntrospector.registerDynamicClass(class, handlerClass)}</li>
 * </ol>
 * JXPath allows access to dynamic properties using these three formats:
 * <ul>
 * <li><code>"myMap/myKey"</code></li>
 * <li><code>"myMap[@name = 'myKey']"</code></li>
 * <li><code>"myMap[name(.) = 'myKey']"</code></li>
 * </ul>
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.3 $ $Date: 2003/03/11 00:59:12 $
 */
public interface DynamicPropertyHandler {

    /**
     * Returns a list of dynamic property names for the supplied object.
     */
    String[] getPropertyNames(Object object);

    /**
     * Returns the value of the specified dynamic property.
     */
    Object getProperty(Object object, String propertyName);

    /**
     * Modifies the value of the specified dynamic property.
     */
    void setProperty(Object object, String propertyName, Object value);
}