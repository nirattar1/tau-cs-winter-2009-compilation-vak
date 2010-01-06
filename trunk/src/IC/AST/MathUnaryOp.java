package IC.AST;

import IC.UnaryOps;
import IC.LIR.PropagatingVisitor;

/**
 * Mathematical unary operation AST node.
 * 
 * @author Tovi Almozlino
 */
public class MathUnaryOp extends UnaryOp {

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	public String accept(PropagatingVisitor<Integer,String> visitor, Integer downInt){
		return visitor.visit(this, downInt);
	}
	
	/**
	 * Constructs a new mathematical unary operation node.
	 * 
	 * @param operator
	 *            The operator.
	 * @param operand
	 *            The operand.
	 */
	public MathUnaryOp(UnaryOps operator, Expression operand) {
		super(operator, operand);
	}

}
