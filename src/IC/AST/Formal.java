package IC.AST;

import IC.LIR.PropagatingVisitor;

/**
 * Method parameter AST node.
 * 
 * @author Tovi Almozlino
 */
public class Formal extends ASTNode {

	private Type type;

	private String name;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	
	public String accept(PropagatingVisitor<Integer,String> visitor, Integer downInt){
		return visitor.visit(this, downInt);
	}

	/**
	 * Constructs a new parameter node.
	 * 
	 * @param type
	 *            Data type of parameter.
	 * @param name
	 *            Name of parameter.
	 */
	public Formal(Type type, String name) {
		super(type.getLine());
		this.type = type;
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

}
