package IC.SymbolTable;

import IC.TypeTable.*;

/**
 * The abstract class for Symbol, a SymbolTable entry
 *
 */
public abstract class Symbol {
	protected String name;
	protected Type type;
	protected int line;
	
	public Symbol(String name){
		this.name = name;
	}
	
	/**
	 * getter for the Symbol name
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * getter for the Symbol type
	 */
	public Type getType(){
		return this.type;
	}
	
	/**
	 * getter for the Symbol kind (local var, method...)
	 */
	public abstract String getKind();
}
