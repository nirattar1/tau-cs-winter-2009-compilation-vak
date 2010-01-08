package IC.AST;

import IC.LIR.*;
import IC.SymbolTable.*;

/**
 * Abstract AST node base class.
 * 
 * @author Tovi Almozlino
 */
public abstract class ASTNode {

	private int line;
	
	/**
	 * will hold the number of registers required to store the value of the ASTNode
	 * (e.g. 2 registers for array location)
	 */
	private int requiredRegs;

	public int getRequiredRegs() {
		return requiredRegs;
	}

	public void setRequiredRegs(int requiredRegs) {
		this.requiredRegs = requiredRegs;
	}

	/** reference to symbol table of enclosing scope **/
	  private SymbolTable enclosingScope;
	
	/**
	 * Double dispatch method, to allow a visitor to visit a specific subclass.
	 * 
	 * @param visitor
	 *            The visitor.
	 * @return A value propagated by the visitor.
	 */
	public abstract Object accept(Visitor visitor);
	
	/**
	 * Double dispatch method, to allow a propagating visitor to visit a specific subcalss
	 * used by IC.LIR package
	 * @param visitor
	 * @param downInt
	 * @return
	 */
	public abstract LIRUpType accept(PropagatingVisitor<Integer, LIRUpType> visitor, Integer downInt);
	
	/**
	 * Constructs an AST node corresponding to a line number in the original
	 * code. Used by subclasses.
	 * 
	 * @param line
	 *            The line number.
	 */
	protected ASTNode(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	/**
	 * A setter for the node's enclosing scope symbol table
	 * @param enc
	 */
	public void setEnclosingScope(SymbolTable enc){
		this.enclosingScope = enc;
	}
	
	/**
	 * A getter for the node's enclosing scope symbol table
	 * @param enc
	 */
	public SymbolTable getEnclosingScope(){
		return this.enclosingScope;
	}
}
