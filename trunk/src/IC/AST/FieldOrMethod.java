package IC.AST;

import java.util.List;
import java.util.ArrayList;

/**
 * A class with two lists: list of Method and list of Field 
 */
public class FieldOrMethod {
	
	private List<Method> lm;
	private List<Field> lf;
	
	// constructors
	public FieldOrMethod(Method m){
		 lm = new ArrayList<Method>();
		 lm.add(m);
		 lf = new ArrayList<Field>();
	}
	
	public FieldOrMethod(List<Field> fields){
		 lf = new ArrayList<Field>();
		 for (Field f: fields){
			 lf.add(f);
		 }
		 lm = new ArrayList<Method>();
	}
	
	// getters
	/**
	 * returns the list of methods
	 */
	public List<Method> getMethodList(){
		return this.lm;
	}
	
	/**
	 * returns the list of fields
	 */
	public List<Field> getFieldList(){
		return this.lf;
	}
	
	// adders
	/**
	 * adds a method
	 * @param m is a method
	 */
	public void addMethod(Method m){
		this.lm.add(m);
	}
	
	/**
	 * adds fields from a list of fields
	 * @param fields is a list of fields
	 */
	public void addField(List<Field> fields){
		for (Field f: fields){
			this.lf.add(f);
		}
	}
}
