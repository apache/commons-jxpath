/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/JXPathCompiledExpression.java,v 1.2 2002/05/08 23:05:05 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2002/05/08 23:05:05 $
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
 * individuals on behalf of the Apache Software Foundation.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.jxpath.ri;

import java.util.Iterator;

import org.apache.commons.jxpath.ri.compiler.Expression;
import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

/**
 *
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2002/05/08 23:05:05 $
 */
public class JXPathCompiledExpression implements CompiledExpression {

    private String xpath;
    private Expression expression;

    public JXPathCompiledExpression(String xpath, Expression expression){
        this.xpath = xpath;
        this.expression = expression;
    }

    /**
     * @see CompiledExpression#getValue(JXPathContext)
     */
    public Object getValue(JXPathContext context) {
        return ((JXPathContextReferenceImpl)context).
                    getValue(xpath, expression);
    }

    /**
     * @see CompiledExpression#getValue(JXPathContext, Class)
     */
    public Object getValue(JXPathContext context, Class requiredType) {
        return ((JXPathContextReferenceImpl)context).
                    getValue(xpath, expression, requiredType);
    }

    /**
     * @see CompiledExpression#setValue(JXPathContext, Object)
     */
    public void setValue(JXPathContext context, Object value) {
        ((JXPathContextReferenceImpl)context).
                    setValue(xpath, expression, value);
    }

    /**
     * @see CompiledExpression#createPath(JXPathContext, Object)
     */
    public Pointer createPathAndSetValue(JXPathContext context, Object value) {
        return ((JXPathContextReferenceImpl)context).
                    createPathAndSetValue(xpath, expression, value);
    }

    /**
     * @deprecated use createPathAndSetValue
     */
    public void createPath(JXPathContext context, Object value) {
        createPathAndSetValue(context, value);
    }

    /**
     * @see CompiledExpression#iterate(JXPathContext)
     */
    public Iterator iterate(JXPathContext context) {
        return ((JXPathContextReferenceImpl)context).
                    iterate(xpath, expression);
    }

    /**
     * @see CompiledExpression#getPointer(JXPathContext, String)
     */
    public Pointer getPointer(JXPathContext context, String xpath) {
        return ((JXPathContextReferenceImpl)context).
                    getPointer(xpath, expression);
    }

    /**
     * @see CompiledExpression#iteratePointers(JXPathContext)
     */
    public Iterator iteratePointers(JXPathContext context) {
        return ((JXPathContextReferenceImpl)context).
                    iteratePointers(xpath, expression);
    }

    /**
     * @see CompiledExpression#remove(JXPathContext)
     */
    public void removePath(JXPathContext context){
        ((JXPathContextReferenceImpl)context).removePath(xpath, expression);
    }

    /**
     * @see CompiledExpression#removeAll(JXPathContext)
     */
    public void removeAll(JXPathContext context){
        ((JXPathContextReferenceImpl)context).removeAll(xpath, expression);
    }
}
