package org.apache.commons.jxpath.ri.model.dynabeans;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;

public class LazyDynaBeanTest extends JXPathTestCase {

    public void testLazyProperty() throws JXPathNotFoundException {
        LazyDynaBean bean = new LazyDynaBean();
        JXPathContext context = JXPathContext.newContext(bean);
        context.getValue("nosuch");
    }

    public void testStrictLazyDynaBeanPropertyFactory() {
        JXPathContextReferenceImpl.addNodePointerFactory(new StrictLazyDynaBeanPointerFactory());
        try {
            testLazyProperty();
            fail();
        } catch (JXPathNotFoundException e) {
            //okay
        }
    }
}
