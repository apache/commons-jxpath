package org.apache.commons.jxpath.ri.compiler;


/**
 * Captures the <code>foo[@name=<i>expr</i>]</code> expression. These
 * expressions are handled in a special way when applied to beans
 * or maps.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.4 $ $Date: 2003/01/19 23:59:24 $
 */
public class NameAttributeTest extends CoreOperationEqual {

    public NameAttributeTest(Expression namePath, Expression nameValue) {
        super(namePath, nameValue);
    }

    public Expression getNameTestExpression() {
        return args[1];
    }

    /**
     * @see Expression#computeContextDependent()
     */
    public boolean computeContextDependent() {
        return true;
    }
}
