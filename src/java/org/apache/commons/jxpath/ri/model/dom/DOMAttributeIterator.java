/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/model/dom/DOMAttributeIterator.java,v 1.6 2002/08/10 16:13:04 dmitri Exp $
 * $Revision: 1.6 $
 * $Date: 2002/08/10 16:13:04 $
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

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * An iterator of attributes of a DOM Node.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2002/08/10 16:13:04 $
 */
public class DOMAttributeIterator implements NodeIterator {
    private NodePointer parent;
    private QName name;
    private List attributes;
    private int position = 0;

    public DOMAttributeIterator(NodePointer parent, QName name){
        this.parent = parent;
        this.name = name;
        attributes = new ArrayList();
        Node node = (Node)parent.getNode();
        if (node.getNodeType() == Node.ELEMENT_NODE){
            String lname = name.getName();
            if (!lname.equals("*")){
                Attr attr = getAttribute((Element)node, name);
                if (attr != null){
                    attributes.add(attr);
                }
            }
            else {
                NamedNodeMap map = node.getAttributes();
                int count = map.getLength();
                for (int i = 0; i < count; i++){
                    Attr attr = (Attr)map.item(i);
                    if (testAttr(attr, name)){
                        attributes.add(attr);
                    }
                }
            }
        }
    }

    private boolean testAttr(Attr attr, QName testName){
        String nodePrefix = DOMNodePointer.getPrefix(attr);
        String nodeLocalName = DOMNodePointer.getLocalName(attr);

        if (nodePrefix != null && nodePrefix.equals("xmlns")){
            return false;
        }

        if (nodePrefix == null && nodeLocalName.equals("xmlns")){
            return false;
        }

        String testLocalName = name.getName();
        if (testLocalName.equals("*") || testLocalName.equals(nodeLocalName)){
            String testPrefix = testName.getPrefix();

            if (equalStrings(testPrefix, nodePrefix)){
                return true;
            }

            String testNS = null;
            if (testPrefix != null){
                testNS = parent.getNamespaceURI(testPrefix);
            }

            String nodeNS = null;
            if (nodePrefix != null){
                nodeNS = parent.getNamespaceURI(nodePrefix);
            }
            return equalStrings(testNS, nodeNS);
        }
        return false;
    }

    private static boolean equalStrings(String s1, String s2){
        if (s1 == null && s2 != null){
            return false;
        }
        if (s1 != null && !s1.equals(s2)){
            return false;
        }
        return true;
    }

    private Attr getAttribute(Element element, QName name){
        String testPrefix = name.getPrefix();
        String testNS = null;

        if (testPrefix != null){
            testNS = parent.getNamespaceURI(testPrefix);
        }

        if (testNS != null){
            Attr attr = element.getAttributeNodeNS(testNS, name.getName());
            if (attr == null){
                // This may mean that the parser does not support NS for
                // attributes, example - the version of Crimson bundled
                // with JDK 1.4.0
                NamedNodeMap nnm = element.getAttributes();
                for (int i = 0; i < nnm.getLength(); i++){
                    attr = (Attr)nnm.item(i);
                    if (testAttr(attr, name)){
                        return attr;
                    }
                }
            }
            return attr;
        }
        else {
            return element.getAttributeNode(name.getName());
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
        return new DOMAttributePointer(parent, (Attr)attributes.get(index));
    }

    public int getPosition(){
        return position;
    }

    public boolean setPosition(int position){
        this.position = position;
        return position >= 1 && position <= attributes.size();
    }
}