package IC.SymbolTable;

import java.util.*;
import IC.TypeTable.*;

/**
 * Symbol table for type Class
 *
 */
public class ClassSymbolTable extends SymbolTable {
	private Map<String,MethodSymbol> methodEntries = new HashMap<String,MethodSymbol>();
	private Map<String,FieldSymbol> fieldEntries = new HashMap<String,FieldSymbol>();
	private ClassSymbol mySymbol;
	private boolean hasSuper;
	
	private Map<String,MethodSymbolTable> methodSymbolTableEntries = new HashMap<String,MethodSymbolTable>();
	private Map<String,ClassSymbolTable> classSymbolTableEntries = new HashMap<String,ClassSymbolTable>();
	
	/**
	 * constructors for class symbol table
	 * parent can be either the single GlobalSymbolTable or a class symbol table
	 * of the super class for the class this symbol table represents
	 * @param name
	 * @param parent
	 */
	public ClassSymbolTable(String name,ClassSymbolTable parent, GlobalSymbolTable global){
		super(parent);
		this.mySymbol = global.getClass(name);
		this.hasSuper = true;
	}
	
	public ClassSymbolTable(String name,GlobalSymbolTable parent){
		super(parent);
		this.mySymbol = parent.getClass(name);
		this.hasSuper = false;
	}

	/**
	 * returns true iff this class has a super class
	 * @return
	 */
	public boolean hasSuperClass(){
		return this.hasSuper;
	}
	
	/**
	 * a method symbol getter
	 * @param name
	 * @return
	 */
	public MethodSymbol getMethodSymbol(String name) throws SemanticError{
		MethodSymbol ms = methodEntries.get(name);
		if (ms == null) throw new SemanticError("method does not exist in "+this.mySymbol.getName(), name);
		else return ms; 
	}
	
	/**
	 * a method symbol getter from this class or super class hierarchy
	 * if method does not exist in the class hierarchy, throws SemanticError
	 * @param name
	 * @return
	 */
	public MethodSymbol getMethodSymbolRec(String name) throws SemanticError{
		MethodSymbol ms = methodEntries.get(name);
		if (ms == null) {
			if (hasSuper){
				ms = ((ClassSymbolTable) parent).getMethodSymbolRec(name);
			} else {
				throw new SemanticError("method does not exist",name);
			}
		}
		return ms;
	}

	/**
	 * a method symbol adder
	 * @param name
	 * @param returnType
	 * @param paramTypes
	 * @param isStatic
	 */
	public void addMethodSymbol(String name, Type returnType, List<Type> paramTypes, boolean isStatic){
		this.methodEntries.put(name, new MethodSymbol(name,returnType,paramTypes, isStatic));
	}
	
	/**
	 * a method symbol adder
	 * @param name
	 * @param ms
	 */
	public void addMethodSymbol(String name, MethodSymbol ms){
		this.methodEntries.put(name, ms);
	}

	/**
	 * a field symbol getter
	 * @param name
	 * @return
	 */
	public FieldSymbol getFieldSymbol(String name) throws SemanticError{
		FieldSymbol fs = fieldEntries.get(name);
		if (fs == null) throw new SemanticError("field does not exist in "+this.mySymbol.getName(),name);
		else return fs;
	}
	
	/**
	 * a field symbol getter from this class or super class hierarchy
	 * if field does not exist in the class hierarchy, throws SemanticError
	 * @param name
	 * @return
	 */
	public FieldSymbol getFieldSymbolRec(String name) throws SemanticError{
		FieldSymbol fs = fieldEntries.get(name);
		if (fs == null) {
			if (hasSuper){
				fs = ((ClassSymbolTable) parent).getFieldSymbolRec(name);
			} else {
				throw new SemanticError("name cannot be resolved",name);
			}
		}
		return fs;
	}

	/**
	 * a field symbol adder
	 * @param symName
	 * @param typeName
	 */
	public void addFieldSymbol(String name, String typeName) throws SemanticError{
		this.fieldEntries.put(name,new FieldSymbol(name,typeName));
	}
	
	/**
	 * a getter for the current class symbol table's symbol in the global symbol table
	 * @return
	 */
	public ClassSymbol getMySymbol(){
		return this.mySymbol;
	}
	
	/**
	 * a MethodSymbolTable adder
	 */
	public void addMethodSymbolTable(MethodSymbolTable mst){
		methodSymbolTableEntries.put(mst.getName(), mst);
	}
	
	/**
	 * a MethodSymbolTable getter
	 */
	public MethodSymbolTable getMethodSymbolTable(String name){
		return methodSymbolTableEntries.get(name);
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
	 * gets a class symbol table, through the hierarchy
	 * @param name - class name
	 * @return
	 */
	public ClassSymbolTable getClassSymbolTableRec(String name){
		ClassSymbolTable csm = classSymbolTableEntries.get(name);
		if (csm != null) return csm;
		else {
			for (ClassSymbolTable csm_l: classSymbolTableEntries.values()){
				csm = csm_l.getClassSymbolTableRec(name);
				if (csm != null) return csm;
			}
		}
		return null;
	}

	
	/**
	 * returns string representation for the ClassSymbolTable fitting the "-dump-symtab" IC.Compiler flag
	 */
	public String toString(){
		String str = "Class Symbol Table: "+this.mySymbol.getName();
		
		// print list of symbols (fields and methods)
		for(FieldSymbol fs: fieldEntries.values()){
			str += "\n\tField: "+fs.getType().getName()+" "+fs.getName();
		}
		
		for(MethodSymbol ms: methodEntries.values()){
			String mType = (ms.isStatic()?"Static":"Virtual")+" method";
			str += "\n\t"+mType+": "+ms.getName()+" "+ms.getType().toString();
		}

		// print list of children tables (classes and methods)
		if(!classSymbolTableEntries.isEmpty() || !methodSymbolTableEntries.isEmpty()){
			str += "\nChildren tables: ";
			
			for(MethodSymbolTable mst: methodSymbolTableEntries.values()){
				str += mst.getName()+", ";
			}
			for(ClassSymbolTable cst: classSymbolTableEntries.values()){
				str += cst.getMySymbol().getName()+", ";
			}
			
			str = str.substring(0, str.length()-2);
		}
		str += "\n\n";
		
		// recursively print method symbol tables
		for(MethodSymbolTable mst: methodSymbolTableEntries.values()){
			str += mst; // overriden in MethodSymbolTable
		}
		for(ClassSymbolTable cst: classSymbolTableEntries.values()){
			str += cst; // overriden in ClassSymbolTable
		}
		
		return str;
	}
}
