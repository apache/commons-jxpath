/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/TestMixedModelBean.java,v 1.2 2003/01/10 02:11:28 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/10 02:11:28 $
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

package org.apache.commons.jxpath;

import java.util.*;

import org.apache.commons.jxpath.xml.DocumentContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Mixed model test bean: Java, collections, map, DOM, Container.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2003/01/10 02:11:28 $
 */
public class TestMixedModelBean {
    private String string;
    private TestBean bean;
    private Container container;
    private Document document;
    private Element element;

    private Map map;

    private List list;
    private int[][] matrix;

    public TestMixedModelBean(){
        string = "string";
        bean = new TestBean();
        map = new HashMap();
        list = new ArrayList();

        container = new DocumentContainer(getClass().getResource("Vendor.xml"));
        document = (Document)container.getValue();
        element = document.getDocumentElement();

        map.put("string", string);
        map.put("bean", bean);
        map.put("map", map);
        map.put("list", list);
        map.put("document", document);
        map.put("element", element);
        map.put("container", container);

        list.add(string);
        list.add(bean);
        list.add(map);
        list.add(new ArrayList(Collections.singletonList("string2")));
        list.add(document);
        list.add(element);
        list.add(container);
        
        matrix = new int[1][];
        matrix[0] = new int[1];
        matrix[0][0] = 3;
    }

    public String getString(){
        return string;
    }
    
    public TestBean getBean() {
        return bean;
    }
    
    public Map getMap(){
        return map;
    }

    public List getList() {
        return list;
    }
        
    public Document getDocument() {
        return document;
    }
    
    public Element getElement() {
        return element;
    }
    
    public Container getContainer(){
        return container;
    }
    
    public int[][] getMatrix(){
        return matrix;
    }
}
