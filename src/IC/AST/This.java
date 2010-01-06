package IC.AST;

import IC.LIR.PropagatingVisitor;

/**
 * 'This' expression AST node.
 * 
 * @author Tovi Almozlino
 */
public class This extends Expression {

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	public String accept(PropagatingVisitor<Integer,String> visitor, Integer downInt){
		return visitor.visit(this, downInt);
	}
	
	/**
	 * Constructs a 'this' expression node.
	 * 
	 * @param line
	 *            Line number of 'this' expression.
	 */
	public This(int line) {
		super(line);
	}

}
