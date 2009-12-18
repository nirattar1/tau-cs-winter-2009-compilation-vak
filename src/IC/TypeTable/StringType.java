package IC.TypeTable;

/**
 * Primitive String Type
 */
public class StringType extends Type {
	public StringType(){
		super("string");
	}
	
	public boolean subtypeOf(Type t){
		if (t.getName() == this.getName()) return true;
		else return false;
	}
}