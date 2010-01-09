package IC.TypeTable;

/**
 * Abstract Type base class
 */
public abstract class Type {
	private String name;
	private int typeID;
	
	public Type(String name){
		this.name = name;
		this.typeID = ++TypeTable.idCounter;
	}
	
	/**
	 * getter for the Type name
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * for classes: returns true iff my type of type t or a subtype of t
	 * for all other types: returns true iff my type is t
	 */
	public abstract boolean subtypeOf(Type t);
	
	/**
	 * getter for the type's unique id
	 * @return
	 */
	public int getTypeID(){
		return this.typeID;
	}
	
	public String toString(){
		return this.name;
	}
}
