package org.apache.commons.jxpath.ri.compiler;


/**
 * Captures the <code>foo[@name=<i>expr</i>]</code> expression. These
 * expressions are handled in a special way when applied to beans
 * or maps.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.3 $ $Date: 2003/01/11 05:41:23 $
 */
public class NameAttributeTest extends CoreOperation {

    public NameAttributeTest(Expression namePath, Expression nameValue) {
        super(OP_EQ, namePath, nameValue);
    }

    public Expression getNameTestExpression() {
        return getArg2();
    }

    /**
     * @see Expression#computeContextDependent()
     */
    public boolean computeContextDependent() {
        return true;
    }
}
