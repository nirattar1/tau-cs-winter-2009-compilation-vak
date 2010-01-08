package IC.AST;

import IC.LIR.*;

/**
 * AST node for expression in parentheses.
 * 
 * @author Tovi Almozlino
 */
public class ExpressionBlock extends Expression {

	private Expression expression;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	public LIRUpType accept(PropagatingVisitor<Integer,LIRUpType> visitor, Integer downInt){
		return visitor.visit(this, downInt);
	}
	
	/**
	 * Constructs a new expression in parentheses node.
	 * 
	 * @param expression
	 *            The expression.
	 */
	public ExpressionBlock(Expression expression) {
		super(expression.getLine());
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}

}
