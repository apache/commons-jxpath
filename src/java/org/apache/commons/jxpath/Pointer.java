/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/Pointer.java,v 1.6 2003/02/19 00:59:52 dmitri Exp $
 * $Revision: 1.6 $
 * $Date: 2003/02/19 00:59:52 $
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

import java.io.Serializable;

/**
 * Pointers represent locations of objects and their properties
 * in Java object graphs. JXPathContext has methods
 * ({@link JXPathContext#getPointer(java.lang.String) getPointer()}
 * and  ({@link JXPathContext#iteratePointers(java.lang.String)
 * iteratePointers()}, which, given an XPath, produce Pointers for the objects
 * or properties described the the path. For example, <code>ctx.getPointer
 * ("foo/bar")</code> will produce a Pointer that can get and set the property
 * "bar" of the object which is the value of the property "foo" of the root
 * object. The value of <code>ctx.getPointer("aMap/aKey[3]")</code> will be a
 * pointer to the 3'rd element of the array, which is the value for the key
 * "aKey" of the map, which is the value of the property "aMap" of the root
 * object.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2003/02/19 00:59:52 $
 */
public interface Pointer extends Cloneable, Comparable, Serializable {

    /**
     * Returns the value of the object, property or collection element
     * this pointer represents. May convert the value to one of the 
     * canonical InfoSet types: String, Number, Boolean, Set.
     * 
     * For example, in the case of an XML element, getValue() will
     * return the text contained by the element rather than 
     * the element itself.
     */
    Object getValue();

    /**
     * Returns the raw value of the object, property or collection element
     * this pointer represents.  Never converts the object to a
     * canonical type: returns it as is. 
     * 
     * For example, for an XML element, getNode() will
     * return the element itself rather than the text it contains.
     */
    Object getNode();

    /**
     * Modifies the value of the object, property or collection element
     * this pointer represents.
     */
    void setValue(Object value);

    /**
     * Returns the node this pointer is based on. 
     */
    Object getRootNode();
    
    /**
     * Returns a string that is a proper "canonical" XPath that corresponds to
     * this pointer.  Consider this example:
     * <p><code>Pointer  ptr = ctx.getPointer("//employees[firstName = 'John']")
     * </code>
     * <p>The  value of <code>ptr.asPath()</code> will look something like
     * <code>"/departments[2]/employees[3]"</code>, so, basically, it represents
     * the concrete location(s) of the result of a search performed by JXPath.
     * If an object in the pointer's path is a Dynamic Property object (like a
     * Map), the asPath method generates an XPath that looks like this: <code>"
     * /departments[@name = 'HR']/employees[3]"</code>.
     */
    String asPath();
    
    /**
     * Pointers are cloneable
     */
    Object clone();
}