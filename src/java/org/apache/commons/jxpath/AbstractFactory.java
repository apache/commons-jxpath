/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/AbstractFactory.java,v 1.1 2002/04/10 03:40:19 dmitri Exp $
 * $Revision: 1.1 $
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

import java.util.*;

/**
 * The {@link JXPathContext#createPath JXPathContext.createPath()} method of JXPathContext can create
 * missing objects as it traverses an XPath; it utilizes an AbstractFactory for that purpose.
 * Install a factory on JXPathContext by calling {@link JXPathContext#setFactory
 * JXPathContext.setFactory()}.
 * <p>
 * All methods of this class return false.  Override any of them to
 * return true to indicate that the factory has successfully created the described object.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2002/04/10 03:40:19 $
 */
public abstract class AbstractFactory {

    /**
     * The parameters may describe a collection element or an individual object. It is up
     * to the factory to infer which one it is. If it is a collection, the
     * factory should check if the collection exists.  If not, it should create
     * the collection. Then it should create the index'th element of the collection
     * and return it.
     * <p>
     * If the parameters describe an individual object, the factory should only
     * create an object if index == 0.
     * <p>
     * Return the created object or <b>null</b> if this factory cannot create
     * the requested object.
     */
    public boolean createObject(JXPathContext context, Pointer pointer, Object parent, String name, int index){
        return false;
    }

    /**
     * The factory should expand the collection to the specified size and return true. If
     * it cannot expand the collection, it should return false.
     */
    public boolean expandCollection(JXPathContext context, Pointer pointer, Object parent, String name, int size){
        return false;
    }

    /**
     * Create a new object and set it on the specified variable
     */
    public boolean declareVariable(JXPathContext context, String name){
        return false;
    }
}