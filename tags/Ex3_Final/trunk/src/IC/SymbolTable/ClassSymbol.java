package IC.SymbolTable;

import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;
import IC.AST.*;

/**
 * Dynamic class for symbol of type class (for GlobalSymbolTable and MethodSymbolTable)
 *
 */
public class ClassSymbol extends Symbol {
	private ICClass icClass;

	/**
	 * constructor for class symbol
	 * @param c - an ICClass ASTnode
	 * @throws SemanticError
	 * adds a new type to the TypeTable and creates a new Symbol with that type
	 */
	public ClassSymbol(ICClass c) throws SemanticError{
		super(c.getName());
		TypeTable.addClassType(c);
		this.type = TypeTable.getClassType(this.name);
		this.icClass = c;
	}
	
	public ICClass getIcClass() {
		return icClass;
	}
	
	public String getKind(){
		return "CLASS";
	}
}
