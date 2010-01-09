package IC.SymbolTable;

import IC.TypeTable.SemanticError;

/**
 * Dynamic class for symbol of type method parameter (for MethodSymbolTable only)
 *
 */
public class ParamSymbol extends VarSymbol {
	
	public ParamSymbol(String symName, String typeName) throws SemanticError{
		super(symName,typeName);
	}
	
	/**
	 * @override
	 */
	public String getKind(){
		return "PARAM"; 
	}
}
