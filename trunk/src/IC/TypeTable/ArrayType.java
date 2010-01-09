package IC.TypeTable;

/**
 * Array Type
 */
public class ArrayType extends Type {
	private Type elemType; 
	
	public ArrayType(Type elemType){
		super(elemType.getName()+"[]");
		this.elemType = elemType;
	}
	
	public boolean subtypeOf(Type t){
		if (t == this) return true;
		//if (t.getName() == this.getName()) return true;
		else return false;
	}
	
	/**
	 * getter for the array elem type
	 */
	public Type getElemType(){
		return this.elemType;
	}
}
