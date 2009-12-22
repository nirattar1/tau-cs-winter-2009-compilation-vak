package IC.SymbolTable;

import java.util.*;
import IC.TypeTable.*;

/**
 * Symbol table for type Block
 *
 */
public class BlockSymbolTable extends SymbolTable {
	protected Map<String,VarSymbol> varEntries = new HashMap<String,VarSymbol>();
	protected List<BlockSymbolTable> blockSymbolTableEntries = new ArrayList<BlockSymbolTable>();
	
	public BlockSymbolTable(SymbolTable parent){
		super(parent);
	}
	
	/**
	 * a local variable symbol getter
	 * @param name
	 * @return
	 */
	public VarSymbol getVarSymbol(String name) throws SemanticError{
		VarSymbol vs = varEntries.get(name);
		if (vs == null) throw new SemanticError("variable does not exist in block", name);
		else return vs;
	}
	
	/**
	 * a local variable recursive symbol getter
	 * returns the variable from the first scope it encounters it in, or throws
	 * semantic error if not found
	 * @param name
	 * @return
	 * @throws SemanticError
	 */
	public VarSymbol getVarSymbolRec(String name) throws SemanticError{
		VarSymbol vs = varEntries.get(name);
		if (vs == null) vs = ((BlockSymbolTable) parent).getVarSymbolRec(name);
		return vs;
	}
	
	/**
	 * a local variable symbol adder
	 * @param name
	 * @param typeName
	 * @throws SemanticError
	 */
	public void addVarSymbol(String name, String typeName) throws SemanticError{
		this.varEntries.put(name, new VarSymbol(name,typeName));
	}
	
	/**
	 * a BlockSymbolTable adder
	 */
	public void addBlockSymbolTable(BlockSymbolTable bst){
		blockSymbolTableEntries.add(bst);
	}
	
	/**
	 * a BlockSymbolTable list getter
	 */
	public List<BlockSymbolTable> getBlockSymbolTableList(){
		return blockSymbolTableEntries;
		
	}
	
	/**
	 * returns string representation for the BlockSymbolTable fitting the "-dump-symtab" IC.Compiler flag
	 * @param path: current path of the block ("statement in ")+method_name
	 */
	public String toString(String path){
		String str = "Statement Block Symbol Table ( located in "+path+" )";
		
		// print list of symbols (local variables)
		for(VarSymbol vs: varEntries.values()){
			str += "\n\tLocal variable: "+vs.getType().getName()+" "+vs.getName();
		}
		
		// update path to include another "statement block in "
		path = "statement block in "+path;
		
		// print list of children tables (blocks only)
		if(!blockSymbolTableEntries.isEmpty()){
			str += "\nChildren tables: ";
			for(BlockSymbolTable bst: blockSymbolTableEntries)
				str += "statement block in "+path+", ";
			str = str.substring(0, str.length()-2);
		}
		str += "\n\n";
		
		// recursively print block symbol tables
		for(BlockSymbolTable bst: blockSymbolTableEntries){
			str += bst.toString(path); // in BlockSymbolTable
		}
		
		return str;
	}
}
