/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/beans/NullElementPointer.java,v 1.11 2002/11/28 01:02:04 dmitri Exp $
 * $Revision: 1.11 $
 * $Date: 2002/11/28 01:02:04 $
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

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Used when there is a need to construct a Pointer for
 * a collection element that does not exist.  For example,
 * if the path is "foo[3]", but the collection "foo" only has
 * one element or is empty or is null, the NullElementPointer
 * can be used to capture this situatuin without putting
 * a regular NodePointer into an invalid state.  Just create
 * a NullElementPointer with index 2 (= 3 - 1) and a "foo" pointer
 * as the parent.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.11 $ $Date: 2002/11/28 01:02:04 $
 */
public class NullElementPointer extends PropertyOwnerPointer {

    public NullElementPointer(NodePointer parent, int index){
        super(parent);
        this.index = index;
    }

    public QName getName(){
        return null;
    }

    public Object getBaseValue(){
        return null;
    }

    public Object getImmediateNode(){
        return null;
    }
    
    public boolean isLeaf() {
        return true;
    }    
    
    public boolean isCollection(){
        return false;
    }

    public PropertyPointer getPropertyPointer(){
        return new NullPropertyPointer(this);
    }

    public NodePointer getValuePointer(){
        return new NullPointer(this, getName());
    }

    public void setValue(Object value){
        super.setValue(value);
        if (parent instanceof PropertyPointer){
            parent.setValue(value);
        }
        else {
            throw new UnsupportedOperationException("Cannot setValue of an object that is not some other object's property");
        }
    }

    public boolean isActual(){
        return false;
    }

    public boolean isContainer(){
        return true;
    }

    public NodePointer createPath(JXPathContext context, Object value){
        if (parent instanceof PropertyPointer){
            return parent.getParent().createChild(context, parent.getName(), index, value);
        }
        else {
            return parent.createChild(context, null, index, value);
        }
    }

    public NodePointer createPath(JXPathContext context){
        if (parent instanceof PropertyPointer){
            return parent.getParent().createChild(context, parent.getName(), index);
        }
        else {
            return parent.createChild(context, null, index);
        }
    }

    public NodePointer createChild(JXPathContext context, QName name, int index, Object value){
        if (index != 0 && index != WHOLE_COLLECTION){
            throw new JXPathException("Internal error. " +
                "Indexed passed to NullElementPointer.createChild() is not 0: " + index);
        }
        if (parent instanceof PropertyPointer){
            return parent.getParent().createChild(context, parent.getName(), getIndex(), value);
        }
        else {
            return parent.createChild(context, name, getIndex(), value);
        }
    }

    public NodePointer createChild(JXPathContext context, QName name, int index){
        if (index != 0 && index != WHOLE_COLLECTION){
            throw new JXPathException("Internal error. " +
                "Indexed passed to NullElementPointer.createChild() is not 0: " + index);
        }
        if (parent instanceof PropertyPointer){
            return parent.getParent().createChild(context, parent.getName(), getIndex());
        }
        else {
            return parent.createChild(context, name, getIndex());
        }
    }

    public int hashCode(){
        return getParent().hashCode() + index;
    }

    public boolean equals(Object object){
        if (object == this){
            return true;
        }

        if (!(object instanceof NullElementPointer)){
            return false;
        }

        NullElementPointer other = (NullElementPointer)object;
        return getParent() == other.getParent() &&
            index == other.index;
    }

    public String asPath(){
        return parent.asPath() + "[" + (index + 1) + "]";
    }

    public int getLength(){
        return 0;
    }
}