/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/beans/NullPropertyPointer.java,v 1.1 2002/04/21 21:52:33 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2002/04/21 21:52:33 $
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
package org.apache.commons.jxpath.ri.model.beans;

import org.apache.commons.jxpath.*;
import org.apache.commons.jxpath.ri.*;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.*;

import java.lang.reflect.*;
import java.util.*;
import java.beans.*;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2002/04/21 21:52:33 $
 */
public class NullPropertyPointer extends PropertyPointer {

    private String propertyName = "*";
    private boolean dynamic = false;

    /**
     */
    public NullPropertyPointer(NodePointer parent){
        super(parent);
    }

    public QName getName(){
        return new QName(null, propertyName);
    }

    public void setPropertyIndex(int index){
    }

    public int getLength(){
        return 0;
    }

    public Object getBaseValue(){
        return null;
    }

    public Object getValue(){
        return null;
    }

    public NodePointer getValuePointer(){
        return new NullPointer(this,  new QName(null, getPropertyName()));
    }

    protected boolean isActualProperty(){
        return false;
    }

    public boolean isActual(){
        return false;
    }

    public boolean isNode(){
        return false;
    }

    public void setValue(Object value){
        throw new RuntimeException("Cannot set property " + asPath() +
            ", the target object is null");
    }

    public NodePointer createPath(JXPathContext context){
        return parent.createChild(context, getName(), getIndex());
    }

    public void createPath(JXPathContext context, Object value){
        parent.createChild(context, getName(), getIndex(), value);
    }

    public void createChild(JXPathContext context, QName name, int index, Object value){
        createPath(context).createChild(context, name, index, value);
    }

    public NodePointer createChild(JXPathContext context, QName name, int index){
        return createPath(context).createChild(context, name, index);
    }

    public String getPropertyName(){
        return propertyName;
    }

    public void setPropertyName(String propertyName){
        this.propertyName = propertyName;
    }

    public void setDynamic(boolean flag){
        dynamic = flag;
    }

    public boolean isCollection(){
        return getIndex() != WHOLE_COLLECTION;
    }

    public int getPropertyCount(){
        return 0;
    }

    public String[] getPropertyNames(){
        return new String[0];
    }

    public String asPath(){
        if (!dynamic){
            return super.asPath();
        }
        else {
            StringBuffer buffer = new StringBuffer();
            buffer.append(getParent().asPath());
            buffer.append("[@name='");
            buffer.append(escape(getPropertyName()));
            buffer.append("']");
            if (index != WHOLE_COLLECTION){
                buffer.append('[').append(index + 1).append(']');
            }
            return buffer.toString();
        }
    }

    private String escape(String string){
        int index = string.indexOf('\'');
        while (index != -1){
            string = string.substring(0, index) + "&apos;" + string.substring(index + 1);
            index = string.indexOf('\'');
        }
        index = string.indexOf('\"');
        while (index != -1){
            string = string.substring(0, index) + "&quot;" + string.substring(index + 1);
            index = string.indexOf('\"');
        }
        return string;
    }
}