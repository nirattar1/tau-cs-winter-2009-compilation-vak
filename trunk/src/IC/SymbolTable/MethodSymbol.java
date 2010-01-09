package IC.SymbolTable;

import IC.TypeTable.SemanticError;
import IC.TypeTable.Type;
import IC.TypeTable.TypeTable;
import IC.AST.*;
import java.util.*;

/**
 * Dynamic class for symbol of type method (for ClassSymbolTable only)
 *
 */
public class MethodSymbol extends Symbol {
	private boolean isStatic;
	
	/**
	 * constructor for method symbol
	 * @param symName
	 * @param returnType
	 * @param paramTypes
	 * @param isStatic
	 * @throws SemanticError
	 * adds a new method type to the TypeTable and creates a new Symbol with that type
	 */
	public MethodSymbol(String symName, Type returnType, List<Type> paramTypes, boolean isStatic){
		super(symName);
		this.type = TypeTable.methodType(returnType, paramTypes);
		this.isStatic = isStatic;
	}
	
	/**
	 * constructor for method symbol that uses the Method ASTNode
	 * @param m
	 * @throws SemanticError
	 */
	public MethodSymbol(Method m) throws SemanticError{
		super(m.getName());
		List<Type> paramTypes = new ArrayList<Type>();
		for (Formal f: m.getFormals()){
			paramTypes.add(TypeTable.getType(f.getType().getFullName())); 
		}
		Type retType = TypeTable.getType(m.getType().getFullName());
		
		this.type = TypeTable.methodType(retType, paramTypes);
		this.isStatic = m.isStatic();
	}
	
	/**
	 * returns true iff the method is static, false o/w
	 * @return
	 */
	public boolean isStatic(){
		return this.isStatic;
	}
	
	public String getKind(){
		return "METHOD"; 
	}
}
