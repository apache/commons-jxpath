/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/AbstractFactory.java,v 1.6 2003/03/11 00:59:11 dmitri Exp $
 * $Revision: 1.6 $
 * $Date: 2003/03/11 00:59:11 $
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
 * The  {@link JXPathContext#createPath JXPathContext.createPath()} method of
 * JXPathContext can create missing objects as it traverses an XPath; it
 * utilizes an AbstractFactory for that purpose. Install a factory on
 * JXPathContext by calling {@link JXPathContext#setFactory JXPathContext.
 * setFactory()}.
 * <p>
 * All  methods of this class return false.  Override any of them to return true
 * to indicate that the factory has successfully created the described object.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2003/03/11 00:59:11 $
 */
public abstract class AbstractFactory {

    /**
     * The  parameters may describe a collection element or an individual
     * object. It is up to the factory to infer which one it is. If it is a
     * collection, the factory should check if the collection exists.  If not,
     * it should create the collection. Then it should create the index'th
     * element of the collection and return true.
     * <p>
     * 
     * @param context can be used to evaluate other XPaths, get to variables
     * etc.
     * @param pointer describes the location of the node to be created
     * @param parent is the object that will server as a parent of the new
     * object
     * @param name is the name of the child of the parent that needs to be
     * created. In the case of DOM may be qualified.
     * @param index is used if the pointer represents a collection element. You
     * may need to expand or even create the collection to accomodate the new
     * element.
     * 
     * @return true if the object was successfully created
     */
    public boolean createObject(JXPathContext context, Pointer pointer, 
                                Object parent, String name, int index) 
    {
        return false;
    }

    /**
     * Declare the specified variable
     * 
     * @param context hosts variable pools. See 
     * {@link JXPathContext#getVariables() JXPathContext.getVariables()}
     * @param name is the name of the variable without the "$" sign
     * 
     * @return true if the variable was successfully defined
     */
    public boolean declareVariable(JXPathContext context, String name) {
        return false;
    }
}