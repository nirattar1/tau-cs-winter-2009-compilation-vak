package IC.TypeTable;

import IC.AST.*;

/**
 * User defined class Type
 */
public class ClassType extends Type {
	private String super_name;
	private ICClass classAST;
	
	public ClassType(ICClass classAST){
		super(classAST.getName());
		this.super_name = classAST.getSuperClassName();
		this.classAST = classAST;
	}
	
	public boolean subtypeOf(Type t){
		if (!(t instanceof ClassType)) return false;	// t is not a class type		
		if (t.getName() == this.getName()) return true;	// t is me
		if (this.super_name == null) return false;		// I don't have a super
		else try{
			return TypeTable.getClassType(super_name).subtypeOf(t);	// try my super class
		} catch (SemanticError se){ // will never get here
			return false;
		}
	}
	
	/**
	 * getter for the super class name
	 */
	public String getSuperName(){
		return this.super_name;
	}
	
	/**
	 * getter for the class ast node
	 */
	public ICClass getClassNode(){
		return this.classAST;
	}
	
	public String toString(){
		String str = this.getName();

		if (this.classAST.hasSuperClass()){
			try {
				str += ", Superclass ID: "+TypeTable.getClassType(this.super_name).getTypeID();
			} catch (SemanticError se){} // will never be thrown
		}
		
		return str;
	}
}