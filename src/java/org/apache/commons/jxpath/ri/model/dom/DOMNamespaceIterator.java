/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/dom/DOMNamespaceIterator.java,v 1.2 2002/04/24 04:05:40 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2002/04/24 04:05:40 $
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * An iterator of namespaces of a DOM Node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2002/04/24 04:05:40 $
 */
public class DOMNamespaceIterator implements NodeIterator {
    private NodePointer parent;
    private List attributes;
    private int position = 0;

    public DOMNamespaceIterator(NodePointer parent){
        this.parent = parent;
        attributes = new ArrayList();
        collectNamespaces(attributes, (Node)parent.getValue());
    }

    private void collectNamespaces(List attributes, Node node){
        Node parent = node.getParentNode();
        if (parent != null){
            collectNamespaces(attributes, parent);
        }
        if (node.getNodeType() == Node.ELEMENT_NODE){
            NamedNodeMap map = node.getAttributes();
            int count = map.getLength();
            for (int i = 0; i < count; i++){
                Attr attr = (Attr)map.item(i);
                String prefix = DOMNodePointer.getPrefix(attr);
                String name = DOMNodePointer.getLocalName(attr);
                if ((prefix != null && prefix.equals("xmlns")) ||
                        (prefix == null && name.equals("xmlns"))){
                    attributes.add(attr);
                }
            }
        }
    }

    public NodePointer getNodePointer(){
        if (position == 0){
            if (!setPosition(1)){
                return null;
            }
            position = 0;
        }
        int index = position - 1;
        if (index < 0){
            index = 0;
        }
        String prefix = "";
        Attr attr = (Attr)attributes.get(index);
        String name = attr.getPrefix();
        if (name != null && name.equals("xmlns")){
            prefix = DOMNodePointer.getLocalName(attr);
        }
        return new NamespacePointer(parent, prefix, attr.getValue());
    }

    public int getPosition(){
        return position;
    }

    public boolean setPosition(int position){
        this.position = position;
        return position >= 1 && position <= attributes.size();
    }
}