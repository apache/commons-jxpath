/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ExpressionContext.java,v 1.7 2003/10/09 21:31:38 rdonkin Exp $
 * $Revision: 1.7 $
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Plotnix, Inc,
 * <http://www.plotnix.com/>.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.jxpath;

import java.util.List;

/**
 * If an extenstion function has an argument of type ExpressionContext,
 * it can gain access to the current node of an XPath expression context.
 * <p>
 * Example:
 * <blockquote><pre>
 * public class MyExtenstionFunctions {
 *    public static String objectType(ExpressionContext context){
 *       Object value = context.getContextNodePointer().getValue();
 *       if (value == null){
 *           return "null";
 *       }
 *       return value.getClass().getName();
 *    }
 * }
 * </pre></blockquote>
 *
 * You can then register this extension function using a {@link ClassFunctions
 * ClassFunctions} object and call it like this:
 * <blockquote><pre>
 *   "/descendent-or-self::node()[ns:objectType() = 'java.util.Date']"
 * </pre></blockquote>
 * This expression will find all nodes of the graph that are dates.
 */
public interface ExpressionContext {
    
    /**
     * Get the JXPathContext in which this function is being evaluated.
     *
     * @return A list representing the current context nodes.
     */
    JXPathContext getJXPathContext();

    /**
     * Get the current context node.
     *
     * @return The current context node pointer.
     */
    Pointer getContextNodePointer();

    /**
     * Get the current context node list.  Each element of the list is
     * a Pointer.
     *
     * @return A list representing the current context nodes.
     */
    List getContextNodeList();

    /**
     * Returns the current context position.
     */
    int getPosition();
}
