package IC.SymbolTable;

import IC.TypeTable.*;
import java.util.*;

/**
 * Symbol table for type Method
 *
 */
public class MethodSymbolTable extends BlockSymbolTable {
	private ReturnVarSymbol returnVarSymbol;
	private String name;
	
	public MethodSymbolTable(String name, ClassSymbolTable parent){
		super(parent);
		this.name = name;
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
		return this.returnVarSymbol;
	}
	
	/**
	 * a return variable symbol setter
	 * @param name
	 * @param typeName
	 * @throws SemanticError
	 */
	public void setReturnVarSymbol(String typeName) throws SemanticError{
		this.returnVarSymbol = new ReturnVarSymbol("_ret",typeName);
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
			} else { // local variable case
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
}
