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
package org.apache.commons.jxpath.ri;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathContextFactory;
import org.apache.commons.jxpath.JXPathContextFactoryConfigurationError;

/**
 * Default implementation of JXPathContextFactory.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2004/02/29 14:17:45 $
 */
public class JXPathContextFactoryReferenceImpl extends JXPathContextFactory {

    public JXPathContextFactoryReferenceImpl() {
    }

    public JXPathContext newContext(
        JXPathContext parentContext,
        Object contextBean)
        throws JXPathContextFactoryConfigurationError 
    {
        return new JXPathContextReferenceImpl(parentContext, contextBean);
    }
}