/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/axes/RootContext.java,v 1.13 2003/10/09 21:31:39 rdonkin Exp $
 * $Revision: 1.13 $
 * $Date: 2003/10/09 21:31:39 $
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
package org.apache.commons.jxpath.ri.axes;

import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * EvalContext that is used to hold the root node for the path traversal.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.13 $ $Date: 2003/10/09 21:31:39 $
 */
public class RootContext extends EvalContext {
    private JXPathContextReferenceImpl jxpathContext;
    private NodePointer pointer;
    private Object registers[];
    private int availableRegister = 0;
    public static final Object UNKNOWN_VALUE = new Object();
    private static final int MAX_REGISTER = 4;

    public RootContext(
        JXPathContextReferenceImpl jxpathContext,
        NodePointer pointer) 
    {
        super(null);
        this.jxpathContext = jxpathContext;
        this.pointer = pointer;
    }

    public JXPathContext getJXPathContext() {
        return jxpathContext;
    }

    public RootContext getRootContext() {
        return this;
    }
    
    public EvalContext getAbsoluteRootContext() {
        return jxpathContext.getAbsoluteRootContext();
    }

    public NodePointer getCurrentNodePointer() {
        return pointer;
    }

    public int getCurrentPosition() {
        throw new UnsupportedOperationException();
    }

    public boolean nextNode() {
        throw new UnsupportedOperationException();
    }

    public boolean nextSet() {
        throw new UnsupportedOperationException();
    }

    public boolean setPosition(int position) {
        throw new UnsupportedOperationException();
    }

    public EvalContext getConstantContext(Object constant) {
        if (constant instanceof NodeSet) {
            return new NodeSetContext(
                new RootContext(jxpathContext, null),
                (NodeSet) constant);
        }
                
        NodePointer pointer;
        if (constant instanceof NodePointer) {
            pointer = (NodePointer) constant;
        }
        else {
            pointer =
                NodePointer.newNodePointer(new QName(null, ""), constant, null);
        }
        return new InitialContext(new RootContext(jxpathContext, pointer));
    }

    public EvalContext getVariableContext(QName variableName) {
        return new InitialContext(
            new RootContext(
                jxpathContext,
                jxpathContext.getVariablePointer(variableName)));
    }

    public Function getFunction(QName functionName, Object[] parameters) {
        return jxpathContext.getFunction(functionName, parameters);
    }

    public Object getRegisteredValue(int id) {
        if (registers == null || id >= MAX_REGISTER || id == -1) {
            return UNKNOWN_VALUE;
        }
        return registers[id];
    }

    public int setRegisteredValue(Object value) {
        if (registers == null) {
            registers = new Object[MAX_REGISTER];
            for (int i = 0; i < MAX_REGISTER; i++) {
                registers[i] = UNKNOWN_VALUE;
            }
        }
        if (availableRegister >= MAX_REGISTER) {
            return -1;
        }
        registers[availableRegister] = value;
        availableRegister++;
        return availableRegister - 1;
    }

    public String toString() {
        return super.toString() + ":" + pointer.asPath();
    }
}