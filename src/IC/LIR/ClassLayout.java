package IC.LIR;

import java.util.*;
import IC.AST.*;

/**
 * ClassLayout
 * ===========
 * class layout implementation
 * holds methods and fields offsets
 */
public class ClassLayout {

	private ICClass icClass;
	private Map<Method,Integer> methodToOffset = new HashMap<Method,Integer>();
	private Map<Field,Integer> fieldToOffset = new HashMap<Field,Integer>();
	private int methodCounter = 0;
	private int fieldCounter = 0;
	
	/**
	 * constructor for class layout
	 * @param icClass
	 */
	public ClassLayout(ICClass icClass){
		// put methods
		for(Method m: icClass.getMethods()){
			methodToOffset.put(m, methodCounter++);
		}
		// put fields
		for(Field f: icClass.getFields()){
			fieldToOffset.put(f, fieldCounter++);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ClassLayout (ICClass icClass, ClassLayout superLayout){
		this(icClass);
		
		// 
		methodToOffset = (HashMap<Method, Integer>)((HashMap<Method, Integer>)superLayout.getMethodToOffsetMap()).clone();
		fieldToOffset = (HashMap<Field, Integer>)((HashMap<Field, Integer>)superLayout.getFieldToOffsetMap()).clone();
		
		
	}
	
	
	//////////////
	//	getters	//
	//////////////
	
	/**
	 * getter for this class layout's ICClass
	 */
	public ICClass getICClass(){
		return this.icClass;
	}
	
	/**
	 * getter for this class name
	 * @return
	 */
	public String getClassName(){
		return this.icClass.getName();
	}
	
	/**
	 * getter for map of methods and offsets
	 * @return
	 */
	public Map<Method,Integer> getMethodToOffsetMap(){
		return this.methodToOffset;
	}
	
	/**
	 * getter for method's offset
	 * @param m
	 * @return
	 */
	public Integer getMethodOffset(Method m){
		return methodToOffset.get(m);
	}
	
	/**
	 * getter for map of fields and offsets
	 * @return
	 */
	public Map<Field,Integer> getFieldToOffsetMap(){
		return this.fieldToOffset;
	}
	
	/**
	 * getter for field's offset
	 * @param f
	 * @return
	 */
	public Integer getFieldOffset(Field f){
		return fieldToOffset.get(f);
	}
	
	//////////////
	//	adders	//
	//////////////
	
	/**
	 * adder for method to offset
	 * @param m
	 * @param offset
	 */
	public void addMethodToOffset(Method m, Integer offset){
		methodToOffset.put(m, offset);
	}
	
	/**
	 * adder for field to offset
	 * @param f
	 * @param offset
	 */
	public void addFieldToOffset(Field f, Integer offset){
		fieldToOffset.put(f, offset);
	}
}
