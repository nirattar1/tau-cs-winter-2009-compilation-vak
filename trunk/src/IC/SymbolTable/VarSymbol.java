package IC.SymbolTable;

import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

/**
 * Dynamic class for symbol of type local variable (for MethodSymbolTable and BlockSymbolTable)
 *
 */
public class VarSymbol extends Symbol {
	
	public VarSymbol(String symName, String typeName) throws SemanticError{
		super(symName);
		this.type = TypeTable.getType(typeName);
	}
	
	public String getKind(){
		return "VAR"; 
	}
}
