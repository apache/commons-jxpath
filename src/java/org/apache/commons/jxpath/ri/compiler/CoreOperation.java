/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/CoreOperation.java,v 1.4 2002/04/24 04:05:38 dmitri Exp $
 * $Revision: 1.4 $
 * $Date: 2002/04/24 04:05:38 $
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
package org.apache.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;

/**
 * A compile tree element representing one of the core operations like "+",
 * "-", "*" etc.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.4 $ $Date: 2002/04/24 04:05:38 $
 */
public class CoreOperation extends Operation {

    private Object dynamicPropertyAccessHint;
    public static final String DYNAMIC_PROPERTY_ACCESS_HINT = "dynamicPropertyAccessHint";
    private static final Object NO_HINT = new Object();
    private static QName QNAME_NAME = new QName(null, "name");

    public CoreOperation(int code, Expression args[]){
        super(code, args);
    }

    public CoreOperation(int code, Expression arg){
        super(code, new Expression[]{arg});
    }

    public CoreOperation(int code, Expression arg1, Expression arg2){
        super(code, new Expression[]{arg1, arg2});
    }

    public Expression getArg1(){
        return args[0];
    }

    public Expression getArg2(){
        return args[1];
    }

    /**
     * Recognized predicated formatted as <code>[@name = <i>expr</i>]</code>
     */
    public Object getEvaluationHint(String hint){
        if (getExpressionTypeCode() != OP_EQ ||
                !hint.equals(DYNAMIC_PROPERTY_ACCESS_HINT)){
            return null;
        }

        if (dynamicPropertyAccessHint == null){
            dynamicPropertyAccessHint = NO_HINT;

            Expression arg1 = getArg1();
            if (arg1.getExpressionTypeCode() == Expression.OP_LOCATION_PATH){
                Step[] steps = ((LocationPath)arg1).getSteps();
                if (steps.length == 1 &&
                        steps[0].getAxis() == Compiler.AXIS_ATTRIBUTE &&
                        steps[0].getNodeTest() instanceof NodeNameTest &&
                        ((NodeNameTest)steps[0].getNodeTest()).getNodeName().equals(QNAME_NAME)){
                    dynamicPropertyAccessHint = getArg2();
                }
            }
        }
        if (dynamicPropertyAccessHint == NO_HINT){
            return null;
        }
        return dynamicPropertyAccessHint;
    }
}