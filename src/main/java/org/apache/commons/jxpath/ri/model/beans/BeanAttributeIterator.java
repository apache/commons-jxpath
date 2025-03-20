/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jxpath.ri.model.beans;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * An iterator of attributes of a JavaBean. Returns bean properties as well as the "xml:lang" attribute.
 */
public class BeanAttributeIterator extends PropertyIterator {

    private final NodePointer parent;
    private int position;
    private final boolean includeXmlLang;

    /**
     * Constructs a new BeanAttributeIterator.
     * 
     * @param parent parent pointer
     * @param qName   name of this bean
     */
    public BeanAttributeIterator(final PropertyOwnerPointer parent, final QName qName) {
        super(parent, qName.getPrefix() == null && (qName.getName() == null || qName.getName().equals("*")) ? null : qName.toString(), false, null);
        this.parent = parent;
        includeXmlLang = qName.getPrefix() != null && qName.getPrefix().equals("xml") && (qName.getName().equals("lang") || qName.getName().equals("*"));
    }

    @Override
    public NodePointer getNodePointer() {
        return includeXmlLang && position == 1 ? new LangAttributePointer(parent) : super.getNodePointer();
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public boolean setPosition(final int position) {
        this.position = position;
        if (includeXmlLang) {
            return position == 1 || super.setPosition(position - 1);
        }
        return super.setPosition(position);
    }
}
