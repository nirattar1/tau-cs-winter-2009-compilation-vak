package IC.AST;

import IC.BinaryOps;

import IC.LIR.*;

/**
 * Mathematical binary operation AST node.
 * 
 * @author Tovi Almozlino
 */
public class MathBinaryOp extends BinaryOp {

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	
	public LIRUpType accept(PropagatingVisitor<Integer,LIRUpType> visitor, Integer downInt){
		return visitor.visit(this, downInt);
	}

	/**
	 * Constructs a new mathematical binary operation node.
	 * 
	 * @param operand1
	 *            The first operand.
	 * @param operator
	 *            The operator.
	 * @param operand2
	 *            The second operand.
	 */
	public MathBinaryOp(Expression operand1, BinaryOps operator, Expression operand2) {
		super(operand1, operator, operand2);
	}

}
