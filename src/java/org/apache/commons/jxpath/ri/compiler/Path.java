/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/Path.java,v 1.2 2002/04/10 03:40:20 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2002/04/10 03:40:20 $
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

import java.util.*;
import org.apache.commons.jxpath.ri.Compiler;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2002/04/10 03:40:20 $
 */
public class Path extends Expression {

    private Step[] steps;
    public static final String BASIC_PATH_HINT = "basicPathHint";
    private boolean basicKnown = false;
    private boolean basic;

    public Path(int typeCode, Step[] steps){
        super(typeCode);
        this.steps = steps;
    }

    public Step[] getSteps(){
        return steps;
    }

    public boolean computeContextDependent(){
        if (steps != null){
            for (int i = 0; i < steps.length; i++){
                if (steps[i].isContextDependent()){
                    return true;
                }
            }
        }

        return false;
    }

    public void setEvaluationMode(int mode){
        super.setEvaluationMode(mode);
        if (steps != null){
            for (int i = 0; i < steps.length; i++){
                if (steps[i].isContextDependent()){
                    steps[i].setEvaluationMode(Expression.EVALUATION_MODE_ALWAYS);
                }
                else {
                    switch(mode){
                        case EVALUATION_MODE_ALWAYS:
                        case EVALUATION_MODE_ONCE_AND_SAVE:
                                steps[i].setEvaluationMode(Expression.EVALUATION_MODE_ONCE_AND_SAVE);
                            break;
                        case EVALUATION_MODE_ONCE:
                            steps[i].setEvaluationMode(Expression.EVALUATION_MODE_ONCE);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Recognized paths formatted as <code>foo/bar[3]/baz[@name = 'biz']</code>.  The
     * evaluation of such "simple" paths is optimized and streamlined.
     */
    public Object getEvaluationHint(String hint){
        if (!hint.equals(BASIC_PATH_HINT)){
            return null;
        }

        if (!basicKnown){
            basicKnown = true;
            basic = true;
            Step[] steps = getSteps();
            for (int i = 0; i < steps.length; i++){
//                System.err.println("STEP: " + steps[i]);
                if (steps[i].getAxis() != Compiler.AXIS_CHILD ||
                        !(steps[i].getNodeTest() instanceof NodeNameTest) ||
                        ((NodeNameTest)steps[i].getNodeTest()).getNodeName().getName().equals("*")){
                    basic = false;
                    break;
                }
                Expression predicates[] = steps[i].getPredicates();
                basic = basic && areBasicPredicates(predicates);
            }
        }
        return basic ? Boolean.TRUE : Boolean.FALSE;
    }

    protected boolean areBasicPredicates(Expression predicates[]){
        if (predicates != null && predicates.length != 0){
            boolean firstIndex = true;
            for (int i = 0; i < predicates.length; i++){
                Expression dyn = (Expression)predicates[i].getEvaluationHint(CoreOperation.DYNAMIC_PROPERTY_ACCESS_HINT);
                if (dyn != null){
                    if (dyn.isContextDependent()){
                        return false;
                    }
                }
                else if (predicates[i].isContextDependent()){
                    return false;
                }
                else {
                    if (!firstIndex){
                        return false;
                    }
                    firstIndex = false;
                }
            }
        }
        return true;
    }
}