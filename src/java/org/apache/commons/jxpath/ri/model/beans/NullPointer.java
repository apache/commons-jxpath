/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/beans/NullPointer.java,v 1.6 2002/08/10 01:49:46 dmitri Exp $
 * $Revision: 1.6 $
 * $Date: 2002/08/10 01:49:46 $
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

import java.util.Locale;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2002/08/10 01:49:46 $
 */
public class NullPointer extends PropertyOwnerPointer {
    private QName name;
    private String id;

    public NullPointer(QName name, Locale locale){
        super(null, locale);
        this.name = name;
    }

    /**
     * Used for the root node
     */
    public NullPointer(NodePointer parent, QName name){
        super(parent);
        this.name = name;
    }

    public NullPointer(Locale locale, String id){
        super(null, locale);
        this.id = id;
    }

    public QName getName(){
        return name;
    }

    public Object getBaseValue(){
        return null;
    }

    public void setValue(Object value){
        super.setValue(value);
        if (parent instanceof PropertyPointer){
            parent.setValue(value);
        }
        else {
            throw new UnsupportedOperationException("Cannot setValue of an object that is not some other object's property/child");
        }
    }

    public boolean isActual(){
        return false;
    }

    public PropertyPointer getPropertyPointer(){
        return new NullPropertyPointer(this);
    }

    public NodePointer createPath(JXPathContext context, Object value){
        if (parent != null){
            if (parent instanceof PropertyPointer){
                return parent.createPath(context, value);
            }
            else {
                return parent.createChild(context, getName(), 0, value);
            }
        }
        else {
            throw new UnsupportedOperationException("Cannot create the root object: " + asPath());
        }
    }

    public NodePointer createPath(JXPathContext context){
        if (parent != null){
            if (parent instanceof PropertyPointer){
                return parent.createPath(context).getValuePointer();
            }
            else {
                return parent.createChild(context, getName(), 0).getValuePointer();
            }
        }
        throw new UnsupportedOperationException("Cannot create the root object: " + asPath());
    }

    public NodePointer createChild(JXPathContext context, QName name, int index, Object value){
        if (parent != null){
            NodePointer pointer = createPath(context);
            if (pointer != null){
                pointer = pointer.getValuePointer().createChild(context, name, index, value);
                return pointer;
            }
        }
        throw new UnsupportedOperationException("Cannot create the root object: " + asPath());
    }

    public NodePointer createChild(JXPathContext context, QName name, int index){
        if (parent != null){
            NodePointer pointer = createPath(context);
            if (pointer != null){
                return pointer.createChild(context, name, index);
            }
        }
        throw new UnsupportedOperationException("Cannot create the root object: " + asPath());
    }

    public int hashCode(){
        return name == null ? 0 : name.hashCode();
    }

    public boolean equals(Object object){
        if (object == this){
            return true;
        }

        if (!(object instanceof NullPointer)){
            return false;
        }

        NullPointer other = (NullPointer)object;
        return (name == null && other.name == null) ||
               (name != null && name.equals(other.name));
    }

    public String asPath(){
        if (id != null){
            return "id(" + id + ")";
        }

        if (parent != null){
            return super.asPath();
        }
        return "null()";
    }

    public int getLength(){
        return 0;
    }
}