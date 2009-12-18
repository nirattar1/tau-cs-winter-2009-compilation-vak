package IC.SymbolTable;

import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

/**
 * Dynamic class for symbol of type method returned variable (for MethodSymbolTable only)
 *
 */
public class ReturnVarSymbol extends Symbol {
	
	public ReturnVarSymbol(String symName, String typeName) throws SemanticError{
		super(symName);
		this.type = TypeTable.getType(typeName);
	}
	
	public String getKind(){
		return "RET_VAR"; 
	}
}
