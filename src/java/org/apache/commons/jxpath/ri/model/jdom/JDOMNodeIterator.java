/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/jdom/JDOMNodeIterator.java,v 1.5 2003/10/09 21:31:41 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/10/09 21:31:41 $
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
package org.apache.commons.jxpath.ri.model.jdom;

import java.util.Collections;
import java.util.List;

import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.jdom.Document;
import org.jdom.Element;

/**
 * An iterator of children of a JDOM Node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2003/10/09 21:31:41 $
 */
public class JDOMNodeIterator implements NodeIterator {
    private NodePointer parent;
    private NodeTest nodeTest;

    private boolean reverse;
    private int position = 0;
    private int index = 0;
    private List children;
    private Object child;

    public JDOMNodeIterator(
            NodePointer parent, NodeTest nodeTest,
            boolean reverse, NodePointer startWith)
    {
        this.parent = parent;
        if (startWith != null) {
            this.child = startWith.getNode();
        }
        // TBD: optimize me for different node tests
        Object node = parent.getNode();
        if (node instanceof Document) {
            this.children = ((Document) node).getContent();
        }
        else if (node instanceof Element) {
            this.children = ((Element) node).getContent();
        }
        else {
            this.children = Collections.EMPTY_LIST;
        }
        this.nodeTest = nodeTest;
        this.reverse = reverse;
    }

    public NodePointer getNodePointer() {
        if (child == null) {
            if (!setPosition(1)) {
                return null;
            }
            position = 0;
        }

        return new JDOMNodePointer(parent, child);
    }

    public int getPosition() {
        return position;
    }

    public boolean setPosition(int position) {
        while (this.position < position) {
            if (!next()) {
                return false;
            }
        }
        while (this.position > position) {
            if (!previous()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This is actually never invoked during the normal evaluation
     * of xpaths - an iterator is always going forward, never backwards.
     * So, this is implemented only for completeness and perhaps for
     * those who use these iterators outside of XPath evaluation.
     */
    private boolean previous() {
        position--;
        if (!reverse) {
            while (--index >= 0) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
        }
        else {
            for (; index < children.size(); index++) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean next() {
        position++;
        if (!reverse) {
            if (position == 1) {
                index = 0;
                if (child != null) {
                    index = children.indexOf(child) + 1;
                }
            }
            else {
                index++;
            }
            for (; index < children.size(); index++) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
            return false;
        }
        else {
            if (position == 1) {
                index = children.size() - 1;
                if (child != null) {
                    index = children.indexOf(child) - 1;
                }
            }
            else {
                index--;
            }
            for (; index >= 0; index--) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean testChild() {
        return JDOMNodePointer.testNode(parent, child, nodeTest);
    }
}