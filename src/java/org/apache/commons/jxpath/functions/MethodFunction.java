/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/functions/MethodFunction.java,v 1.5 2002/04/26 03:28:37 dmitri Exp $
 * $Revision: 1.5 $
 * $Date: 2002/04/26 03:28:37 $
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
package org.apache.commons.jxpath.functions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.util.TypeUtils;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2002/04/26 03:28:37 $
 */
public class MethodFunction implements Function {

    private Method method;
    private static final Object EMPTY_ARRAY[] = new Object[0];

    public MethodFunction(Method method){
        this.method = method;
    }

    public Object invoke(ExpressionContext context, Object[] parameters){
        try {
            Object target;
            Object[] args;
            if (Modifier.isStatic(method.getModifiers())){
                target = null;
                if (parameters == null){
                    parameters = EMPTY_ARRAY;
                }
                int pi = 0;
                Class types[] = method.getParameterTypes();
                if (types.length >= 1 && ExpressionContext.class.isAssignableFrom(types[0])){
                    pi = 1;
                }
                args = new Object[parameters.length + pi];
                if (pi == 1){
                    args[0] = context;
                }
                for (int i = 0; i < parameters.length; i++){
                    args[i + pi] = TypeUtils.convert(parameters[i], types[i + pi]);
                }
            }
            else {
                int pi = 0;
                Class types[] = method.getParameterTypes();
                if (types.length >= 1 && ExpressionContext.class.isAssignableFrom(types[0])){
                    pi = 1;
                }
                target = TypeUtils.convert(parameters[0], method.getDeclaringClass());
                args = new Object[parameters.length - 1 + pi];
                if (pi == 1){
                    args[0] = context;
                }
                for (int i = 1; i < parameters.length; i++){
                    args[pi + i - 1] = TypeUtils.convert(parameters[i], types[i - 1]);
                }
            }

            return method.invoke(target, args);
        }
        catch (Exception exception){
            throw new JXPathException("Cannot invoke " + method, exception);
        }
    }
}