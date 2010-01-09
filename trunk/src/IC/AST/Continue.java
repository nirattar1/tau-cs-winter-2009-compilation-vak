package IC.AST;

import IC.LIR.*;

/**
 * Continue statement AST node.
 * 
 * @author Tovi Almozlino
 */
public class Continue extends Statement {

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	
	public LIRUpType accept(PropagatingVisitor<Integer,LIRUpType> visitor, Integer downInt){
		return visitor.visit(this, downInt);
	}

	/**
	 * Constructs a continue statement node.
	 * 
	 * @param line
	 *            Line number of continue statement.
	 */
	public Continue(int line) {
		super(line);
	}

}
