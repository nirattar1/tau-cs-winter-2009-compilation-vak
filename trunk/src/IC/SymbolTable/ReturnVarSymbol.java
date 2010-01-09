package IC.SymbolTable;

import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

/**
 * Dynamic class for symbol of type method returned variable (for MethodSymbolTable only)
 *
 */
public class ReturnVarSymbol extends VarSymbol {
	
	public ReturnVarSymbol(String symName, String typeName) throws SemanticError{
		super(symName, typeName);
	}
	
	public String getKind(){
		return "RET_VAR"; 
	}
}
