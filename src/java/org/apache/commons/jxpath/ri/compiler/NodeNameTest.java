/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.ri.QName;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.9 $ $Date: 2004/02/29 14:17:38 $
 */
public class NodeNameTest extends NodeTest {
    private QName qname;

    public NodeNameTest(QName qname) {
        this.qname = qname;
    }

    public QName getNodeName() {
        return qname;
    }

    public String toString() {
        return qname.toString();
    }
}