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
package org.apache.commons.jxpath.ri;

import java.io.Serializable;


/**
 * A qualified name: a combination of an optional namespace prefix
 * and an local name.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class QName implements Serializable {
    private static final long serialVersionUID = 7616199282015091496L;

    private String prefix;
    private String name;
    private String qualifiedName;

    /**
     * Create a new QName.
     * @param qualifiedName value
     */
    public QName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
        int index = qualifiedName.indexOf(':');
        prefix = index < 0 ? null : qualifiedName.substring(0, index);
        name = index < 0 ? qualifiedName : qualifiedName.substring(index + 1);
    }

    /**
     * Create a new QName.
     * @param prefix ns
     * @param localName String
     */
    public QName(String prefix, String localName) {
        this.prefix = prefix;
        this.name = localName;
        this.qualifiedName = prefix == null ? localName : prefix + ':' + localName;
    }

    /**
     * Get the prefix of this QName.
     * @return String
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get the local name.
     * @return String
     */
    public String getName() {
        return name;
    }

    public String toString() {
        return qualifiedName;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof QName)) {
            return false;
        }
        return qualifiedName.equals(((QName) object).qualifiedName);
    }
}
