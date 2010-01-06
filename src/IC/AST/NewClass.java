package IC.AST;

import IC.LIR.PropagatingVisitor;

/**
 * Class instance creation AST node.
 * 
 * @author Tovi Almozlino
 */
public class NewClass extends New {

	private String name;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	public String accept(PropagatingVisitor<Integer,String> visitor, Integer downInt){
		return visitor.visit(this, downInt);
	}
	
	/**
	 * Constructs a new class instance creation expression node.
	 * 
	 * @param line
	 *            Line number of expression.
	 * @param name
	 *            Name of class.
	 */
	public NewClass(int line, String name) {
		super(line);
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
