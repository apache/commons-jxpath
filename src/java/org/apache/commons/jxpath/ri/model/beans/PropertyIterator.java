/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/beans/PropertyIterator.java,v 1.5 2002/10/12 21:02:24 dmitri Exp $
 * $Revision: 1.5 $
 * $Date: 2002/10/12 21:02:24 $
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

import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Iterates property values of an object pointed at with a PropertyOwnerPointer.
 * Examples of such objects are JavaBeans and objects with Dynamic Properties.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2002/10/12 21:02:24 $
 */
public class PropertyIterator implements NodeIterator {
    private boolean empty = false;
    private boolean reverse;
    private String name;
    private int startIndex = 0;
    private boolean targetReady = false;
    private int position = 0;
    private PropertyPointer propertyNodePointer;
    private int startPropertyIndex;

    private boolean ready = false;
    private boolean includeStart = false;

    public PropertyIterator(PropertyOwnerPointer pointer, String name, boolean reverse, NodePointer startWith){
        propertyNodePointer = pointer.getPropertyPointer();
        this.name = name;
        this.reverse = reverse;
        this.includeStart = true;
        if (reverse){
            this.startPropertyIndex = PropertyPointer.UNSPECIFIED_PROPERTY;
            this.startIndex = -1;
        }
        if (startWith != null){
            while (startWith != null && startWith.getParent() != pointer){
                startWith = startWith.getParent();
            }
            if (startWith == null){
                throw new JXPathException(
                    "PropertyIerator startWith parameter is not a child of the supplied parent");
            }
            this.startPropertyIndex = ((PropertyPointer)startWith).getPropertyIndex();
            this.startIndex = startWith.getIndex();
            if (this.startIndex == NodePointer.WHOLE_COLLECTION){
                this.startIndex = 0;
            }
            this.includeStart = false;
            if (reverse && startIndex == -1){
                this.includeStart = true;
            }
        }
    }

    public void reset(){
        position = 0;
        targetReady = false;
    }

    public NodePointer getNodePointer(){
        if (position == 0){
            if (name != null){
                if (!targetReady){
                    prepare();
                }
                // If there is no such property - return null
                if (empty){
                    return null;
                }
            }
            else {
                if (!setPosition(1)){
                    return null;
                }
                reset();
            }
        }
        return propertyNodePointer.getValuePointer();
    }

    public int getPosition(){
        return position;
    }

    public boolean setPosition(int position){
        if (name != null){
            return setPositionIndividualProperty(position);
        }
        else {
            return setPositionAllProperties(position);
        }
    }

    private boolean setPositionIndividualProperty(int position){
        this.position = position;
        if (position < 1){
            return false;
        }

        if (!targetReady){
            prepare();
        }

        if (empty){
            return false;
        }

        int length = propertyNodePointer.getLength();   // TBD: cache length
        int index;
        if (!reverse){
            index = position + startIndex;
            if (!includeStart){
                index++;
            }
            if (index > length){
                return false;
            }
        }
        else {
            int end = startIndex;
            if (end == -1){
                end = length - 1;
            }
            index = end - position + 2;
            if (!includeStart){
                index--;
            }
            if (index < 1){
                return false;
            }
        }
        propertyNodePointer.setIndex(index - 1);
        return true;
    }

    private boolean setPositionAllProperties(int position){
        this.position = position;
        if (position < 1){
            return false;
        }

        int offset;
        int count = propertyNodePointer.getPropertyCount();
        if (!reverse){
            int index = 1;
            for (int i = startPropertyIndex; i < count; i++){
                propertyNodePointer.setPropertyIndex(i);
                int length = propertyNodePointer.getLength();
                if (i == startPropertyIndex){
                    length -= startIndex;
                    if (!includeStart){
                        length--;
                    }
                    offset = startIndex + position - index;
                    if (!includeStart){
                        offset++;
                    }
                }
                else {
                    offset = position - index;
                }
                if (index <= position && position < index + length){
                    propertyNodePointer.setIndex(offset);
                    return true;
                }
                index += length;
            }
        }
        else {
            int index = 1;
            int start = startPropertyIndex;
            if (start == PropertyPointer.UNSPECIFIED_PROPERTY){
                start = count - 1;
            }
            for (int i = start; i >= 0; i--){
                propertyNodePointer.setPropertyIndex(i);
                int length = propertyNodePointer.getLength();
                if (i == startPropertyIndex){
                    int end = startIndex;
                    if (end == -1){
                        end = length - 1;
                    }
                    length = end + 1;
                    offset = end - position + 1;
                    if (!includeStart){
                        offset--;
                        length--;
                    }
                }
                else {
                    offset = length - (position - index) - 1;
                }

                if (index <= position && position < index + length){
                    propertyNodePointer.setIndex(offset);
                    return true;
                }
                index += length;
            }
        }
        return false;
    }

    private void prepare(){
        targetReady = true;
        empty = true;
        // TBD: simplify
        if (propertyNodePointer instanceof DynamicPropertyPointer){
            propertyNodePointer.setPropertyName(name);
        }

        String names[] = propertyNodePointer.getPropertyNames();
        if (!reverse){
            if (startPropertyIndex == PropertyPointer.UNSPECIFIED_PROPERTY){
                startPropertyIndex = 0;
            }
            if (startIndex == NodePointer.WHOLE_COLLECTION){
                startIndex = 0;
            }
            for (int i = startPropertyIndex; i < names.length; i++){
                if (names[i].equals(name)){
                    propertyNodePointer.setPropertyIndex(i);
                    if (i != startPropertyIndex){
                        startIndex = 0;
                        includeStart = true;
                    }
                    empty = false;
                    break;
                }
            }
        }
        else {
            if (startPropertyIndex == PropertyPointer.UNSPECIFIED_PROPERTY){
                startPropertyIndex = names.length - 1;
            }
            if (startIndex == NodePointer.WHOLE_COLLECTION){
                startIndex = -1;
            }
            for (int i = startPropertyIndex; i >= 0; i--){
                if (names[i].equals(name)){
                    propertyNodePointer.setPropertyIndex(i);
                    if (i != startPropertyIndex){
                        startIndex = -1;
                        includeStart = true;
                    }
                    empty = false;
                    break;
                }
            }
        }
    }
}