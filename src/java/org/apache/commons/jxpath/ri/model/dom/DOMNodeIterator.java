/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/dom/DOMNodeIterator.java,v 1.1 2002/04/21 21:52:33 dmitri Exp $
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
package org.apache.commons.jxpath.ri.model.dom;

import org.apache.commons.jxpath.*;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.*;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

import java.lang.reflect.*;
import java.util.*;
import java.beans.*;
import org.w3c.dom.*;

/**
 * An iterator of children of a DOM Node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2002/04/21 21:52:33 $
 */
public class DOMNodeIterator implements NodeIterator {
    private NodePointer parent;
    private NodeTest nodeTest;
    private String namespaceURI;
    private Node node;
    private Node child = null;
    private boolean reverse;
    private int position = 0;

    public DOMNodeIterator(NodePointer parent, NodeTest nodeTest, boolean reverse, NodePointer startWith){
        this.parent = parent;
        this.node = (Node)parent.getValue();
        if (startWith != null){
            this.child = (Node)startWith.getValue();
        }
        this.nodeTest = nodeTest;
        this.reverse = reverse;
    }

    public NodePointer getNodePointer(){
        if (child == null){
            if (!setPosition(1)){
                return null;
            }
            position = 0;
        }

        return new DOMNodePointer(parent, child);
    }

    public int getPosition(){
        return position;
    }

    public boolean setPosition(int position){
        while (this.position < position){
            if (!next()){
                return false;
            }
        }
        while (this.position > position){
            if (!previous()){
                return false;
            }
        }
//        System.err.println(getNodePointer().asPath() + " SET POSITION: " + position);
        return true;
    }

    private boolean previous(){
        position--;
        if (!reverse){
            child = child.getPreviousSibling();
            while (child != null && !testChild()){
                child = child.getPreviousSibling();
            }
        }
        else {
            child = child.getNextSibling();
            while (child != null && !testChild()){
                child = child.getNextSibling();
            }
        }
        return child != null;
    }

    private boolean next(){
        position++;
        if (!reverse){
            if (position == 1){
                if (child == null){
                    child = node.getFirstChild();
                }
                else {
                    child = child.getNextSibling();
                }
            }
            else {
                child = child.getNextSibling();
            }
            while (child != null && !testChild()){
                child = child.getNextSibling();
            }
        }
        else {
            if (position == 1){
                if (child == null){
                    child = node.getLastChild();
                }
                else {
                    child = child.getPreviousSibling();
                }
            }
            else {
                child = child.getPreviousSibling();
            }
            while (child != null && !testChild()){
                child = child.getPreviousSibling();
            }
        }
        return child != null;
    }

    private boolean testChild(){
        return DOMNodePointer.testNode(parent, child, nodeTest);
    }
/*
    public NodePointer getNodePointer(){
        if (child == null){
            if (!setPosition(1)){
                return null;
            }
            position = 0;
        }

        if (children){
            return new DOMNodePointer(parent, child);
        }
        else {
            return new DOMNodePointer(parent.getParent(), child);
        }
    }

    public int getPosition(){
        return position;
    }

    public boolean setPosition(int position){
        while (this.position < position){
            if (!next()){
                return false;
            }
        }
        while (this.position > position){
            if (!previous()){
                return false;
            }
        }
//        System.err.println(getNodePointer().asPath() + " SET POSITION: " + position);
        return true;
    }

    private boolean previous(){
        position--;
        if (!reverse){
            child = child.getPreviousSibling();
            while (child != null && !testChild()){
                child = child.getPreviousSibling();
            }
        }
        else {
            child = child.getNextSibling();
            while (child != null && !testChild()){
                child = child.getNextSibling();
            }
        }
        return child != null;
    }

    private boolean next(){
        position++;
        if (!reverse){
            if (position == 1){
                if (children){
                    child = node.getFirstChild();
                }
                else {
                    child = node.getNextSibling();
                }
            }
            else {
                child = child.getNextSibling();
            }
            while (child != null && !testChild()){
                child = child.getNextSibling();
            }
        }
        else {
            if (position == 1){
                if (children){
                    child = node.getLastChild();
                }
                else {
                    child = node.getPreviousSibling();
                }
            }
            else {
                child = child.getPreviousSibling();
            }
            while (child != null && !testChild()){
                child = child.getPreviousSibling();
            }
        }
        return child != null;
    }

    private boolean testChild(){
        return DOMNodePointer.testNode(parent, child, nodeTest);
    }
    */
}