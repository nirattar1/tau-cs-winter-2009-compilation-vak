package IC.SymbolTable;

import IC.TypeTable.*;
import IC.AST.*;
import java.util.*;

/**
 * Symbol table for type Global
 *
 */
public class GlobalSymbolTable extends SymbolTable {
	private Map<String,ClassSymbol> classEntries;
	private Map<String,ClassSymbolTable> classSymbolTableEntries;
	private String icFileName;
	
	/**
	 * a constructor for the program's global symbol table
	 * will be instanced only once, has no parent
	 */
	public GlobalSymbolTable(String icFileName){
		super(null);
		this.classEntries = new HashMap<String, ClassSymbol>();
		this.classSymbolTableEntries = new HashMap<String, ClassSymbolTable>();
		this.icFileName = icFileName;
	}
	
	/**
	 * a symbol class adder
	 */
	public void addClass(ICClass c) throws SemanticError{
		classEntries.put(c.getName(), new ClassSymbol(c));
	}
	
	/**
	 * a class symbol getter
	 */
	public ClassSymbol getClass(String name){
		return classEntries.get(name);
	}
	
	/**
	 * a ClassSymbolTable adder
	 */
	public void addClassSymbolTable(ClassSymbolTable cst){
		classSymbolTableEntries.put(cst.getMySymbol().getName(), cst);
	}
	
	/**
	 * a ClassSymbolTable getter
	 */
	public ClassSymbolTable getClassSymbolTable(String name){
		return classSymbolTableEntries.get(name);
	}
	
	/**
	 * returns string representation for the GlobalSymbolTable fitting the "-dump-symtab" IC.Compiler flag
     * @param icFileName: the name of the ic input program file
	 * @return
	 */
	public String toString(){
		String str = "Global Symbol Table: "+icFileName;
		
		// print list of symbols (classes)
		for(ClassSymbol cs: classEntries.values()){
			str += "\n\tClass: "+cs.getName();
		}
		
		// print list of children tables
		if(!classSymbolTableEntries.isEmpty()){
			str += "\nChildren tables: ";
			for(ClassSymbolTable cst: classSymbolTableEntries.values())
				str += cst.getMySymbol().getName()+", ";
			str = str.substring(0, str.length()-2);
		}
		str += "\n\n";
		
		// recursively print class symbol tables
		for(ClassSymbolTable cst: classSymbolTableEntries.values()){
			str += cst; // overriden in ClassSymbolTable
		}
		
		return str;
	}
}
