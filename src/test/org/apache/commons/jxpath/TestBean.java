/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/TestBean.java,v 1.1 2001/08/23 00:47:02 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/08/23 00:47:02 $
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

/**
 * General purpose test bean for JUnit tests for the "jxpath" component.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:02 $
 */
public class TestBean {

    // ------------------------------------------------------------- Properties


    /**
     * An array of nested java beans.
     */
    private NestedTestBean[] beans;
    public NestedTestBean[] getBeans(){
        if (beans == null){
            beans = new NestedTestBean[2];
            beans[0] = new NestedTestBean("Name 1");
            beans[1] = new NestedTestBean("Name 2");
        }
        return beans;
    }

    /**
     * A boolean property.
     */
    private boolean bool = false;
    public boolean getBoolean(){
        return bool;
    }

    public void setBoolean(boolean bool){
        this.bool = bool;
    }

    private int integer = 1;
    /**
     * A read-only integer property
     */
    public int getInt(){
        return integer;
    }

    public void setInt(int integer){
        this.integer = integer;
    }

    /**
     * A read-only array of integers
     */
    private int[] array = {1, 2, 3, 4};
    public int[] getIntegers(){
        return array;
    }

    public int getIntegers(int index){
        return array[index];
    }

    public void setIntegers(int index, int value){
        array[index] = value;
    }

    /**
     * A heterogeneous list: String, Integer, NestedTestBean
     */
    private ArrayList list;
    public List getList(){
        if (list == null){
            list = new ArrayList();
            list.add("String 3");
            list.add(new Integer(3));
            list.add(new NestedTestBean("Name 3"));
        }
        return list;
    }

    /**
     * A Map
     */
    private HashMap map;
    public Map getMap(){
        if (map == null){
            map = new HashMap();
            map.put("Key1", "Value 1");
            map.put("Key2", new NestedTestBean("Name 6"));
//            map.put("Key3", null);
        }
        return map;
    }

    /**
     * A nested read-only java bean
     */
    private NestedTestBean nestedBean = new NestedTestBean("Name 0");
    public NestedTestBean getNestedBean(){
        return nestedBean;
    }

    private NestedTestBean object = new NestedTestBean("Name 5");

    /**
     * Returns a NestedTestBean: testing recognition of generic objects
     */
    public Object getObject(){
        return object;
    }

    /**
     * Returns an array of ints: testing recognition of generic objects
     */
    public Object getObjects(){
        return getIntegers();
    }

    /**
     * A heterogeneous set: String, Integer, NestedTestBean
     */
    private HashSet set;
    public Set getSet(){
        if (set == null){
            set = new HashSet();
            set.add("String 4");
            set.add(new Integer(4));
            set.add(new NestedTestBean("Name 4"));
        }
        return set;
    }

    public String toString(){
        return "ROOT";
    }
}
