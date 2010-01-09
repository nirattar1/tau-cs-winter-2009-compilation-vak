package IC.SymbolTable;

import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

/**
 * Dynamic class for symbol of type field (for ClassSymbolTable only)
 *
 */
public class FieldSymbol extends VarSymbol {
	
	public FieldSymbol(String symName, String typeName) throws SemanticError{
		super(symName, typeName);
	}
	
	@Override
	public String getKind(){
		return "FIELD"; 
	}
}
