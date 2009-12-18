package IC.TypeTable;

import java.util.*;

/**
 * Method Type
 * Holds the input types and output type
 */
public class MethodType extends Type {
	private List<Type> paramTypes;
	private Type returnType;
	
	public MethodType(Type returnType, List<Type> paramTypes){
		super(null);
		this.returnType = returnType;
		this.paramTypes = paramTypes;
	}
	
	public boolean subtypeOf(Type t){
		if (t.getName() == this.getName()) return true;
		else return false;
	}
	
	/**
	 * getter for the output type
	 */
	public Type getReturnType(){
		return this.returnType;
	}
	
	/**
	 * getter for the input types list
	 */
	public List<Type> getParamTypes(){
		return this.paramTypes;
	}

	/**
	 * checks if mt equals this (by name, returned type and all parameter types)
	 * @param mt
	 * @return
	 */
	public boolean equals(MethodType mt){
		if (this.getName() != mt.getName()) return false;
		else if (this.returnType != mt.getReturnType()) return false;
		else{
			Iterator<Type> myIter = this.paramTypes.iterator();
			Iterator<Type> otherIter = mt.paramTypes.iterator();
			
			while (myIter.hasNext() && otherIter.hasNext()){
				if (myIter.next() != otherIter.next()) return false; 
			}
			if (myIter.hasNext() || otherIter.hasNext()) return false;
			else return true;
		}
	}
	
	/**
	 * returns the string representation for method type
	 */
	public String toString(){
		String str = "{";

		// parameter types
		Iterator<Type> paramIter = paramTypes.iterator();
		if (paramIter.hasNext()) str += paramIter.next().getName(); // put first parameter if exists
		while (paramIter.hasNext()) str += ", "+paramIter.next().getName(); // put others if exist
		
		// return type
		str += " -> "+this.returnType.getName()+"}";
		
		return str;
	}
}