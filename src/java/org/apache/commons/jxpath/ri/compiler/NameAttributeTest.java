package org.apache.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.ri.EvalContext;

/**
 *
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2002/05/08 00:39:59 $
 */
public class NameAttributeTest extends CoreOperation {

    public NameAttributeTest(Expression namePath, Expression nameValue){
        super(OP_EQ, namePath, nameValue);
    }

    public Expression getNameTestExpression(){
        return getArg2();
    }

    /**
     * @see Expression#computeContextDependent()
     */
    public boolean computeContextDependent() {
        return true;
    }
}
