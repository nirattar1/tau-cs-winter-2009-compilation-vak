package IC.TypeTable;

import java.util.*;
import IC.AST.*;

/**
 * Main class to hold the type table for the input program
 *
 */
public class TypeTable {
    private static Map<String,ClassType> uniqueClassTypes = new HashMap<String,ClassType>();
    private static Map<Type,ArrayType> uniqueArrayTypes = new HashMap<Type,ArrayType>();
    private static Map<String,MethodType> uniqueMethodTypes = new HashMap<String,MethodType>();
    private static Map<String,Type> uniquePrimitiveTypes = new HashMap<String,Type>();
    
    protected static int idCounter = 0;
    private static String icFileName = null;
    
    /**
     * initialize the type table
     */
    public static void initTypeTable(String icFileName){
    	uniquePrimitiveTypes.put("int", new IntType());
    	uniquePrimitiveTypes.put("boolean", new BoolType());
    	uniquePrimitiveTypes.put("null", new NullType());
    	uniquePrimitiveTypes.put("string", new StringType());
    	uniquePrimitiveTypes.put("void", new VoidType());
    	TypeTable.icFileName = icFileName;
    }
    
    /**
     * getter for the ic program file name
     */
    public static String getFileName(){
    	return icFileName;
    }

    /**
     *  Returns unique array type object
     * 
     */
    public static ArrayType arrayType(Type elemType) {
       if (uniqueArrayTypes.containsKey(elemType)) {
          // array type object already created – return it
          return uniqueArrayTypes.get(elemType);
       }
       else {
          // object doesn't exist – create and return it
          ArrayType arrt = new ArrayType(elemType);
          uniqueArrayTypes.put(elemType,arrt);
          return arrt;
       }
    }
    
    /**
     * Adds a new ClassType entry to TypeTable. If the class is already defined
     * or extends a class that was not previously defined, throws SemanticError. 
     * @param c
     * @throws SemanticError
     */
    public static void addClassType(ICClass c) throws SemanticError{
    	if (uniqueClassTypes.containsKey(c.getName())){ 
    		throw new SemanticError("class already defined",c.getLine(),c.getName());
    	}
    	if (c.hasSuperClass()) {
    		if (!uniqueClassTypes.containsKey(c.getSuperClassName()))
    			throw new SemanticError("super-class is undefined",c.getLine(),c.getSuperClassName());
    	}
    	
    	ClassType ct = new ClassType(c);
    	uniqueClassTypes.put(c.getName(),ct);
    }
    
    /** 
     * Returns unique class type object
     */
    public static ClassType getClassType(String name) throws SemanticError{
    	ClassType ct = uniqueClassTypes.get(name);
    	if (ct == null) throw new SemanticError("class is undefined",name);
    	else return ct;
    }
    
    public static MethodType methodType(Type returnType, List<Type> paramTypes){
    	MethodType mt = new MethodType(returnType,paramTypes);
    	String key = mt.toString();
    	
    	MethodType mt2 = uniqueMethodTypes.get(key);
    	if (mt2 == null) {
    		uniqueMethodTypes.put(key, mt);
    		return mt;
    	} else return mt2;
    	
    }
    
    /**
     * A getter that gets a String and returns the type
     */
    public static Type getType(String typeName) throws SemanticError{
    	Type t;
    	
    	// case primitive type
    	t = uniquePrimitiveTypes.get(typeName);
    	if (t != null) return t;
    	// case array type
    	if (typeName.endsWith("[]")) return arrayType(getType(typeName.substring(0, typeName.length()-2)));
    	// case class type
    	else return getClassType(typeName);
    }
    
    /**
     * returns string representation for the TypeTable fitting the "-dump-symtab" IC.Compiler flag
     * @return
     */
    public static String staticToString(){
    	String str = "Type Table: "+icFileName+"\n";
    	
    	// construct string representation for primitive types
    	Iterator<Type> uniquePrimitiveTypesIter = uniquePrimitiveTypes.values().iterator();
    	String primitiveTypesStr = "";
    	while (uniquePrimitiveTypesIter.hasNext()){
    		Type t = uniquePrimitiveTypesIter.next();
    		primitiveTypesStr += "\t"+t.getTypeID()+": Primitive type: "+t.getName()+"\n";
    	}
    	
    	// construct string representation for class types
    	Iterator<ClassType> uniqueClassTypesIter = uniqueClassTypes.values().iterator();
    	String classTypesStr = "";
    	while (uniqueClassTypesIter.hasNext()){
    		ClassType ct = uniqueClassTypesIter.next();
    		classTypesStr += "\t"+ct.getTypeID()+": Class: "+ct.toString()+"\n";
    	}
    	
    	// construct string representation for array types
    	Iterator<ArrayType> uniqueArrayTypesIter = uniqueArrayTypes.values().iterator();
    	String arrayTypesStr = "";
    	while (uniqueArrayTypesIter.hasNext()){
    		ArrayType at = uniqueArrayTypesIter.next();
    		arrayTypesStr += "\t"+at.getTypeID()+": Array type: "+at.toString()+"\n";
    	}
    	
    	// construct string representation for method types
    	String methodTypesStr = "";
    	for (MethodType mt: uniqueMethodTypes.values()){
    		methodTypesStr += "\t"+mt.getTypeID()+": Method type: "+mt.toString()+"\n";
    	}
    	
    	str += primitiveTypesStr+classTypesStr+arrayTypesStr+methodTypesStr;
    	return str;
    }
    
    /**
     * Checks whether the name is of a primitive type (except for null or string).
     * @param name - type name.
     * @return true if type is primitive, false otherwise.
     */
    public static boolean isPrimitive(String name){
    	return ((name == "int") || (name == "boolean") || (name == "void"));
    }
    
}
