/*
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.commons.jxpath.ri.model.dynamic;

import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyIterator;
import org.apache.commons.jxpath.ri.model.beans.PropertyOwnerPointer;

/**
 * @deprecated - no longer needed, as it is identical to PropertyIterator.
 * 
 * @author <a href="mailto:dmitri@apache.org">Dmitri Plotnikov</a>
 * @version $Id: DynamicPropertyIterator.java,v 1.5 2004/02/29 14:17:44 scolebourne Exp $
 */
public class DynamicPropertyIterator extends PropertyIterator {

    public DynamicPropertyIterator(
            PropertyOwnerPointer pointer,
            String name,
            boolean reverse,
            NodePointer startWith) 
    {
        super(pointer, name, reverse, startWith);
    }
}
