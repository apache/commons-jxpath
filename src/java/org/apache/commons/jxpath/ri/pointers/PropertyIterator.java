/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/pointers/Attic/PropertyIterator.java,v 1.1 2001/09/03 01:22:31 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/09/03 01:22:31 $
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
package org.apache.commons.jxpath.ri.pointers;

/**
 * Iterates property values of an object pointed at with a PropertyOwnerPointer.
 * Examples of such objects are JavaBeans and objects with Dynamic Properties.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/09/03 01:22:31 $
 */
public class PropertyIterator implements NodeIterator {
    boolean empty = false;
    private boolean reverse;
    private String name;
    private int startIndex = 0;
    private boolean targetReady = false;
    private int position = 0;
    private PropertyPointer propertyNodePointer;
    private int startPropertyIndex;
    private boolean firstIteration = true;

    private boolean ready = false;
    private boolean includeStart = false;

    public PropertyIterator(PropertyOwnerPointer pointer, boolean children, String name, boolean reverse){
        if (children){
            propertyNodePointer = pointer.getPropertyPointer();
            this.name = name;
            this.reverse = reverse;
            this.includeStart = true;
            if (reverse){
                this.startPropertyIndex = -1;
                this.startIndex = -1;
            }
        }
        else {
            this.propertyNodePointer = (PropertyPointer)pointer.clone();
            this.name = name;
            this.reverse = reverse;
            this.startPropertyIndex = propertyNodePointer.getPropertyIndex();
            this.startIndex = propertyNodePointer.getIndex();
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
        if (firstIteration){
            firstIteration = false;
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
        return propertyNodePointer.childNodePointer();
    }

    public int getPosition(){
        return position;
    }

    public boolean setPosition(int position){
        firstIteration = false;
        if (name != null){
            return setPositionIndividual(position);
        }
        else {
            return setPositionMultiple(position);
        }
    }

    private boolean setPositionIndividual(int position){
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

        int length = propertyNodePointer.getLength();
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

    private boolean setPositionMultiple(int position){
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
            if (start == -1){
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
        if (propertyNodePointer instanceof DynamicPropertyPointer){
            propertyNodePointer.setPropertyName(name);
        }

        String names[] = propertyNodePointer.getPropertyNames();
        if (!reverse){
            int startPropertyIndex = propertyNodePointer.getPropertyIndex();
            if (startPropertyIndex == PropertyOwnerPointer.UNSPECIFIED_PROPERTY){
                startPropertyIndex = 0;
            }
            if (propertyNodePointer.getIndex() == NodePointer.WHOLE_COLLECTION){
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
            int startPropertyIndex = propertyNodePointer.getPropertyIndex();
            if (startPropertyIndex == PropertyOwnerPointer.UNSPECIFIED_PROPERTY){
                startPropertyIndex = names.length - 1;
            }
            if (propertyNodePointer.getIndex() == NodePointer.WHOLE_COLLECTION){
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