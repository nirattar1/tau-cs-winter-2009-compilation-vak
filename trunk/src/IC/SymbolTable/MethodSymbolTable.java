package IC.SymbolTable;

import IC.TypeTable.*;

import java.util.*;

/**
 * Symbol table for type Method
 *
 */
public class MethodSymbolTable extends BlockSymbolTable {
	//private ReturnVarSymbol returnVarSymbol;
	private String name;
	private boolean isStatic;
	
	public MethodSymbolTable(String name, ClassSymbolTable parent){
		super(parent);
		this.name = name;
		try{
			this.isStatic = parent.getMethodSymbol(name).isStatic();
		} catch(SemanticError se){} // will never be thrown
	}
	
	/**
	 * a getter for the name of the method for this method symbol table
	 * @return
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * a local variable / parameter symbol getter
	 * @param name
	 * @return
	 */
	public VarSymbol getVarParamSymbol(String name) throws SemanticError{
		VarSymbol vps = varEntries.get(name);
		if (vps == null) throw new SemanticError("variable/parameter does not exist in "+this.getName(), name);
		else return vps;
	}
	
	/**
	 * a parameter symbol adder
	 * @param name
	 * @param typeName
	 * @throws SemanticError
	 */
	public void addParamSymbol(String name, String typeName) throws SemanticError{
		this.varEntries.put(name, new ParamSymbol(name, typeName));
	}
	
 	/**
	 * a return variable symbol getter
	 * @return
	 */
	public ReturnVarSymbol getReturnVarSymbol(){
		try{
			return (ReturnVarSymbol) this.getVarParamSymbol("_ret");
		} catch (SemanticError se){ // never get here
			return null;
		}
	}
	
	/**
	 * a return variable symbol setter
	 * @param name
	 * @param typeName
	 * @throws SemanticError
	 */
	public void setReturnVarSymbol(String typeName) throws SemanticError{
		this.varEntries.put("_ret", new ReturnVarSymbol("_ret", typeName));
	}
	
	/**
	 * a local variable recursive symbol getter
	 * returns the variable from the first scope it encounters it in, or throws
	 * semantic error if not found
	 * only in a case of a virtual method, will continue recursive field search in its parent (class)
	 * @param name
	 * @return
	 * @throws SemanticError
	 */
	public VarSymbol getVarSymbolRec(String name) throws SemanticError{
		VarSymbol vs = varEntries.get(name); // parameters and local variables of method
		if (vs == null){
			if (this.isStatic){ // the method whose this symbol table belongs to is a static method
				throw new SemanticError("name cannot be resolved",name);
			} else { // it is a virtual method, continue recursive search
				vs = ((ClassSymbolTable) parent).getFieldSymbolRec(name);
			}
		}
		return vs;
	}
	
	/**
	 * Returns true if and only if the first encounter with name in the Symbol table hierarchy
	 * is in an enclosing class.
	 * This method will only be called in the translation to LIR. Consequently, The variable name exists (passed checks),
	 * and so, if it wasn't in any of the blocks, and is not in the method, it has to be a field in the class.
	 * @param name - name of the variable
	 * @return true iff name is a name of a field
	 */
	public boolean isVarField (String name){
		if (varEntries.containsKey(name)) return false;
		else return true;
	}

	
	/**
	 * get the depth of the symbol table
	 * @param name
	 * @return
	 */
	public int getVarDepthRec(String name){
		int vd = varEntries.containsKey(name) ? this.getDepth() :
			((ClassSymbolTable) parent).getFieldDepthRec(name);
		return vd;
	}

	/**
	 * returns string representation for the MethodSymbolTable fitting the "-dump-symtab" IC.Compiler flag
	 */
	public String toString(){
		String str = "Method Symbol Table: "+this.getName();
		
		// print list of symbols (parameters and local variables)
		String pListStr = "";
		String lvListStr = "";
		for(VarSymbol vs: varEntries.values()){
			if (vs.getKind() == "PARAM"){ // parameter case
				pListStr += "\n\tParameter: "+vs.getType().getName()+" "+vs.getName();
			} else if (vs.getKind() != "RET_VAR"){ // local variable case
				lvListStr += "\n\tLocal variable: "+vs.getType().getName()+" "+vs.getName();
			}
		}
		str += pListStr+lvListStr;
		
		// print list of children tables (blocks only)
		String path = "statement block in "+this.getName();
		
		if (!blockSymbolTableEntries.isEmpty()){
			str += "\nChildren tables: ";
			for(BlockSymbolTable bst: blockSymbolTableEntries)
				str += path+", ";
			str = str.substring(0, str.length()-2);
		}
		str += "\n\n";
		
		// recursively print block symbol tables
		for(BlockSymbolTable bst: blockSymbolTableEntries){
			str += bst.toString(this.getName()); // in BlockSymbolTable
		}
		
		return str;
	}
	
	/**
	 * returns the method's enclosing class
	 * @return Enclosing Class
	 */
	public ClassSymbolTable getEnclosingClassSymbolTable(){
		return (ClassSymbolTable) parent;
	}

}
